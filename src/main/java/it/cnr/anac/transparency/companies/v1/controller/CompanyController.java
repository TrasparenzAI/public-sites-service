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
import jakarta.websocket.server.PathParam;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/companies")
public class CompanyController {

  private final CompanyRepository companyRepository;
  private final CompanyMapper mapper;
  private final DtoToEntityConverter dtoToEntityConverter;
  
  @GetMapping(ApiRoutes.SHOW)
  public ResponseEntity<CompanyShowDto> show(@NotNull @PathVariable("id") Long id) {
    val company = companyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con id = " + id));
    return ResponseEntity.ok().body(mapper.convert(company));
  }

  @GetMapping(ApiRoutes.LIST)
  public ResponseEntity<Page<CompanyShowDto>> list(@NotNull final Pageable pageable) {
    val companies = companyRepository.findAllActive(pageable).map(mapper::convert);
    return ResponseEntity.ok().body(companies);
  }

  @PutMapping(ApiRoutes.CREATE)
  public ResponseEntity<CompanyShowDto> create(@NotNull @Valid @RequestBody CompanyCreateDto companyDto) {
    log.debug("CompanyController::create companyDto = {}", companyDto);
    val company = dtoToEntityConverter.createEntity(companyDto);
    companyRepository.save(company);
    log.info("Creato Ente {}", company);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convert(company));
  }

  @PostMapping(ApiRoutes.UPDATE)
  public ResponseEntity<CompanyShowDto> update(@NotNull @Valid @RequestBody CompanyUpdateDto companyDto) {
    log.debug("CompanyController::update personDto = {}", companyDto);
    val commpany = dtoToEntityConverter.updateEntity(companyDto);
    companyRepository.save(commpany);
    log.info("Aggiornato Ente, i nuovi dati sono {}", commpany);
    return ResponseEntity.ok().body(mapper.convert(commpany));
  }

  @DeleteMapping(ApiRoutes.DELETE)
  ResponseEntity<Void> delete(@NotNull @PathVariable("id") Long id, @PathParam("forever") Optional<Boolean> forever) {
    log.debug("CompanyController::delete id = {}, forever= {}", id, forever);
    val company = companyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con id = " + id));
    if (forever.orElse(false)) {
      companyRepository.delete(company);
      log.info("Eliminato definitivamente ente {}", company);
    } else {
      company.setDataCancellazione(LocalDate.now());
      companyRepository.save(company);
      log.info("Imposta data di cancellazione ad oggi per ente {}", company);
    }
    return ResponseEntity.ok().build();
  }
}