package ru.bmstu.statistic.network

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import ru.bmstu.statistic.models.dto.AttendanceDTO
import ru.bmstu.statistic.models.dto.PerformanceDTO
import ru.bmstu.statistic.models.entities.*

@Configuration
public class WebClientConfiguration {
    @Bean
    public fun getClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl("http://bmstu_etl.ru").build();
    }
}

@Component
public class DataFetcher(
    private val client: WebClient
) {

    public suspend fun getAllStudentInfo(): List<Student> {
        return client.get().uri("/student/all")
            .awaitExchange()
            .awaitBody()
    }

    public suspend fun getAllGroupInfo(): List<Group> {
        return client.get().uri("/group/all")
            .awaitExchange()
            .awaitBody()
    }

    public suspend fun getAllChairInfo(): List<Chair> {
        return client.get().uri("/chair/all")
            .awaitExchange()
            .awaitBody()
    }

    public suspend fun getAllFacultyInfo(): List<Faculty> {
        return client.get().uri("/faculty/all")
            .awaitExchange()
            .awaitBody()
    }

    public suspend fun getPerformanceStatistic(studentName: String): List<PerformanceDTO> {
        return client.get().uri("/performance?name=$studentName")
            .awaitExchange()
            .awaitBody()
    }

    public suspend fun getAttendanceStatistic(studentName: String): List<AttendanceDTO> {
        return client.get().uri("/attendance?name=$studentName")
            .awaitExchange()
            .awaitBody()
    }

}