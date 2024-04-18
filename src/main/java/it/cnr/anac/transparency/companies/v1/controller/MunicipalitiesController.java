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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.anac.transparency.companies.repositories.MunicipalityDao;
import it.cnr.anac.transparency.companies.repositories.MunicipalityRepository;
import it.cnr.anac.transparency.companies.v1.ApiRoutes;
import it.cnr.anac.transparency.companies.v1.dto.MunicipalityMapper;
import it.cnr.anac.transparency.companies.v1.dto.MunicipalityShowDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Controller", description = "Metodi di supporto per visualizzazione e gestione dei comuni italiani")
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/municipalities")
public class MunicipalitiesController {

  private final MunicipalityRepository municipalityRepository;
  private final MunicipalityDao municipalityDao;
  private final MunicipalityMapper mapper;
  
  @Operation(
      summary = "Visualizzazione delle informazioni di un comune.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituiti i dati del comune."),
      @ApiResponse(responseCode = "404", 
          description = "Comune non trovato con l'id fornito.",
          content = @Content)
  })
  @GetMapping(ApiRoutes.SHOW)
  public ResponseEntity<MunicipalityShowDto> show(@NotNull @PathVariable("id") Long id) {
    val company = municipalityRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Comune non trovato con id = " + id));
    return ResponseEntity.ok().body(mapper.convert(company));
  }
  

  
  @Operation(
      summary = "Visualizzazione di tutti i comuni attivi presenti nel sistema.",
      description = "Le informazioni sono restituite paginte'.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restitutita una pagina della lista dei comuni presenti.")
  })
  @GetMapping(ApiRoutes.LIST)
  public ResponseEntity<Page<MunicipalityShowDto>> list(
      @RequestParam("codiceCatastale") Optional<String> codiceCatastale, 
      @RequestParam("codiceComune") Optional<String> codiceComune, 
      @RequestParam("denominazione") Optional<String> denominazione, 
      @RequestParam("denominazioneAltraLingua") Optional<String> denominazioneAltraLingua, 
      @RequestParam("denominazioneRegione") Optional<String> denominazioneRegione, 
      @RequestParam("ripartizioneGeografica") Optional<String> ripartizioneGeografica, 
      @RequestParam("siglaAutomobilistica") Optional<String> siglaAutomobilistica,
      @Parameter(required = false, allowEmptyValue = true, example = "{ \"page\": 0, \"size\":100, \"sort\":\"id\"}") 
      Pageable pageable) {

    val municipalities =
        municipalityDao.findAllActive(codiceCatastale, codiceComune, denominazione, 
            denominazioneAltraLingua, denominazioneRegione, ripartizioneGeografica, siglaAutomobilistica, pageable)
          .map(mapper::convert);
    return ResponseEntity.ok().body(municipalities);
  }
}
