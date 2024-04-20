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
package it.cnr.anac.transparency.companies.utils;

import com.google.common.base.Verify;

import it.cnr.anac.transparency.companies.geo.AddressMapper;
import it.cnr.anac.transparency.companies.geo.GeoService;
import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.v1.dto.CompanyCreateDto;
import it.cnr.anac.transparency.companies.v1.dto.CompanyMapper;
import it.cnr.anac.transparency.companies.v1.dto.CompanyUpdateDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

/**
 * Classe di utilità per convertire un DTO nella corrispondente Entity.
 *
 */
@RequiredArgsConstructor
@Component
public class DtoToEntityConverter {

  private final CompanyMapper companyMapper;
  private final CompanyRepository repo;
  private final GeoService geoService;
  private final AddressMapper addressMapper;
  /**
   * Crea una nuova Entity Company a partire dai dati del DTO.
   */
  public Company createEntity(CompanyCreateDto companyDto) {
    Company company = new Company();
    companyMapper.update(company, companyDto);
    val geoAddress = geoService.getBestMatchingGeoAddress(company);
    if (geoAddress.isPresent()) {
      company.setAddress(addressMapper.convert(geoAddress.get()));
    }
    return company;
  }

  /**
   * Aggiorna l'entity riferita dal DTO con i dati passati.
   */
  public Company updateEntity(CompanyUpdateDto companyDto) {
    Verify.verifyNotNull(companyDto);
    val company = repo.findById(companyDto.getId())
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Ente con id = %d non trovato", companyDto.getId())));
    companyMapper.update(company, companyDto);
    val geoAddress = geoService.getBestMatchingGeoAddress(company);
    if (geoAddress.isPresent()) {
      company.setAddress(addressMapper.convert(geoAddress.get()));
    }
    return company;
  }
  
}