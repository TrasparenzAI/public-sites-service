package it.cnr.anac.transparency.companies.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfiguration {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(1)
                .limitRefreshPeriod(Duration.ofSeconds(2))
                .timeoutDuration(Duration.ofSeconds(300))
                .build();
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        // Pre-registriamo il limiter per assicurarci che sia pronto con la configurazione corretta
        registry.rateLimiter("nominatimRateLimiter", config);
        return registry;
    }

    @Bean(name = "nominatimRateLimiter")
    public RateLimiter nominatimRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("nominatimRateLimiter");
    }
}
