# Arkitektur

Velkommen til WattsUpSuns arkitektur guide! I denne guiden får du en oversikt over hvordan prosjektet er strukturert, hvilke kodestil- og arkitekturvalg vi har tatt, og generelt hvordan du kan sette deg inn og videreutvikle appen vår.

## MVVM

Dataflyten og filstrukturen vår er inspirert av [Model–view–viewmodel (MVVM)](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) arkitekturen. Dette hjelper oss blant annet å holde UI og API logikk separat, som er viktig for å holde kodebasen intuitiv og oversiktlig, spesielt når prosjektet blir mer og mer komplekst.

## Filstruktur

- `model`: model
- `data`: viewmodel
- `ui`: view
