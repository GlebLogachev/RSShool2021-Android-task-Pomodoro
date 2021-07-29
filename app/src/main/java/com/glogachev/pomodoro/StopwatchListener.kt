package com.glogachev.pomodoro

interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int, currentTime: Long)

    fun delete(id: Int)

    fun updateCurrentTime(time : Long)
}