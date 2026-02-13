package com.example.totrain

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExercisesActivity : AppCompatActivity() {

    private val workoutViewModel: WorkoutViewModel by viewModels {
        WorkoutViewModelFactory((application as WorkoutApplication).repository)
    }

    private lateinit var adapter: ArrayAdapter<String>
    private val exercises = mutableListOf<Exercise>()
    private var dayId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dayId = intent.getIntExtra("DAY_ID", -1)
        val dayName = intent.getStringExtra("DAY_NAME")
        supportActionBar?.title = dayName

        val listView = findViewById<ListView>(R.id.listViewExercises)
        val fab = findViewById<FloatingActionButton>(R.id.fabAddExercise)

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            mutableListOf()
        )
        listView.adapter = adapter

        // Получаем данные через ViewModel
        workoutViewModel.getExercisesForDay(dayId).observe(this) { exerciseList ->
            exercises.clear()
            exercises.addAll(exerciseList)

            adapter.clear()
            adapter.addAll(exerciseList.map { it.name })
            adapter.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedExercise = exercises[position]
            val intent = Intent(this, ExerciseDetailActivity::class.java)
            intent.putExtra("EXERCISE_ID", selectedExercise.id)
            intent.putExtra("EXERCISE_NAME", selectedExercise.name)
            startActivity(intent)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            showEditOrDeleteDialog(exercises[position])
            true
        }

        fab.setOnClickListener {
            showAddExerciseDialog()
        }
    }

    private fun showAddExerciseDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle("Добавить упражнение")

        val view = layoutInflater.inflate(R.layout.dialog_edit_exercise, null)
        builder.setView(view)

        val exerciseNameEditText = view.findViewById<EditText>(R.id.editTextExerciseName)

        builder.setPositiveButton("Сохранить") { _, _ ->
            val newName = exerciseNameEditText.text.toString()
            if (newName.isNotBlank()) {
                val newExercise = Exercise(dayId = dayId, name = newName)
                workoutViewModel.insertExercise(newExercise)
            }
        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showEditOrDeleteDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle("Редактировать или удалить?")

        val view = layoutInflater.inflate(R.layout.dialog_edit_exercise, null)
        builder.setView(view)

        val exerciseNameEditText = view.findViewById<EditText>(R.id.editTextExerciseName)
        exerciseNameEditText.setText(exercise.name)

        builder.setPositiveButton("Сохранить") { _, _ ->
            val newName = exerciseNameEditText.text.toString()
            if (newName.isNotBlank()) {
                val updatedExercise = exercise.copy(name = newName)
                workoutViewModel.updateExercise(updatedExercise)
            }
        }

        builder.setNegativeButton("Удалить") { _, _ ->
            showDeleteConfirmationDialog(exercise)
        }

        builder.setNeutralButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showDeleteConfirmationDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle("Удалить упражнение?")
        builder.setMessage("Вы уверены, что хотите удалить упражнение \"${exercise.name}\"?")

        builder.setPositiveButton("Да") { _, _ ->
            workoutViewModel.deleteExercise(exercise)
        }

        builder.setNegativeButton("Нет") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
