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

package it.cnr.anac.transparency.companies.municipalities;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import lombok.Data;

/**
 * DTO con le informazioni principali estratte dal CSV dei comuni di Istat.
 */
@Data
public class MunicipalityCsvDto {

  @CsvBindByPosition(position = 0)
  private String codiceRegione;
  @CsvBindByPosition(position = 6)
  private String denominazione;
  @CsvBindByPosition(position = 7)
  private String denominazioneAltraLingua;
  @CsvBindByPosition(position = 8)
  private String codiceRipartizioneGeografica;
  @CsvBindByPosition(position = 9)
  private String ripartizioneGeografica;
  @CsvBindByPosition(position = 10)
  private String denominazioneRegione;
  @CsvBindByPosition(position = 11)
  private String denominazioneUnitaSovracomunale; //di solito la provincia
  //Flag Comune capoluogo di provincia/città metropolitana/libero consorzio
  //@CsvBindByPosition(position = 13)
  @CsvCustomBindByPosition(position = 13, converter = ConvertItalianToBoolean.class)
  private Boolean capoluogo;
  @CsvBindByPosition(position = 14)
  private String siglaAutomobilistica;
  @CsvBindByPosition(position = 15)
  private String codiceComune;
  @CsvBindByPosition(position = 19)
  private String codiceCatastale;

}