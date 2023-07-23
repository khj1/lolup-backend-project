package com.lolup.lolup_project.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.member.domain.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional
	public void updateSummonerName(Long memberId, String summonerName) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(IllegalArgumentException::new);

		member.changeSummonerName(summonerName);
	}
}