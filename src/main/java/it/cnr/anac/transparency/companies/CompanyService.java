package it.cnr.anac.transparency.companies;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import it.cnr.anac.transparency.companies.geo.AddressMapper;
import it.cnr.anac.transparency.companies.geo.GeoService;
import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.repositories.AddressRepository;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {
  
  private final CompanyRepository companyRepository;
  private final AddressRepository addressRepository;
  private final GeoService geoService;
  private final AddressMapper addressMapper; 

  public Integer geolocalizeCompanies(Optional<Integer> limit) {
    List<Company> companies = companyRepository.findWithoutAddress();
    if (limit.isPresent()) {
      companies = companies.stream().limit(limit.get()).collect(Collectors.toList());
    }
    companies.stream().forEach(company -> {
      val geoAddress = geoService.getBestMatchingGeoAddress(company);
      if (geoAddress.isPresent()) {
        val address = addressMapper.convert(geoAddress.get());
        addressRepository.save(address);
        company.setAddress(address);
        companyRepository.save(company);
        log.info("Impostato indirizzo a {}", company);
      }
    });
    return companies.size();
  }
}
