/*
 * Copyright (C) 2026 Consiglio Nazionale delle Ricerche
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
package it.cnr.anac.transparency.companies.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Storico delle esecuzioni di aggiornamento degli enti tramite IndicePA.
 */
@ToString(callSuper = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "indice_pa_update_history")
@Entity
public class IndicePaUpdateHistory extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 6645598169657373994L;

  @Column(name = "update_date", nullable = false)
  private LocalDateTime updateDate;

  @Column(name = "updated_from")
  private LocalDate updatedFrom;

  @Column(name = "total_indice_pa_companies", nullable = false)
  private Integer totalIndicePaCompanies;

  @Column(name = "processed_companies", nullable = false)
  private Integer processedCompanies;

  @Column(name = "total_active_companies", nullable = false)
  private Integer totalActiveCompanies;

  @Column(name = "total_visible_companies", nullable = false)
  private Integer totalVisibleCompanies;

  @Column(name = "inserted_companies", nullable = false)
  private Integer insertedCompanies;

  @Column(name = "modified_companies", nullable = false)
  private Integer modifiedCompanies;

  @Column(name = "deleted_companies", nullable = false)
  private Integer deletedCompanies;

}
