package com.glogachev.pomodoro

import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.glogachev.pomodoro.databinding.RvItemTimerBinding

class TimerViewHolder(
    private val binding: RvItemTimerBinding,
    private val listener: StopwatchListener
) : RecyclerView.ViewHolder(binding.root) {
    private var countDownTimer: CountDownTimer? = null

    fun bind(timer: TimerModel) {
        binding.tvTimer.text = timer.currentTime.displayTime()
        binding.customView.setPeriod(timer.initialTime)
        if (timer.status == TimerStatus.STARTED) {
            startTimer(timer)
        } else {
            stopTimer()
        }
        initButtonsListeners(timer)
    }


    private fun initButtonsListeners(timer: TimerModel) {
        binding.btnStartStopRestart.setOnClickListener {
            if (timer.status == TimerStatus.STARTED) {
                listener.stop(timer.id, timer.currentTime)
            } else {
                listener.start(timer.id)
            }
        }
        binding.btnDelete.setOnClickListener {
            countDownTimer?.cancel()
            listener.delete(timer.id)
            binding.customView.setCurrent(0L)
            listener.updateCurrentTime(0L)
        }
    }

    private fun startTimer(timer: TimerModel) {

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timer.initialTime, UNIT_ONE_SEC) {
            val interval = UNIT_ONE_SEC

            override fun onTick(millisUntilFinished: Long) {
                timer.currentTime -= interval
                listener.updateCurrentTime(timer.currentTime)
                binding.tvTimer.text = timer.currentTime.displayTime()
                binding.customView.setCurrent(timer.currentTime)
                if (timer.currentTime <= 0L) {
                    onFinish()
                }
            }

            override fun onFinish() {
                finishTimer(timer)
            }
        }
        countDownTimer?.start()
        binding.btnStartStopRestart.text = "стоп"
        val context = binding.root.context
        binding.cvRoot.setCardBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )

        binding.indicator.isInvisible = false
        (binding.indicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer() {
        binding.btnStartStopRestart.text = "старт"
        countDownTimer?.cancel()


        binding.indicator.isInvisible = true
        (binding.indicator.background as? AnimationDrawable)?.stop()
    }

    // TODO : дополнительное состояние Finished
    private fun finishTimer(timer: TimerModel) {
        val context = binding.root.context
        binding.cvRoot.setCardBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.purple_200
            )
        )
        listener.stop(timer.id, timer.initialTime)
    }
}