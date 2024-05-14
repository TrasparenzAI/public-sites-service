package it.cnr.anac.transparency.companies.geo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GoogleMapsAddressDto {

  @NoArgsConstructor
  @Data
  public final static  class Geometry {
    @NoArgsConstructor
    @Data
    private final static class Location {
      private double lat;
      private double lng;
    }
    private Location location;
  }

  private Geometry geometry;
  @JsonProperty("formatted_address")
  private String formattedAddress;
}