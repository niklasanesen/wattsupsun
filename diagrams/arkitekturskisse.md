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
    MetDataSource --> |// vær/klimadata fra met API| MetRes

    %% Styling for bobler
    class PVGISRes,DRCalcRes,StromprisRes,MetRes,CTX circle;
    
    classDef circle stroke:#333,stroke-width:4px,rx:50%;

 





