package com.lolup.lolup_project.token;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lolup.lolup_project.auth.AuthorizationExtractor;
import com.lolup.lolup_project.auth.EmptyAuthorizationHeaderException;
import com.lolup.lolup_project.auth.InvalidTokenException;
import com.lolup.lolup_project.member.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String[] AUTHORIZED_PATTERN = {"/duo*", "/member*", "/auth*"};

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) {
		return !PatternMatchUtils.simpleMatch(AUTHORIZED_PATTERN, request.getRequestURI());
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
									final FilterChain filterChain) throws ServletException, IOException {
		try {
			String token = AuthorizationExtractor.extract(request);
			jwtTokenProvider.verifyToken(token);

			String memberId = jwtTokenProvider.getPayload(token);
			Authentication authentication = getAuthentication(memberId);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (EmptyAuthorizationHeaderException | InvalidTokenException e) {
			request.setAttribute("exception", e);
		}
		filterChain.doFilter(request, response);
	}

	private Authentication getAuthentication(String memberId) {
		return new UsernamePasswordAuthenticationToken(
				Long.parseLong(memberId),
				null,
				List.of(new SimpleGrantedAuthority(Role.USER.getKey()))
		);
	}
}