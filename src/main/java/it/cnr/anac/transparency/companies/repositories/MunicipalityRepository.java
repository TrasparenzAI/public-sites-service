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

import it.cnr.anac.transparency.companies.models.Municipality;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Repository per l'accesso ai dati dei comuni.
 *
 */
public interface MunicipalityRepository  extends JpaRepository<Municipality,Long>, QuerydslPredicateExecutor<Municipality>{

  public Optional<Municipality> findById(Long id);

  public Optional<Municipality> findByCodiceCatastale(String codiceCatastale);

  @Query("SELECT c FROM Company c WHERE c.dataCancellazione is null")
  public Page<Municipality> findAllActive(Pageable page);

}