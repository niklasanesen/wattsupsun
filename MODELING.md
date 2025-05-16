# MODELLERING

## Introduksjon
Denne filen er strukturet med denne korte introen, begrunnelse for valg av diagrammer, og til slutt selve diagrammene/beskrivelsene.

## Usecases
1. Som huseier ønsker jeg å sjekke det økonomiske solcellepotensialet til mitt eget tak, slik at jeg kan spare penger på strømregningen. (VIKTIGSTE USECASE)
2. Som solcellepanelselger ønsker jeg å søke opp solcellepotensialet til Oslo lufthavn, slik at jeg har konkrete data å bruke, når jeg skal pitche solcellepanel til Avinor.
3. Som nysgjerrig nabo ønsker jeg å lagre data om naboens solcellepotensial, slik at jeg kan argumentere for hvorfor det er lurt for dem å investere i solcellepanel.

## Valg av diagrammer og begrunnelse
Vi har valgt disse diagrammene: 

- **Arkitekturskisse**: Gir en overordnet oversikt over de ulike APIene og hvordan data fra disse sendes til skjermene de skal til.

- **Sekvensdiagram**: For å beskrive samhandlingen mellom de ulike komponentene når bruker legger inn adresse, tegner takflate og velger panel.

- **Aktivitetsdiagram**: Denne er ment til å vise hendelsesflyten i det viktigste usecasete. Med fokus fra navigasjon i kartet til visning av data på den endelige skjermen. Dette er hovedfunksjonaliteten i appen vår.




---


## Arkitekturskisse

```mermaid
graph TD
    

    %% Homescreen
    HomeScreen --> HomeViewModel
    HomeScreen --> MapViewModel

    HomeViewModel --> |// App statedata som skal passes ved skjermbytte| CTX
    
    %% InputScreen
	InputScreen
    
    %% Infoscreen til viewmodels
    InfoScreen --> PVGISViewModel
    InfoScreen --> StromprisViewModel
    InfoScreen --> MetViewModel


    %% viewmodels til repo
    PVGISViewModel --> PVGISRepository
    StromprisViewModel --> StromprisRepository
    MetViewModel --> MetRepository


    %% repo til datasource
    PVGISRepository --> PVGISDatasource
    StromprisRepository --> StromprisDataSource
    MetRepository --> MetDataSource


    %% dataspurce til dataene
    PVGISDatasource --> |// Forventet strømproduksjonsdata| PVGISRes
    PVGISDatasource --> |// solinnstrålingsdata| DRCalcRes

    StromprisDataSource --> |// Liste med strømpris per time| StromprisRes
    MetDataSource --> |// værstasjoner med data hos MET| WeatherStation
    MetDataSource --> |// År med data hos MET| AllValidYears
    MetDataSource --> |// vær/klimadata fra met API levert som liste| WeatherList

    %% Styling for bobler
    class PVGISRes,DRCalcRes,StromprisRes,WeatherStation,AllValidYears,WeatherList,CTX circle;
    
    classDef circle stroke:#333,stroke-width:4px,rx:50%;
```

---

## Sekvensdiagram

```mermaid

sequenceDiagram

    actor Bruker
    participant APP
    participant MapBox
    participant PVGIS_API
    participant StromPris_API
    participant Met_API

    Bruker ->> APP: choose adress
    APP ->> MapBox: fetchCoords()
    MapBox ->> APP: (Asks user to draw roof dimensions on coords)

    Bruker ->> APP: draw rooftop
    Bruker ->> APP: click "Neste"

    MapBox -->> APP: calculateAreal(len, width)

    APP ->> PVGIS_API: fetchPVGISdata(koordinater)
    PVGIS_API -->> APP: return solinnstråling, forventet_stromproduksjon

    APP ->> StromPris_API: fetchStromPrisdata(koordinater)
    StromPris_API -->> APP: return dato, strompris

    APP ->> Met_API: fetchMetdata(koordinater)
    Met_API -->> APP: return diverse værdata, (snø, skydekke, temperatur)

    MapBox ->> APP: (Asks user to provide roofAngle, direction and paneltype)

    Bruker ->> APP: Write roofAngle and direction
    Bruker ->> APP: Choose paneltype
    Bruker ->> APP: click "Neste"

    

    alt success
        APP -->> Bruker: Calculate the data and display something meaningful.

    else alternativt: if failed to fetch

        PVGIS_API -->> APP: return 400 Bad Request

        StromPris_API -->> APP: return 400 Bad Request

        Met_API -->> APP: return 403 Forbidden

        APP -->> Bruker: show error where data is not fetched

    end


```

---

## Aktivitetsdiagram

```mermaid

    flowchart TD;

    Start1((Start1))
    VelgAddresse([Velg adresse])
    OppgiMål([Oppgi mål])
    TegnFlate([Tegn flate])
    LeggInnData([Legge inn data/tegning])

   
    VisSkjerm([Vis skjerm med solcellepanel data])
    metEnd(Viser metData)
    PVGISEnd(Viser PVGIS data)



    Start1 --> VelgAddresse
    VelgAddresse --> valg1
    valg1 --SKRIV--> OppgiMål
    valg1 --TEGN--> TegnFlate

    OppgiMål --> LeggInnData
    TegnFlate --> LeggInnData

    LeggInnData --> valg2

    valg2 --NESTE--> VisSkjerm
    valg2 --TILBAKE--> VelgAddresse

    VisSkjerm --> valg3{se data eller tilbake?}
    valg3 --TILBAKE--> LeggInnData
    
    valg3 --SE DATA--> valg4

    valg4 --PVGIS--> PVGISEnd
    valg4 --MET--> metEnd


    PVGISEnd --> valg5
    metEnd --> valg5

    valg5 --HJEMKNAPP--> VelgAddresse





   

    valg1{tegne eller skrive?}
    valg2{Neste eller tilbake?}
    valg3{se data eller tilbake?}
    valg4{PVGIS eller MET data?}
    valg5{Hjem igjen?}
```
