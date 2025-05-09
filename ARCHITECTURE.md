# Arkitektur

Velkommen til WattsUpSuns arkitektur guide! I denne guiden får du en oversikt over hvordan prosjektet er strukturert, hvilke kodestil- og arkitekturvalg vi har tatt, og generelt hvordan du kan sette deg inn og videreutvikle appen vår.

## MVVM

Dataflyten og filstrukturen vår er inspirert av Model–view–viewmodel arkitekturen, noe som blant annet hjelper oss å holde UI og API logikk separat. Dette er viktig for å holde kodebasen enkel og oversiktlig når prosjektet blir mer komplekst.

## Filstruktur

- `model`: model
- `data`: viewmodel
- `ui`: view
