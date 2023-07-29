package com.lolup.config.oauth;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.auth.application.JwtTokenProvider;
import com.lolup.auth.domain.RefreshToken;
import com.lolup.auth.domain.RefreshTokenRepository;
import com.lolup.member.domain.Member;
import com.lolup.member.domain.MemberRepository;
import com.lolup.member.exception.NoSuchMemberException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private static final String COOKIE_PATH = "/";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final int COOKIE_MAX_AGE = 1210000000;

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${front.redirect_url}")
	private String redirect_url;

	@Override
	@Transactional
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
										final Authentication authentication) throws IOException {
		UserProfile userProfile = extractUserProfile(authentication);

		Member member = memberRepository.findByEmailAndSocialType(
				userProfile.getEmail(),
				userProfile.getSocialType()
		).orElseThrow(NoSuchMemberException::new);

		Long memberId = member.getId();
		String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(memberId));
		String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(memberId));

		saveRefreshToken(member, refreshToken);
		writeTokenResponse(response, accessToken, refreshToken);
	}

	private void saveRefreshToken(final Member member, final String refreshToken) {
		refreshTokenRepository.findByMemberId(member.getId())
				.ifPresent(savedToken -> refreshTokenRepository.deleteByTokenValue(savedToken.getTokenValue()));

		refreshTokenRepository.save(RefreshToken.create(member, refreshToken));
	}

	private UserProfile extractUserProfile(final Authentication authentication) {
		Map<String, Object> attributes = getAttributes(authentication);
		String registrationId = getRegistrationId(authentication);
		return OAuthAttributes.extract(registrationId, attributes);
	}

	private Map<String, Object> getAttributes(final Authentication authentication) {
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		return oAuth2User.getAttributes();
	}

	private String getRegistrationId(final Authentication authentication) {
		OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken)authentication;
		return authToken.getAuthorizedClientRegistrationId();
	}

	private void writeTokenResponse(final HttpServletResponse response, final String accessToken,
									final String refreshToken) throws IOException {
		Cookie cookie = makeCookie(refreshToken);
		response.addCookie(cookie);
		response.sendRedirect(redirect_url + accessToken);
	}

	private Cookie makeCookie(final String refreshToken) {
		Cookie cookie = new Cookie(REFRESH_TOKEN, refreshToken);
		cookie.setMaxAge(COOKIE_MAX_AGE);
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setPath(COOKIE_PATH);

		return cookie;
	}
}
