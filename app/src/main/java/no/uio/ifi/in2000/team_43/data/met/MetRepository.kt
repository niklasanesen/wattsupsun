package no.uio.ifi.in2000.team_43.data.met

import android.util.Log
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

            val yearList = allTimesForElement?.validYearsList?.map { year ->
                Pair(year.validTo, year.validFrom)
            }

            println("allTimesForElement: $allTimesForElement")
            println("size of yearList: ${yearList?.size}")

            yearList?.forEach { year ->
                println("the weatherelement has these valid dates: from: ${year.second}, to: ${year.first}")
            }

            if (allTimesForElement == null || allTimesForElement.validYearsList.isEmpty()) {
                println("No valid years returned for $weatherElement at station ${weatherStation.id}")
            }

            val validDate = allTimesForElement?.validYearsList?.takeIf { it.isNotEmpty() }
                ?.let { getMostRecentStation(allTimesForElement) }

            if (validDate == null) {
                println("Valid date was null for element: $weatherElement")
                return null
            }

            Pair(validDate.validFrom, validDate.validTo)
        }

        if (mostRecentTimes.size < weatherElements.size) {
            println("Not all weather elements returned data.")
            return null
        }

        val latestDate = mostRecentTimes.minByOrNull { it.second }?.second

        val localDate = latestDate?.atZone(ZoneOffset.UTC)?.toLocalDate()

        val newLocalDate = localDate?.minusYears(1)

        println("earliest date is: $latestDate and it is valid until: ${newLocalDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()}")

        if (latestDate != null) {
            if (mostRecentTimes.all { (start, end) ->
                    latestDate >= start && latestDate <= end
                }) {
                val localDate = latestDate.atZone(ZoneOffset.UTC).toLocalDate()
                val newLocalDate = localDate.minusYears(1)
                val earliestDate = newLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant()

                return ValidYears(
                    validTo = latestDate,
                    validFrom = earliestDate,
                    elementId = "",
                    weatherStation = weatherStation
                )
            }
        }

        return null
    }

    suspend fun getDataSet(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {
        val formattedStartDate = formatDate(validYears.validFrom)
        val formattedEndDate = formatDate(validYears.validTo)

        return metDataSource.getWeatherData(weatherStation, validYears.elementId, formattedStartDate, formattedEndDate)
    }

    suspend fun getGlobalRadiationData(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {
        val formattedStartDate = formatDate(validYears.validFrom)
        val formattedEndDate = formatDate(validYears.validTo)

        val allSolarData = metDataSource.getWeatherData(weatherStation, validYears.elementId, formattedStartDate, formattedEndDate)

        val groupedByDay = allSolarData!!.dataList.groupBy {
            formatDate(it.timeMeasured)
        }

        val dailyAverage = groupedByDay.map { (date, weatherDataList) ->
            val sumValue = weatherDataList.sumOf { it.value.coerceAtLeast(0.0) }
            WeatherData(
                elementId = validYears.elementId,
                value = sumValue,
                unit = weatherDataList.first().unit,
                weatherStation = weatherStation,
                timeMeasured = Instant.parse("${date}T00:00:00Z")
            )
        }

        return WeatherList(dataList = dailyAverage)
    }

    suspend fun getSnowThickness(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {
        val formattedStartDate = formatDate(validYears.validFrom)
        val formattedEndDate = formatDate(validYears.validTo)
        val dates = generateSequence(validYears.validFrom) { it.plus(1, ChronoUnit.DAYS) }
            .takeWhile { it <= validYears.validTo.minus(Duration.ofDays(1)) }
        val datesList = dates.toList()

        val allSnowData = metDataSource.getWeatherData(weatherStation, validYears.elementId, formattedStartDate, formattedEndDate)

        allSnowData?.dataList?.forEach { data ->
            Log.d("monthCheck", "the months in this list: ${data.timeMeasured}")
        }

        // Group by month from the data
        val groupedByMonth: Map<Month, WeatherData> = allSnowData!!.dataList.associateBy {
            it.timeMeasured.atZone(ZoneId.of("UTC")).month
        }

        val allMonths = Month.entries.toTypedArray()
        val firstYearStartMonth = validYears.validFrom.atZone(ZoneId.of("UTC")).month
        val firstYear = validYears.validFrom.atZone(ZoneId.of("UTC")).year
        val correctValidTo = validYears.validTo.minus(Duration.ofDays(1))
        val secondYear = correctValidTo.atZone(ZoneId.of("UTC")).year

        // Create completeMonthMap, filling in missing months with default data
        val completeMonthMap: Map<Month, WeatherData> = allMonths.associateWith { month: Month ->
            // Check if the month belongs to the first or second year
            val yearToUse = when {
                month >= firstYearStartMonth -> firstYear // months >= start month in the first year
                else -> secondYear // months in the second year
            }

            groupedByMonth.getOrDefault(month, WeatherData(
                elementId = "mean(surface_snow_thickness P1M)",
                unit = "cm",
                value = 0.0,
                timeMeasured = Instant.parse("${yearToUse}-${month.value.toString().padStart(2, '0')}-01T00:00:00Z"),
                weatherStation = validYears.weatherStation
            ))
        }

        // Log the grouped months
        groupedByMonth.forEach { dato ->
            Log.d("monthCheck", "The months in this list: ${dato.key}")
        }

        Log.d("monthCheck", "size: ${groupedByMonth.size}")

        // Create the data list for all days by mapping over the date range
        val dataForAllDays = datesList.mapNotNull { date ->
            val zonedDateTime = date.atZone(ZoneId.of("UTC"))
            val firstMonthData = completeMonthMap[zonedDateTime.month]

            firstMonthData?.let {
                WeatherData(
                    elementId = it.elementId,
                    value = it.value,
                    unit = it.unit,
                    weatherStation = it.weatherStation,
                    timeMeasured = date
                )
            }
        }
        return WeatherList(dataList = dataForAllDays)
    }
}