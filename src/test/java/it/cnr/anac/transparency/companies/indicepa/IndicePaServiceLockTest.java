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
package it.cnr.anac.transparency.companies.indicepa;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import it.cnr.anac.transparency.companies.indicepa.IndicePaResponse.IndicePaResult;
import it.cnr.anac.transparency.companies.models.CompanySource;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.services.CompanyService;
import it.cnr.anac.transparency.companies.v1.dto.CompanyMapper;

@ExtendWith(MockitoExtension.class)
class IndicePaServiceLockTest {

  @Mock
  private IndicePaClient indicePaClient;

  @Mock
  private CompanyMapper mapper;

  @Mock
  private CompanyRepository repo;

  @Mock
  private CompanyService companyService;

  @Mock
  private IndicePaUpdateLockService lockService;

  @InjectMocks
  private IndicePaService indicePaService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(indicePaService, "indicePaResourceId", "test-resource-id");
  }

  @Test
  void updateLanciaEccezioneQuandoLockAttivo() {
    when(lockService.isLocked()).thenReturn(true);

    assertThrows(IndicePaUpdateLockedException.class,
        () -> indicePaService.updateCompaniesFromIndicePa(Optional.empty()));

    verifyNoInteractions(indicePaClient);
    verifyNoInteractions(repo);
  }

  @Test
  void updateProcedeCorrrettamenteQuandoLockNonAttivo() {
    when(lockService.isLocked()).thenReturn(false);

    IndicePaResult result = new IndicePaResult();
    result.setRecords(List.of());
    IndicePaResponse response = new IndicePaResponse();
    response.setResult(result);
    when(indicePaClient.publicCompanies(any())).thenReturn(response);
    when(repo.findBySorgente(CompanySource.indicePA)).thenReturn(List.of());

    indicePaService.updateCompaniesFromIndicePa(Optional.empty());

    verify(lockService).recordUpdate();
  }

}
