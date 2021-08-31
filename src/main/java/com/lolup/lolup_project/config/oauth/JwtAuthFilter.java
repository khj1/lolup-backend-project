package com.lolup.lolup_project.config.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.exception.ErrorResult;
import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.Role;
import com.lolup.lolup_project.member.UserProfile;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionErrorResponse;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String token = jwtProvider.resolveToken(request, "Bearer");
        log.info("Jwt 필터에서 호출된 토큰 값={}", token);

        try {

            if (token != null) {
                if (jwtProvider.verifyToken(token)) {
                    // 토큰이 유효하면 토큰으로 부터 유저 정보를 받아온다.
                    String email = jwtProvider.getTokenClaims(token);

                    log.info("Decoded email from JWT={}", email);

                    Member member = memberRepository.findByEmail(email).get();
                    UserProfile userProfile = Member.toUserProfileWithMember(member);

                    Authentication auth = getAuthentication(userProfile);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (ExpiredJwtException e) {
            log.error("JWT expired");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        } catch (AccessDeniedException e) {
            log.error("Access denied");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(req, res);
    }

    private Authentication getAuthentication(UserProfile userProfile) {
        return new UsernamePasswordAuthenticationToken(userProfile, "",
                Arrays.asList(new SimpleGrantedAuthority(Role.USER.getKey())));
    }
}
