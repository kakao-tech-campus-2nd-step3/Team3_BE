package com.splanet.splanet.oauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets; // 변경된 부분
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
  private Key secretKey;
  @Value("${jwt.secret}")
  private String secret;

  @PostConstruct
  protected void init() {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
  }

  public String createToken(Authentication authentication) {
    String nickname = ((OAuth2User) authentication.getPrincipal()).getAttribute("nickname");
    Long userId = ((OAuth2User) authentication.getPrincipal()).getAttribute("id");

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + 3600000);

    return Jwts.builder()
            .setSubject(nickname)
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
  }

  public Long getUserIdFromToken(String token) {
    Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

    return claims.get("userId", Long.class);
  }

  public String getUserNameFromToken(String token) {
    Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

    return claims.getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
              .setSigningKey(secretKey)
              .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}