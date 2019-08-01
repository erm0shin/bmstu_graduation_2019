package ru.bmstu.statistic.dao

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import ru.bmstu.statistic.models.entities.PerformanceStatistic

interface PerformanceStatisticRepository : ReactiveMongoRepository<PerformanceStatistic, Long>