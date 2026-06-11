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
package it.cnr.anac.transparency.companies.v1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import it.cnr.anac.transparency.companies.indicepa.IndicePaService;
import it.cnr.anac.transparency.companies.indicepa.IndicePaUpdateLockService;
import it.cnr.anac.transparency.companies.indicepa.IndicePaUpdateLockedException;
import it.cnr.anac.transparency.companies.municipalities.MunicipalityService;
import it.cnr.anac.transparency.companies.models.IndicePaUpdateHistory;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.repositories.IndicePaUpdateHistoryRepository;
import it.cnr.anac.transparency.companies.services.CachingService;
import it.cnr.anac.transparency.companies.v1.dto.IndicePaUpdateHistoryMapper;
import it.cnr.anac.transparency.companies.v1.dto.IndicePaUpdateHistoryShowDto;

/**
 * Test REST per i nuovi endpoint di lock/unlock/lastUpdate su AdminController.
 * Utilizza @WebMvcTest per caricare solo il web layer con MockMvc.
 * Usa jwt() post processor per simulare autenticazione OAuth2.
 */
@WebMvcTest(AdminController.class)
@ImportAutoConfiguration({
    SecurityAutoConfiguration.class,
    ServletWebSecurityAutoConfiguration.class
})
@TestPropertySource(properties = {
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/dummy-jwks",
    "security.oauth2.enabled=true",
    "security.oauth2.roles.POST=ADMIN",
    "security.oauth2.roles.PUT=ADMIN",
    "security.oauth2.roles.DELETE=ADMIN"
})
class AdminControllerLockTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private IndicePaService indicePaService;

  @MockitoBean
  private IndicePaUpdateLockService lockService;

  @MockitoBean
  private MunicipalityService municipalityService;

  @MockitoBean
  private CachingService cachingService;

  @MockitoBean
  private CompanyRepository companyRepository;

  @MockitoBean
  private IndicePaUpdateHistoryRepository updateHistoryRepository;

  @MockitoBean
  private IndicePaUpdateHistoryMapper updateHistoryMapper;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  // --- GET /v1/admin/lockStatus ---

  @Test
  void getLockStatusRestituisceFalseQuandoNonBloccato() throws Exception {
    when(lockService.isLocked()).thenReturn(false);

    mockMvc.perform(get("/v1/admin/lockStatus").with(jwt()))
        .andExpect(status().isOk())
        .andExpect(content().string("false"));
  }

  @Test
  void getLockStatusRestituisceTrueQuandoBloccato() throws Exception {
    when(lockService.isLocked()).thenReturn(true);

    mockMvc.perform(get("/v1/admin/lockStatus").with(jwt()))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  // --- POST /v1/admin/lock ---

  @Test
  void postLockRestituisce200EChiamaLockService() throws Exception {
    mockMvc.perform(post("/v1/admin/lock")
        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk());

    verify(lockService).lock();
  }

  @Test
  void postLockSenzaAutenticazioneVieneNegato() throws Exception {
    mockMvc.perform(post("/v1/admin/lock"))
        .andExpect(status().is4xxClientError());
  }

  // --- POST /v1/admin/unlock ---

  @Test
  void postUnlockRestituisce200EChiamaUnlockService() throws Exception {
    mockMvc.perform(post("/v1/admin/unlock")
        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk());

    verify(lockService).unlock();
  }

  // --- GET /v1/admin/lastUpdate ---

  @Test
  void getLastUpdateRestituisceNullSeNonAncoraAggiornato() throws Exception {
    when(companyRepository.findMaxUpdatedAt()).thenReturn(Optional.empty());

    mockMvc.perform(get("/v1/admin/lastUpdate").with(jwt()))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  void getLastUpdateRestituisceTimestampSePresente() throws Exception {
    LocalDateTime timestamp = LocalDateTime.of(2026, 3, 15, 10, 30, 0);
    when(companyRepository.findMaxUpdatedAt()).thenReturn(Optional.of(timestamp));

    mockMvc.perform(get("/v1/admin/lastUpdate").with(jwt()))
        .andExpect(status().isOk())
        .andExpect(content().string("\"2026-03-15T10:30:00\""));
  }

  // --- POST /v1/admin/updateIndicePaCompanies con lock attivo ---

  @Test
  void updateIndicePaRestituisce409QuandoLockAttivo() throws Exception {
    when(indicePaService.updateCompaniesFromIndicePa(any()))
        .thenThrow(new IndicePaUpdateLockedException());

    mockMvc.perform(post("/v1/admin/updateIndicePaCompanies")
        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isConflict());
  }

  @Test
  void updateIndicePaRestituisce200QuandoLockNonAttivo() throws Exception {
    when(indicePaService.updateCompaniesFromIndicePa(any())).thenReturn(42);

    mockMvc.perform(post("/v1/admin/updateIndicePaCompanies")
        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(content().string("42"));
  }

  @Test
  void getIndicePaUpdateHistoryRestituiscePaginaStorico() throws Exception {
    IndicePaUpdateHistory history = new IndicePaUpdateHistory();
    history.setId(7L);

    IndicePaUpdateHistoryShowDto dto = new IndicePaUpdateHistoryShowDto();
    dto.setId(7L);
    dto.setUpdateDate(LocalDateTime.of(2026, 2, 16, 6, 30));
    dto.setUpdatedFrom(LocalDate.of(2026, 2, 1));
    dto.setTotalIndicePaCompanies(100);
    dto.setProcessedCompanies(90);
    dto.setTotalActiveCompanies(95);
    dto.setTotalVisibleCompanies(88);
    dto.setInsertedCompanies(5);
    dto.setModifiedCompanies(3);
    dto.setDeletedCompanies(2);

    when(updateHistoryRepository.findAll(any(PageRequest.class)))
        .thenReturn(new PageImpl<>(List.of(history), PageRequest.of(0, 20), 1));
    when(updateHistoryMapper.convert(history)).thenReturn(dto);

    mockMvc.perform(get("/v1/admin/indicePaUpdateHistory")
        .param("page", "0")
        .param("size", "20")
        .with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(7))
        .andExpect(jsonPath("$.content[0].updateDate").value("2026-02-16T06:30:00"))
        .andExpect(jsonPath("$.content[0].updatedFrom").value("2026-02-01"))
        .andExpect(jsonPath("$.content[0].totalIndicePaCompanies").value(100))
        .andExpect(jsonPath("$.content[0].processedCompanies").value(90))
        .andExpect(jsonPath("$.content[0].totalActiveCompanies").value(95))
        .andExpect(jsonPath("$.content[0].totalVisibleCompanies").value(88))
        .andExpect(jsonPath("$.content[0].insertedCompanies").value(5))
        .andExpect(jsonPath("$.content[0].modifiedCompanies").value(3))
        .andExpect(jsonPath("$.content[0].deletedCompanies").value(2));
  }

}
