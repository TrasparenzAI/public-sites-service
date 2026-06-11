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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import it.cnr.anac.transparency.companies.indicepa.IndicePaResponse.IndicePaResult;
import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.models.CompanySource;
import it.cnr.anac.transparency.companies.models.IndicePaUpdateHistory;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.repositories.IndicePaUpdateHistoryRepository;
import it.cnr.anac.transparency.companies.services.CompanyService;
import it.cnr.anac.transparency.companies.v1.dto.CompanyMapper;
import it.cnr.anac.transparency.companies.v1.dto.CompanyShowDto;

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

  @Mock
  private IndicePaUpdateHistoryRepository updateHistoryRepository;

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
    when(repo.countBySorgenteAndDataCancellazioneIsNull(CompanySource.indicePA)).thenReturn(0L);

    indicePaService.updateCompaniesFromIndicePa(Optional.empty());

    verify(lockService).recordUpdate();
  }

  @Test
  void updateSalvaStoricoConConteggiAggiornamento() {
    when(lockService.isLocked()).thenReturn(false);

    EnteDto insertedRecord = new EnteDto();
    insertedRecord.setCodiceIpa("ipa-new");
    insertedRecord.setDataAggiornamento(LocalDate.of(2026, 1, 20));
    EnteDto updatedRecord = new EnteDto();
    updatedRecord.setCodiceIpa("ipa-existing");
    updatedRecord.setDataAggiornamento(LocalDate.of(2026, 1, 20));

    IndicePaResult result = new IndicePaResult();
    result.setRecords(List.of(insertedRecord, updatedRecord));
    IndicePaResponse response = new IndicePaResponse();
    response.setResult(result);
    when(indicePaClient.publicCompanies(anyString())).thenReturn(response);

    CompanyShowDto insertedDto = companyDto("ipa-new", "Nuovo ente");
    CompanyShowDto updatedDto = companyDto("ipa-existing", "Ente aggiornato");
    insertedDto.setDataAggiornamento(LocalDate.of(2026, 1, 20));
    updatedDto.setDataAggiornamento(LocalDate.of(2026, 1, 20));
    when(mapper.convert(insertedRecord)).thenReturn(insertedDto);
    when(mapper.convert(updatedRecord)).thenReturn(updatedDto);

    Company existingCompany = company("ipa-existing", "Vecchia denominazione", null);
    Company deletedCompany = company("ipa-deleted", "Ente cancellato", null);
    when(repo.findBySorgente(CompanySource.indicePA)).thenReturn(List.of(existingCompany, deletedCompany));
    when(repo.findByCodiceIpa("ipa-existing")).thenReturn(Optional.of(existingCompany));
    when(companyService.createCompany(insertedDto)).thenReturn(company("ipa-new", "Nuovo ente", null));
    lenient().when(companyService.updateCompany(existingCompany, updatedDto)).thenReturn(existingCompany);
    when(repo.countBySorgenteAndDataCancellazioneIsNull(CompanySource.indicePA)).thenReturn(2L);
    when(repo.countBySorgenteAndDataCancellazioneIsNullAndVisibileTrue(CompanySource.indicePA)).thenReturn(1L);

    int updated = indicePaService.updateCompaniesFromIndicePa(Optional.of(LocalDate.of(2026, 1, 1)));

    assertEquals(3, updated);
    verify(lockService).recordUpdate();
    verify(companyService).createCompany(insertedDto);
    verify(companyService).updateCompany(existingCompany, updatedDto);
    verify(repo).save(eq(deletedCompany));

    ArgumentCaptor<IndicePaUpdateHistory> historyCaptor =
        ArgumentCaptor.forClass(IndicePaUpdateHistory.class);
    verify(updateHistoryRepository).save(historyCaptor.capture());
    IndicePaUpdateHistory history = historyCaptor.getValue();

    assertEquals(LocalDate.of(2026, 1, 1), history.getUpdatedFrom());
    assertEquals(2, history.getTotalIndicePaCompanies());
    assertEquals(2, history.getProcessedCompanies());
    assertEquals(2, history.getTotalActiveCompanies());
    assertEquals(1, history.getTotalVisibleCompanies());
    assertEquals(1, history.getInsertedCompanies());
    assertEquals(1, history.getModifiedCompanies());
    assertEquals(1, history.getDeletedCompanies());
  }

  private CompanyShowDto companyDto(String codiceIpa, String denominazione) {
    CompanyShowDto companyDto = new CompanyShowDto();
    companyDto.setCodiceIpa(codiceIpa);
    companyDto.setDenominazioneEnte(denominazione);
    return companyDto;
  }

  private Company company(String codiceIpa, String denominazione, LocalDate dataCancellazione) {
    Company company = new Company();
    company.setCodiceIpa(codiceIpa);
    company.setDenominazioneEnte(denominazione);
    company.setDataCancellazione(dataCancellazione);
    company.setSorgente(CompanySource.indicePA);
    return company;
  }

}
