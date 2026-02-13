package com.example.totrain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutDayAdapter(
    private val onItemClicked: (WorkoutDay) -> Unit,
    private val onItemLongClicked: (WorkoutDay) -> Unit
) : RecyclerView.Adapter<WorkoutDayAdapter.WorkoutDayViewHolder>() {

    private var days: List<WorkoutDay> = emptyList()
    private var highlightedPosition: Int = -1

    inner class WorkoutDayViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val dayName: TextView = itemView.findViewById(R.id.textViewDayName)
        private val dayDescription: TextView = itemView.findViewById(R.id.textViewDayDescription)

        fun bind(day: WorkoutDay, position: Int) {
            dayName.text = day.name
            dayDescription.text = day.description

            itemView.alpha =
                if (position == highlightedPosition) 1.0f else 0.5f

            itemView.setOnClickListener {
                onItemClicked(day)
            }

            itemView.setOnLongClickListener {
                onItemLongClicked(day)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_day, parent, false)
        return WorkoutDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutDayViewHolder, position: Int) {
        holder.bind(days[position], position)
    }

    override fun getItemCount(): Int = days.size

    fun updateData(newDays: List<WorkoutDay>, newHighlightedPosition: Int) {
        days = newDays
        highlightedPosition = newHighlightedPosition
        notifyDataSetChanged()
    }
}
