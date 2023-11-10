# Public Sites Service
## Public Sites - REST Services

[![Supported JVM Versions](https://img.shields.io/badge/JVM-11-brightgreen.svg?style=for-the-badge&logo=Java)](https://openjdk.java.net/install/)

Public Sites Service è parte della suite di servizi per la verifica delle informazioni sulla
Trasparenza dei siti web delle Pubbliche amministrazioni italiane.
 
## Public Sites Service

Public Sites Service è il componente che si occupa di gestire le informazioni principali relative agli enti
pubblici italiani ed in particolare i siti istituzionali.

Publis Sites Service mantiene nel proprio datastore locale le informazioni degli enti che possono essere 
inserite/aggiornate tramite gli OpenData di IndicePA, oppure inserite tramite appositi servizi endopoint REST.

Public Sites fornisce alcuni servizi REST utilizzabili in produzione per:

 - mostrare la lista degli enti presenti negli OpenData di IndicePA
 - inserire ed aggiornare all'interno del servizio le informazioni degli Enti tramite gli OpenData di IndicePA 
 - visualizzare i dati di un Ente
 - mostrare la lista degli Enti presenti nel servizio

I servizi saranno estesi per coprire tutte le funzionalità necessarie al crawling ed elaborazione dei siti web
delle PA Italiane.


## 👏 Come Contribuire 

E' possibile contribuire a questo progetto utilizzando le modalità standard della comunità opensource 
(issue + pull request) e siamo grati alla comunità per ogni contribuito a correggere bug e miglioramenti.

## 📄 Licenza

Public Sites Service è concesso in licenza GNU AFFERO GENERAL PUBLIC LICENSE, come si trova nel file
[LICENSE][l].

[l]: https://github.com/cnr-anac/public-sites-service/blob/master/LICENSE
