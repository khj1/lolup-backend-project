package com.lolup.lolup_project.message;

import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;

    public MessageDto save(MessageDto messageDto) {
        Member findMember = memberRepository.findById(messageDto.getMemberId()).orElse(null);
        Message message = Message.create(findMember, messageDto.getRoomId(), messageDto.getMessage());
        messageRepository.save(message);

        return messageRepository.findMessageDtoById(message.getId()).orElse(null);
    }
}
