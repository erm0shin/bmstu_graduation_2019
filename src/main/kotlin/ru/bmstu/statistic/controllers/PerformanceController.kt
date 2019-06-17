package ru.bmstu.statistic.controllers

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import ru.bmstu.statistic.models.entities.PerformanceStatistic
import ru.bmstu.statistic.services.PerformanceService

@FlowPreview
@RestController
@RequestMapping("/performance")
class PerformanceController(val performanceService: PerformanceService) {

    @GetMapping(produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun getAllPerformanceStatistic(): Flow<PerformanceStatistic> = performanceService.getAllPerformanceStatistic()

    @PutMapping
    suspend fun addNewPerformanceStatistic(@RequestParam(value = "data", defaultValue = "World") data: String) =
        performanceService.addPerformanceStatistic(data)

}