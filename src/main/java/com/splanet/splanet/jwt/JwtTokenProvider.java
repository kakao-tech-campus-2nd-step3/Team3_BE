package com.splanet.splanet.jwt;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.core.properties.JwtProperties;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

  private Key secretKey;

  private static final long TOKEN_VALIDITY_IN_MILLISECONDS = 3600000; // 1시간
  private static final long REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS = 604800000; // 7일
  private final JwtProperties jwtProperties;

  public JwtTokenProvider(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  @PostConstruct
  protected void init() {
    this.secretKey = new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
  }

  public String createAccessToken(Long userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + TOKEN_VALIDITY_IN_MILLISECONDS);

    return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
  }


  public String createRefreshToken(Long userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);

    return Jwts.builder()
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
  }

  public String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }
    return header.substring(7);
  }

  public Long getUserIdFromToken(String token) {
    return getClaimsFromToken(token).get("userId", Long.class);
  }

  public String getUserNameFromToken(String token) {
    return getClaimsFromToken(token).getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parser()
              .setSigningKey(secretKey)
              .parseClaimsJws(token)
              .getBody();
    } catch (JwtException e) {
      handleJwtException(e);
      return null; // 이 라인은 실제로 도달하지 않습니다. 예외 발생 시 이 메서드에서 종료됩니다.
    }
  }

  private void handleJwtException(JwtException e) {
    if (e instanceof ExpiredJwtException) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "만료된 토큰입니다.");
    } else if (e instanceof UnsupportedJwtException) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "지원되지 않는 토큰입니다.");
    } else if (e instanceof MalformedJwtException) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
    } else if (e instanceof SignatureException) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "서명 검증에 실패했습니다.");
    } else {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "토큰이 비어 있거나 유효하지 않습니다.");
    }
  }
}
