package com.example.totrain

import android.graphics.Color
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
    private var highlightedPosition = -1

    // ViewHolder остается без изменений
    class WorkoutDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewDayName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDayDescription)

        fun bind(day: WorkoutDay, onItemClicked: (WorkoutDay) -> Unit, onItemLongClicked: (WorkoutDay) -> Unit) {
            nameTextView.text = day.name
            descriptionTextView.text = day.description
            itemView.setOnClickListener { onItemClicked(day) }
            itemView.setOnLongClickListener {
                onItemLongClicked(day)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutDayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_day, parent, false)
        return WorkoutDayViewHolder(view)
    }

    override fun getItemCount() = days.size

    override fun onBindViewHolder(holder: WorkoutDayViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day, onItemClicked, onItemLongClicked)

        val card = holder.itemView as com.google.android.material.card.MaterialCardView

        if (position == highlightedPosition) {
            card.setCardBackgroundColor(Color.parseColor("#E3F2FD")) // мягкая подсветка
            card.strokeWidth = 3
            card.strokeColor = Color.parseColor("#2196F3")
        } else {
            card.setCardBackgroundColor(Color.WHITE)
            card.strokeWidth = 1
            card.strokeColor = Color.parseColor("#DDDDDD")
        }
    }

    /**
     * Обновляет данные в адаптере и позицию для подсветки.
     */
    fun updateData(newDays: List<WorkoutDay>, newHighlightedPosition: Int) {
        this.days = newDays
        this.highlightedPosition = newHighlightedPosition
        notifyDataSetChanged()
    }
}