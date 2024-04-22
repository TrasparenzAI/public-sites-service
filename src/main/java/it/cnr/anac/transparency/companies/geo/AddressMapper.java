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

import it.cnr.anac.transparency.companies.models.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct da indirizzi OpenstreetMap ad Address (entity).
 *
 * @author Cristian Lucchesi
 */
@Mapper(componentModel = "spring")
public interface AddressMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "addresstype", target = "addressType")
  @Mapping(source = "display_name", target = "displayName")
  @Mapping(source = "lat", target = "latitude")
  @Mapping(source = "lon", target = "longitude")
  @Mapping(source = "osm_id", target = "osmId")
  @Mapping(source = "osm_type", target = "osmType")
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract Address convert(OpenstreetMapAddressDto dto);

}