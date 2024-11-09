package com.gdg.kkia.chatbot.service;

import com.gdg.kkia.chatbot.repository.ChatbotResponseRepository;
import com.gdg.kkia.common.exception.BadRequestException;
import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.gemini.entity.GeminiRequestType;
import com.gdg.kkia.gemini.dto.GeminiContent;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import com.gdg.kkia.chatbot.entity.ChatbotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatbotResponseService {

    private final ChatbotResponseRepository chatbotResponseRepository;
    private final MemberRepository memberRepository;

    public void saveChatbotResponses(Long memberId, GeminiRequestType type, List<GeminiContent> conversations) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("memberId에 해당하는 멤버가 없습니다."));

        System.out.println(conversations.size());

        for(int i = 0; i < conversations.size(); i += 2) {
            if (conversations.size() <= i + 1) break;

            ChatbotResponse chatbotResponse = new ChatbotResponse();

            GeminiContent question = conversations.get(i);
            GeminiContent answer = conversations.get(i + 1);

            if (!Objects.equals(question.getRole(), "model") || !Objects.equals(answer.getRole(), "user"))
                throw new BadRequestException("대화는 '질문-대답' 구조이어야 합니다.");

            chatbotResponse.setQuestion(question.getParts().get(0).getText());
            chatbotResponse.setResponse(answer.getParts().get(0).getText());
            chatbotResponse.setType(type);
            chatbotResponse.setMember(member);

            chatbotResponseRepository.save(chatbotResponse);
        }
    }

}
