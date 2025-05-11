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

View representerer UI-delen av appen, altså det brukeren ser og interakterer med. Vi bruker [Jetpack Compose](https://developer.android.com/compose) for å simplet kunne lage skjermer ved bruk av composable:

```kotlin
@Composable
fun PointScreen(viewModel: PointViewModel) {
    val point by viewModel.point.collectAsState()
    Text("Punkt: (${point.x}, ${point.y})")
}
```

### ViewModel

ViewModel er selve data staten, og fungerer som limet mellom Model og View. Den starter ved å hente data i samme format som modellen i det som kalles en `datasource`. I vårt tilfelle gjør vi API kall til tredjeparts datakilder ved bruk av [Ktor](https://ktor.io):

```kotlin
class PointDataSource {
    suspend fun getPointData(): Point? {
        val url = "https://example.com/point-data"
        return client.get(url).body()
    }
}
```

Så skjer alt av eventuell logikk og prosessering av dataen i det som kalles et `repository`:

```kotlin
class PointRepository {
    suspend fun getPoint(): Point? {
        var point = dataSource.getPointData()
        // some logic
        return point
    }
}
```

Til slutt lager vi en `viewmodel` som håndterer datatillstanden, og gjør at viewen kan enkelt hente dataen:

```kotlin
class PointViewModel() : ViewModel() {
    private val _point = MutableStateFlow<Point?>(null)
    val point: StateFlow<Point?> = _point.asStateFlow()

    init {
        viewModelScope.launch {
            _point.value = repository.getPoint()
        }
    }

    private suspend fun fetchPoint() {
    }
}
```

## Filstruktur

- `model`: model
- `data`: viewmodel
- `ui`: view
