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
        // Configurazione di default prudente nel caso in cui le properties non siano caricate
        RateLimiterConfig defaultConfig = RateLimiterConfig.custom()
                .limitForPeriod(1)
                .limitRefreshPeriod(Duration.ofSeconds(2))
                .timeoutDuration(Duration.ofSeconds(300))
                .build();
        return RateLimiterRegistry.of(defaultConfig);
    }

    @Bean(name = "nominatimRateLimiter")
    public RateLimiter nominatimRateLimiter(RateLimiterRegistry registry) {
        // Usa o crea (se mancante) il rate limiter con nome "nominatimRateLimiter"
        return registry.rateLimiter("nominatimRateLimiter");
    }
}
