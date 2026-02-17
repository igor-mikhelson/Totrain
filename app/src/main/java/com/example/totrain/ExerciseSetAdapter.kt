package com.example.totrain

import android.content.Context
import android.graphics.Typeface
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ExerciseSetAdapter(
    private val context: Context,
    private val listener: OnItemInteractionListener,
    private var historicMaxWeight: Int
) : ListAdapter<ExerciseSet, ExerciseSetAdapter.ExerciseSetViewHolder>(ExerciseSetDiffCallback()) {

    private val selectedItems = SparseBooleanArray()
    private var isSelectionMode = false

    interface OnItemInteractionListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseSetViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_set, parent, false)
        return ExerciseSetViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseSetViewHolder, position: Int) {
        val isSelected = selectedItems.get(position, false)
        val set = getItem(position)
        holder.bind(set, isSelected, isSelectionMode)

    }

    inner class ExerciseSetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val repsTextView: TextView = itemView.findViewById(R.id.textViewReps)
        private val weightTextView: TextView = itemView.findViewById(R.id.textViewWeight)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_set)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(position)
                }
                true
            }
        }

        fun bind(set: ExerciseSet, isSelected: Boolean, isSelectionMode: Boolean) {
            repsTextView.text = "${set.reps} повт."
            weightTextView.text = "${set.weight} кг"

            if (set.weight > historicMaxWeight) {
                weightTextView.setTextColor(ContextCompat.getColor(context, R.color.record_red))
                weightTextView.setTypeface(null, Typeface.BOLD)
            } else {
                weightTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
                weightTextView.setTypeface(null, Typeface.NORMAL)
            }

            // Управление состоянием выбора
            checkBox.isChecked = isSelected

            if (isSelectionMode) {
                checkBox.visibility = View.VISIBLE
            } else {
                checkBox.visibility = View.GONE
            }

            // Устанавливаем цвет фона в зависимости от того, выбран ли элемент
            if (isSelected) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.selected_item))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            }
        }
    }

    fun setSelectionMode(enabled: Boolean) {
        val wasInSelectionMode = isSelectionMode
        isSelectionMode = enabled
        if (wasInSelectionMode && !isSelectionMode) {
            clearSelection()
        } else if (isSelectionMode) {
            notifyDataSetChanged() // Invalidate all items to show checkboxes
        }
    }

    fun toggleSelection(position: Int) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }


    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }


    fun getSelectedCount(): Int = selectedItems.size()

    private fun getSelectedPositions(): List<Int> {
        val items = mutableListOf<Int>()
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    fun getSelectedItems(): List<ExerciseSet> {
        return getSelectedPositions().mapNotNull { getItem(it) }
    }

    private fun isSelected(position: Int): Boolean = selectedItems[position]

    fun updateHistoricMaxWeight(newHistoricMaxWeight: Int) {
        historicMaxWeight = newHistoricMaxWeight
        notifyDataSetChanged() // Assuming this can affect all visible items
    }
}

class ExerciseSetDiffCallback : DiffUtil.ItemCallback<ExerciseSet>() {
    override fun areItemsTheSame(oldItem: ExerciseSet, newItem: ExerciseSet): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExerciseSet, newItem: ExerciseSet): Boolean {
        return oldItem == newItem
    }
}
