# Setup
Doorloop voor het begin van de workshop de volgende onderdelen.

## Inhoud
* [Stap 1: Installeer Java SE 8](#stap-1-installeer-java-se-8)
* [Stap 2: Installeer Postman](#stap-2-installeer-postman)
* [Stap 3: Clone github repository](#stap-3-clone-github-repository)
* [Stap 4: Importeer in IDE](#stap-4-importeer-in-ide)

## Stap 1: Installeer Java SE 8
Voor de workshop wordt gebruik gemaakt van Java 8 omdat Spring deze versie van Java vereist. Installeer Java wanneer je dat nog niet gedaan hebt via [oracle java downloads](https://www.oracle.com/technetwork/java/javase/downloads/index.html). De applicatie is getest met Java SE 8u202.

## Stap 2: Installeer Postman
Om de REST web service aan te kunnen roepen en informatie naar toe te kunnen sturen is een rest client nodig. Hier zijn verschillende oplossingen voor te vinden, maar voor deze workshop gebruiken we Postman welke ook in de lessen is behandeld. Installeren kan via [Postman downloads](https://www.getpostman.com/downloads) wanneer je dit nog niet hebt gedaan.

## Stap 3: Clone github repository
Omdat de git workshop na deze workshop wordt gehouden hieronder een kort stappenplan voor het clonen van de repository.
* Klik op de groene knop 'clone or download' op de beginpagina van het project op github

**Gebruik via terminal**

* Navigeer naar de locatie waar je de github repo binnen wilt halen, zoals bijvoorbeeld ```cd school/iac```
* Controleer eventueel met ```pwd``` of het bestandspad klopt
* Run ```git clone GITHUBURL.git```

**Gebruik van zip bestand**

* Pak het zip bestand uit op een gewenste locatie

## Stap 4: Importeer in IDE
Je kunt zelf kiezen welke IDE je gebruikt voor deze workshop, maar voor de setup wordt gebruik gemaakt van Intellij IDEA omdat deze ingebouwde support heeft voor Spring.

**Importeer maven project in Intellij IDEA**
* Open Intellij IDEA en klik op 'Importeer project' of wanneer je al een project hebt geopend in de menubalk op 'file > new > Project from Existing Sources' en selecteer de map die is aangemaakt bij de vorige stap
* Selecteer 'Import project from external model' en klik op maven
* Klik een aantal keer op next om de standaard import instellingen te gebruiken en het project in Intellij IDEA te importeren
* Controleer of er een Spring Boot configuratie verschijnt en of je deze kan runnen
* Wanneer er geen foutmeldingen bij het opstarten van Spring Boot in de console worden getoond kan je naar de url ```localhost:POORTNUMMER``` in de browser of Postman gaan. Het poortnummer verschijnt in een TomcatWebServer log in de console. Je krijgt vervolgens een 404 foutmelding terug omdat er nog geen api is aangemaakt welke in de workshop behandeld gaat worden.

**Problemen met starten van de applicatie?**
* Wanneer je Skype of een ander programma gebruikt zou het kunnen zijn dat de standaard poort 8080 al in gebruik is. Verander daarvoor de poort in het bestand ```resources/application.properties```

## Volgende stappen
Als bovenstaande stappen zijn gelukt ben je klaar met de voorbereiding voor de opdrachten. Klik hieronder op volgende om de opdrachten van de workshop door te lopen.

[Volgende: Introduction](02-introduction.md)