package ru.bmstu.statistic.models.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("faculties")
data class Faculty(
    @Id val id: String? = null,
    var value: String = ""
)

@Document("chairs")
data class Chair(
    @Id val id: String? = null,
    var value: String = "",
    var facultyId: String? = null
)

@Document("groups")
data class Group(
    @Id val id: String? = null,
    var value: String = "",
    var chairId: String? = null
)

@Document("students")
data class Student(
    @Id val id: String? = null,
    var name: String = "",
    var age: Int = 0,
    var sex: Sex? = null,
    var hasHostel: Boolean = false,
    var groupId: String? = null
)

enum class Sex {
    MALE,
    FEMALE
}

@Document("courses")
data class Course(
    @Id val id: String? = null,
    var value: String = "",
    var chairId: String? = null
)

@Document("test_types")
data class TestType(
    @Id val id: String? = null,
    var type: KRType
)

enum class KRType(val desc: String) {
    EXAMEN("Экзамен"),
    MINI_EXAMEN("Зачет"),
    RK("РК"),
    KR("КР"),
    DZ("ДЗ");

    val description: String = "";
}

@Document("score_types")
data class ScoreType(
    @Id val id: String? = null,
    var type: KRScoreType
)

enum class KRScoreType(val desc: String) {
    SCORE("Баллы"),
    MINI_EXAMEN("Зачет"),
    EXAMINATION("Оценка");

    val description: String = "";
}

@Document("performance")
data class Performance(
    var student: String? = null,
    var course: String? = null,
    var testType: TestType? = null,
    var scoreType: ScoreType? = null,
    var score: Double = 0.0,
    var beginTime: LocalDate = LocalDate.now(),
    var endTime: LocalDate = LocalDate.now()
)

@Document("attendance")
data class Attendance(
    var student: String? = null,
    var course: String? = null,
    var score: Double = 0.0,
    var beginTime: LocalDate = LocalDate.now(),
    var endTime: LocalDate = LocalDate.now()
)

@Document("group_performance")
public data class GroupPerformance(
    var group: String? = null,
    var course: String? = null,
    var min: Double,
    var avg: Double,
    var max: Double,
    var beginTime: LocalDate,
    var endTime: LocalDate
)

@Document("group_attendance")
public data class GroupAttendance(
    var group: String? = null,
    var course: String? = null,
    var min: Double,
    var avg: Double,
    var max: Double,
    var beginTime: LocalDate,
    var endTime: LocalDate
)

@Document("chair_performance")
public data class ChairPerformance(
    var chair: String? = null,
    var course: String? = null,
    var min: Double,
    var avg: Double,
    var max: Double,
    var beginTime: LocalDate,
    var endTime: LocalDate
)

@Document("chair_attendance")
public data class ChairAttendance(
    var chair: String? = null,
    var course: String? = null,
    var min: Double,
    var avg: Double,
    var max: Double,
    var beginTime: LocalDate,
    var endTime: LocalDate
)

@Document("faculty_performance")
public data class FacultyPerformance(
    var faculty: String? = null,
    var course: String? = null,
    var min: Double,
    var avg: Double,
    var max: Double,
    var beginTime: LocalDate,
    var endTime: LocalDate
)

@Document("faculty_attendance")
public data class FacultyAttendance(
    var faculty: String? = null,
    var course: String? = null,
    var min: Double,
    var avg: Double,
    var max: Double,
    var beginTime: LocalDate,
    var endTime: LocalDate
)
