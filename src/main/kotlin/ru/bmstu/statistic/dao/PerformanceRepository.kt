package ru.bmstu.statistic.dao

import org.springframework.data.mongodb.repository.MongoRepository
import ru.bmstu.statistic.models.entities.PerformanceStatistic

interface PerformanceRepository:MongoRepository<PerformanceStatistic, Long>