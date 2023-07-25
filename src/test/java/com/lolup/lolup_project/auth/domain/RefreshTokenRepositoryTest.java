package com.lolup.lolup_project.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lolup.lolup_project.common.RepositoryTest;
import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.member.domain.Role;

class RefreshTokenRepositoryTest extends RepositoryTest {

	private static final String REFRESH_TOKEN = "valid.refresh.token";

	@DisplayName("리프레시 토큰 값으로 리프레시 토큰을 조회할 수 있다.")
	@Test
	void findByRefreshToken() {
		Member member = memberRepository.save(createMember());
		RefreshToken savedRefreshToken = refreshTokenRepository.save(RefreshToken.create(member, REFRESH_TOKEN));

		RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(REFRESH_TOKEN).orElseThrow();

		assertThat(savedRefreshToken).isSameAs(findRefreshToken);
	}

	@DisplayName("리프레스 토큰 값으로 리프레시 토큰을 제거할 수 있다.")
	@Test
	void deleteByMember() {
		Member member = memberRepository.save(createMember());
		refreshTokenRepository.save(RefreshToken.create(member, REFRESH_TOKEN));

		refreshTokenRepository.deleteByRefreshToken(REFRESH_TOKEN);

		assertThat(refreshTokenRepository.findByRefreshToken(REFRESH_TOKEN))
				.isEqualTo(Optional.empty());
	}

	private Member createMember() {
		return Member.builder()
				.name("member")
				.email("aaa@bbb.ccc")
				.role(Role.USER)
				.picture("picture")
				.summonerName("summonerName")
				.build();
	}
}
