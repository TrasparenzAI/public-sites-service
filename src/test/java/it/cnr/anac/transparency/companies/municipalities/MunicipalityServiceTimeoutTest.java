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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Duration;

import org.junit.jupiter.api.Test;

class MunicipalityServiceTimeoutTest {

  @Test
  void downloadCsvVaInTimeoutSeIlServerNonRisponde() throws Exception {
    //Server che accetta la connessione ma non invia mai la risposta
    try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
      Thread acceptor = Thread.ofVirtual().start(() -> {
        try (Socket ignored = server.accept()) {
          Thread.sleep(Duration.ofSeconds(10));
        } catch (Exception e) {
          //chiusura del server a fine test
        }
      });

      MunicipalityService service = new MunicipalityService(null, null, null);
      service.istatCsvUrl = "http://localhost:" + server.getLocalPort() + "/comuni.csv";
      service.istatCsvEncoding = "ISO-8859-1";
      service.istatCsvConnectTimeout = Duration.ofSeconds(1);
      service.istatCsvReadTimeout = Duration.ofMillis(500);

      assertTimeoutPreemptively(Duration.ofSeconds(5), () ->
          assertThrows(SocketTimeoutException.class, service::getMunicipalitiesFromCsv));
      acceptor.interrupt();
    }
  }

}
