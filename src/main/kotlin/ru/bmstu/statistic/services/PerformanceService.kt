package ru.bmstu.statistic.services

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.flow.asFlow
import org.springframework.stereotype.Service
import ru.bmstu.statistic.dao.PerformanceStatisticRepository
import ru.bmstu.statistic.models.entities.PerformanceStatistic

@FlowPreview
@Service
class PerformanceService(val performanceStatisticRepository: PerformanceStatisticRepository) {

    suspend fun addPerformanceStatistic(data: String) {
        val performanceStatistic = PerformanceStatistic()
        performanceStatistic.student = data
        performanceStatisticRepository.save(performanceStatistic).awaitSingle()
    }

    fun getAllPerformanceStatistic() = performanceStatisticRepository.findAll().asFlow()

}