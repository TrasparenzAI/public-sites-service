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

import com.opencsv.bean.CsvToBeanBuilder;
import it.cnr.anac.transparency.companies.models.Municipality;
import it.cnr.anac.transparency.companies.repositories.MunicipalityRepository;
import it.cnr.anac.transparency.companies.v1.dto.MunicipalityMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MunicipalityService {

  @Value("${transparency.municipalities.csv.url}")
  String istatCsvUrl;
  @Value("${transparency.municipalities.csv.encoding}")
  String istatCsvEncoding;

  private final MunicipalityRepository repo;
  private final MunicipalityMapper mapper;

  public List<MunicipalityCsvDto> getMunicipalitiesFromCsv() throws IOException {
    URL csvURL = new URL(istatCsvUrl);
    BufferedReader in = new BufferedReader(new InputStreamReader(csvURL.openStream(), istatCsvEncoding));
    List<MunicipalityCsvDto> municipalities = new CsvToBeanBuilder<MunicipalityCsvDto>(in)
        .withType(MunicipalityCsvDto.class)
        .withSeparator(';')
        .build().parse();
    //La prima riga è l'intestazione, che evitiamo
    return municipalities.stream().skip(1).collect(Collectors.toList());
  }

  public int updateMunicipalitiesFromIstat() throws IOException {
    int municipalitiesUpdated = 0;
    var istatMunicipalities = getMunicipalitiesFromCsv();

    val localMunicipalities = repo.findAll();

    val istatMunicipalitiesMap = 
        istatMunicipalities.stream().collect(Collectors.toMap(MunicipalityCsvDto::getCodiceCatastale, Function.identity()));
    val localMunicipalitiesMap = 
        localMunicipalities.stream().collect(Collectors.toMap(Municipality::getCodiceCatastale, Function.identity()));

    List<MunicipalityCsvDto> municipalitiesNotInLocal = istatMunicipalitiesMap.keySet().stream()
        .filter(element -> !localMunicipalitiesMap.keySet().contains(element))
        .map(codiceIpa -> istatMunicipalitiesMap.get(codiceIpa))
        .collect(Collectors.toList());

    municipalitiesUpdated += insertMunicipalities(municipalitiesNotInLocal);

    List<MunicipalityCsvDto> municipalitiesInLocal = istatMunicipalitiesMap.keySet().stream()
        .filter(element -> localMunicipalitiesMap.keySet().contains(element))
        .map(codiceIpa -> istatMunicipalitiesMap.get(codiceIpa))
        .collect(Collectors.toList());

    municipalitiesUpdated += updateMunicipalities(municipalitiesInLocal);

    //Per la disattivazione dei comuni presenti in locale è necessario verificare che non siano
    //più presenti in ISTAT, considerando tutti i comuni in ISTAT non solo quelli filtrati in
    //precedenza per data. 
    //Inoltre vengono disabilitati solo gli enti pubblici che avevano come "sorgente" indicePA.
    val allIstatMunicipalitiesMap = 
        istatMunicipalities.stream().collect(Collectors.toMap(MunicipalityCsvDto::getCodiceCatastale, Function.identity()));
    List<Municipality> localNotInIstatToDisable = localMunicipalitiesMap.keySet().stream()
        .filter(element -> !allIstatMunicipalitiesMap.keySet().contains(element))
        .map(codiceIpa -> localMunicipalitiesMap.get(codiceIpa))
        .filter(company -> company.getDataCancellazione() == null)
        .collect(Collectors.toList());

    municipalitiesUpdated += disableMunicipalities(localNotInIstatToDisable);

    return municipalitiesUpdated;
  }

  private int insertMunicipalities(List<MunicipalityCsvDto> municipalities) {
    int municipalitiesInserted = 0;
    for (MunicipalityCsvDto municipalityDto : municipalities) {
      Municipality municipality = new Municipality();
      mapper.update(municipality, municipalityDto);
      repo.save(municipality);
      municipalitiesInserted++;
      log.info("Inserito nuovo comune da dati ISTAT -> {}", municipality);
    }
    return municipalitiesInserted;
  }

  private int updateMunicipalities(List<MunicipalityCsvDto> municipalities) {
    int companyUpdated = 0;
    for (MunicipalityCsvDto municipalityDto : municipalities) {
      Municipality municipality = 
          repo.findByCodiceCatastale(municipalityDto.getCodiceCatastale())
            .orElseThrow(() -> new RuntimeException(
                String.format("Comune non trovato con codice catastalte %s", municipalityDto.getCodiceCatastale())));
      if (!areEqual(municipality, municipalityDto)) {
        mapper.update(municipality, municipalityDto);
        repo.save(municipality);
        log.info("Aggiornato comune da dati ISTAT -> {}", municipality);
        companyUpdated++;
      }
    }
    return companyUpdated;
  }

  private int disableMunicipalities(List<Municipality> municipalities) {
    log.debug("Disabilito {} comuni presenti in locale ma non in dati ISTAT", municipalities.size());
    int municipalitiesDisabled = 0;
    for (Municipality municipality : municipalities) {
      municipality.setDataCancellazione(LocalDate.now());
      repo.save(municipality);
      municipalitiesDisabled++;
      log.info("Impostata data di cancellazione a comune {}", municipality);
    }
    return municipalitiesDisabled;
  }

  private boolean areEqual(Municipality municipality, MunicipalityCsvDto municipalityDto) {
    return Objects.equals(municipality.getCodiceCatastale(), municipalityDto.getCodiceCatastale()) 
        && Objects.equals(municipality.getCodiceComune(), municipalityDto.getCodiceComune())
        && Objects.equals(municipality.getCodiceRegione(), municipalityDto.getCodiceRegione())
        && Objects.equals(municipality.getCodiceRipartizioneGeografica(), municipalityDto.getCodiceRipartizioneGeografica())
        && Objects.equals(municipality.getDenominazione(), municipalityDto.getDenominazione())
        && Objects.equals(municipality.getDenominazioneAltraLingua(), municipalityDto.getDenominazioneAltraLingua())
        && Objects.equals(municipality.getCapoluogo(), municipalityDto.getCapoluogo())
        && Objects.equals(municipality.getDenominazioneRegione(), municipalityDto.getDenominazioneRegione())
        && Objects.equals(municipality.getDenominazioneUnitaSovracomunale(), municipalityDto.getDenominazioneUnitaSovracomunale())
        && Objects.equals(municipality.getRipartizioneGeografica(), municipalityDto.getRipartizioneGeografica())
        && Objects.equals(municipality.getSiglaAutomobilistica(), municipalityDto.getSiglaAutomobilistica());
  }

}