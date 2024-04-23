package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,

    @NotNull
    Integer maxBucketSize,

    @NotNull
    RetryConfig retryConfig

) {
    public record RetryConfig(@NotNull @Positive Integer maxAttempts, @NotNull DelayType delayType,
                              @NotNull Duration delayTime, @NotNull @NotEmpty List<Integer> responseCodes) {
        public enum DelayType {
            FIXED,
            EXPONENTIAL,
            LINEAR
        }
    }
}
