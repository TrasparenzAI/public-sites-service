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
package it.cnr.anac.transparency.companies.repositories;

import com.querydsl.core.BooleanBuilder;
import it.cnr.anac.transparency.companies.models.Municipality;
import it.cnr.anac.transparency.companies.models.QMunicipality;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * DAO per le ricerche sulle Municipality.
 *
 */
@RequiredArgsConstructor
@Component
public class MunicipalityDao {

  private final MunicipalityRepository repo;
  
  public Page<Municipality> findAllActive(
      Optional<String> codiceCatastale, Optional<String> codiceComune, 
      Optional<String> denominazione, Optional<String> denominazioneAltraLingua, Optional<String> denominazioneRegione, 
      Optional<String> ripartizioneGeografica, Optional<String> siglaAutomobilistica, Pageable pageable) {
    QMunicipality mnicipality = QMunicipality.municipality;
    BooleanBuilder builder = new BooleanBuilder(mnicipality.dataCancellazione.isNull());
    if (codiceCatastale.isPresent()) {
      builder.and(mnicipality.codiceCatastale.equalsIgnoreCase(codiceCatastale.get()));
    }
    if (codiceComune.isPresent()) {
      builder.and(mnicipality.codiceComune.equalsIgnoreCase(codiceComune.get()));
    }
    if (denominazione.isPresent()) {
      builder.and(mnicipality.denominazione.equalsIgnoreCase(denominazione.get()));
    }
    if (denominazioneAltraLingua.isPresent()) {
      builder.and(mnicipality.denominazioneAltraLingua.containsIgnoreCase(denominazioneAltraLingua.get()));
    }
    if (denominazioneRegione.isPresent()) {
      builder.and(mnicipality.denominazioneRegione.containsIgnoreCase(denominazioneRegione.get()));
    }
    if (ripartizioneGeografica.isPresent()) {
      builder.and(mnicipality.ripartizioneGeografica.containsIgnoreCase(ripartizioneGeografica.get()));
    }
    if (siglaAutomobilistica.isPresent()) {
      builder.and(mnicipality.siglaAutomobilistica.containsIgnoreCase(siglaAutomobilistica.get()));
    }
    return repo.findAll(builder.getValue(), pageable);
  }

}