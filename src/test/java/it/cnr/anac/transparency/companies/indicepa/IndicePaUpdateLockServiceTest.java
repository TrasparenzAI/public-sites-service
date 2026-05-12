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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.cnr.anac.transparency.companies.models.IndicePaUpdateSettings;
import it.cnr.anac.transparency.companies.repositories.IndicePaUpdateSettingsRepository;

@ExtendWith(MockitoExtension.class)
class IndicePaUpdateLockServiceTest {

  @Mock
  private IndicePaUpdateSettingsRepository repo;

  @InjectMocks
  private IndicePaUpdateLockService lockService;

  private IndicePaUpdateSettings settings;

  @BeforeEach
  void setUp() {
    settings = new IndicePaUpdateSettings();
    settings.setLocked(false);
    when(repo.findAll()).thenReturn(List.of(settings));
    lenient().when(repo.save(any(IndicePaUpdateSettings.class))).thenAnswer(inv -> inv.getArgument(0));
  }

  @Test
  void isLockedReturnsFalseQuandoNonBloccato() {
    assertFalse(lockService.isLocked());
  }

  @Test
  void isLockedReturnsTrueQuandoBloccato() {
    settings.setLocked(true);
    assertTrue(lockService.isLocked());
  }

  @Test
  void lockImpostaLockedTrue() {
    lockService.lock();
    assertTrue(settings.isLocked());
    verify(repo).save(settings);
  }

  @Test
  void unlockImpostaLockedFalse() {
    settings.setLocked(true);
    lockService.unlock();
    assertFalse(settings.isLocked());
    verify(repo).save(settings);
  }

  @Test
  void getLastUpdateRestituisceEmptySeNonAncoraAggiornato() {
    Optional<LocalDateTime> result = lockService.getLastUpdate();
    assertTrue(result.isEmpty());
  }

  @Test
  void getLastUpdateRestituisceIlValoreImpostato() {
    LocalDateTime timestamp = LocalDateTime.of(2026, 3, 15, 10, 30);
    settings.setLastUpdate(timestamp);

    Optional<LocalDateTime> result = lockService.getLastUpdate();
    assertTrue(result.isPresent());
    assertEquals(timestamp, result.get());
  }

  @Test
  void recordUpdateImpostaLastUpdateAOra() {
    LocalDateTime prima = LocalDateTime.now().minusSeconds(1);
    lockService.recordUpdate();
    assertNotNull(settings.getLastUpdate());
    assertTrue(settings.getLastUpdate().isAfter(prima));
    verify(repo).save(settings);
  }

  // --- Edge case: nessuna riga di settings nel database ---

  @Test
  void getSettingsLanciaIllegalStateExceptionSeNessunaRiga() {
    when(repo.findAll()).thenReturn(Collections.emptyList());

    assertThrows(IllegalStateException.class, () -> lockService.isLocked());
  }

  // --- Idempotenza: lock su oggetto già bloccato ---

  @Test
  void lockQuandoGiàBloccatoNonCambiaStatoEridestina() {
    settings.setLocked(true);

    lockService.lock();

    assertTrue(settings.isLocked());
    verify(repo).save(settings);
  }

  // --- Idempotenza: unlock su oggetto già sbloccato ---

  @Test
  void unlockQuandoGiàSblockatoNonCambiaStatoEridestina() {
    settings.setLocked(false);

    lockService.unlock();

    assertFalse(settings.isLocked());
    verify(repo).save(settings);
  }

  // --- recordUpdate sovrascrive lastUpdate precedente ---

  @Test
  void recordUpdateSovrascrivePrecedenteLastUpdate() {
    LocalDateTime vecchioTimestamp = LocalDateTime.of(2025, 1, 1, 0, 0);
    settings.setLastUpdate(vecchioTimestamp);

    LocalDateTime prima = LocalDateTime.now().minusSeconds(1);
    lockService.recordUpdate();

    assertTrue(settings.getLastUpdate().isAfter(prima));
    verify(repo, times(1)).save(settings);
  }

}
