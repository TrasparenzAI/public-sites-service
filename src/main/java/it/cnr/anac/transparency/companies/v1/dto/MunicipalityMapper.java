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

import it.cnr.anac.transparency.companies.models.Municipality;
import it.cnr.anac.transparency.companies.municipalities.MunicipalityCsvDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapping dei dati delle Entity nei rispettivi DTO.
 */
@Mapper(componentModel = "spring")
public interface MunicipalityMapper {

  MunicipalityShowDto convert(Municipality company);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "dataCancellazione", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  void update(@MappingTarget Municipality municipality, MunicipalityCsvDto municipalityDto);
}