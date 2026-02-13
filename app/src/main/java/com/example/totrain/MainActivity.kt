package com.example.totrain

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val workoutViewModel: WorkoutViewModel by viewModels {
        WorkoutViewModelFactory((application as WorkoutApplication).repository)
    }

    private lateinit var workoutAdapter: WorkoutDayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDays)

        workoutAdapter = WorkoutDayAdapter(
            onItemClicked = { day -> navigateToExercises(day) },
            onItemLongClicked = { day -> showEditDayDialog(day) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = workoutAdapter

        workoutViewModel.allDays.observe(this) { days ->
            days?.let {

                val calendar = Calendar.getInstance()
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                val highlightedPosition = when (dayOfWeek) {
                    Calendar.MONDAY -> it.indexOfFirst { day -> day.name == "Понедельник" }
                    Calendar.WEDNESDAY -> it.indexOfFirst { day -> day.name == "Среда" }
                    Calendar.FRIDAY -> it.indexOfFirst { day -> day.name == "Пятница" }
                    else -> -1
                }

                workoutAdapter.updateData(it, highlightedPosition)
            }
        }
    }

    private fun showEditDayDialog(day: WorkoutDay) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle("Редактировать день")

        val view = layoutInflater.inflate(R.layout.dialog_edit_day, null)
        builder.setView(view)

        val dayNameEditText = view.findViewById<EditText>(R.id.editTextDayName)
        val dayDescriptionEditText = view.findViewById<EditText>(R.id.editTextDayDescription)

        dayNameEditText.setText(day.name)
        dayDescriptionEditText.setText(day.description)

        builder.setPositiveButton("Сохранить") { _, _ ->
            val newName = dayNameEditText.text.toString()
            val newDescription = dayDescriptionEditText.text.toString()

            if (newName.isNotBlank() && newDescription.isNotBlank()) {
                val updatedDay = day.copy(name = newName, description = newDescription)
                workoutViewModel.updateDay(updatedDay)
            }
        }

        builder.setNegativeButton("Отмена", null)
        builder.show()
    }

    private fun navigateToExercises(day: WorkoutDay) {
        val intent = Intent(this, ExercisesActivity::class.java)
        intent.putExtra("DAY_ID", day.id)
        intent.putExtra("DAY_NAME", day.description)
        startActivity(intent)
    }
}
