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

package it.cnr.anac.transparency.companies.v1.dto;

import it.cnr.anac.transparency.companies.models.CompanySource;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.ToString;

/**
 * Data transfer object per le informazioni relativa alla creazione di un Company.
 */
@ToString
@Data
public class CompanyCreateDto {

  @NotNull
  private String codiceIpa;
  @NotNull
  private String denominazioneEnte;
  private String codiceFiscaleEnte;
  private String tipologia;
  private String codiceCategoria;
  private String codiceNatura;
  private String acronimo;
  @NotNull
  private String sitoIstituzionale;
  private CompanySource sorgente;
  private LocalDate dataAggiornamento;
  private String codiceComuneIstat;
  private String codiceCatastaleComune;
  private String cap;
  private String indirizzo;

  private String nomeResponsabile;
  private String cognomeResponsabile;
  private String titoloResponsabile;

  private String mail1;
  private String tipoMail1;
  private String mail2;
  private String tipoMail2;
  private String mail3;
  private String tipoMail3;
  private String mail4;
  private String tipoMail4;
  private String mail5;
  private String tipoMail5;
}