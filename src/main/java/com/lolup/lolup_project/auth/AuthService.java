package com.lolup.lolup_project.auth;

import com.lolup.lolup_project.token.JwtProvider;
import com.lolup.lolup_project.token.Token;
import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.Role;
import com.lolup.lolup_project.member.UserProfile;
import com.lolup.lolup_project.token.RefreshToken;
import com.lolup.lolup_project.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public Map<String, Object> checkAuth(Principal principal) {
        UserProfile userProfile = (UserProfile) ((Authentication) principal).getPrincipal();
        Member findMember = memberRepository.findByEmail(userProfile.getEmail()).orElse(null);

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", findMember.getId());
        map.put("summonerName", findMember.getSummonerName());
        map.put("login", true);

        return map;
    }

    public Map<String, Object> refresh(String refreshToken, HttpServletResponse response) {

        if (!jwtProvider.verifyToken(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 만료되었습니다.");
        }

        String email = jwtProvider.getTokenClaims(refreshToken);
        Token newToken = jwtProvider.generateToken(email, Role.USER.getKey());
        Member findMember = memberRepository.findByEmail(email).orElse(null);

        RefreshToken newRefreshToken = RefreshToken.create(findMember, refreshToken);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(newRefreshToken);

        setCookie(response, savedRefreshToken.getRefreshToken());

        Map<String, Object> map = new HashMap<>();
        map.put("token", newToken.getToken());

        return map;
    }

    public Map<String, Object> logout(Long memberId, HttpServletResponse response) {
        Member findMember = memberRepository.findById(memberId).orElse(null);
        refreshTokenRepository.deleteByMember(findMember);
        deleteCookie(response);

        Map<String, Object> map = new HashMap<>();
        map.put("logout", true);

        return map;
    }

    private void setCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(60 * 60 * 24 * 30); // 1달
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
