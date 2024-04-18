package it.cnr.anac.transparency.companies.geo;

import it.cnr.anac.transparency.companies.models.Company;
import it.cnr.anac.transparency.companies.repositories.CompanyRepository;
import it.cnr.anac.transparency.companies.repositories.MunicipalityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GeoService {

  private final CompanyRepository companyRepository;
  private final MunicipalityRepository municipalityRepository;

  /**
   * Aggiorna il comune_id nell'entity company facendo il match del comune tramite
   * il suo codice catastale.
   *
   * @return il numero di company aggiornate.
   */
  public int fixCompanyWithoutMunicipality() {
    int companiesUpdated = 0;
    for (Company company : companyRepository.findWithoutMunicipality()) {
      if (company.getCodiceCatastaleComune() != null) {
        company.setComune(municipalityRepository.findByCodiceCatastale(company.getCodiceCatastaleComune()).orElse(null));
        companyRepository.save(company);
        log.info("Aggiornato riferimento al comune per ente {}", company);
        companiesUpdated++;
      }
    }
    return companiesUpdated;
  }

}