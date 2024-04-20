package it.cnr.anac.transparency.companies.v1.dto;

import lombok.Data;

@Data
public class CompanyAddressDto {

  private Long companyId;
  private Long addressId;
  private String companyName;
  private String ipaCode;
  private String latitude;
  private String longitude;
}
