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
    MapBox -->> APP: return koordinater

    
    Bruker ->> APP: click on retrieve weatherInfo button

    APP ->> PVGIS_API: fetchPVGISdata(koordinater)
    PVGIS_API -->> APP: return solinnstråling, forventet_stromproduksjon

    APP ->> StromPris_API: fetchStromPrisdata(koordinater)
    StromPris_API -->> APP: return dato, strompris

    APP ->> Met_API: fetchMetdata(koordinater)
    Met_API -->> APP: return diverse værdata, (snø, skydekke, temperatur)

    

    alt success
        APP -->> Bruker: Show retrived data to user

    else alternativt: if failed to fetch

        PVGIS_API -->> APP: return 400 Bad Request

        StromPris_API -->> APP: return 400 Bad Request

        Met_API -->> APP: return 403 Forbidden

        APP -->> Bruker: show errorscreen

    end




    



```