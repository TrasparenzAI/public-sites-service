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

package it.cnr.anac.transparency.companies.indicepa;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Data;
import lombok.ToString;

/**
 * Data transfer object per le informazioni relative ad un ente pubblico
 * ricevute dal OpenData di IndicePA.
 *
 */
@ToString
@Data
public class EnteDto {

  @JsonProperty("Codice_IPA")
  private String codiceIpa;
  @JsonProperty("Denominazione_ente")
  private String denominazioneEnte;
  @JsonProperty("Codice_fiscale_ente")
  private String codiceFiscaleEnte;
  @JsonProperty("Tipologia")
  private String tipologia;
  @JsonProperty("Codice_Categoria")
  private String codiceCategoria;
  @JsonProperty("Codice_natura")
  private String codiceNatura;
  @JsonProperty("Acronimo")
  private String acronimo;
  @JsonProperty("Sito_istituzionale")
  private String sitoIstituzionale;
  @JsonProperty("Data_aggiornamento")
  private LocalDate dataAggiornamento;

}