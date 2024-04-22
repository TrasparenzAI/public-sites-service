/*
 * Copyright (C) 2024 Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.cnr.anac.transparency.companies.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.anac.transparency.companies.geo.GeoService;
import it.cnr.anac.transparency.companies.geo.OpenstreetMapAddressDto;
import it.cnr.anac.transparency.companies.indicepa.IndicePaService;
import it.cnr.anac.transparency.companies.municipalities.MunicipalityCsvDto;
import it.cnr.anac.transparency.companies.municipalities.MunicipalityService;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.services.CachingService;
import it.cnr.anac.transparency.companies.services.CompanyService;
import it.cnr.anac.transparency.companies.v1.ApiRoutes;
import it.cnr.anac.transparency.companies.v1.dto.CompanyShowDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Controller", description = "Metodi di supporto per la gestione del servizio")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/admin")
public class AdminController {

  private final IndicePaService indicePaService;
  private final MunicipalityService municipalityService;
  private final GeoService geoService;
  private final CompanyService companyService;
  private final CompanyRepository companyRepository;
  private final CachingService cachingService;

  @Operation(
      summary = "Visualizzazione di tutti gli enti presenti in IndicePA.",
      description = "Il servizio effettua una chiamata agli OpenData di IndicePA e li presenta"
          + " come info json")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restitutita la lista degli enti presenti in IndicePA.")
  })
  @GetMapping("/indicePaCompanies")
  public ResponseEntity<List<CompanyShowDto>> indicePaCompanies(
      @RequestParam(name = "limit") Optional<Integer> limit) {
    val companies = indicePaService.getCompaniesFromIndicePa(limit);
    return ResponseEntity.ok(companies);
  }

  @Operation(
      summary = "Aggiornamento degli enti presenti nel servizio tramite IndicePA.",
      description = "Aggiorna i dati degli enti presenti nel sistema prelevando le inforrmazioni "
          + "degli enti tramite IndicePA.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Numero degli enti presenti in IndicePA aggiornati all'interno del servizio.")
  })
  @PostMapping("/updateIndicePaCompanies")
  public ResponseEntity<Integer> updateIndicePaCompanies(
      @RequestParam(name = "updatedFrom") Optional<LocalDate> updatedFrom) {
    log.info("Aggiornamento enti utilizzando i dati dell'indicePA, con data aggiornamento a "
        + "partire da {}", updatedFrom);
    val companiesUpdated = indicePaService.updateCompaniesFromIndicePa(updatedFrom);
    return ResponseEntity.ok(companiesUpdated);
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

  @Operation(
      summary = "Visualizzazione di tutti i comuni presenti nel file CSV di Istat.",
      description = "Il servizio scarica il file CSV dal sito di istat e li presenta"
          + " come info json")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restitutita la lista dei comuni presenti nel file di Istat.")
  })
  @GetMapping("/istatMunicipalities")
  public ResponseEntity<List<MunicipalityCsvDto>> istatMunicipalities(
      @RequestParam(name = "limit") Optional<Integer> limit) throws IOException {
    var municipalities = municipalityService.getMunicipalitiesFromCsv();
    if (limit.isPresent()) {
      municipalities = municipalities.stream().limit(limit.get()).collect(Collectors.toList());
    }
    return ResponseEntity.ok(municipalities);
  }

  @Operation(
      summary = "Aggiornamento degli enti presenti nel servizio tramite IndicePA.",
      description = "Aggiorna i dati degli enti presenti nel sistema prelevando le inforrmazioni "
          + "degli enti tramite IndicePA.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Numero degli enti presenti in IndicePA aggiornati all'interno del servizio.")
  })
  @PostMapping("/updateIstatMunicipalities")
  public ResponseEntity<Integer> updateIstatMunicipalities() throws IOException {
    log.info("Aggiornamento comuni utilizzando i dati dell'istat");
    val municipalitiesUpdated = municipalityService.updateMunicipalitiesFromIstat();
    return ResponseEntity.ok(municipalitiesUpdated);
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
      summary = "Visualizzazione della geolicalizzazione con la maggiore 'importanceì tra le possibile "
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
      summary = "Aggiornamento della geolocalizzazione deli enti presenti nel servizio tramite Nominatim di OSM.",
      description = "Aggiorna gli indirizzi degli enti geolocalizzandoli tramite il servizio Nominatm di OpenStreetMap.")
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
    val companiesUpdated = companyService.geolocalizeCompanies(limit, skip);
    log.info("Terminata la geolocalizzazione, {} indirizzi geolocalizzati con successo.", companiesUpdated);
    return ResponseEntity.ok(companiesUpdated);
  }

  @Operation(
      summary = "Svuota le cache utilizzate, in particolare quella degli indirizzi geolicalizzati degli enti.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Cache svuotata correttamente")
  })
  @DeleteMapping("/evictCaches")
  public ResponseEntity<Void> evictCaches() {
    log.debug("Richiesta evict di tutte le cache");
    cachingService.evictAllCaches();
    return ResponseEntity.ok().build();
  }
}