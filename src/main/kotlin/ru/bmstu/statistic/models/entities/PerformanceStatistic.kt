package ru.bmstu.statistic.models.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("performance")
data class PerformanceStatistic(
    @Id val id: String? = null,
    var student: String = "",
    var study: String = "",
    var group: String = "",
    var faculty: String = "",
    var course: String = "",
    var time: LocalDate = LocalDate.now(),
    var value: Float = 0f
)
