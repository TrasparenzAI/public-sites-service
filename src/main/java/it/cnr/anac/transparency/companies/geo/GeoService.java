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
package it.cnr.anac.transparency.companies.geo;

import com.google.common.base.Verify;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.repositories.MunicipalityRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.geojson.Feature;
import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.springframework.stereotype.Service;

/**
 * Servizio di Geo localizzazione degli indirizzi degli enti pubblici.
 *
 * @author Cristian Lucchesi
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GeoService {

  private final CompanyRepository companyRepository;
  private final MunicipalityRepository municipalityRepository;
  private final NominatimClient geoClient;

  /**
   * Aggiorna il comune_id nell'entity company facendo il match del comune tramite
   * il suo codice catastale.
   *
   * @return il numero di company aggiornate.
   */
  public int fixCompanyWithoutMunicipality() {
    int companiesUpdated = 0;
    for (Company company : companyRepository.findWithoutMunicipality()) {
      if (company.getCodiceCatastaleComune() != null) {
        company.setComune(municipalityRepository.findByCodiceCatastale(company.getCodiceCatastaleComune()).orElse(null));
        companyRepository.save(company);
        log.info("Aggiornato riferimento al comune per ente {}", company);
        companiesUpdated++;
      }
    }
    return companiesUpdated;
  }

  public List<OpenstreetMapAddressDto> getGeoAddresses(Company company) {
    if (company.getIndirizzo() == null || company.getComune() == null 
        || company.getComune().getDenominazione() == null) {
      return Lists.newArrayList();
    }
    return geoClient.searchAddress(
        String.format("%s, %s", company.getIndirizzo(), company.getComune().getDenominazione()));
  }

  public Optional<OpenstreetMapAddressDto> getBestMatchingGeoAddress(Company company) {
    val addresses = getGeoAddresses(company);
    if (addresses.size() == 0) {
      return Optional.empty();
    }
    return Optional.of(addresses.stream().max((a1, a2) -> a1.getImportance().compareTo(a2.getImportance())).get());
  }

  public Feature mapCompanyToFeature(Company company) {
    Verify.verifyNotNull(company);
    val address = company.getAddress();
    if (address == null) {
      return null;
    }
    val feature = new Feature();
    val point = new Point();
    point.setCoordinates(
        new LngLatAlt(
            Double.parseDouble(address.getLongitude()), 
            Double.parseDouble(address.getLatitude())));
    feature.setGeometry(point);
    feature.setId(company.getId().toString());
    val properties = Maps.<String, Object>newHashMap();
    properties.put("denominazioneEnte", company.getDenominazioneEnte());
    properties.put("codiceIpa", company.getCodiceIpa());
    properties.put("codiceFiscaleEnte", company.getCodiceFiscaleEnte());
    feature.setProperties(properties);
    return feature;
  }
}