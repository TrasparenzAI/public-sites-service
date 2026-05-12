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

import it.cnr.anac.transparency.companies.models.IndicePaUpdateSettings;
import it.cnr.anac.transparency.companies.repositories.IndicePaUpdateSettingsRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servizio per la gestione del meccanismo di lock dell'aggiornamento degli enti da IndicePA.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class IndicePaUpdateLockService {

  private final IndicePaUpdateSettingsRepository repo;

  private IndicePaUpdateSettings getSettings() {
    return repo.findAll().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Configurazione aggiornamento IndicePA non trovata nel database"));
  }

  /**
   * Attiva il lock impedendo ulteriori aggiornamenti da IndicePA.
   */
  @Transactional
  public void lock() {
    IndicePaUpdateSettings settings = getSettings();
    settings.setLocked(true);
    repo.save(settings);
    log.info("Lock aggiornamento IndicePA attivato");
  }

  /**
   * Disattiva il lock consentendo nuovamente gli aggiornamenti da IndicePA.
   */
  @Transactional
  public void unlock() {
    IndicePaUpdateSettings settings = getSettings();
    settings.setLocked(false);
    repo.save(settings);
    log.info("Lock aggiornamento IndicePA disattivato");
  }

  /**
   * @return true se il lock è attivo e gli aggiornamenti sono bloccati
   */
  public boolean isLocked() {
    return getSettings().isLocked();
  }

  /**
   * @return la data e ora dell'ultimo aggiornamento degli enti da IndicePA, se disponibile
   */
  public Optional<LocalDateTime> getLastUpdate() {
    return Optional.ofNullable(getSettings().getLastUpdate());
  }

  /**
   * Registra la data e ora dell'ultimo aggiornamento completato.
   */
  @Transactional
  public void recordUpdate() {
    IndicePaUpdateSettings settings = getSettings();
    settings.setLastUpdate(LocalDateTime.now());
    repo.save(settings);
  }

}
