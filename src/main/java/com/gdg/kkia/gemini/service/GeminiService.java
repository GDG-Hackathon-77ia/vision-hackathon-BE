package com.gdg.kkia.gemini.service;

import com.gdg.kkia.gemini.dto.GeminiRequest;
import com.gdg.kkia.gemini.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public String getContents(String prompt) {
        String requestUrl = apiUrl + "?key=" + geminiApiKey;

        GeminiRequest request = new GeminiRequest(prompt);
        GeminiResponse response = restTemplate.postForObject(requestUrl, request, GeminiResponse.class);

        String message = response.getCandidates().get(0).getContent().getParts().get(0).getText().toString();

        return message;
    }

}