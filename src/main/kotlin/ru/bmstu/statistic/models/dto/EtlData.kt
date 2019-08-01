package ru.bmstu.statistic.models.dto

import ru.bmstu.statistic.models.entities.ScoreType
import ru.bmstu.statistic.models.entities.TestType
import java.time.LocalDate

public data class StudentInfo(
    var name: String,
    var group: GroupInfo
)

public data class GroupInfo(
    var name: String,
    var chair: ChairInfo
)

public data class ChairInfo(
    var name: String,
    var faculty: FacultyInfo
)

public data class FacultyInfo(
    var name: String
)

public data class Science(
    var name: String
)

data class PerformanceDTO(
    var student: String,
    var course: String,
    var score: Int,
    var maxAvailableScore: Int,
    var beginTime: LocalDate,
    var endTime: LocalDate,
    var testType: TestType? = null,
    var scoreType: ScoreType? = null
)

data class AttendanceDTO(
    var student: String,
    var course: String,
    var count: Int,
    var maxAvailableCount: Int,
    var beginTime: LocalDate,
    var endTime: LocalDate
)

public data class GroupPerformance(
    var group: GroupInfo,
    var science: Science,
    var min: Double,
    var avg: Double,
    var max: Double
)

public data class GroupAttendance(
    var group: GroupInfo,
    var science: Science,
    var min: Double,
    var avg: Double,
    var max: Double
)

public data class ChairPerformance(
    var group: ChairInfo,
    var science: Science,
    var min: Double,
    var avg: Double,
    var max: Double
)

public data class ChairAttendance(
    var group: ChairInfo,
    var science: Science,
    var min: Double,
    var avg: Double,
    var max: Double
)

public data class FacultyPerformance(
    var group: FacultyInfo,
    var science: Science,
    var min: Double,
    var avg: Double,
    var max: Double
)

public data class FacultyAttendance(
    var group: FacultyInfo,
    var science: Science,
    var min: Double,
    var avg: Double,
    var max: Double
)