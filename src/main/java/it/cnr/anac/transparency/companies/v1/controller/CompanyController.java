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
import it.cnr.anac.transparency.companies.v1.ApiRoutes;
import it.cnr.anac.transparency.companies.v1.dto.CompanyDto;
import it.cnr.anac.transparency.companies.v1.dto.CompanyMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE_PATH + "/companies")
public class CompanyController {

  private final CompanyRepository companyRepository;
  private final CompanyMapper mapper;

  @GetMapping(ApiRoutes.SHOW)
  public ResponseEntity<CompanyDto> show(@NotNull @PathVariable("id") Long id) {
    val company = companyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ente non trovato con id = " + id));
    return ResponseEntity.ok().body(mapper.convert(company));
  }

  @GetMapping(ApiRoutes.LIST)
  public ResponseEntity<Page<CompanyDto>> list(@NotNull final Pageable pageable) {
    val companies = companyRepository.findAllActive(pageable).map(mapper::convert);
    return ResponseEntity.ok().body(companies);
  }

}