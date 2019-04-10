package ru.bmstu.statistic.services

import org.springframework.stereotype.Service
import ru.bmstu.statistic.dao.PerformanceRepository
import ru.bmstu.statistic.models.entities.PerformanceStatistic

@Service
class PerformanceService(val performanceRepository: PerformanceRepository) {

    fun addPerformanceStatistic(data: String) {
        val performanceStatistic = PerformanceStatistic()
        performanceStatistic.student = data
        performanceRepository.save(performanceStatistic)
    }

    fun getAllPerformanceStatistic() = performanceRepository.findAll()

}