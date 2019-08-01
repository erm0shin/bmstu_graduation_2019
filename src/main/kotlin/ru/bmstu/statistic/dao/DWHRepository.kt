package ru.bmstu.statistic.dao

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import ru.bmstu.statistic.models.entities.*
import java.time.LocalDate

interface FacultyRepository : ReactiveMongoRepository<Faculty, String> {
    fun findByStudentName(student: String?): Faculty
}

interface ChairRepository : ReactiveMongoRepository<Chair, String> {
    fun findByStudentName(student: String?): Chair
}

interface GroupRepository : ReactiveMongoRepository<Group, String> {
    fun findByStudentName(student: String?): Group
}

interface StudentRepository : ReactiveMongoRepository<Student, String> {
    fun findByName(student: String): Student?
    fun getIdByName(student: String): String?
}


interface PerformanceRepository : ReactiveMongoRepository<Performance, String> {
    fun findAllByBeginTimeAfterAndEndTimeBefore(begin: LocalDate, end: LocalDate): List<Performance>
}

interface AttendanceRepository : ReactiveMongoRepository<Attendance, String> {
    fun findAllByBeginTimeAfterAndEndTimeBefore(begin: LocalDate, end: LocalDate): List<Attendance>
}


interface GroupPerformanceRepository : ReactiveMongoRepository<GroupPerformance, String>

interface GroupAttendanceRepository : ReactiveMongoRepository<GroupAttendance, String>

interface ChairPerformanceRepository : ReactiveMongoRepository<ChairPerformance, String>

interface ChairAttendanceRepository : ReactiveMongoRepository<ChairAttendance, String>

interface FacultyPerformanceRepository : ReactiveMongoRepository<FacultyPerformance, String>

interface FacultyAttendanceRepository : ReactiveMongoRepository<FacultyAttendance, String>