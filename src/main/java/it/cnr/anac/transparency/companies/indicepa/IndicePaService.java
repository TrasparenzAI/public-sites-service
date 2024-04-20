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

package it.cnr.anac.transparency.companies.indicepa;

import it.cnr.anac.transparency.companies.CompanyService;
import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.models.CompanySource;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.v1.dto.CompanyShowDto;
import it.cnr.anac.transparency.companies.v1.dto.CompanyMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service per l'interrogazione del indicePA e l'aggiornamento dei dati
 * locali a partire dall'indicePA. 
 *
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class IndicePaService {

  @Value("${transparency.indice-pa.opendata.resource-id}")
  private String indicePaResourceId;
  private final IndicePaClient indicePaClient;
  private final CompanyMapper mapper;
  private final CompanyRepository repo;
  private final CompanyService companyService;

  /**
   * @return la lista delle aziende pubbliche presenti nel IndicePA
   */
  public List<CompanyShowDto> getCompaniesFromIndicePa(Optional<Integer> limit) {
    String indicePaSql = 
        String.format("SELECT * from \"%s\"", indicePaResourceId);

    if (limit.isPresent()) {
      indicePaSql += String.format(" LIMIT %d", limit.get());
    }

    return indicePaClient.publicCompanies(indicePaSql)
        .getResult().getRecords().stream().map(mapper::convert).collect(Collectors.toList());
  }

  public int updateCompaniesFromIndicePa(Optional<LocalDate> lastUpdateFrom) {
    int companiesUpdated = 0;
    val allIndicePaCompanies = getCompaniesFromIndicePa(Optional.empty());
    var indicePaCompanies = allIndicePaCompanies;

    if (lastUpdateFrom.isPresent()) {
      indicePaCompanies = 
          indicePaCompanies.stream()
            .filter(c -> c.getDataAggiornamento() != null 
              && c.getDataAggiornamento().isAfter(lastUpdateFrom.get())).toList();
    }
    val localCompanies = repo.findBySorgente(CompanySource.indicePA);

    val indicePaCompaniesMap = 
        indicePaCompanies.stream().collect(Collectors.toMap(CompanyShowDto::getCodiceIpa, Function.identity()));
    val localCompaniesMap = 
        localCompanies.stream().collect(Collectors.toMap(Company::getCodiceIpa, Function.identity()));

    List<CompanyShowDto> indicePaNotInLocal = indicePaCompaniesMap.keySet().stream()
        .filter(element -> !localCompaniesMap.keySet().contains(element))
        .map(codiceIpa -> indicePaCompaniesMap.get(codiceIpa))
        .collect(Collectors.toList());

    companiesUpdated += insertCompanies(indicePaNotInLocal);

    List<CompanyShowDto> indicePaInLocal = indicePaCompaniesMap.keySet().stream()
        .filter(element -> localCompaniesMap.keySet().contains(element))
        .map(codiceIpa -> indicePaCompaniesMap.get(codiceIpa))
        .collect(Collectors.toList());

    companiesUpdated += updateCompanies(indicePaInLocal);

    //Per la disattivazione degli enti presenti in locale è necessario verificare che non siano
    //più presenti in indicePA, considerando tutti gli enti in indicePA non solo quelli filtrati in
    //precedenza per data. 
    //Inoltre vengono disabilitati solo gli enti pubblici che avevano come "sorgente" indicePA.
    val allIndicePaCompaniesMap = 
        allIndicePaCompanies.stream().collect(Collectors.toMap(CompanyShowDto::getCodiceIpa, Function.identity()));
    List<Company> localNotInIndicePaToDisable = localCompaniesMap.keySet().stream()
        .filter(element -> !allIndicePaCompaniesMap.keySet().contains(element))
        .map(codiceIpa -> localCompaniesMap.get(codiceIpa))
        .filter(company -> company.getDataCancellazione() == null)
        .collect(Collectors.toList());

    companiesUpdated += disableCompanies(localNotInIndicePaToDisable);

    return companiesUpdated;
  }

  private int insertCompanies(List<CompanyShowDto> companies) {
    int companyInserted = 0;
    for (CompanyShowDto companyDto : companies) {
      val company = companyService.createCompany(companyDto);
      company.setSorgente(CompanySource.indicePA);
      repo.save(company);
      companyInserted++;
      log.info("Inserito nuovo ente pubblico da indice PA -> {}", company);
    }
    return companyInserted;
  }

  private int updateCompanies(List<CompanyShowDto> companies) {
    int companyUpdated = 0;
    for (CompanyShowDto companyDto : companies) {
      Company company = 
          repo.findByCodiceIpa(companyDto.getCodiceIpa())
            .orElseThrow(() -> new RuntimeException(
                String.format("Ente pubblico non trovato con codiceIPA %s", companyDto.getCodiceIpa())));
      if (!areEqual(company, companyDto)) {
        company = companyService.updateCompany(company, companyDto);
        repo.save(company);
        log.info("Aggiornato ente pubblico da indice PA -> {}", company);
        companyUpdated++;
      }
    }
    return companyUpdated;
  }

  private int disableCompanies(List<Company> companies) {
    log.debug("Disabilito {} enti pubblici presenti in locale ma non in indicePA", companies.size());
    int companiesDisabled = 0;
    for (Company company : companies) {
      company.setDataCancellazione(LocalDate.now());
      repo.save(company);
      companiesDisabled++;
      log.info("Impostata data di cancellazione a ente pubblico {}", company);
    }
    return companiesDisabled;
  }

  private boolean areEqual(Company company, CompanyShowDto companyDto) {
    return Objects.equals(company.getAcronimo(), companyDto.getAcronimo()) 
        && Objects.equals(company.getCodiceCategoria(), companyDto.getCodiceCategoria())
        && Objects.equals(company.getCodiceFiscaleEnte(), companyDto.getCodiceFiscaleEnte())
        && Objects.equals(company.getCodiceIpa(), companyDto.getCodiceIpa())
        && Objects.equals(company.getCodiceNatura(), companyDto.getCodiceNatura())
        && Objects.equals(company.getDenominazioneEnte(), companyDto.getDenominazioneEnte())
        && Objects.equals(company.getSitoIstituzionale(), companyDto.getSitoIstituzionale())
        && Objects.equals(company.getTipologia(), companyDto.getTipologia())

        && Objects.equals(company.getCodiceComuneIstat(), companyDto.getCodiceComuneIstat())
        && Objects.equals(company.getCodiceCatastaleComune(), companyDto.getCodiceCatastaleComune())
        && Objects.equals(company.getCap(), companyDto.getCap())
        && Objects.equals(company.getIndirizzo(), companyDto.getIndirizzo())

        && Objects.equals(company.getNomeResponsabile(), companyDto.getNomeResponsabile())
        && Objects.equals(company.getCognomeResponsabile(), companyDto.getCognomeResponsabile())
        && Objects.equals(company.getTitoloResponsabile(), companyDto.getTitoloResponsabile())

        && Objects.equals(company.getMail1(), companyDto.getMail1())
        && Objects.equals(company.getTipoMail1(), companyDto.getTipoMail1())
        && Objects.equals(company.getMail2(), companyDto.getMail2())
        && Objects.equals(company.getTipoMail2(), companyDto.getTipoMail2())
        && Objects.equals(company.getMail3(), companyDto.getMail3())
        && Objects.equals(company.getTipoMail3(), companyDto.getTipoMail3())
        && Objects.equals(company.getMail4(), companyDto.getMail4())
        && Objects.equals(company.getTipoMail4(), companyDto.getTipoMail4())
        && Objects.equals(company.getMail5(), companyDto.getMail5())
        && Objects.equals(company.getTipoMail5(), companyDto.getTipoMail5());
  }
}