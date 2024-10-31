package com.splanet.splanet.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    String requestURI = request.getRequestURI();

    if (isSwaggerPath(requestURI) || isExemptedPath(requestURI)) {
      filterChain.doFilter(request, response);
      return;
    }

    if (isApiPath(requestURI)) {
      handleTokenAuthentication(request, response);
    }

    filterChain.doFilter(request, response);
  }

  private void handleTokenAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String token = resolveToken(request);

    if (token == null) {
      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다.");
      return;
    }

    try {
      if (jwtTokenProvider.validateToken(token)) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
      } else {
        sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "유효하지 않은 토큰입니다.");
      }
    } catch (JwtException e) {
      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
    } catch (BusinessException e) {
      sendErrorResponse(response, e.getErrorCode().getStatus().value(), e.getMessage());
    } catch (Exception e) {
      sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }
  }

  private boolean isSwaggerPath(String requestURI) {
    return requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/v3/api-docs/") ||
            requestURI.startsWith("/swagger-resources/") || requestURI.startsWith("/webjars/");
  }

  private boolean isApiPath(String requestURI) {
    return requestURI.startsWith("/api/");
  }

  private boolean isExemptedPath(String requestURI) {
    return requestURI.equals("/api/users/create") || requestURI.startsWith("/api/token") || requestURI.startsWith("/api/stt") || requestURI.equals("/api/gpt/trial");
  }

  private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    ErrorResponse errorResponse = new ErrorResponse(message, status);
    new ObjectMapper().writeValue(response.getWriter(), errorResponse);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    return (bearerToken != null && bearerToken.startsWith("Bearer ")) ? bearerToken.substring(7) : null;
  }
}
