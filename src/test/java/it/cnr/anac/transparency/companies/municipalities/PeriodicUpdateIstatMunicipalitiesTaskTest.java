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
package it.cnr.anac.transparency.companies.municipalities;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.cnr.anac.transparency.companies.indicepa.IndicePaUpdateLockedException;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PeriodicUpdateIstatMunicipalitiesTaskTest {

  @Mock
  private MunicipalityService service;

  @InjectMocks
  private PeriodicUpdateIstatMunicipalitiesTask task;

  @Test
  void taskNonLanciaEccezioneQuandoLockAttivo() throws IOException {
    when(service.updateMunicipalitiesFromIstat())
        .thenThrow(new IndicePaUpdateLockedException());

    assertDoesNotThrow(() -> task.updateIstatMunicipalities());
  }

  @Test
  void taskEseguiAggiornamentoQuandoLockNonAttivo() throws IOException {
    when(service.updateMunicipalitiesFromIstat()).thenReturn(5);

    task.updateIstatMunicipalities();

    verify(service).updateMunicipalitiesFromIstat();
  }

}
