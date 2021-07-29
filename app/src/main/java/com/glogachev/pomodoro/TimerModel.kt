package com.glogachev.pomodoro

data class TimerModel(
    val id: Int,
    var currentTime: Long,
    val initialTime: Long,
    var status: TimerStatus
)

enum class TimerStatus {
    STARTED, STOPPED
}
