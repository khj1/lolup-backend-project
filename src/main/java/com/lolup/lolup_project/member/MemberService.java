package com.lolup.lolup_project.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final EntityManager em;
    private final MemberRepository memberRepository;

    @Transactional
    public Map<String, Object> updateSummonerName(Long memberId, String summonerName) {
        Map<String, Object> map = new HashMap<>();

        Member member = memberRepository.findById(memberId).orElse(null);
        member.changeSummonerName(summonerName);

        map.put("updatedSummonerName", summonerName);

        return map;
    }
}
