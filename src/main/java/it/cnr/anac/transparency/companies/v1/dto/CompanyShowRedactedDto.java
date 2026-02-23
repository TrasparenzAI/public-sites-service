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
package it.cnr.anac.transparency.companies.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data transfer object per le informazioni sulle Company, con la visibilità del campo
 * titoloResponsabile definita in configurazione.
 */
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyShowRedactedDto extends CompanyBaseDto {

  private Long id;

  private String denominazioneComune;
  private String denominazioneUnitaSovracomunale;
  private String denominazioneRegione;

  @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = TitoloResponsabileFilter.class)
  private String titoloResponsabile;
  @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = TitoloResponsabileFilter.class)
  private String nomeResponsabile;
  @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = TitoloResponsabileFilter.class)
  private String cognomeResponsabile;

  private LocalDate dataCancellazione;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}