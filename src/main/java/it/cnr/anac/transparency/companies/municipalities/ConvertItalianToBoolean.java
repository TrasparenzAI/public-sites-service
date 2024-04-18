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