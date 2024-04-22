package it.cnr.anac.transparency.companies.v1.controller;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.anac.transparency.companies.geo.GeoService;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.v1.ApiRoutes;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Geo Controller", description = "Visualizzazione delle informazioni geografiche",
  externalDocs =  
    @ExternalDocumentation(
        description = "Mappa Leaflet Pubbliche Amministrazioni Italiane.", 
        url = "../map.html"))
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/geo")
public class GeoController {

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
    Collection<Feature> features =
        companyRepository.findAllActiveWithAddress().stream().map(geoService::mapCompanyToFeature).collect(Collectors.toList());
    featureCollection.addAll(features);
    return ResponseEntity.ok(featureCollection);
  }
}
