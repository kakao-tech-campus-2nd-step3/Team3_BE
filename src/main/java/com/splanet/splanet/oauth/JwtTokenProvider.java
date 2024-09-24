package com.splanet.splanet.oauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.util.Date;

@Component
public class JwtTokenProvider {

  private final String SECRET_KEY = "yourSecretKey";

  // JWT 생성
  public String createToken(Authentication authentication) {
    String nickname = ((OAuth2User) authentication.getPrincipal()).getAttribute("nickname");
    Long userId = ((OAuth2User) authentication.getPrincipal()).getAttribute("id");

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + 3600000); // 1시간 유효

    return Jwts.builder()
            .setSubject(nickname)
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact();
  }

  // JWT에서 userId 추출
  public Long getUserIdFromToken(String token) {
    Claims claims = Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody();

    return claims.get("userId", Long.class);
  }

  // JWT에서 userName(subject) 추출
  public String getUserNameFromToken(String token) {
    Claims claims = Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody();

    return claims.getSubject();  // subject에서 userName 추출
  }

  // JWT 유효성 검증
// JWT 유효성 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      System.out.println("Invalid JWT token: " + e.getMessage());
      return false;
    }
  }
}