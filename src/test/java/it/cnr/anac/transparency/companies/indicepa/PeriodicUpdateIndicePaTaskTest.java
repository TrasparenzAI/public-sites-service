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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PeriodicUpdateIndicePaTaskTest {

  @Mock
  private IndicePaService service;

  @InjectMocks
  private PeriodicUpdateIndicePaTask task;

  @Test
  void taskNonLanciaEccezioneQuandoLockAttivo() {
    when(service.updateCompaniesFromIndicePa(Optional.empty()))
        .thenThrow(new IndicePaUpdateLockedException());

    assertDoesNotThrow(() -> task.updateIndicePaCompanies());
  }

  @Test
  void taskEseguiAggiornamentoQuandoLockNonAttivo() {
    when(service.updateCompaniesFromIndicePa(Optional.empty())).thenReturn(5);

    task.updateIndicePaCompanies();

    verify(service).updateCompaniesFromIndicePa(Optional.empty());
  }

}
