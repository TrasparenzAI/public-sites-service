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

import java.io.Serial;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Impostazioni per il meccanismo di aggiornamento degli enti da IndicePA.
 * Contiene il flag di lock e l'informazione sull'ultimo aggiornamento effettuato.
 */
@ToString(callSuper = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "indice_pa_update_settings")
@Entity
public class IndicePaUpdateSettings extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 7312048271839401923L;

  @Column(nullable = false)
  private boolean locked;

  @Column(name = "last_update")
  private LocalDateTime lastUpdate;

}
