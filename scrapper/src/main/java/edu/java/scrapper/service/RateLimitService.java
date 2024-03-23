package edu.java.scrapper.service;

import edu.java.scrapper.configuration.ApplicationConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private final int maxBucketSize;
    private final Map<String, Bucket> bucketCache;

    public RateLimitService(ApplicationConfig applicationConfig) {
        maxBucketSize = applicationConfig.maxBucketSize();
        bucketCache = new ConcurrentHashMap<>();
    }

    public Bucket resolveBucket(String ip) {
        return bucketCache.computeIfAbsent(ip, this::createBucket);
    }

    private Bucket createBucket(String s) {
        Bandwidth limit = Bandwidth.builder()
            .capacity(maxBucketSize)
            .refillGreedy(maxBucketSize, Duration.ofMinutes(1))
            .build();

        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

}
