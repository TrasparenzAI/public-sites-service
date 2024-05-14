package it.cnr.anac.transparency.companies.geo;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;

@Data
public class GoogleMapsResponseDto {

  private List<GoogleMapsAddressDto> results = Lists.newArrayList();
  private String status;

}