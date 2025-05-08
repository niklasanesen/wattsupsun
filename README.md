<p align="center">
  <img src="app/src/main/res/drawable/logo_circle.png" width="200" height="200" alt="logo">
</p>
<h1 align="center">WattsUpSun</h1>
<p align="center">En solcellepanel app fra prosjektarbeidet i IN2000 - Vår 2025</p>
<p align="center">
    <i>~ Laget av Kristin, Niklas, Jonatan, Danial, Affaq og Kavin ~</i>
</p>
<br/>

WattsUpSun er en app designet for huseiere som ønsker å vurdere lønnsomheten av solcellepaneler. Appen lar huseier enkelt slå opp adresse, tegne takflate og legge inn areal/vinkel/retning for å beregne forventet strømproduksjon.

## Installasjon

For å laste ned og kjøre appen trenger du [git](https://git-scm.com) og [Android Studio](https://developer.android.com/studio). Når begge er installert, kan du klone prosjektet ved å kjøre følgende kommando i terminalen:

```
git clone https://github.uio.no/IN2000-V25/team-43.git
```

Deretter kan du åpne prosjektet i Android Studio, og kjøre appen i en emulator med API-level 34.

## Datakilder

Korrekt og oppdatert data er essensielt for å få mest mulig nøyaktig beregning, derfor henter vi data fra flere pålitelige kilder:

- Værdata fra [Frost](https://frost.met.no).
- Strømpriser fra [HvaKosterStrømmen](https://www.hvakosterstrommen.no/strompris-api).
- Kart- og lokasjonsdata fra [Mapbox](https://mapbox.com).
- Solstrålingsdata fra [PVGIS](https://joint-research-centre.ec.europa.eu/photovoltaic-geographical-information-system-pvgis_en) for å sammenlikne med [Frost](https://frost.met.no).

## Tillatelser

For å få mest mulig ut av appen og all funksjoneliteten den tilbyr, må appen ha tilgang til visse systemfunksjoner som er står definert i `AndroidManifest.xml` filen:

- `INTERNET`. Som nevnt tidligere henter appen data fra diverse datakilder, og trenger naturligvis internett-tilgang for å gjøre det.
- `CAMERA` og `READ_MEDIA_IMAGES`. Lar bruker legge til et profilbilde, enten med kameraet eller fra galleriet.
- `READ_EXTERNAL_STORAGE` og `WRITE_EXTERNAL_STORAGE`. Gjør at appen kan lagre informasjon lokalt på brukerens enhet, som gjøres blant annet med profil- og adressedata.

## Credits

Takk til Fredrik Løvoll som hjalp oss med `reverseGeocode` koden.
