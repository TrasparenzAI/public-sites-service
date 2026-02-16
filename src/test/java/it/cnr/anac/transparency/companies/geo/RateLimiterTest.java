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
        
        // Eseguiamo 3 chiamate consecutive al servizio.
        // Il rate limiter interno a GeoService dovrebbe gestire l'attesa.
        
        geoService.getGeoAddresses(company);
        geoService.getGeoAddresses(company);
        geoService.getGeoAddresses(company);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("[DEBUG_LOG] Duration for 3 calls: " + duration + "ms");
        
        // Il rate limiter è configurato a 1 chiamata ogni 2 secondi.
        // Per 3 chiamate, la durata dovrebbe essere >= 2000ms. 
        // Se la prima è immediata, la seconda attende 2s, la terza attende altri 2s (totale 4s).
        // Tuttavia, a seconda del refresh period, la seconda potrebbe avvenire prima se il ciclo era già iniziato.
        // Una durata > 2000ms indica che almeno un'attesa è avvenuta.
        if (duration < 2000) {
             throw new RuntimeException("Rate limiter non sembra funzionare. Durata per 3 chiamate: " + duration + "ms");
        }
    }
}
