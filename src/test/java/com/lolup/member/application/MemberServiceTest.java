package com.lolup.member.application;

import static com.lolup.common.fixture.MemberFixture.소환사_등록_회원;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lolup.common.ServiceTest;
import com.lolup.member.domain.Member;
import com.lolup.member.exception.NoSuchMemberException;
import com.lolup.riot.match.exception.NoSuchSummonerException;

class MemberServiceTest extends ServiceTest {

	private static final long INVALID_MEMBER_ID = 99L;
	private static final String UPDATED_SUMMONER_NAME = "updated name";
	private static final String INVALID_SUMMONER_NAME = "invalid summoner name";

	@DisplayName("소환사 이름을 변경한다.")
	@Test
	void updateSummonerName() {
		Member member = memberRepository.save(소환사_등록_회원());
		Long memberId = member.getId();

		memberService.updateSummonerName(memberId, UPDATED_SUMMONER_NAME);
		Member findMember = memberRepository.findById(memberId)
				.orElseThrow();

		assertThat(findMember.getSummonerName()).isEqualTo(UPDATED_SUMMONER_NAME);
	}

	@DisplayName("소환사 이름을 변경 시 멤버 ID가 유효하지 않으면 예외가 발생한다.")
	@Test
	void updateSummonerNameWithInvalidMemberId() {
		memberRepository.save(소환사_등록_회원());

		assertThatThrownBy(() -> memberService.updateSummonerName(INVALID_MEMBER_ID, UPDATED_SUMMONER_NAME))
				.isInstanceOf(NoSuchMemberException.class);
	}

	@DisplayName("소환사 이름 변경 시 소환사 이름이 유효하지 않으면 예외가 발생한다")
	@Test
	void updateSummonerNameWithInvalidSummonerName() {
		willThrow(new NoSuchSummonerException())
				.given(summonerService)
				.requestAccountInfo(INVALID_SUMMONER_NAME);

		Long memberId = memberRepository.save(소환사_등록_회원())
				.getId();

		assertThatThrownBy(() -> memberService.updateSummonerName(memberId, INVALID_SUMMONER_NAME))
				.isInstanceOf(NoSuchSummonerException.class);
	}
}
