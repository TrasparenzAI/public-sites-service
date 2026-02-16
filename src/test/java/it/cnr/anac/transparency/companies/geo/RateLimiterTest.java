package it.cnr.anac.transparency.companies.geo;

import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.models.Municipality;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RateLimiterTest {

    @Autowired
    private GeoService geoService;

    @Autowired
    @Qualifier("nominatimRateLimiter")
    private RateLimiter rateLimiter;

    @MockBean
    private NominatimClient client;

    @Test
    public void testRateLimiter() {
        System.out.println("[DEBUG_LOG] RateLimiter config: " + rateLimiter.getRateLimiterConfig());
        when(client.searchAddress(anyString())).thenReturn(new ArrayList<>());

        Municipality municipality = new Municipality();
        municipality.setDenominazione("Pennabilli");

        Company company = new Company();
        company.setIndirizzo("Piazza Montefeltro, 6");
        company.setCap("47864");
        company.setComune(municipality);

        long startTime = System.currentTimeMillis();
        
        // Eseguiamo 2 chiamate. 
        // Con il rate limiter impostato a 1 richiesta ogni 2 secondi, 
        // la seconda chiamata dovrebbe attendere circa 2 secondi.
        
        geoService.getGeoAddresses(company);
        rateLimiter.acquirePermission();
        geoService.getGeoAddresses(company);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("[DEBUG_LOG] Duration for 2 calls: " + duration + "ms");
        
        // Il rate limiter è configurato a 1 chiamata ogni 2 secondi.
        // La durata dovrebbe essere >= 2000ms. 
        // In alcuni casi il refresh period può essere già iniziato, 
        // ma una durata > 1800ms indica che il limiter sta facendo il suo lavoro.
        if (duration < 1800) {
             throw new RuntimeException("Rate limiter non sembra funzionare. Durata: " + duration + "ms");
        }
    }
}
