package com.gdg.kkia.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.kkia.chatbot.dto.ChatRequest;
import com.gdg.kkia.chatbot.dto.ChatResponse;
import com.gdg.kkia.chatbot.entity.ChatbotResponse;
import com.gdg.kkia.chatbot.entity.GeminiJsonResponse;
import com.gdg.kkia.chatbot.entity.GeminiRequestType;
import com.gdg.kkia.chatbot.entity.GeminiContent;
import com.gdg.kkia.chatbot.dto.GeminiRequest;
import com.gdg.kkia.chatbot.dto.GeminiResponse;
import com.gdg.kkia.chatbot.repository.ChatbotResponseRepository;
import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import com.gdg.kkia.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GeminiService {

    private final MemberRepository memberRepository;
    private final ChatbotResponseRepository chatbotResponseRepository;
    private final SurveyService surveyService;

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
            "대화는 다음과 같은 형태로 전달됩니다. 대화 형태: " +
            "Question: LLM이 보낸 질문, Responses: 사용자의 응답, ResponseTime: 응답한 시각, Type: chat(일반 채팅) 혹은 diary(일기 내용 기반 채팅)" +
            "모델 역할에서 사용자의 대화를 보고 그들의 감정을 파악해 주세요. 대화를 통해 사용자가 어떤 감정이나 느낌을 가졌을지 " +
            "분석해 주시고 분석한 내용을 바탕으로 각 질문에 대해 LLM이 사용자 역할에서 답변을 작성하고, 그에 따른 감정 상태와 점수를 " +
            "평가해 주세요. 문진표 질문과 대화의 문맥을 통해 사용자가 느낄 수 있는 감정을 파악하고, " +
            "우울감이 어느 정도인지 점수를 통해 제시해 주세요. 정리하자면 Json 형태로 다음과 형식에 맞춰 반환합니다. " +
            "반환 형태 : { totalScore: 문진 자가 진단 결과 점수 합계, question: [ {num: 문항 번호, score: 문항에 대한 점수, " +
            "reason: 문항에 대해 해당 점수를 매긴 이유} ], summary: 분석 요약 } 이 형태에 맞춰 추가적인 설명 없이 JSON으로만 반환합니다." +
            "다음은 문진표입니다:" +
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

    private GeminiResponse getContents(List<GeminiContent> prompt, boolean isJsonResponse) {
        String requestUrl = apiUrl + "?key=" + geminiApiKey;

        GeminiRequest request = new GeminiRequest();
        for (GeminiContent content : prompt) {
            request.addContent(content);
        }

        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig();
        if (isJsonResponse) {
            config.setResponse_mime_type("application/json");
            // JSON 스키마 설정 (필요한 경우)
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "object");
            schema.put("properties", new HashMap<String, Object>() {{
                put("totalScore", new HashMap<String, Object>() {{
                    put("type", "number");
                    put("description", "문진 자가 진단 결과 점수 합계");
                }});
                put("question", new HashMap<String, Object>() {{
                    put("type", "array");
                    put("items", new HashMap<String, Object>() {{
                        put("type", "object");
                        put("properties", new HashMap<String, Object>() {{
                            put("num", new HashMap<String, Object>() {{
                                put("type", "number");
                                put("description", "문항 번호");
                            }});
                            put("score", new HashMap<String, Object>() {{
                                put("type", "number");
                                put("description", "문항에 대한 점수");
                            }});
                            put("reason", new HashMap<String, Object>() {{
                                put("type", "string");
                                put("description", "문항에 대해 해당 점수를 매긴 이유");
                            }});
                        }});
                        put("required", Arrays.asList("num", "score", "reason"));
                    }});
                }});
                put("summary", new HashMap<String, Object>() {{
                    put("type", "string");
                    put("description", "분석 요약");
                }});
            }});
            schema.put("required", Arrays.asList("totalScore", "question", "summary"));
            config.setResponse_schema(schema);
        } else {
            config.setResponse_mime_type("text/plain");
        }

        return restTemplate.postForObject(requestUrl, request, GeminiResponse.class);
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

        GeminiResponse response = getContents(prompt, false);

        return ChatResponse.builder()
                .response(response.getResponseText())
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

    @Transactional
    public GeminiJsonResponse selfTest(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("memberId에 해당하는 멤버가 없습니다."));

        LocalDateTime start = LocalDateTime.now().minusWeeks(1);
        LocalDateTime end = LocalDateTime.now().toLocalDate().atTime(23, 59, 59, 999999999);

        List<ChatbotResponse> chatResponses = chatbotResponseRepository.findByMemberAndTypeAndResponseDateTimeBetween(member, GeminiRequestType.chat, start, end);
        List<ChatbotResponse> diaryResponses = chatbotResponseRepository.findByMemberAndTypeAndResponseDateTimeBetween(member, GeminiRequestType.diary, start, end);

        StringBuilder conversations = new StringBuilder();

        for (ChatbotResponse chatbotResponse : chatResponses) {
            ChatRequest chat = ChatRequest.builder()
                    .question(chatbotResponse.getQuestion())
                    .response(chatbotResponse.getResponse())
                    .responseDateTime(chatbotResponse.getResponseDateTime())
                    .type(chatbotResponse.getType())
                    .build();
            conversations.append(chat.toString());
        }
        for (ChatbotResponse chatbotResponse : diaryResponses) {
            ChatRequest chat = ChatRequest.builder()
                    .question(chatbotResponse.getQuestion())
                    .response(chatbotResponse.getResponse())
                    .responseDateTime(chatbotResponse.getResponseDateTime())
                    .type(chatbotResponse.getType())
                    .build();
            conversations.append(chat.toString());
        }

        GeminiContent condition = GeminiContent.builder().role("user").text(CEHCKSTATUS + conversations).build();

        List<GeminiContent> prompt = new ArrayList<>();
        prompt.add(condition);

        GeminiResponse geminiResponse = getContents(prompt, true);
        // JSON 응답 처리
        String jsonResponse = geminiResponse.getJsonResponse();
        jsonResponse = jsonResponse.replaceAll("```json\n", "").replaceAll("\n```", "");
        // JSON 문자열을 Java 객체로 변환
        ObjectMapper mapper = new ObjectMapper();
        try {
            GeminiJsonResponse response = mapper.readValue(jsonResponse, GeminiJsonResponse.class);
            // answer score 추출
            List<Integer> answers = new ArrayList<>();
            for (int i = 0; i < response.getQuestion().size(); i++) {
                answers.add(response.getQuestion().get(i).getScore());
            }
            surveyService.saveSurveyAnswerWrittenByModel(answers, member);
            return response;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 오류", e);
        }
    }
}