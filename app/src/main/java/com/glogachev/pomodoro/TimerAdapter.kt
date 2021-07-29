package com.glogachev.pomodoro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.glogachev.pomodoro.databinding.RvItemTimerBinding

class TimerAdapter(
    private val listener: StopwatchListener
) : ListAdapter<TimerModel, TimerViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemTimerBinding.inflate(inflater, parent, false)
        return TimerViewHolder(
            binding,
            listener = listener
        )
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) =
        holder.bind(getItem(position))
}

private val diffUtil = object : DiffUtil.ItemCallback<TimerModel>() {
    override fun areItemsTheSame(oldItem: TimerModel, newItem: TimerModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TimerModel, newItem: TimerModel): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: TimerModel, newItem: TimerModel) = Any()
}