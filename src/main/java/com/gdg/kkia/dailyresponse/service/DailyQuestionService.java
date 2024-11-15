package com.gdg.kkia.dailyresponse.service;

import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.dailyresponse.dto.DailyQuestionRequest;
import com.gdg.kkia.dailyresponse.dto.DailyQuestionResponse;
import com.gdg.kkia.dailyresponse.entity.DailyQuestion;
import com.gdg.kkia.dailyresponse.entity.DailyResponse;
import com.gdg.kkia.dailyresponse.repository.DailyQuestionRepository;
import com.gdg.kkia.dailyresponse.repository.DailyResponseRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyQuestionService {

    private static final Logger logger = LoggerFactory.getLogger(DailyQuestionService.class);
    private final static int NUMBER_OF_RECENTLY_QUESTION_TO_EXCLUDE = 3;

    private final DailyQuestionRepository dailyQuestionRepository;
    private final DailyResponseRepository dailyResponseRepository;

//    @PostConstruct
//    public void loadQuestionsFromFile() {
//        try (InputStream inputStream = getClass().getResourceAsStream("/dailyQuestions.txt");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String finalLine = line;
//                if (dailyQuestionRepository.findAll().stream().noneMatch(q -> q.getQuestion().equals(finalLine.trim()))) {
//                    DailyQuestion question = new DailyQuestion(line.trim());
//                    dailyQuestionRepository.save(question);
//                }
//            }
//        } catch (IOException e) {
//            logger.error("Failed to load questions from file", e);
//        }
//    }

    @Transactional
    public void addDailyQuestion(DailyQuestionRequest dailyQuestionRequest) {
        DailyQuestion dailyQuestion = new DailyQuestion(dailyQuestionRequest.question());
        dailyQuestionRepository.save(dailyQuestion);
    }

    @Transactional
    public DailyQuestionResponse getRandomQuestionExcludingRecent(Long memberId) {
        DailyResponse dailyResponse = dailyResponseRepository.findByMemberId(memberId)
                .orElse(null);

        if (dailyResponse != null && dailyResponse.getResponseDate().equals(LocalDate.now())) {
            return new DailyQuestionResponse(dailyResponse.getDailyQuestion().getId(), dailyResponse.getDailyQuestion().getQuestion(), true, dailyResponse.getResponse());
        }

        HttpSession session = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getSession();

        List<Long> recentlyFetchedQuestionIds = (List<Long>) session.getAttribute("recentlyQuestionIds");
        if (recentlyFetchedQuestionIds == null) {
            recentlyFetchedQuestionIds = new LinkedList<>();
        }

        List<DailyQuestion> allQuestions = dailyQuestionRepository.findAll();

        List<Long> finalRecentlyFetchedQuestionIds = recentlyFetchedQuestionIds;
        allQuestions.removeIf(question -> finalRecentlyFetchedQuestionIds.contains(question.getId()));

        if (!allQuestions.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(allQuestions.size());
            DailyQuestion randomQuestion = allQuestions.get(index);

            recentlyFetchedQuestionIds.add(randomQuestion.getId());
            if (recentlyFetchedQuestionIds.size() > NUMBER_OF_RECENTLY_QUESTION_TO_EXCLUDE) {
                recentlyFetchedQuestionIds.removeFirst();
            }
            session.setAttribute("recentlyQuestionIds", recentlyFetchedQuestionIds);

            return new DailyQuestionResponse(randomQuestion.getId(), randomQuestion.getQuestion(), false, "아직 응답이 없습니다.");
        }

        return new DailyQuestionResponse(null, "조회된 질문이 없습니다.", false, null);
    }

    @Transactional(readOnly = true)
    public List<DailyQuestionResponse> getAllDailyQuestionForManager() {
        return dailyQuestionRepository.findAll()
                .stream()
                .map(DailyQuestion -> new DailyQuestionResponse(
                        DailyQuestion.getId(),
                        DailyQuestion.getQuestion(),
                        false,
                        "아직 응답이 없습니다."))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateDailyQuestion(Long id, DailyQuestionRequest dailyQuestionRequest) {
        DailyQuestion dailyQuestion = dailyQuestionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 질문이 없습니다."));

        dailyQuestion.updateQuestion(dailyQuestionRequest.question());
    }

    @Transactional
    public void deleteDailyQuestion(Long id) {
        DailyQuestion dailyQuestion = dailyQuestionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 질문이 없습니다."));

        dailyQuestionRepository.deleteById(id);
    }

}
