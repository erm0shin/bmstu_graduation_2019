package ru.bmstu.statistic.controllers

import org.springframework.web.bind.annotation.*
import ru.bmstu.statistic.models.dto.Greeting
import ru.bmstu.statistic.services.PerformanceService
import java.util.concurrent.atomic.AtomicLong

@RestController
@RequestMapping("/performance")
class PerformanceController(val performanceService: PerformanceService) {

    @GetMapping
    fun getAllPerformanceStatistic() = performanceService.getAllPerformanceStatistic()

    @PutMapping
    fun addNewPerformanceStatistic(@RequestParam(value = "data", defaultValue = "World") data: String) =
            performanceService.addPerformanceStatistic(data)

}