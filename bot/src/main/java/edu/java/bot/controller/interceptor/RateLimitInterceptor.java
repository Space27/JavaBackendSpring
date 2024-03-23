package edu.java.bot.controller.interceptor;

import edu.java.bot.controller.interceptor.exception.TooManyRequestsException;
import edu.java.bot.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService limitService;

    @Override
    public boolean preHandle(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull Object handler
    ) {
        String ip = getIP(request);
        Bucket bucket = limitService.resolveBucket(ip);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            return true;
        } else {
            throw new TooManyRequestsException(ip);
        }
    }

    private String getIP(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");

        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
