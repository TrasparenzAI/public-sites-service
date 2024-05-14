package it.cnr.anac.transparency.companies.geo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Client feign per la ricerca e geolocalizzazione degli indirizzi tramite API Google Maps.
 *
 * @author Cristian Lucchesi
 */
@FeignClient(name = "google-maps-client", url = "${transparency.google.maps.url}")
public interface GoogleMapsClient {

  @GetMapping("/maps/api/geocode/json")
  abstract GoogleMapsResponseDto searchAddress(
      @RequestParam(name="address") String address,
      @RequestParam(name="key") String key);
}
