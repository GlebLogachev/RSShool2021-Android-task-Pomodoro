package com.glogachev.pomodoro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.glogachev.pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {
    private lateinit var binding: ActivityMainBinding
    private var timersList = mutableListOf<TimerModel>()
    private val timerAdapter = TimerAdapter(this)
    private var nextId = 0
    private var activeTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.adapter = timerAdapter
        setupCallbacks()
    }

    private fun setupCallbacks() {
        binding.btnAddTimer.setOnClickListener {
            val enteredTime = binding.tieTimer.text
            if (enteredTime.isNullOrEmpty()) {
                Toast.makeText(this, "Введите количество минут", Toast.LENGTH_LONG).show()
            }
            if (enteredTime.toString().toLongOrNull() == null) {
                Toast.makeText(this, "Неверный формат ввода", Toast.LENGTH_LONG).show()
            } else {
                when {
                    enteredTime.toString().toLong() > 5999 -> {
                        Toast.makeText(
                            this,
                            "Превышено максимальное количество минут(max 5999)",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    timersList.size > 15 -> {
                        Toast.makeText(
                            this,
                            "Превышено максимальное количество таймеров",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        timersList.add(
                            TimerModel(
                                id = nextId++,
                                currentTime = enteredTime.toString().toLong() * 1000 * 60,
                                initialTime = enteredTime.toString().toLong() * 1000 * 60,
                                status = TimerStatus.STOPPED
                            )
                        )
                    }
                }
            }
            timerAdapter.submitList(timersList.toMutableList())
        }
    }

    override fun start(id: Int) {
        changeTimerList(id, null, TimerStatus.STARTED)
    }


    override fun stop(id: Int, currentTime: Long) {
        changeTimerList(id, currentTime = currentTime, TimerStatus.STOPPED)
    }

    override fun delete(id: Int) {
        timersList.remove(timersList.find { it.id == id })
        timerAdapter.submitList(timersList.toMutableList())
    }

    override fun updateCurrentTime(time: Long) {
        activeTime = time
    }

    private fun changeTimerList(id: Int, currentTime: Long?, timerStatus: TimerStatus) {
        val newTimers = mutableListOf<TimerModel>()
        timersList.forEach {
            if (it.id == id) {
                newTimers.add(
                    TimerModel(
                        it.id,
                        currentTime = currentTime ?: it.currentTime,
                        initialTime = it.initialTime,
                        status = timerStatus
                    )
                )
            } else {
                newTimers.add(it)
            }
        }
        if (timerStatus == TimerStatus.STARTED) {
            newTimers.filter { it.id != id }.map {
                it.status = TimerStatus.STOPPED
            }
        }
        timerAdapter.submitList(newTimers.toMutableList())
        /** TODO: 29.07.2021 :
        без notifyDataSetChanged() не получилось сделать один активный таймер
        в списке всё ок, но bind не вызывается для измененного здесь элемента.
        Видимо как-то проходит через listAdapter и diffUtill незамеченным моё изменение..
        toMutableList() не помогает, хотя создает новый список
        запись типа:
        timerAdapter.submitList(null)
        timerAdapter.submitList(newTimers.toMutableList())
        создает проблемы с таймером
         * */
        timerAdapter.notifyDataSetChanged()
        timersList.clear()
        timersList.addAll(newTimers)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (activeTime != 0L) {
            val startIntent = Intent(this, TimerService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(STARTED_TIMER_TIME_MS, activeTime)
            startService(startIntent)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, TimerService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
}