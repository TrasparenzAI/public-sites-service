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
package it.cnr.anac.transparency.companies;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.stereotype.Service;

import com.google.common.base.Verify;

import it.cnr.anac.transparency.companies.geo.AddressMapper;
import it.cnr.anac.transparency.companies.geo.GeoService;
import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.repositories.AddressRepository;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.v1.dto.CompanyCreateDto;
import it.cnr.anac.transparency.companies.v1.dto.CompanyMapper;
import it.cnr.anac.transparency.companies.v1.dto.CompanyUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {
  
  private final CompanyRepository companyRepository;
  private final AddressRepository addressRepository;
  private final CompanyMapper companyMapper;
  private final GeoService geoService;
  private final AddressMapper addressMapper; 


  @Value("${transparency.geo.enabled}")
  private Boolean geoEnabled;

  public Integer geolocalizeCompanies(Optional<Integer> limit, Optional<Integer> skip) {
    List<Company> companies = companyRepository.findWithoutAddress();
    if (skip.isPresent()) {
      companies = companies.stream().sorted(Comparator.comparing(Company::getId)).skip(skip.get()).collect(Collectors.toList());
    }
    if (limit.isPresent()) {
      companies = companies.stream().sorted(Comparator.comparing(Company::getId)).limit(limit.get()).collect(Collectors.toList());
    }
    int geolocalizedCompanies = 0;
    for (Company company : companies) {
      val geoAddress = geoService.getBestMatchingGeoAddress(company);
      if (geoAddress.isPresent()) {
        val address = addressMapper.convert(geoAddress.get());
        addressRepository.save(address);
        company.setAddress(address);
        companyRepository.save(company);
        geolocalizedCompanies++;
        log.info("Impostato indirizzo a {}", company);
      } else {
        log.warn("Geolocalizzazione indirizzo non riuscita per {}", company);
      }
    }
    return geolocalizedCompanies;
  }

  /**
   * Crea una nuova Entity Company a partire dai dati del DTO.
   */
  public Company createCompany(CompanyCreateDto companyDto) {
    Company company = new Company();
    companyMapper.update(company, companyDto);
    if (geoEnabled) {
      val geoAddress = geoService.getBestMatchingGeoAddress(company);
      if (geoAddress.isPresent()) {
        val address = addressMapper.convert(geoAddress.get());
        addressRepository.save(address);
        company.setAddress(address);
      } else {
        log.warn("Geolocalizzazione indirizzo non riuscita per {}", company);
      }
    }
    return company;
  }

  /**
   * Aggiorna l'entity riferita dal DTO con i dati passati.
   */
  public Company updateCompany(Company company, CompanyUpdateDto companyDto) {
    Verify.verifyNotNull(company);
    Verify.verifyNotNull(companyDto);
    companyMapper.update(company, companyDto);
    if (geoEnabled) {
      val geoAddress = geoService.getBestMatchingGeoAddress(company);
      if (geoAddress.isPresent()) {
        val address = addressMapper.convert(geoAddress.get());
        addressRepository.save(address);
        company.setAddress(address);
      } else {
        log.warn("Geolocalizzazione indirizzo non riuscita per {}", company);
      }
    }
    return company;
  }
}