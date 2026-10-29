/*
 * Copyright (C) 2026 Consiglio Nazionale delle Ricerche
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import it.cnr.anac.transparency.companies.indicepa.EnteDto;
import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.repositories.MunicipalityRepository;

@ExtendWith(MockitoExtension.class)
class CompanyMapperTest {

  @Mock
  private MunicipalityRepository municipalRepository;

  private CompanyMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CompanyMapperImpl();
    ReflectionTestUtils.setField(mapper, "municipalRepository", municipalRepository);
  }

  @Test
  void updateFromIndicePaKeepsCompanyNotVisible() {
    Company company = new Company();
    company.setCodiceIpa("camera");
    company.setVisibile(false);

    EnteDto enteDto = new EnteDto();
    enteDto.setCodiceIpa("camera");
    enteDto.setDenominazioneEnte("Camera dei Deputati");
    CompanyShowDto companyDto = mapper.convert(enteDto);
    assertTrue(companyDto.getVisibile());

    mapper.update(company, companyDto);

    assertFalse(company.getVisibile());
    assertEquals("Camera dei Deputati", company.getDenominazioneEnte());
  }

  @Test
  void updateWithoutVisibileKeepsCompanyVisible() {
    Company company = new Company();
    company.setCodiceIpa("c_e386");

    CompanyUpdateDto companyDto = new CompanyUpdateDto();
    companyDto.setCodiceIpa("c_e386");

    mapper.update(company, companyDto);

    assertTrue(company.getVisibile());
  }
}
