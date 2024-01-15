/*
 * Copyright (C) 2023 Consiglio Nazionale delle Ricerche
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
import it.cnr.anac.transparency.companies.repositories.CompanyDao;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.utils.DtoToEntityConverter;
import it.cnr.anac.transparency.companies.v1.ApiRoutes;
import it.cnr.anac.transparency.companies.v1.dto.CompanyCreateDto;
import it.cnr.anac.transparency.companies.v1.dto.CompanyMapper;
import it.cnr.anac.transparency.companies.v1.dto.CompanyShowDto;
import it.cnr.anac.transparency.companies.v1.dto.CompanyUpdateDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Company Controller", description = "Gestione delle informazioni degli Enti")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/companies")
public class CompanyController {

  private final CompanyRepository companyRepository;
  private final CompanyDao companyDao;
  private final CompanyMapper mapper;
  private final DtoToEntityConverter dtoToEntityConverter;

  @Operation(
      summary = "Visualizzazione delle informazioni di un ente.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituiti i dati dell'ente."),
      @ApiResponse(responseCode = "404", 
          description = "Ente non trovata con l'id fornito.",
          content = @Content)
  })
  @GetMapping(ApiRoutes.SHOW)
  public ResponseEntity<CompanyShowDto> show(@NotNull @PathVariable("id") Long id) {
    val company = companyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con id = " + id));
    return ResponseEntity.ok().body(mapper.convert(company));
  }

  @Operation(
      summary = "Visualizzazione di tutti gli enti presenti nel sistema.",
      description = "Le informazioni sono restituite paginte'.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restitutita una pagina della lista degli enti presenti.")
  })
  @GetMapping(ApiRoutes.LIST)
  public ResponseEntity<Page<CompanyShowDto>> list(
      @RequestParam("codiceCategoria") Optional<String> codiceCategoria,
      @RequestParam("codiceFiscaleEnte") Optional<String> codiceFiscaleEnte,
      @RequestParam("codiceIpa") Optional<String> codiceIpa,
      @RequestParam("denominazioneEnte") Optional<String> denominazioneEnte,
      @Parameter(required = false, allowEmptyValue = true, example = "{ \"page\": 0, \"size\":100, \"sort\":\"id\"}") 
      Pageable pageable) {
	codiceCategoria = codiceCategoria.isPresent() && codiceCategoria.get().isEmpty() ? 
			Optional.empty() : codiceCategoria;
    val companies = 
        companyDao.findAllActive(codiceCategoria, codiceFiscaleEnte, codiceIpa, 
            denominazioneEnte, pageable)
          .map(mapper::convert);
    return ResponseEntity.ok().body(companies);
  }

  @Operation(
      summary = "Creazione di un ente.",
      description = "Questa è la creazione di ente non presente in IndicePA, quelli presenti"
          + "in IndicePA sono importati automaticamente.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ente creato correttamente."),
      @ApiResponse(responseCode = "400", description = "Validazione delle informazioni obbligatorie fallita.", 
          content = @Content)
  })
  @PutMapping(ApiRoutes.CREATE)
  public ResponseEntity<CompanyShowDto> create(@NotNull @Valid @RequestBody CompanyCreateDto companyDto) {
    log.debug("CompanyController::create companyDto = {}", companyDto);
    val company = dtoToEntityConverter.createEntity(companyDto);
    companyRepository.save(company);
    log.info("Creato Ente {}", company);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convert(company));
  }

  @Operation(
      summary = "Aggiornamento dei dati di un ente.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Persona aggiornata correttamente."),
      @ApiResponse(responseCode = "400", description = "Validazione delle informazioni obbligatorie fallita.")
  })
  @PostMapping(ApiRoutes.UPDATE)
  public ResponseEntity<CompanyShowDto> update(@NotNull @Valid @RequestBody CompanyUpdateDto companyDto) {
    log.debug("CompanyController::update personDto = {}", companyDto);
    val commpany = dtoToEntityConverter.updateEntity(companyDto);
    companyRepository.save(commpany);
    log.info("Aggiornato Ente, i nuovi dati sono {}", commpany);
    return ResponseEntity.ok().body(mapper.convert(commpany));
  }

  @Operation(
      summary = "Eliminazione di un ente.", 
      description = "L'ente viene disattivato impostando una data di cancellazione, è possibile "
          + "cancellarlo definitivamente utilizzando il parametro \"forever\".")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ente eliminato correttamente"),
      @ApiResponse(responseCode = "409", description = "Ente già cancellato")
  })
  @DeleteMapping(ApiRoutes.DELETE)
  ResponseEntity<Void> delete(
      @NotNull @PathVariable("id") Long id, 
      @PathVariable("forever") Optional<Boolean> forever) {
    log.debug("CompanyController::delete id = {}, forever= {}", id, forever);
    val company = companyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con id = " + id));
    if (forever.orElse(false)) {
      companyRepository.delete(company);
      log.info("Eliminato definitivamente ente {}", company);
    } else {
      if (company.getDataCancellazione() != null) {
        return ResponseEntity.status(409).build();
      }
      company.setDataCancellazione(LocalDate.now());
      companyRepository.save(company);
      log.info("Imposta data di cancellazione ad oggi per ente {}", company);
    }
    return ResponseEntity.ok().build();
  }
}