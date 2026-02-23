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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TitoloResponsabileFilter {
    private static boolean mostraResponsabile;

    @Value("${transparency.companies.show-responsabile:false}")
    public void setMostraTitoloResponsabile(boolean mostra) {
        mostraResponsabile = mostra;
    }

    @Override
    public boolean equals(Object obj) {
        // Se ritorna true, Jackson ESCLUDE il campo dal JSON
        return !mostraResponsabile;
    }
}
