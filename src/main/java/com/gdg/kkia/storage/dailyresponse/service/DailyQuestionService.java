package com.gdg.kkia.storage.dailyresponse.service;

import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.storage.dailyresponse.dto.DailyQuestionRequest;
import com.gdg.kkia.storage.dailyresponse.dto.DailyQuestionResponse;
import com.gdg.kkia.storage.dailyresponse.entity.DailyQuestion;
import com.gdg.kkia.storage.dailyresponse.repository.DailyQuestionRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyQuestionService {

    private final static int NUMBER_OF_RECENTLY_QUESTION_TO_EXCLUDE = 3;

    private final DailyQuestionRepository dailyQuestionRepository;

    @Transactional
    public void addDailyQuestion(DailyQuestionRequest dailyQuestionRequest) {
        DailyQuestion dailyQuestion = new DailyQuestion(dailyQuestionRequest.question());
        dailyQuestionRepository.save(dailyQuestion);
    }

    @Transactional
    public DailyQuestionResponse getRandomQuestionExcludingRecent() {
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

            return new DailyQuestionResponse(randomQuestion.getId(), randomQuestion.getQuestion());
        }

        return new DailyQuestionResponse(null, "조회된 질문이 없습니다.");
    }

    @Transactional(readOnly = true)
    public List<DailyQuestionResponse> getAllDailyQuestionForManager() {
        return dailyQuestionRepository.findAll()
                .stream()
                .map(DailyQuestion -> new DailyQuestionResponse(
                        DailyQuestion.getId(),
                        DailyQuestion.getQuestion()))
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
