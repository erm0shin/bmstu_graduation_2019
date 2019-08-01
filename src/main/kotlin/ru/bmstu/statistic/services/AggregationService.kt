package ru.bmstu.statistic.services

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.bmstu.statistic.dao.*
import ru.bmstu.statistic.models.dto.AttendanceDTO
import ru.bmstu.statistic.models.dto.PerformanceDTO
import ru.bmstu.statistic.models.entities.*
import ru.bmstu.statistic.network.DataFetcher
import java.time.LocalDate

@Service
public class AggregationService(
    private val dataFetcher: DataFetcher,
    private val studentRepository: StudentRepository,
    private val groupRepository: GroupRepository,
    private val chairRepository: ChairRepository,
    private val facultyRepository: FacultyRepository,
    private val performanceRepository: PerformanceRepository,
    private val attendanceRepository: AttendanceRepository,
    private val groupPerformanceRepository: GroupPerformanceRepository,
    private val groupAttendanceRepository: GroupAttendanceRepository,
    private val chairPerformanceRepository: ChairPerformanceRepository,
    private val chairAttendanceRepository: ChairAttendanceRepository,
    private val facultyPerformanceRepository: FacultyPerformanceRepository,
    private val facultyAttendanceRepository: FacultyAttendanceRepository
) {

    @FlowPreview
    @Scheduled(cron = "0 0 0 1 */6 *")
    private suspend fun fillDWH() {
        dataFetcher.getAllFacultyInfo().forEach { facultyRepository.save(it) }
        dataFetcher.getAllChairInfo().forEach { chairRepository.save(it) }
        dataFetcher.getAllGroupInfo().forEach { groupRepository.save(it) }
        dataFetcher.getAllStudentInfo().forEach { studentRepository.save(it) }

        studentRepository.findAll().asFlow().map {
            val performance = dataFetcher.getPerformanceStatistic(it.name)
            val attendance = dataFetcher.getAttendanceStatistic(it.name)
            performance.forEach { perf ->
                val performanceEntity = convertPerformance(perf)
                performanceRepository.save(performanceEntity)
            }
            attendance.forEach { attend ->
                val attendanceEntity = convertAttendance(attend)
                attendanceRepository.save(attendanceEntity)
            }
        }

        aggregate()
    }

    private fun convertAttendance(attendance: AttendanceDTO): Attendance {
        val result = Attendance()
        result.student = studentRepository.getIdByName(attendance.student)
        result.beginTime = attendance.beginTime
        result.endTime = attendance.endTime
        result.course = attendance.course
        result.score = (attendance.count / attendance.maxAvailableCount * 100.0)
        return result
    }

    private fun convertPerformance(performance: PerformanceDTO): Performance {
        val result = Performance()
        result.testType = performance.testType
        result.student = studentRepository.getIdByName(performance.student)
        result.beginTime = performance.beginTime
        result.endTime = performance.endTime
        result.course = performance.course
        result.scoreType = performance.scoreType
        when (performance.testType?.type) {
            KRType.EXAMEN -> result.score = (performance.score * 200.0)
            KRType.MINI_EXAMEN -> result.score = (performance.score * 500.0)
            else -> {
                result.score = (performance.score / performance.maxAvailableScore * 100.0)
            }
        }
        return result
    }

    private fun aggregate() {
        val performance = performanceRepository.findAllByBeginTimeAfterAndEndTimeBefore(
            LocalDate.now().minusMonths(6),
            LocalDate.now()
        )
        val attendance = attendanceRepository.findAllByBeginTimeAfterAndEndTimeBefore(
            LocalDate.now().minusMonths(6),
            LocalDate.now()
        )

        // Успеваемость по группам
        performance
            .groupBy { groupRepository.findByStudentName(it.student) }
            .map { groupsPerformance ->
                groupsPerformance.value
                    .groupBy { it.course }
                    .map { groupsPerformanceInCourse ->
                        groupsPerformanceInCourse.value.forEach { perf ->
                            val performanceEntity = GroupPerformance()
                            performanceEntity.group = groupsPerformanceInCourse.key ?: ""
                            performanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            performanceEntity.endTime = LocalDate.now()
                            performanceEntity.course = perf.course
                            performanceEntity.min =
                                groupsPerformanceInCourse.value.minPerformanceAmongStudent()
                            performanceEntity.max =
                                groupsPerformanceInCourse.value.maxPerformanceAmongStudent()
                            performanceEntity.avg =
                                groupsPerformanceInCourse.value.avgPerformanceAmongStudent()
                            groupPerformanceRepository.save(performanceEntity)
                        }
                    }
            }
        // Успеваемость по кафедрам
        performance
            .groupBy { chairRepository.findByStudentName(it.student) }
            .map { chairsPerformance ->
                chairsPerformance.value
                    .groupBy { it.course }
                    .map { chairsPerformanceInCourse ->
                        chairsPerformanceInCourse.value.forEach { perf ->
                            val performanceEntity = ChairPerformance()
                            performanceEntity.chair = chairsPerformanceInCourse.key ?: ""
                            performanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            performanceEntity.endTime = LocalDate.now()
                            performanceEntity.course = perf.course
                            performanceEntity.min =
                                chairsPerformanceInCourse.value.minPerformanceAmongStudent()
                            performanceEntity.max =
                                chairsPerformanceInCourse.value.maxPerformanceAmongStudent()
                            performanceEntity.avg =
                                chairsPerformanceInCourse.value.avgPerformanceAmongStudent()
                            chairPerformanceRepository.save(performanceEntity)
                        }
                    }
            }
        // Успеваемость по факультетам
        performance
            .groupBy { facultyRepository.findByStudentName(it.student) }
            .map { facultysPerformance ->
                facultysPerformance.value
                    .groupBy { it.course }
                    .map { facultysPerformanceInCourse ->
                        facultysPerformanceInCourse.value.forEach { perf ->
                            val performanceEntity = FacultyPerformance()
                            performanceEntity.faculty = facultysPerformanceInCourse.key ?: ""
                            performanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            performanceEntity.endTime = LocalDate.now()
                            performanceEntity.course = perf.course
                            performanceEntity.min =
                                facultysPerformanceInCourse.value.minPerformanceAmongStudent()
                            performanceEntity.max =
                                facultysPerformanceInCourse.value.maxPerformanceAmongStudent()
                            performanceEntity.avg =
                                facultysPerformanceInCourse.value.avgPerformanceAmongStudent()
                            facultyPerformanceRepository.save(performanceEntity)
                        }
                    }
            }

        // Посещаемость по группам
        attendance
            .groupBy { groupRepository.findByStudentName(it.student) }
            .map { groupsAttendance ->
                groupsAttendance.value
                    .groupBy { it.course }
                    .map { groupsAttendanceInCourse ->
                        groupsAttendanceInCourse.value.forEach { perf ->
                            val attendanceEntity = GroupAttendance()
                            attendanceEntity.group = groupsAttendanceInCourse.key ?: ""
                            attendanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            attendanceEntity.endTime = LocalDate.now()
                            attendanceEntity.course = perf.course
                            attendanceEntity.min =
                                groupsAttendanceInCourse.value.minAttendanceAmongStudent()
                            attendanceEntity.max =
                                groupsAttendanceInCourse.value.maxAttendanceAmongStudent()
                            attendanceEntity.avg =
                                groupsAttendanceInCourse.value.avgAttendanceAmongStudent()
                            groupAttendanceRepository.save(attendanceEntity)
                        }
                    }
            }
        // Посещаемость по кафедрам
        attendance
            .groupBy { chairRepository.findByStudentName(it.student) }
            .map { chairsAttendance ->
                chairsAttendance.value
                    .groupBy { it.course }
                    .map { chairsAttendanceInCourse ->
                        chairsAttendanceInCourse.value.forEach { perf ->
                            val attendanceEntity = ChairAttendance()
                            attendanceEntity.chair = chairsAttendanceInCourse.key ?: ""
                            attendanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            attendanceEntity.endTime = LocalDate.now()
                            attendanceEntity.course = perf.course
                            attendanceEntity.min =
                                chairsAttendanceInCourse.value.minAttendanceAmongStudent()
                            attendanceEntity.max =
                                chairsAttendanceInCourse.value.maxAttendanceAmongStudent()
                            attendanceEntity.avg =
                                chairsAttendanceInCourse.value.avgAttendanceAmongStudent()
                            chairAttendanceRepository.save(attendanceEntity)
                        }
                    }
            }
        // Посещаемость по факультетам
        attendance
            .groupBy { facultyRepository.findByStudentName(it.student) }
            .map { facultysAttendance ->
                facultysAttendance.value
                    .groupBy { it.course }
                    .map { facultysAttendanceInCourse ->
                        facultysAttendanceInCourse.value.forEach { perf ->
                            val attendanceEntity = FacultyAttendance()
                            attendanceEntity.faculty = facultysAttendanceInCourse.key ?: ""
                            attendanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            attendanceEntity.endTime = LocalDate.now()
                            attendanceEntity.course = perf.course
                            attendanceEntity.min =
                                facultysAttendanceInCourse.value.minAttendanceAmongStudent()
                            attendanceEntity.max =
                                facultysAttendanceInCourse.value.maxAttendanceAmongStudent()
                            attendanceEntity.avg =
                                facultysAttendanceInCourse.value.avgAttendanceAmongStudent()
                            facultyAttendanceRepository.save(attendanceEntity)
                        }
                    }
            }
    }

    private fun List<Performance>.minPerformanceAmongStudent(): Double {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { perf -> perf.score }.sum() }
            .min() ?: 0.0
    }

    private fun List<Performance>.maxPerformanceAmongStudent(): Double {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { perf -> perf.score }.sum() }
            .max() ?: 100.0
    }

    private fun List<Performance>.avgPerformanceAmongStudent(): Double {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { perf -> perf.score }.sum() }
            .average() ?: 50.0
    }

    private fun List<Attendance>.minAttendanceAmongStudent(): Double {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { att -> att.score }.sum() }
            .min() ?: 0.0
    }

    private fun List<Attendance>.maxAttendanceAmongStudent(): Double {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { att -> att.score }.sum() }
            .max() ?: 100.0
    }

    private fun List<Attendance>.avgAttendanceAmongStudent(): Double {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { att -> att.score }.sum() }
            .average() ?: 50.0
    }
}