package it.cnr.anac.transparency.companies.geo;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.cnr.anac.transparency.companies.models.Company;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleMapsService {

  private final GoogleMapsClient googleMapsClient;

  @Value("${transparency.google.maps.key}")
  private String googleMapsKey;
  
  @Value("${transparency.google.maps.enabled}")
  private Boolean googleMapsEnabled;

  public Boolean isGoogleMapsConfigured() {
    return !Strings.isNullOrEmpty(googleMapsKey) && googleMapsEnabled;
  }

  public List<GoogleMapsAddressDto> getGoogleMapsAdresses(Company company) {
    if (!isGoogleMapsConfigured()) {
      log.warn("Google maps non abilitato dalla configurazione, oppure google maps key non presente.");
      return Lists.newArrayList();
    }
    if (company.getIndirizzo() == null || company.getComune() == null 
        || company.getComune().getDenominazione() == null) {
      return Lists.newArrayList();
    }
    return googleMapsClient.searchAddress(
        String.format("%s, %s", company.getIndirizzo(), company.getComune().getDenominazione()), googleMapsKey).getResults();
  }

  public Optional<GoogleMapsAddressDto> getGoogleMapsAddress(Company company) {
    val addresses = getGoogleMapsAdresses(company);
    if (addresses.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(addresses.get(0));
  }
}