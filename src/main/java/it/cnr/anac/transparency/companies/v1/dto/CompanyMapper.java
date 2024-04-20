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
package it.cnr.anac.transparency.companies.v1.dto;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import it.cnr.anac.transparency.companies.indicepa.EnteDto;
import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.repositories.MunicipalityRepository;

/**
 * Mapping dei dati delle Entity nei rispettivi DTO e da DTO relativi
 * a fonti esterne come IndicePA ai DTO esportati dal servizio.
 *
 */
@Component
@Mapper(componentModel = "spring")
public abstract class CompanyMapper {

  @Inject
  protected MunicipalityRepository municipalRepository;

  @Mapping(target = "dataAggiornamento", ignore = true)
  public abstract CompanyShowDto convert(Company company);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sorgente", constant = "indicePA")
  @Mapping(target = "dataCancellazione", ignore = true)
  @Mapping(target = "denominazioneComune", ignore = true)
  @Mapping(target = "denominazioneUnitaSovracomunale", ignore = true)
  @Mapping(target = "denominazioneRegione", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract CompanyShowDto convert(EnteDto enteDto);

  @Mapping(target = "comune", 
      expression = "java(municipalRepository.findByCodiceCatastale(companyDto.getCodiceCatastaleComune()).orElse(null))")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "dataCancellazione", ignore = true)
  @Mapping(target = "address", ignore = true)
  @Mapping(target = "denominazioneComune", ignore = true)
  @Mapping(target = "denominazioneUnitaSovracomunale", ignore = true)
  @Mapping(target = "denominazioneRegione", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  public  abstract void update(@MappingTarget Company company, CompanyCreateDto companyDto);

}