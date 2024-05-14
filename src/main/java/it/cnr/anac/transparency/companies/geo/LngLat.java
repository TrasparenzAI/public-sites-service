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
package it.cnr.anac.transparency.companies.geo;

import lombok.Data;

/**
 * Longitutide e latitudine.
 */
@Data
public class LngLat {

  private double longitude;
  private double latitude;

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (!(o instanceof LngLat)) {
          return false;
      }
      LngLat lngLatAlt = (LngLat)o;
      return Double.compare(lngLatAlt.latitude, latitude) == 0 && Double.compare(lngLatAlt.longitude, longitude) == 0;
  }

  @Override
  public int hashCode() {
      long temp = Double.doubleToLongBits(longitude);
      int result = (int)(temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(latitude);
      result = 31 * result + (int)(temp ^ (temp >>> 32));
      return result;
  }

}