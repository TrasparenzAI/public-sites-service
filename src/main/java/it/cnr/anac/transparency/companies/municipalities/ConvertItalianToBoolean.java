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

import com.opencsv.bean.customconverter.ConverterLanguageToBoolean;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class ConvertItalianToBoolean <T, I> extends ConverterLanguageToBoolean<T, I> {

  public ConvertItalianToBoolean() {
    //EmptyConstructor
  }

  private static final String VERO = "vero";
  private static final String FALSO = "falso";
  private static final String[] TRUE_STRINGS = {VERO, "si", "s", "1", "v"};
  private static final String[] FALSE_STRINGS = {FALSO, "no", "n", "0", "f"};

  @Override
  protected String getLocalizedTrue() { return VERO; }
  @Override
  protected String getLocalizedFalse() { return FALSO; }
  @Override
  protected String[] getAllLocalizedTrueValues() { return TRUE_STRINGS; }
  @Override
  protected String[] getAllLocalizedFalseValues() { return FALSE_STRINGS; }
  @Override
  protected Object convert(String value) {
    try {
      return super.convert(value);
    } catch (CsvDataTypeMismatchException e) {
      return Boolean.FALSE;
    }
  }
}