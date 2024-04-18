package it.cnr.anac.transparency.companies.models;

import lombok.Data;

@Data
public class Address {

  private String addressType;
  private String category;
  private String displayName;
  private String latitude;
  private String longitude;
  private Integer osmId;
  private String osmType;
  private String type;
}
