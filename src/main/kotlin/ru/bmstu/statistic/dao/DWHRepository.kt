package ru.bmstu.statistic.dao

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import ru.bmstu.statistic.models.entities.*

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


interface PerformanceRepository : ReactiveMongoRepository<Performance, String>

interface AttendanceRepository : ReactiveMongoRepository<Attendance, String>


interface GroupPerformanceRepository : ReactiveMongoRepository<GroupPerformance, String>

interface GroupAttendanceRepository : ReactiveMongoRepository<GroupAttendance, String>

interface ChairPerformanceRepository : ReactiveMongoRepository<ChairPerformance, String>

interface ChairAttendanceRepository : ReactiveMongoRepository<ChairAttendance, String>

interface FacultyPerformanceRepository : ReactiveMongoRepository<FacultyPerformance, String>

interface FacultyAttendanceRepository : ReactiveMongoRepository<FacultyAttendance, String>