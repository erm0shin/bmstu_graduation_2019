package ru.bmstu.statistic.services

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.flow.asFlow
import org.springframework.stereotype.Service
import ru.bmstu.statistic.dao.PerformanceRepository
import ru.bmstu.statistic.models.entities.PerformanceStatistic

@FlowPreview
@Service
class PerformanceService(val performanceRepository: PerformanceRepository) {

    suspend fun addPerformanceStatistic(data: String) {
        val performanceStatistic = PerformanceStatistic()
        performanceStatistic.student = data
        performanceRepository.save(performanceStatistic).awaitSingle()
    }

    fun getAllPerformanceStatistic() = performanceRepository.findAll().asFlow()

}