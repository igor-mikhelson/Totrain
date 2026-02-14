package com.example.totrain

import android.content.Context
import android.graphics.Typeface
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class ExerciseSetAdapter(context: Context, sets: List<ExerciseSet>, private var historicMaxWeight: Int) :
    ArrayAdapter<ExerciseSet>(context, 0, sets) {

    private val selectedItems = SparseBooleanArray()
    private var isSelectionMode = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_set, parent, false)
        val set = getItem(position)

        val repsTextView = view.findViewById<TextView>(R.id.textViewReps)
        val weightTextView = view.findViewById<TextView>(R.id.textViewWeight)

        if (set != null) {
            repsTextView.text = "${set.reps} повт."
            weightTextView.text = "${set.weight} кг"

            if (set.weight > historicMaxWeight) {
                weightTextView.setTextColor(ContextCompat.getColor(context, R.color.record_red))
                weightTextView.setTypeface(null, Typeface.BOLD)
            } else {
                weightTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
                weightTextView.setTypeface(null, Typeface.NORMAL)
            }
        }

        view.setBackgroundColor(
            if (selectedItems[position]) ContextCompat.getColor(context, R.color.selected_item) else ContextCompat.getColor(context, android.R.color.transparent)
        )

        return view
    }

    fun updateData(newSets: List<ExerciseSet>, newHistoricMaxWeight: Int) {
        this.historicMaxWeight = newHistoricMaxWeight
        clear()
        addAll(newSets)
    }

    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        if (!enabled) {
            selectedItems.clear()
        }
        notifyDataSetChanged()
    }

    fun toggleSelection(position: Int) {
        if (selectedItems[position]) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyDataSetChanged()
    }

    fun getSelectedCount(): Int = selectedItems.size()

    fun getSelectedItems(): List<ExerciseSet> {
        val items = mutableListOf<ExerciseSet>()
        for (i in 0 until count) {
            if (selectedItems[i]) {
                getItem(i)?.let { items.add(it) }
            }
        }
        return items
    }
}