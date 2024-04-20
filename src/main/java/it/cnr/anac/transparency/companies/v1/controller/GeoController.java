package it.cnr.anac.transparency.companies.v1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.v1.ApiRoutes;
import it.cnr.anac.transparency.companies.v1.dto.AddressDtoMapper;
import it.cnr.anac.transparency.companies.v1.dto.CompanyAddressDto;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Tag(name = "Geo Controller", description = "Visualizzazione delle informazioni geografiche",
  externalDocs =  
    @ExternalDocumentation(
        description = "Mappa Leaflet Pubbliche Amministrazioni Italiane.", 
        url = "../../map.html"))
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/geo")
public class GeoController {

  private final CompanyRepository companyRepository;
  private final AddressDtoMapper mapper;

  @Operation(
      summary = "Visualizzazione degli indirizzi geolocalizzati presenti nel sistema.",
      description = "Le informazioni sono restituite paginate'.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restitutita una pagina con la lista degli indirizzi geolocalizzati presenti.")
  })
  @GetMapping(ApiRoutes.LIST)
  public ResponseEntity<List<CompanyAddressDto>> list(
      @Parameter(required = false, allowEmptyValue = true, example = "{ \"page\": 0, \"size\":100, \"sort\":\"id\"}") 
      Pageable pageable) {

    val companiesWithAddress =
        companyRepository.findAllActiveWithAddress().stream()
          .map(mapper::convert).collect(Collectors.toList());
    return ResponseEntity.ok().body(companiesWithAddress);
  }
}
