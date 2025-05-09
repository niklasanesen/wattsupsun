# Arkitektur

Velkommen til WattsUpSuns arkitektur guide! I denne guiden får du en oversikt over hvordan prosjektet er strukturert, hvilke kodestil- og arkitekturvalg vi har tatt, og generelt hvordan du kan sette deg inn og videreutvikle appen vår.

## MVVM

Dataflyten og filstrukturen vår er inspirert av [Model–view–viewmodel (MVVM)](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) arkitekturen. Dette hjelper oss blant annet å holde UI og API logikk separat, som er viktig for å holde kodebasen intuitiv og oversiktlig, spesielt når prosjektet blir mer og mer komplekst. Måten MVVM gjør dette er å dele dataflyten opp i 3:

### Model

Alt starter med en modell, som egentlig bare er en [objektorientert](https://en.wikipedia.org/wiki/Object-oriented_programming) måte å representere data på. En modell kan inneholde data (kalt felter/attributter) og kan utføre handlinger (kalt metoder/prosedyrer) kun spesifikt til seg selv, som bidrar til [høy kohesjon](<https://en.wikipedia.org/wiki/Cohesion_(computer_science)>). Her er et eksempel av en modell som vi skal ta gjennom hele dataflyten:

```kotlin
data class Point(
    val x: Int,
    val y: Int
)
```

### View

### ViewModel

## Filstruktur

- `model`: model
- `data`: viewmodel
- `ui`: view
