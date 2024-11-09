package com.gdg.kkia.storage.dailyresponse.service;

import com.gdg.kkia.storage.dailyresponse.dto.DailyQuestionResponse;
import com.gdg.kkia.storage.dailyresponse.entity.DailyQuestion;
import com.gdg.kkia.storage.dailyresponse.repository.DailyQuestionRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DailyQuestionService {

    private final static int NUMBER_OF_RECENTLY_QUESTION_TO_EXCLUDE = 3;

    private final DailyQuestionRepository dailyQuestionRepository;

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


}
