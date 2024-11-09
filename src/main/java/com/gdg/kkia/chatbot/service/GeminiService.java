package com.gdg.kkia.chatbot.service;

import com.gdg.kkia.chatbot.dto.ChatRequest;
import com.gdg.kkia.chatbot.dto.ChatResponse;
import com.gdg.kkia.chatbot.entity.GeminiRequestType;
import com.gdg.kkia.chatbot.entity.GeminiContent;
import com.gdg.kkia.chatbot.dto.GeminiRequest;
import com.gdg.kkia.chatbot.dto.GeminiResponse;
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

    private static final String CHATPROMPT = "사용자가 일상이나 감정을 표현할 때, 대화에 따뜻함과 배려를 담아 응답해 주세요. " +
            "사용자의 감정 표현을 이해하고 공감하는 태도를 유지하며, 특히 우울감이 느껴질 경우 긍정적이고 위로가 되는 말로 응답해 주세요. " +
            "모든 답변은 한국어로 제공해주세요.";

    private static final String DIARYPROMPT = "사용자가 일기 내용을 제공하면, 그 일기에서 추상적인 부분을 식별하고, " +
            "그에 대해 사용자가 더 구체적으로 답할 수 있도록 질문을 던져주세요. 만약 추상적인 부분이 없다면, " +
            " 그 일기 내용을 바탕으로 공감을 표현하며 긍정적인 반응을 보여주세요. 모든 답변은 한국어로 제공해주세요.";

    private static final String CEHCKSTATUS = "다음은 우울증 상태를 체크하기 위한 문진표 항목과 사용자와의 대화 기록입니다. " +
            "모델 역할에서 사용자의 대화를 보고 그들의 감정을 파악해 주세요. 대화를 통해 사용자가 어떤 감정이나 느낌을 가졌을지 " +
            "분석해 주시고 분석한 내용을 바탕으로 각 질문에 대해 LLM이 사용자 역할에서 답변을 작성하고, 그에 따른 감정 상태와 점수를 " +
            "평가해 주세요. 문진표 질문과 대화의 문맥을 통해 사용자가 느낄 수 있는 감정을 파악하고, " +
            "우울감이 어느 정도인지 점수를 통해 제시해 주세요. 다음은 문진표입니다:" +
            "지난 2주일 동안 당신은 다음의 문제들로 인해서 얼마나 자주 방해를 받았습니까?" +
            "1. 일 또는 여가 활동을 하는데 흥미나 즐거움을 느끼지 못함\n" +
            "\n" +
            "전혀 방해받지 않았다 (0점)\n" +
            "며칠 동안 방해받았다 (1점)\n" +
            "7일 이상 방해받았다 (2점)\n" +
            "거의 매일 방해받았다 (3점)\n" +
            "기분이 가라앉거나, 우울하거나, 희망이 없음\n" +
            "\n" +
            "전혀 방해받지 않았다 (0점)\n" +
            "며칠 동안 방해받았다 (1점)\n" +
            "7일 이상 방해받았다 (2점)\n" +
            "거의 매일 방해받았다 (3점)\n" +
            "잠이 들거나 계속 잠을 자는 것이 어려움. 또는 잠을 너무 많이 잠\n" +
            "\n" +
            "전혀 방해받지 않았다 (0점)\n" +
            "며칠 동안 방해받았다 (1점)\n" +
            "7일 이상 방해받았다 (2점)\n" +
            "거의 매일 방해받았다 (3점)\n" +
            "피곤하다고 느끼거나 기운이 거의 없음\n" +
            "\n" +
            "전혀 방해받지 않았다 (0점)\n" +
            "며칠 동안 방해받았다 (1점)\n" +
            "7일 이상 방해받았다 (2점)\n" +
            "거의 매일 방해받았다 (3점)\n" +
            "입맛이 없거나 과식을 함\n" +
            "\n" +
            "전혀 방해받지 않았다 (0점)\n" +
            "며칠 동안 방해받았다 (1점)\n" +
            "7일 이상 방해받았다 (2점)\n" +
            "거의 매일 방해받았다 (3점)\n" +
            "자신을 부정적으로 봄 – 혹은 자신이 실패자라고 느끼거나 자신 또는 가족을 실망시킴\n" +
            "\n" +
            "전혀 방해받지 않았다 (0점)\n" +
            "며칠 동안 방해받았다 (1점)\n" +
            "7일 이상 방해받았다 (2점)\n" +
            "거의 매일 방해받았다 (3점)\n" +
            "신문을 읽거나 텔레비전 보는 것과 같은 일에 집중하는 것이 어려움\n" +
            "\n" +
            "전혀 방해받지 않았다 (0점)\n" +
            "며칠 동안 방해받았다 (1점)\n" +
            "7일 이상 방해받았다 (2점)\n" +
            "거의 매일 방해받았다 (3점)\n" +
            "다른 사람들이 주목할 정도로 너무 느리게 움직이거나 말을 함, 또는 반대로 평상시보다 많이 움직여서 너무 안절부절 못하거나 들떠 있음\n" +
            "\n" +
            "전혀 방해받지 않았다 (0점)\n" +
            "며칠 동안 방해받았다 (1점)\n" +
            "7일 이상 방해받았다 (2점)\n" +
            "거의 매일 방해받았다 (3점)\n" +
            "자신이 죽는 것이 더 낫다고 생각하거나 어떤 식으로든 자신을 해칠 것이라고 생각함\n" +
            "\n" +
            "전혀 방해받지 않았다 (0점)\n" +
            "며칠 동안 방해받았다 (1점)\n" +
            "7일 이상 방해받았다 (2점)\n" +
            "거의 매일 방해받았다 (3점)" +
            "다음은 대화 기록입니다: ";

    private String getContents(List<GeminiContent> prompt) {
        String requestUrl = apiUrl + "?key=" + geminiApiKey;

        GeminiRequest request = new GeminiRequest();
        for (GeminiContent content : prompt) {
            request.addContent(content);
        }

        GeminiResponse response = restTemplate.postForObject(requestUrl, request, GeminiResponse.class);

        return response.getResponseText();
    }

    public ChatResponse startChat(GeminiRequestType type, List<ChatRequest> conversations) {
        List<GeminiContent> prompt = new ArrayList<>();

        GeminiContent condition = getPromptCondition(type);
        prompt.add(condition);

        for (ChatRequest chat : conversations) {
            GeminiContent question = new GeminiContent("model", chat.getQuestion());
            prompt.add(question);
            GeminiContent response = new GeminiContent("user", chat.getResponse());
            prompt.add(response);
        }

        String response = getContents(prompt);

        return ChatResponse.builder()
                .response(response)
                .type(type)
                .build();
    }

    private GeminiContent getPromptCondition(GeminiRequestType type) {
        String prompt = "";

        switch (type) {
            case chat -> prompt = CHATPROMPT;
            case diary -> prompt = DIARYPROMPT;
        }

        return GeminiContent.builder().role("model").text(prompt).build();
    }
}