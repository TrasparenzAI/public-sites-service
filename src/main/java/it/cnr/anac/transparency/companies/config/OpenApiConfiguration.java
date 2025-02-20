/*
 * Copyright (C) 2023 Consiglio Nazionale delle Ricerche
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

package it.cnr.anac.transparency.companies.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione dei parametri generali della documentazione
 * tramite OpenAPI.
 *
 * @author Cristian Lucchesi
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Public sites Service", 
    version = "0.1.0", 
    description = "Public Sites Service si occupa di gestire le informazioni principali relative "
        + "agli enti pubblici italiani ed in particolare i siti istituzionali"),
    servers = {
        @Server(url = "/public-sites-service", description = "Public Sites Service URL"),
        @Server(url = "/", description = "Public Sites Service URL")}
    )
@SecuritySchemes(value = {
    @SecurityScheme(
        name = "bearer_authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer")
})
public class OpenApiConfiguration {

  //Empty class
}