package com.gdg.kkia.gemini.service;

import com.gdg.kkia.gemini.GeminiRequestType;
import com.gdg.kkia.gemini.dto.GeminiContent;
import com.gdg.kkia.gemini.dto.GeminiRequest;
import com.gdg.kkia.gemini.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Qualifier("geminiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String DIARYPROMPT = "사용자가 일기 내용을 제공하면, 나는 그 일기에서 추상적인 부분을 식별하고, " +
            "그에 대해 사용자가 더 구체적으로 답할 수 있도록 질문을 던집니다. 만약 추상적인 부분이 없다면, " +
            " 그 일기 내용을 바탕으로 공감을 표현하며 긍정적인 반응을 보여줍니다. 모든 답변은 한국어로 제공됩니다.";


    public GeminiContent getContents(List<GeminiContent> prompt) {
        String requestUrl = apiUrl + "?key=" + geminiApiKey;

        GeminiRequest request = new GeminiRequest();
        for (GeminiContent content : prompt) {
            request.addContent(content);
        }

        GeminiResponse response = restTemplate.postForObject(requestUrl, request, GeminiResponse.class);

        GeminiContent geminiResponse = GeminiContent.builder()
                .role("model")
                .text(response.getResponseText())
                .build();
        return geminiResponse;
    }

    public GeminiContent startChat(GeminiRequestType type, List<GeminiContent> conversations) {
        List<GeminiContent> prompt = new ArrayList<>();

        GeminiContent condition = getPromptCondition(type);
        prompt.add(condition);
        prompt.addAll(conversations);

        return getContents(prompt);
    }

    private GeminiContent getPromptCondition(GeminiRequestType type) {
        String prompt = "";
        
        switch (type) {
            case chat -> prompt = DIARYPROMPT;
            case diary -> prompt = DIARYPROMPT;
        }

        return GeminiContent.builder().role("model").text(prompt).build();
    }
}