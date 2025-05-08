
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

