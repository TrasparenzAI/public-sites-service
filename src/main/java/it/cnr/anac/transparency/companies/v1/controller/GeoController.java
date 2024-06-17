package it.cnr.anac.transparency.companies.v1.controller;

import java.util.List;
import java.util.Optional;

import org.geojson.FeatureCollection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.anac.transparency.companies.geo.GeoService;
import it.cnr.anac.transparency.companies.geo.GoogleMapsAddressDto;
import it.cnr.anac.transparency.companies.geo.GoogleMapsService;
import it.cnr.anac.transparency.companies.geo.OpenstreetMapAddressDto;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.services.CompanyService;
import it.cnr.anac.transparency.companies.v1.ApiRoutes;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Geo Controller", description = "Visualizzazione e gestione delle informazioni geografiche",
  externalDocs =  
    @ExternalDocumentation(
        description = "Mappa Leaflet Pubbliche Amministrazioni Italiane.", 
        url = "../map.html"))
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/geo")
public class GeoController {

  private final GoogleMapsService googleMapsService;
  private final CompanyService companyService;
  private final CompanyRepository companyRepository;
  private final GeoService geoService;

  @Operation(
      summary = "Indirizzi geolocalizzati presenti nel sistema in formato GeoJson.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restitutita la lista degli indirizzi geolocalizzati presenti.")
  })
  @GetMapping(ApiRoutes.LIST + "/geojson")
  public ResponseEntity<FeatureCollection> geoJson() {
    FeatureCollection featureCollection = new FeatureCollection();
    featureCollection.addAll(companyService.getCompanieGroupedByPositionAsFeatures());
    return ResponseEntity.ok(featureCollection);
  }

  @Operation(
      summary = "Aggiornamento del riferimento al comune degli enti che ne sono sprovvisti.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Numero degli enti di cui è stato aggiornamento il riferimento al comune.")
  })
  @PostMapping("/fixCompaniesWithoutMunicipality")
  public ResponseEntity<Integer> fixCompaniesWithoutMunicipality() {
    log.info("Aggiornamento del comune degli enti senza il relativo riferimento");
    val companiesUpdated = geoService.fixCompanyWithoutMunicipality();
    return ResponseEntity.ok(companiesUpdated);
  }

  @GetMapping("/geoCompanyGoogleMapsAddresses" + ApiRoutes.SHOW)
  public ResponseEntity<List<GoogleMapsAddressDto>> geoCompanyGoogleMapsAddresses(
      @NotNull @PathVariable("id") Long id) {
    val company = companyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con id = " + id));
    log.info("company = {}", company);
    val addresses = googleMapsService.getGoogleMapsAdresses(company);
    return ResponseEntity.ok(addresses);
  }

  @Operation(
      summary = "Visualizzazione di tutte le geolocalizzazioni del indirizzo dell'ente, "
          + "trovate tramite OpenstreetMap.",
      description = "Il servizio effettua una chiamata al servizio Nominatim di OSM.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restitutita la lista delle geolocalizzazioni dell'indirizzo dell'ente.")
  })
  @GetMapping("/geoCompanyAddresses" + ApiRoutes.SHOW)
  public ResponseEntity<List<OpenstreetMapAddressDto>> geoCompanyAddress(
      @NotNull @PathVariable("id") Long id) {
    val company = companyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con id = " + id));
    val addresses = geoService.getGeoAddresses(company);
    return ResponseEntity.ok(addresses);
  }

  @Operation(
      summary = "Visualizzazione della geolocalizzazione con la maggiore 'importance' tra le possibile "
          + "geolocalizzazioni del indirizzo dell'ente trovate tramite OpenstreetMap.",
      description = "Il servizio effettua una chiamata al servizio Nominatim di OSM.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restitutita la geocalizzazione dell'indirizzo dell'ente.")
  })
  @GetMapping("/geoCompanyAddress" + ApiRoutes.SHOW)
  public ResponseEntity<Optional<OpenstreetMapAddressDto>> geoCompanyAddresses(
      @NotNull @PathVariable("id") Long id) {
    val company = companyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con id = " + id));
    val addresses = geoService.getBestMatchingGeoAddress(company);
    return ResponseEntity.ok(addresses);
  }

  @Operation(
      summary = "Aggiornamento della geolocalizzazione del indirizzo dell'ente trovate tramite OpenstreetMap.",
      description = "Il servizio effettua una chiamata al servizio Nominatim di OSM.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Aggiornata la geocalizzazione dell'indirizzo dell'ente.")
  })
  @PostMapping("/updateCompanyAddress" + ApiRoutes.SHOW)
  public ResponseEntity<Boolean> updateCompanyAddresses(
      @NotNull @PathVariable("id") Long id) {
    val company = companyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con id = " + id));
    val updated = companyService.geolocalizeCompany(company, Optional.empty());
    return ResponseEntity.ok(updated);
  }

  @Operation(
      summary = "Aggiornamento della geolocalizzazione deli enti senza indirizzo presenti nel servizio tramite Nominatim di OSM.",
      description = "Aggiorna gli indirizzi degli enti senza indirizzo geolocalizzandoli tramite il servizio Nominatim di OpenStreetMap.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Numero degli enti presenti di cui è stata aggiornata la geolocalizzazione.")
  })
  @PostMapping("/updateCompanyAddresses")
  public ResponseEntity<Integer> updateCompanyAddresses(
      @RequestParam(name = "limit") Optional<Integer> limit,
      @RequestParam(name = "skip") Optional<Integer> skip) {
    log.info("Geolocalizzazione indirizzi degli enti utilizzando Nominatim di OSM, con limite = {}, skip = {}",
        limit, skip);
    val companiesUpdated = companyService.geolocalizeCompanies(limit, skip, Optional.empty());
    log.info("Terminata la geolocalizzazione, {} indirizzi geolocalizzati con successo.", companiesUpdated);
    return ResponseEntity.ok(companiesUpdated);
  }

  @Operation(
      summary = "Aggiornamento della geolocalizzazione degli enti già geolocalizzati con indirizzo di Nominatim di OSM.",
      description = "Aggiorna gli indirizzi degli enti già geolocalizzati tramite il servizio Nominatim di OpenStreetMap.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Numero degli enti presenti di cui è stata aggiornata la geolocalizzazione.")
  })
  @PostMapping("/updateExistingCompanyAddresses")
  public ResponseEntity<Integer> updateExistingCompanyAddresses() {
    log.info("Aggiornamento della geolocalizzazione dgli indirizzi già geolocalizzati tramite Nominatim di OSM");
    val companiesUpdated = companyService.geolocalizeCompaniesByNominatim();
    log.info("Terminata la geolocalizzazione, {} indirizzi geolocalizzati con successo.", companiesUpdated);
    return ResponseEntity.ok(companiesUpdated);
  }

  @Operation(
      summary = "Aggiornamento della geolocalizzazione degli enti presenti nel servizio tramite Google Maps.",
      description = "Aggiorna gli indirizzi degli enti geolocalizzandoli tramite il servizio Google Maps.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Numero degli enti presenti di cui è stata aggiornata la geolocalizzazione."),
      @ApiResponse(responseCode = "400", 
          description = "Integrazione Google Maps non attiva.")
  })
  @PostMapping("/updateCompanyAddressesUsingGoogleMaps")
  public ResponseEntity<Integer> updateCompanyAddressesUsingGoogleMaps(
      @RequestParam(name = "limit") Optional<Integer> limit,
      @RequestParam(name = "skip") Optional<Integer> skip) {
    log.info("Geolocalizzazione indirizzi degli enti utilizzando Google Maps, con limite = {}, skip = {}",
        limit, skip);
    if (!googleMapsService.isGoogleMapsConfigured()) {
      return ResponseEntity.badRequest().build();
    }
    val companiesUpdated = companyService.geolocalizeCompanies(limit, skip, Optional.of(true));
    log.info("Terminata la geolocalizzazione tramite Google Maps, {} indirizzi geolocalizzati con successo.", companiesUpdated);
    return ResponseEntity.ok(companiesUpdated);
  }

  @Operation(
      summary = "Aggiornamento della geolocalizzazione dell'ente indicato tramite codiceIpa utilizzando il servizio Google Maps.",
      description = "Aggiorna dell'indirizzo dell'ente indicato tramite codiceIpa tramite il servizio Google Maps.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "True se l'indirizzo è stato geolocalizzato, false altrimenti."),
      @ApiResponse(responseCode = "400", 
          description = "Integrazione Google Maps non attiva.")
  })
  @PostMapping("/updateCompanyAddressUsingGoogleMaps")
  public ResponseEntity<Boolean> updateCompanyAddressUsingGoogleMaps(
      @RequestParam(name = "codiceIpa") String codiceIpa) {
    log.info("Geolocalizzazione indirizzo dell'ente con codiceIpa = {}", codiceIpa);
    if (!googleMapsService.isGoogleMapsConfigured()) {
      return ResponseEntity.badRequest().build();
    }
    val company = companyRepository.findByCodiceIpa(codiceIpa)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con codiceIpa = " + codiceIpa));
    if (companyService.geolocalizeCompany(company, Optional.of(true))) {
      log.info("Terminata la geolocalizzazione tramite Google Maps, indirizzo di geolocalizzato con successo.", company);
      return ResponseEntity.ok(true);
    };
    return ResponseEntity.ok(false);
  }
}
