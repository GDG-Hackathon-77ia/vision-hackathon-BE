package com.gdg.kkia.chatbot.service;

import com.gdg.kkia.chatbot.dto.ChatRequest;
import com.gdg.kkia.chatbot.dto.ChatResponse;
import com.gdg.kkia.chatbot.dto.GeminiRequest;
import com.gdg.kkia.chatbot.entity.ChatbotResponse;
import com.gdg.kkia.chatbot.entity.GeminiContent;
import com.gdg.kkia.chatbot.entity.GeminiRequestType;
import com.gdg.kkia.chatbot.entity.GeminiResponse;
import com.gdg.kkia.chatbot.repository.ChatbotResponseRepository;
import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.diary.dto.DiaryReadResponse;
import com.gdg.kkia.diary.entity.Diary;
import com.gdg.kkia.diary.service.DiaryService;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GeminiService {

    private final MemberRepository memberRepository;
    private final ChatbotResponseRepository chatbotResponseRepository;
    private final DiaryService diaryService;

    @Qualifier("geminiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String CHATPROMPT =
            "심리학자로서 당신은 Charles R. Snyder의 희망 이론에 기반한 방법을 적용하고 있습니다. 당신은 현재 나와 함께 상담을 진행하며, " +
                    "나의 심리적 및 정서적 상태에 대해 질문을 던져 Charles R. Snyder의 방식으로 심리적 지원을 제공하려고 합니다. " +
                    "나는 설명을 요청할 때만 제공해야 합니다. 대화를 한 번에 전부 작성하지 마십시오. 이 상담은 오직 당신과 나 사이에서만 이루어집니다. " +
                    "Charles R. Snyder가 질문을 제시했을 법한 방식으로 한 번에 하나의 질문을 하고, 내 대답을 기다리십시오. 세 번의 대답 후, " +
                    "내가 당신에게 공유한 내용을 바탕으로 직면한 장애물, 추구해야 할 목표, 이를 달성하기 위해 나아가야 할 경로, " +
                    "그리고 동기 부여적 지원에 대한 조언을 제공하십시오. 이 과정에서 그의 이론의 이름은 언급하지 않고, 직접적으로 나에게 이야기하십시오. " +
                    "형식적인 언어를 사용하고 중복 없이 표현하십시오.";

    private static final String DIARYPROMPT =
            "사용자가 일기 내용을 제공하면, 그 일기에서 추상적인 부분을 식별하고, " +
                    "그에 대해 사용자가 더 구체적으로 답할 수 있도록 질문을 던져주세요. 만약 추상적인 부분이 없다면, " +
                    " 그 일기 내용을 바탕으로 공감을 표현하며 긍정적인 반응을 보여주세요. 모든 답변은 한국어로 제공해주세요.";

    private static final String CEHCKSTATUS =
            "다음은 우울증 상태를 체크하기 위한 문진표 항목과 사용자와의 대화 기록입니다. 대화는 다음과 같은 형태로 전달됩니다. 대화 형태: " +
                    "Question: 당신이 보낸 질문, Responses: 사용자의 응답, ResponseTime: 응답한 시각, Type: chat(일반 채팅) 혹은 diary(일기 내용 기반 채팅)" +
                    "모델 역할에서 사용자의 대화를 보고 그들의 감정을 파악해 주세요. 대화를 통해 사용자가 어떤 감정이나 느낌을 가졌을지 " +
                    "분석해 주시고 분석한 내용을 바탕으로 각 질문에 대해 당신이 사용자 역할에서 답변을 작성하고, 그에 따른 감정 상태와 점수를 " +
                    "평가해 주세요. 문진표 질문과 대화의 문맥을 통해 사용자가 느낄 수 있는 감정을 파악하고, 우울감이 어느 정도인지 점수를 계산해 분석해주세요. " +
                    "반환되는 내용에는 질문과 점수를 표현하지 않고 해당 정보를 활용해서 사용자의 우울증 정도나 심리 상태를 분석하여 300자 이상 작성하여 주세요." +
                    "응답은 JSON 형식이 아닌, 순수 텍스트 형식의 간단한 요약문으로 작성해 주세요. 특수한 기호 없이 일반적인 텍스트로만 작성해 주세요." +
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

    private GeminiResponse getContents(List<GeminiContent> prompt) {
        String requestUrl = apiUrl + "?key=" + geminiApiKey;

        GeminiRequest request = new GeminiRequest();
        for (GeminiContent content : prompt) {
            request.addContent(content);
        }

        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig();
        config.setResponse_mime_type("text/plain");

        return restTemplate.postForObject(requestUrl, request, GeminiResponse.class);
    }

    public ChatResponse startChat(Long memberId, GeminiRequestType type, List<ChatRequest> conversations) {
        List<GeminiContent> prompt = new ArrayList<>();

        GeminiContent condition;

        // Check if there is written diary today
        List<DiaryReadResponse> diaryList = diaryService.getAllDiaryWrittenByMemberInLocalDate(memberId, LocalDate.now());
        diaryList.sort(Comparator.comparing(DiaryReadResponse::writtenDateTime).reversed());

        System.out.println(diaryList);

        if (diaryList.isEmpty()) {
            condition = getPromptCondition(GeminiRequestType.chat);
            prompt.add(condition);
        } else {
            condition = getPromptCondition(GeminiRequestType.diary);
            prompt.add(condition);

            for (DiaryReadResponse diaryReadResponse : diaryList) {
                String question = "";
                if (diaryReadResponse.type() == Diary.Type.DAY) {
                    question = "오늘은 어떤 일이 있었나요?";
                } else if (diaryReadResponse.type() == Diary.Type.EMOTION) {
                    question = "당신의 감정을 솔직하게 적어주세요.";
                } else if (diaryReadResponse.type() == Diary.Type.MEMO) {
                    question = "추가적으로 남기고 싶은 말이 있나요?";
                }
                ChatRequest chat = ChatRequest.builder()
                        .question(question)
                        .response(diaryReadResponse.content())
                        .responseDateTime(diaryReadResponse.writtenDateTime())
                        .type(GeminiRequestType.diary)
                        .build();
                conversations.addFirst(chat);
            }
        }

        for (ChatRequest chat : conversations) {
            GeminiContent question = new GeminiContent("model", chat.getQuestion());
            prompt.add(question);
            GeminiContent response = new GeminiContent("user", chat.getResponse());
            prompt.add(response);
        }

        GeminiResponse response = getContents(prompt);

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
    public ChatResponse selfTest(Long memberId) {
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

        GeminiResponse response = getContents(prompt);

        return ChatResponse.builder()
                .response(response.getResponseText())
                .type(GeminiRequestType.test)
                .build();
    }
}
