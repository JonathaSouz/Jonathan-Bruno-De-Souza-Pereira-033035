package br.com.seplag.musicapi.infra.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

  private final RateLimitProperties props;
  private final Map<String, Window> windows = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var userKey = (String) request.getAttribute("rateLimitKey");
    if (userKey == null || userKey.isBlank()) {
      userKey = "anonymous";
    }

    if (!allow(userKey)) {
      response.setStatus(429);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write(
    "{\"error\":\"RATE_LIMIT\",\"message\":\"Limite de "
        + props.requestsPerMinute()
        + " req/min excedido.\"}");

        return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean allow(String key) {
    var now = Instant.now().getEpochSecond();
    var windowStart = now - (now % 60);

    var w = windows.compute(key, (k, existing) -> {
      if (existing == null || existing.windowStart != windowStart) {
        return new Window(windowStart, 1);
      }
      return new Window(existing.windowStart, existing.count + 1);
    });

    return w.count <= props.requestsPerMinute();
  }

  private record Window(long windowStart, int count) {}
}
