
```mermaid
classDiagram
    
    %% ctx
    class Ctx {
        +Point? coords
        +Input? input
        +Areal? areal
        +String? address
        +StromDistrikt? stromdistrikt
    }

    %% metres
    class WeatherStation {
        +String name
        +String id
    }

    class ValidYears {
        +Instant validTo
        +Instant validFrom
        +String elementId
        +WeatherStation weatherStation
    }

    class AllValidYears {
        +List<ValidYears> validYearsList
    }

    class WeatherData {
        +String elementId
        +Double value
        +String unit
        +WeatherStation weatherStation
        +Instant timeMeasured
    }

    class WeatherList {
        +List<WeatherData> dataList
    }

    class AggregatedData {
        +List<Pair<Instant, WeatherList>> dailyObservation
    }

    class Coordinates {
        +Double latitude
        +Double longitude
    }



    class PVGISRes {
        +String location
        +double solarPotential
        +double efficiency
    }

    class DRCalcRes {
        +double dailyEnergyOutput
        +double totalSavings
    }

    class StromprisRes {
        +double currentPrice
        +String currency
    }

    class MetDatasource {
        +fetchMetData(): WeatherList
    }

    class PVGISDatasource {
        +fetchPVGISData(): PVGISRes
    }

    class StromprisDatasource {
        +fetchStromprisData(): StromprisRes
    }

    class MetRepository {
        +getMetData(): WeatherList
    }

    class PVGISRepository {
        +getPVGISData(): PVGISRes
    }

    class StromprisRepository {
        +getStromprisData(): StromprisRes
    }

    class MetViewModel {
        +LiveData<WeatherList> metData
        +loadMetData()
    }

    class PVGISViewModel {
        +LiveData<PVGISRes> pvgisData
        +loadPVGISData()
    }

    class StromprisViewModel {
        +LiveData<StromprisRes> stromprisData
        +loadStromprisData()
    }

    %% screens
    class HomeScreen {
        +onCreate()
        +showMetData()
        +showPVGISData()
        +showStromprisData()
    }

    class InfoScreen {
        +onCreate()
        +showMetData()
        +showPVGISData()
        +showStromprisData()
    }

    class InputScreen {
        +onCreate()
        +showMetData()
        +showPVGISData()
        +showStromprisData()
    }



    AllValidYears --> MetRepository : Uses
    WeatherStation --> MetRepository : Uses
    WeatherList --> MetRepository : Uses
    PVGISRes --> PVGISRepository : Uses
    DRCalcRes --> PVGISRepository : Uses
    StromprisRes --> StromprisRepository : Uses

    MetRepository --> MetDatasource : Calls
    PVGISRepository --> PVGISDatasource : Calls
    StromprisRepository --> StromprisDatasource : Calls

    MetViewModel --> MetRepository : calls
    PVGISViewModel --> PVGISRepository : calls
    StromprisViewModel --> StromprisRepository : calls


    %% InfoScreen
    InfoScreen --> MetViewModel : Observes
    InfoScreen --> PVGISViewModel : Observes
    InfoScreen --> StromprisViewModel : Observes

    %% inputscreen
    InputScreen --> MetViewModel : Observes
    InputScreen --> PVGISViewModel : Observes
    InputScreen --> StromprisViewModel : Observes
