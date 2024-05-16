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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GoogleMapsAddressDto {

  @NoArgsConstructor
  @Data
  public final static  class Geometry {
    @NoArgsConstructor
    @Data
    public final static class Location {
      private double lat;
      private double lng;
    }
    private Location location;
    @JsonProperty("location_type")
    private String locationType;
  }

  private Geometry geometry;
  @JsonProperty("formatted_address")
  private String formattedAddress;
  @JsonProperty("place_id")
  private String placeId;
  private List<String> types = Lists.newArrayList();
}