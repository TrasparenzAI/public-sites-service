# Public Sites Service
## Public Sites - REST Services

[![Supported JVM Versions](https://img.shields.io/badge/JVM-17-brightgreen.svg?style=for-the-badge&logo=Java)](https://openjdk.java.net/install/)

Public Sites Service è parte della suite di servizi per la verifica delle informazioni sulla
Trasparenza dei siti web delle Pubbliche amministrazioni italiane.
 
## Public Sites Service

Public Sites Service è il componente che si occupa di gestire le informazioni principali relative agli enti
pubblici italiani ed in particolare i siti istituzionali.

Public Sites Service mantiene nel proprio datastore locale le informazioni degli enti che possono essere 
inserite/aggiornate tramite gli OpenData di IndicePA, oppure inserite tramite appositi servizi endopoint REST.

Public Sites fornisce alcuni servizi REST utilizzabili in produzione per:

 - mostrare la lista degli enti presenti negli OpenData di IndicePA
 - inserire ed aggiornare all'interno del servizio le informazioni degli Enti tramite gli OpenData di IndicePA 
 - visualizzare i dati di un Ente
 - geolocalizzare gli Enti italiani tramite il servizio Nominatim di 
   OpenStreetMap
 - mostrare la lista paginata degli Enti presenti nel servizio, con possibilità di filtrarli per 
   codiceCategoria, codiceFiscaleEnte, codiceIpa, denominazioneEnte
 - inserire, aggiornare e cancellare le informazioni degli Enti all'interno del servizio (direttamente senza passare da IndicePA)

I servizi REST sono documentati tramite OpenAPI consultabile all'indirizzo 
**/swagger-ui/index.html**.
L'OpenAPI del servizio di staging è disponibile all'indirizzo 
https://dica33.ba.cnr.it/public-sites-service/swagger-ui/index.html.

Il servizio sincronizza e rendere disponibili via REST anche le informazioni 
dei comuni italiani, prelevendo ogni notte il CSV dal sito dell'ISTAT dei comuni
e aggiornando questo info dentro il servizio stesso. Le info dei comuni servono
anche per effettuare una geolocalizzazione più precisa degli enti, che su 
IndicePA sono classificati solamente tramite il codice catastale del comune.

L'aggiornamento dei dati locali al servizio Public Sites Service tramite
IndicePA avviene ogni mattina alle 6:30.
L'aggiornamento dei dati locali al servizio Public Sites Service tramite il CSV
di ISTAT avviene ogni mattina alle 6:40.

### Sicurezza

Gli endpoint REST di questo servizio sono protetti tramite autenticazione OAuth con Bearer Token.
E' necessario configurare l'idp da utilizzare per validare i token OAuth tramite le due proprietà
mostrare nell'esempio seguente:

```
    - spring.security.oauth2.resourceserver.jwt.issuer-uri=https://dica33.ba.cnr.it/keycloak/realms/trasparenzai
    - spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://dica33.ba.cnr.it/keycloak/realms/trasparenzai/protocol/openid-connect/certs
```

Per l'accesso in HTTP GET all'API è sufficiente essere autenticati, per gli endpoint accessibili
con PUT/POST/DELETE è necessario oltre che essere autenticati che il token OAuth contenga un 
role ADMIN o SUPERUSER.

# <img src="https://www.docker.com/wp-content/uploads/2021/10/Moby-logo-sm.png" width=80> Startup

#### _Per avviare una istanza del result-service con postgres locale_

Il result-service può essere facilmente installato via docker compose su server Linux utilizzando il file 
docker-compose.yml presente in questo repository.

Accertati di aver installato docker e il plugin di docker `compose` dove vuoi installare il public-sites-service e in seguito
esegui il comando successivo per un setup di esempio.

```
curl -fsSL https://raw.githubusercontent.com/cnr-anac/public-sites-service/main/first-setup.sh -o first-setup.sh && sh first-setup.sh
```

Collegarsi a http://localhost:8080/swagger-ui/index.html per visualizzare la documentazione degli endpoint REST presenti nel servizio. 

## Backups

Il servizio mantiene le informazioni relative alla configurazione nel db postgres, quindi è opportuno fare il backup
del database a scadenza regolare. Nel repository è presente un file di esempio [backups.sh](https://github.com/cnr-anac/public-sites-service/blob/main/backups.sh) per effettuare i backup.

All'interno dello script backups.sh è necessario impostare il corretto path dove si trova il docker-compose.yml del progetto, tramite la
variabile `SERVICE_DIR`.

## 👏 Come Contribuire 

E' possibile contribuire a questo progetto utilizzando le modalità standard della comunità opensource 
(issue + pull request) e siamo grati alla comunità per ogni contribuito a correggere bug e miglioramenti.

## 📄 Licenza

Public Sites Service è concesso in licenza GNU AFFERO GENERAL PUBLIC LICENSE, come si trova nel file
[LICENSE][l].

[l]: https://github.com/cnr-anac/public-sites-service/blob/master/LICENSE
