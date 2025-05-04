<p align="center">
  <img src="app/src/main/res/drawable/logo_circle.png" width="200" height="200" alt="logo">
</p>
<h1 align="center">WattsUpSun</h1>
<p align="center">En solcellepanel app fra prosjektarbeidet i IN2000 - Vår 2025</p>
<p align="center">
    <i>~ Laget av Kristin, Niklas, Jonatan, Danial, Affaq og Kavin ~</i>
</p>

WattsUpSun er en app designet for huseiere som ønsker å vurdere lønnsomheten av solcellepaneler. Appen lar huseier enkelt slå opp adresse, tegne takflate og legge inn areal/vinkel/retning for å beregne forventet strømproduksjon.

## Arkitektur

Dataflyten og filstrukturen vår er inspirert av [Model–view–viewmodel](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) arkitekturen, som blant annet hjelper oss å holde UI og API logikk separat. Dette er viktig for å holde kodebasen enkel og oversiktig når prosjektet blir mer komplekst.

<img src="app/src/main/res/drawable/arkitektur.png" alt="arkitektur">
