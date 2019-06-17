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
        val performance = performanceRepository.findAllInInterval(LocalDate.now().minusMonths(6), LocalDate.now())
        val attendance = attendanceRepository.findAllInInterval(LocalDate.now().minusMonths(6), LocalDate.now())

        val test: List<Performance> = listOf()

        test.minBy { it.score }

        // Успеваемость по группам
        performance
            .groupBy { groupRepository.findByStudentName(it.student) }
            .map { groupsPerformance ->
                groupsPerformance.values()
                    .groupBy { it.course }
                    .map { groupsPerformanceInCourse ->
                        groupsPerformanceInCourse.values().forEach { perf ->
                            val performanceEntity = GroupPerformance()
                            performanceEntity.group = groupsPerformanceInCourse.key()?.value
                            performanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            performanceEntity.endTime = LocalDate.now()
                            performanceEntity.course = perf.course
                            performanceEntity.min =
                                groupsPerformanceInCourse.values().minPerformanceByCourseAmongStudent()
                            performanceEntity.max =
                                groupsPerformanceInCourse.values().maxPerformanceByCourseAmongStudent()
                            performanceEntity.avg =
                                groupsPerformanceInCourse.values().avgPerformanceByCourseAmongStudent()
                            groupPerformanceRepository.save(performanceEntity)
                        }
                    }
            }
        // Успеваемость по кафедрам
        performance
            .groupBy { chairRepository.findByStudentName(it.student) }
            .map { chairsPerformance ->
                chairsPerformance.values()
                    .groupBy { it.course }
                    .map { chairsPerformanceInCourse ->
                        chairsPerformanceInCourse.values().forEach { perf ->
                            val performanceEntity = ChairPerformance()
                            performanceEntity.chair = chairsPerformanceInCourse.key()?.value
                            performanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            performanceEntity.endTime = LocalDate.now()
                            performanceEntity.course = perf.course
                            performanceEntity.min =
                                chairsPerformanceInCourse.values().minPerformanceByCourseAmongStudent()
                            performanceEntity.max =
                                chairsPerformanceInCourse.values().maxPerformanceByCourseAmongStudent()
                            performanceEntity.avg =
                                chairsPerformanceInCourse.values().avgPerformanceByCourseAmongStudent()
                            chairPerformanceRepository.save(performanceEntity)
                        }
                    }
            }
        // Успеваемость по факультетам
        performance
            .groupBy { facultyRepository.findByStudentName(it.student) }
            .map { facultysPerformance ->
                facultysPerformance.values()
                    .groupBy { it.course }
                    .map { facultysPerformanceInCourse ->
                        facultysPerformanceInCourse.values().forEach { perf ->
                            val performanceEntity = FacultyPerformance()
                            performanceEntity.faculty = facultysPerformanceInCourse.key()?.value
                            performanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            performanceEntity.endTime = LocalDate.now()
                            performanceEntity.course = perf.course
                            performanceEntity.min =
                                facultysPerformanceInCourse.values().minPerformanceByCourseAmongStudent()
                            performanceEntity.max =
                                facultysPerformanceInCourse.values().maxPerformanceByCourseAmongStudent()
                            performanceEntity.avg =
                                facultysPerformanceInCourse.values().avgPerformanceByCourseAmongStudent()
                            facultyPerformanceRepository.save(performanceEntity)
                        }
                    }
            }

        // Посещаемость по группам
        attendance
            .groupBy { groupRepository.findByStudentName(it.student) }
            .map { groupsAttendance ->
                groupsAttendance.values()
                    .groupBy { it.course }
                    .map { groupsAttendanceInCourse ->
                        groupsAttendanceInCourse.values().forEach { perf ->
                            val attendanceEntity = GroupAttendance()
                            attendanceEntity.group = groupsAttendanceInCourse.key()?.value
                            attendanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            attendanceEntity.endTime = LocalDate.now()
                            attendanceEntity.course = perf.course
                            attendanceEntity.min =
                                groupsAttendanceInCourse.values().minAttendanceByCourseAmongStudent()
                            attendanceEntity.max =
                                groupsAttendanceInCourse.values().maxAttendanceByCourseAmongStudent()
                            attendanceEntity.avg =
                                groupsAttendanceInCourse.values().avgAttendanceByCourseAmongStudent()
                            groupAttendanceRepository.save(attendanceEntity)
                        }
                    }
            }
        // Посещаемость по кафедрам
        attendance
            .groupBy { chairRepository.findByStudentName(it.student) }
            .map { chairsAttendance ->
                chairsAttendance.values()
                    .groupBy { it.course }
                    .map { chairsAttendanceInCourse ->
                        chairsAttendanceInCourse.values().forEach { perf ->
                            val attendanceEntity = ChairAttendance()
                            attendanceEntity.chair = chairsAttendanceInCourse.key()?.value
                            attendanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            attendanceEntity.endTime = LocalDate.now()
                            attendanceEntity.course = perf.course
                            attendanceEntity.min =
                                chairsAttendanceInCourse.values().minAttendanceByCourseAmongStudent()
                            attendanceEntity.max =
                                chairsAttendanceInCourse.values().maxAttendanceByCourseAmongStudent()
                            attendanceEntity.avg =
                                chairsAttendanceInCourse.values().avgAttendanceByCourseAmongStudent()
                            chairAttendanceRepository.save(attendanceEntity)
                        }
                    }
            }
        // Посещаемость по факультетам
        attendance
            .groupBy { facultyRepository.findByStudentName(it.student) }
            .map { facultysAttendance ->
                facultysAttendance.values()
                    .groupBy { it.course }
                    .map { facultysAttendanceInCourse ->
                        facultysAttendanceInCourse.values().forEach { perf ->
                            val attendanceEntity = FacultyAttendance()
                            attendanceEntity.faculty = facultysAttendanceInCourse.key()?.value
                            attendanceEntity.beginTime = LocalDate.now().minusMonths(6)
                            attendanceEntity.endTime = LocalDate.now()
                            attendanceEntity.course = perf.course
                            attendanceEntity.min =
                                facultysAttendanceInCourse.values().minAttendanceByCourseAmongStudent()
                            attendanceEntity.max =
                                facultysAttendanceInCourse.values().maxAttendanceByCourseAmongStudent()
                            attendanceEntity.avg =
                                facultysAttendanceInCourse.values().avgAttendanceByCourseAmongStudent()
                            facultyAttendanceRepository.save(attendanceEntity)
                        }
                    }
            }
    }

    private fun List<Performance>.minPerformanceByCourseAmongStudent(): Double? {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { perf -> perf.score }.sum() }
            .min()
    }

    private fun List<Performance>.maxPerformanceByCourseAmongStudent(): Double? {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { perf -> perf.score }.sum() }
            .max()
    }

    private fun List<Performance>.avgPerformanceByCourseAmongStudent(): Double? {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { perf -> perf.score }.sum() }
            .average()
    }

    private fun List<Attendance>.minAttendanceByCourseAmongStudent(): Double? {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { perf -> perf.score }.sum() }
            .min()
    }

    private fun List<Attendance>.maxAttendanceByCourseAmongStudent(): Double? {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { perf -> perf.score }.sum() }
            .max()
    }

    private fun List<Attendance>.avgAttendanceByCourseAmongStudent(): Double? {
        return this.groupBy { it.student }
            .map { entry -> entry.value.map { perf -> perf.score }.sum() }
            .average()
    }
}