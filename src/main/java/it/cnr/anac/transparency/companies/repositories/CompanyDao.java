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

package it.cnr.anac.transparency.companies.repositories;

import com.querydsl.core.BooleanBuilder;
import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.models.QCompany;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * DAO per le ricerche sulle Company.
 *
 * @author Cristian Lucchesi
 */
@RequiredArgsConstructor
@Component
public class CompanyDao {

  private final CompanyRepository repo;

  public Page<Company> findAllActive(
      Optional<String> codiceCategoria, Optional<String> codiceFiscaleEnte,
      Optional<String> codiceIpa, Optional<String> denominazioneEnte,
      Optional<String> comune, Optional<String> provincia,
      Optional<Long> idIpaFrom, 
      Optional<Boolean> withoutAddress, Optional<String> regione, 
      Optional<Boolean> visibile, Pageable pageable) {
      QCompany company = QCompany.company;
      BooleanBuilder builder = new BooleanBuilder(company.dataCancellazione.isNull());
      //Di default tutte le company hanno impostato visibile come True
      if (visibile.isEmpty() || visibile.get()) {
        builder.and(company.visibile.isTrue());
      }
      if (visibile.isPresent() && !visibile.get()) {
        builder.and(company.visibile.isFalse());
      }
      codiceCategoria.ifPresent(s -> builder.and(company.codiceCategoria.equalsIgnoreCase(s)));
      codiceFiscaleEnte.ifPresent(s -> builder.and(company.codiceFiscaleEnte.equalsIgnoreCase(s)));
      codiceIpa.ifPresent(s -> builder.and(company.codiceIpa.equalsIgnoreCase(s)));
      denominazioneEnte.ifPresent(s -> builder.and(company.denominazioneEnte.containsIgnoreCase(s)));
      comune.ifPresent(s -> builder.and(
              company.comune.isNotNull()
                      .and(company.comune.denominazione.containsIgnoreCase(s))));
      provincia.ifPresent(s -> builder.and(
              company.comune.isNotNull()
                      .and(company.comune.denominazioneUnitaSovracomunale.containsIgnoreCase(s))));
      idIpaFrom.ifPresent(aLong -> builder.and(company.id.gt(aLong)));
      withoutAddress.ifPresent(aBoolean -> builder.and(company.address.isNull()));
      regione.ifPresent(s -> builder.and(
              company.comune.isNotNull()
                      .and(company.comune.denominazioneRegione.containsIgnoreCase(s))));
      assert builder.getValue() != null;
      return repo.findAll(builder.getValue(), pageable);
  }

  public Page<Company> findAllActiveWithAddress(Pageable pageable) {
    QCompany company = QCompany.company;
    BooleanBuilder builder = new BooleanBuilder(company.dataCancellazione.isNull());
    builder.and(company.address.isNotNull());
    return repo.findAll(builder.getValue(), pageable);
  }
}