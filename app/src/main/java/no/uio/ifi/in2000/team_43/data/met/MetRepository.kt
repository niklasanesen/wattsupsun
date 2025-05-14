package no.uio.ifi.in2000.team_43.data.met

import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.utils.helperfuctions.met.formatDate
import no.uio.ifi.in2000.team_43.utils.helperfuctions.met.getMostRecentStation
import java.time.Duration
import java.time.Instant
import java.time.Month
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class MetRepository(private val metDataSource: MetDataSource = MetDataSource(APIClient.client)) {
    suspend fun getWeatherStation(coordinates: Coordinates, nearestMaxCount: Int): WeatherStation? {
        return metDataSource.getWeatherStationData(coordinates, nearestMaxCount)
    }

    suspend fun getValidTime(weatherStation: WeatherStation): ValidYears? {
        val weatherElements: List<String> = listOf(
            "mean(cloud_area_fraction P1D)",
            "mean(air_temperature P1D)",
            "mean(surface_snow_thickness P1M)",
            "mean(surface_downwelling_shortwave_flux_in_air PT1H)"
        )

        val mostRecentTimes = weatherElements.map { weatherElement ->
            val allTimesForElement = metDataSource.getStartTime(weatherStation, weatherElement)

            val validDate = allTimesForElement?.validYearsList?.takeIf { it.isNotEmpty() }
                ?.let { getMostRecentStation(allTimesForElement) }

            if (validDate == null) {
                println("Valid date was null for element: $weatherElement")
                return null
            }

            validDate
        }

        if (mostRecentTimes.size < weatherElements.size) {
            println("Not all weather elements returned data.")
            return null
        }

        val latestDateObj = mostRecentTimes.minByOrNull { it.validTo }
        val latestDate = latestDateObj?.validTo

        if (latestDate != null) {

            var overlappingCount =  mostRecentTimes.count {
                latestDate >= it.validFrom && latestDate <= it.validTo
            }

            val localDate = latestDate.atZone(ZoneOffset.UTC).toLocalDate()
            val newLocalDate = localDate.minusYears(1)
            val earliestDate = newLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant()

            //Debugging line to check that year interval is correct
            println("earliest date is: $latestDate and it is valid until: ${newLocalDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()}")

            when(overlappingCount) {

                4 -> {
                    return ValidYears(
                        validTo = latestDate,
                        validFrom = earliestDate,
                        elementId = "success",
                        weatherStation = weatherStation
                    )
                }

                3 -> {
                    if (latestDate >= Instant.parse("2005-01-01T00:00:00Z")) {
                        val badObject = mostRecentTimes.find { it.validFrom > latestDate }

                        return ValidYears(
                            validFrom = earliestDate,
                            validTo = latestDate,
                            elementId = badObject!!.elementId,
                            weatherStation = weatherStation
                        )
                    }
                }

                else -> {
                    val badObject = mostRecentTimes.find { it.validTo == latestDate }
                    val newMostRecentTimes = mostRecentTimes.filter { it.validFrom > latestDate }

                    val secondLatestDate = newMostRecentTimes.minByOrNull { it.validTo }?.validTo

                    if (secondLatestDate != null) {
                        val secondOverlappingCount = newMostRecentTimes.count {
                            secondLatestDate >= it.validFrom && secondLatestDate <= it.validTo
                        }

                        val secondLocalDate = secondLatestDate.atZone(ZoneOffset.UTC).toLocalDate()
                        val newSecondLocalDate = secondLocalDate.minusYears(1)
                        val secondEarliestDate = newSecondLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant()

                        if (secondOverlappingCount == 3) {
                            return ValidYears(
                                validFrom = secondEarliestDate,
                                validTo = secondLatestDate,
                                elementId = badObject!!.elementId,
                                weatherStation = weatherStation
                            )
                        }
                    }
                }
            }
        }

        return null
    }

    suspend fun getValidTimeOneObject(weatherStation: WeatherStation, elementId: String, startDate: Instant, endDate: Instant): ValidYears? {
        val allTimes = metDataSource.getStartTime(weatherStation, elementId)

        val mostRecent = allTimes?.validYearsList?.takeIf { it.isNotEmpty() }
            ?.let { getMostRecentStation(allTimes) }

        if (mostRecent != null && mostRecent.validFrom <= startDate && mostRecent.validTo <= endDate) {
            return ValidYears(
                validFrom = startDate,
                validTo = endDate,
                elementId = elementId,
                weatherStation = weatherStation
            )
        }

        return null
    }

    suspend fun getDataSet(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {
        val startDate = validYears.validFrom
        val endDate = validYears.validTo

        val formattedStartDate = formatDate(startDate)
        val formattedEndDate = formatDate(endDate)
        val expectedDays = ChronoUnit.DAYS.between(startDate, endDate).toInt()

        var weatherData = metDataSource.getWeatherData(weatherStation, validYears.elementId, formattedStartDate, formattedEndDate)

        if (weatherData == null || weatherData.dataList.size < expectedDays) {
            val now = Instant.now()
            val tryLater = endDate.atZone(ZoneId.of("UTC")).plusYears(1).toInstant().isBefore(now)

            val alternativeStartDate = if (tryLater) {
                startDate.atZone(ZoneId.of("UTC")).plusYears(1).toInstant()
            } else {
                startDate.atZone(ZoneId.of("UTC")).minusYears(1).toInstant()
            }

            val alternativeEndDate = if (tryLater) {
                endDate.atZone(ZoneId.of("UTC")).plusYears(1).toInstant()
            } else {
                endDate.atZone(ZoneId.of("UTC")).minusYears(1).toInstant()
            }

            val alternativeFormattedStartDate = formatDate(alternativeStartDate)
            val alternativeFormattedEndDate = formatDate(alternativeEndDate)

            val alternativeData = metDataSource.getWeatherData(weatherStation, validYears.elementId, alternativeFormattedStartDate, alternativeFormattedEndDate)

            if (alternativeData != null) {
                val combined = (weatherData?.dataList ?: emptyList()) + alternativeData.dataList

                // Group by month and day
                val groupedByMonthDay = combined.groupBy { data ->
                    val zdt = data.timeMeasured.atZone(ZoneId.of("UTC"))
                    Pair(zdt.monthValue, zdt.dayOfMonth)
                }

                val hasFeb29InOriginalRange = generateSequence(startDate) { it.plus(1, ChronoUnit.DAYS) }
                    .takeWhile { it < endDate }
                    .any { it.atZone(ZoneId.of("UTC")).monthValue == 2 && it.atZone(ZoneId.of("UTC")).dayOfMonth == 29 }

                val normalizedByMonthDay = groupedByMonthDay.mapValues { (_, sameDayEntries) ->
                    sameDayEntries.maxByOrNull { it.timeMeasured }!!
                }

                val completeDateRange = generateSequence(startDate) { it.plus(1, ChronoUnit.DAYS) }
                    .takeWhile { it < endDate }
                    .mapNotNull { date ->
                        val zdt = date.atZone(ZoneId.of("UTC"))
                        val month = zdt.monthValue
                        val day = zdt.dayOfMonth

                        // Skip February 29 if base year doesn't support it
                        if (month == 2 && day == 29 && !hasFeb29InOriginalRange) {
                            return@mapNotNull null
                        }

                        val dataForDay = normalizedByMonthDay[Pair(month, day)]

                        val filledData = if (dataForDay != null) {
                            val newTime = ZonedDateTime.of(zdt.year, month, day, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant()
                            dataForDay.copy(timeMeasured = newTime)
                        } else {

                            // Fill with zero or null value if no data available
                            WeatherData(
                                elementId = validYears.elementId,
                                value = 0.0,
                                unit = if (validYears.elementId == "mean(cloud_area_fraction P1D)") "octas" else "degC",
                                weatherStation = weatherStation,
                                timeMeasured = ZonedDateTime.of(zdt.year, month, day, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant()
                            )
                        }

                        filledData
                    }.toList()
                weatherData = WeatherList(completeDateRange.sortedBy { it.timeMeasured })
            }
        }

        return weatherData
    }

    suspend fun getGlobalRadiationData(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {
        val now = Instant.now()
        val startDate = validYears.validFrom
        val endDate = validYears.validTo
        val formattedStartDate = formatDate(startDate)
        val formattedEndDate = formatDate(endDate)

        var allSolarData = metDataSource.getWeatherData(weatherStation, validYears.elementId, formattedStartDate, formattedEndDate)

        if (allSolarData == null) {
            val tryLater = endDate.atZone(ZoneId.of("UTC")).plusYears(1).toInstant().isBefore(now)

            val altStart = if (tryLater) {
                startDate.atZone(ZoneId.of("UTC")).plusYears(1).toInstant()
            } else {
                startDate.atZone(ZoneId.of("UTC")).minusYears(1).toInstant()
            }
            val altEnd = if (tryLater) {
                endDate.atZone(ZoneId.of("UTC")).plusYears(1).toInstant()
            } else {
                endDate.atZone(ZoneId.of("UTC")).minusYears(1).toInstant()
            }

            allSolarData = metDataSource.getWeatherData(weatherStation, validYears.elementId, formatDate(altStart), formatDate(altEnd))
        }

        if (allSolarData == null) {
            println("No solarData within 2 years of date range")
            return null
        }

        val groupedByDay = allSolarData.dataList.groupBy {
            formatDate(it.timeMeasured)
        }

        val fullDateRange = generateSequence(startDate) { it.plus(1, ChronoUnit.DAYS) }
            .takeWhile { it < endDate }
            .toList()

        val completeDailyData = fullDateRange.map { date ->
            val formatted = formatDate(date)
            val matchingData = groupedByDay[formatted]

            val value = matchingData?.sumOf { it.value.coerceAtLeast(0.0) } ?: 0.0
            val unit = matchingData?.firstOrNull()?.unit ?: "unknown"

            WeatherData(
                elementId = validYears.elementId,
                value = value,
                unit = unit,
                weatherStation = weatherStation,
                timeMeasured = date
            )
        }

        return WeatherList(dataList = completeDailyData)
    }

    suspend fun getSnowThickness(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {
        val startDate = validYears.validFrom
        val endDate = validYears.validTo
        val formattedStartDate = formatDate(startDate)
        val formattedEndDate = formatDate(endDate)

        var snowData = metDataSource.getWeatherData(weatherStation, validYears.elementId, formattedStartDate, formattedEndDate)

        if (snowData == null || snowData.dataList.size < 12) {

            val altStart = startDate.atZone(ZoneId.of("UTC")).minusYears(5).toInstant()
            val altEnd = endDate.atZone(ZoneId.of("UTC")).plusYears(5).toInstant()

            snowData = metDataSource.getWeatherData(weatherStation, validYears.elementId, formatDate(altStart), formatDate(altEnd))
        }

        if (snowData == null) {
            println("No snowData within 10 years of date range")
            return null
        }

        val groupedByMonth = snowData.dataList.groupBy {
            it.timeMeasured.atZone(ZoneId.of("UTC")).month
        }.mapValues { entry ->
            val avgValue = entry.value.map { it.value }.average()
            val first = entry.value.first()
            WeatherData(
                elementId = first.elementId,
                unit = first.unit,
                value = avgValue,
                weatherStation = first.weatherStation,
                timeMeasured = first.timeMeasured
            )
        }

        val allMonths = Month.entries.toTypedArray()
        val firstYearStartMonth = startDate.atZone(ZoneId.of("UTC")).month
        val firstYear = startDate.atZone(ZoneId.of("UTC")).year
        val secondYear = endDate.minus(Duration.ofDays(1)).atZone(ZoneId.of("UTC")).year

        val completeMonthMap = allMonths.associateWith { month ->
            val year = if (month >= firstYearStartMonth) firstYear else secondYear
            groupedByMonth.getOrElse(month) {
                WeatherData(
                    elementId = "mean(surface_snow_thickness P1M)",
                    unit = "cm",
                    value = 0.0,
                    timeMeasured = Instant.parse("${year}-${month.value.toString().padStart(2, '0')}-01T00:00:00Z"),
                    weatherStation = validYears.weatherStation
                )
            }
        }

        val dates = generateSequence(startDate) { it.plus(1, ChronoUnit.DAYS) }
            .takeWhile { it < endDate }
            .toList()

        val dataForAllDays = dates.mapNotNull { date ->
            val month = date.atZone(ZoneId.of("UTC")).month
            completeMonthMap[month]?.copy(timeMeasured = date)
        }

        return WeatherList(dataList = dataForAllDays)
    }
}