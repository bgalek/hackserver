package pl.allegro.experiments.chi.chiserver.infrastructure.druid

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Converts given date into string representing Druid intervals for one day.
 */
fun oneDayIntervals(date: LocalDate): String {
    val dateStr = DateTimeFormatter.ISO_LOCAL_DATE.format(date)
    return "${dateStr}T00Z/${dateStr}T23:59:59.999Z"
}

/**
 * Returns Druid intervals for last day metrics.
 */
fun lastDayIntervals() = oneDayIntervals(LocalDate.now().minusDays(1))
