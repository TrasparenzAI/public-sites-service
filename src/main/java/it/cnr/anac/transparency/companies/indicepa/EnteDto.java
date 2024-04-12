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

  @JsonProperty("Codice_Comune_Istat")
  private String codiceComuneIstat;
  @JsonProperty("Codice_catastale_comune")
  private String codiceCastaleComune;
  @JsonProperty("CAP")
  private String cap;
  @JsonProperty("Indirizzo")
  private String indirizzo;

  @JsonProperty("Nome_responsabile")
  private String nomeResponsabile;
  @JsonProperty("Cognome_responsabile")
  private String cognomeResponsabile;
  @JsonProperty("Titolo_responsabile")
  private String titoloResponsabile;

  @JsonProperty("Mail1")
  private String mail1;
  @JsonProperty("Tipo_Mail1")
  private String tipoMail1;
  @JsonProperty("Mail2")
  private String mail2;
  @JsonProperty("Tipo_Mail2")
  private String tipoMail2;
  @JsonProperty("Mail3")
  private String mail3;
  @JsonProperty("Tipo_Mail3")
  private String tipoMail3;
  @JsonProperty("Mail4")
  private String mail4;
  @JsonProperty("Tipo_Mail4")
  private String tipoMail4;
  @JsonProperty("Mail5")
  private String mail5;
  @JsonProperty("Tipo_Mail5")
  private String tipoMail5;
}