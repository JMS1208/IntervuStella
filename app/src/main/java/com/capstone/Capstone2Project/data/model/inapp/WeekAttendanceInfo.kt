package com.capstone.Capstone2Project.data.model.inapp

data class WeekAttendanceInfo(
    val weekAttendance: List<WeekItem>,
    val continuousCount: Int
)
data class WeekItem(
    val dayOfWeek: DayOfWeek,
    val isPresent: Boolean? //null이면 앞으로 출석해야하는 날들
) {
    companion object {
        fun createWeekItem(
            isPresent: Boolean?,
            index: Int
        ): WeekItem? {
            return when(index) {
                0-> { //월요일
                    WeekItem(
                        isPresent = isPresent,
                        dayOfWeek = DayOfWeek.MONDAY
                    )
                }
                1-> {
                    WeekItem(
                        isPresent = isPresent,
                        dayOfWeek = DayOfWeek.TUESDAY
                    )
                }
                2-> {
                    WeekItem(
                        isPresent = isPresent,
                        dayOfWeek = DayOfWeek.WEDNESDAY
                    )
                }
                3-> {
                    WeekItem(
                        isPresent = isPresent,
                        dayOfWeek = DayOfWeek.THURSDAY
                    )
                }
                4-> {
                    WeekItem(
                        isPresent = isPresent,
                        dayOfWeek = DayOfWeek.FRIDAY
                    )
                }
                5-> {
                    WeekItem(
                        isPresent = isPresent,
                        dayOfWeek = DayOfWeek.SATURDAY
                    )
                }
                6-> {
                    WeekItem(
                        isPresent = isPresent,
                        dayOfWeek = DayOfWeek.SUNDAY
                    )
                }
                else-> {
                    null
                }
            }

        }
    }
}
enum class DayOfWeek(val value: String) {
    MONDAY("MON"),
    TUESDAY("TUE"),
    WEDNESDAY("WED"),
    THURSDAY("THU"),
    FRIDAY("FRI"),
    SATURDAY("SAT"),
    SUNDAY("SUN")
}
