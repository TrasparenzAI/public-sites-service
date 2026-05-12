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
package it.cnr.anac.transparency.companies.repositories;

import it.cnr.anac.transparency.companies.models.IndicePaUpdateSettings;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository per l'accesso alle impostazioni di aggiornamento da IndicePA.
 */
public interface IndicePaUpdateSettingsRepository extends JpaRepository<IndicePaUpdateSettings, Long> {

}
