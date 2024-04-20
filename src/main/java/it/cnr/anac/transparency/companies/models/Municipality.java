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

package it.cnr.anac.transparency.companies.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entity che rappresenta i dati di un Ente pubblico.
 *
 */
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "municipalities")
@Entity
public class Municipality extends MutableModel {

  private static final long serialVersionUID = 6192591205971740773L;

  private String codiceRegione;
  @NotNull @NotEmpty
  private String denominazione;
  private String denominazioneAltraLingua;
  private String codiceRipartizioneGeografica;
  private String ripartizioneGeografica;
  private String denominazioneRegione;
  private String denominazioneUnitaSovracomunale;
  //Flag Comune capoluogo di provincia/città metropolitana/libero consorzio
  private Boolean capoluogo;
  private String siglaAutomobilistica;
  private String codiceComune;
  @NotNull @NotEmpty
  private String codiceCatastale;

  private LocalDate dataCancellazione;
}
