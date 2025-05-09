# Architecture

Dette dokumentet er tiltenkt for personer som skal videreutvikle appen, sette seg inn i den og fortsette å jobbe med “kodestil”/praksis som er brukt.

## MVVM

Dataflyten og filstrukturen vår er inspirert av Model–view–viewmodel arkitekturen, noe som blant annet hjelper oss å holde UI og API logikk separat. Dette er viktig for å holde kodebasen enkel og oversiktlig når prosjektet blir mer komplekst.

## Filstruktur

- `model`: model
- `data`: viewmodel
- `ui`: view
