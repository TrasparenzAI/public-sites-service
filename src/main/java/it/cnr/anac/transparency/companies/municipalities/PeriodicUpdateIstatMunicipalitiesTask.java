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
package it.cnr.anac.transparency.companies.municipalities;

import java.io.IOException;
import javax.inject.Inject;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PeriodicUpdateIstatMunicipalitiesTask {

  @Inject
  MunicipalityService service;

  @Scheduled(cron = "0 40 06 ? * *")
  public void updateIstatMunicipalities() throws IOException {
    log.info("Avvio aggiornamento dei comuni da Istat");
    val updated = service.updateMunicipalitiesFromIstat();
    log.info("Fine aggiornamento comuni da Istat, aggiornati {} comuni", updated);
  }

}