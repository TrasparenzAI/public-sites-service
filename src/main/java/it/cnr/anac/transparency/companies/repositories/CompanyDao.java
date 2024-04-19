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
      Optional<String> codiceIpa, Optional<String> denominazioneEnte, Optional<Long> idIpaFrom, Pageable pageable) {
    QCompany company = QCompany.company;
    BooleanBuilder builder = new BooleanBuilder(company.dataCancellazione.isNull());
    if (codiceCategoria.isPresent()) {
      builder.and(company.codiceCategoria.equalsIgnoreCase(codiceCategoria.get()));
    }
    if (codiceFiscaleEnte.isPresent()) {
      builder.and(company.codiceFiscaleEnte.equalsIgnoreCase(codiceFiscaleEnte.get()));
    }
    if (codiceIpa.isPresent()) {
      builder.and(company.codiceIpa.equalsIgnoreCase(codiceIpa.get()));
    }
    if (denominazioneEnte.isPresent()) {
      builder.and(company.denominazioneEnte.containsIgnoreCase(denominazioneEnte.get()));
    }
    if (idIpaFrom.isPresent()) {
      builder.and(company.id.gt(idIpaFrom.get()));
    }
    return repo.findAll(builder.getValue(), pageable);
  }

}