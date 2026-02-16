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
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val workoutViewModel: WorkoutViewModel by viewModels {
        WorkoutViewModelFactory((application as WorkoutApplication).repository)
    }

    private lateinit var workoutAdapter: WorkoutDayAdapter
    private var highlightedDayId: Int? = null
    private var isInitialLogicDone = false
    private var isDialogShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        val cutoff = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        workoutViewModel.deleteOldData(cutoff)
    }
    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDays)
        workoutAdapter = WorkoutDayAdapter(
            onItemClicked = { day -> navigateToExercises(day) },
            onItemLongClicked = { day -> showEditDayDialog(day) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = workoutAdapter
    }

    private fun setupObservers() {
        workoutViewModel.allDays.observe(this) { days ->
            if (days.isNullOrEmpty()) {
                workoutAdapter.updateData(emptyList(), -1)
                return@observe
            }

            if (!isInitialLogicDone) {
                checkTodayAndAskIfNeeded(days)
                isInitialLogicDone = true
            } else {
                // Для последующих обновлений (например, после редактирования)
                // просто обновляем список, сохраняя подсветку.
                val currentPosition = days.indexOfFirst { it.id == highlightedDayId }
                workoutAdapter.updateData(days, currentPosition)
            }
        }
    }

    /**
     * Определяет текущий день, подсвечивает его или показывает диалог выбора.
     * Выполняется один раз при запуске.
     */
    private fun checkTodayAndAskIfNeeded(days: List<WorkoutDay>) {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val today = when (dayOfWeek) {
            Calendar.MONDAY -> days.find { it.name == "Понедельник" }
            Calendar.WEDNESDAY -> days.find { it.name == "Среда" }
            Calendar.FRIDAY -> days.find { it.name == "Пятница" }
            else -> null
        }

        val position = if (today != null) {
            highlightedDayId = today.id
            days.indexOf(today)
        } else {
            -1
        }

        workoutAdapter.updateData(
            newDays = days,
            newHighlightedPosition = position
        )

    }

    /**
     * Показывает диалог для выбора тренировочного дня.
     */
    private fun showDayChooserDialog(days: List<WorkoutDay>) {
        if (isDialogShowing) return
        isDialogShowing = true

        val dayDescriptions = days.map { it.description }.toTypedArray()

        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Какой тренировочный день вы хотите выбрать?")
            .setItems(dayDescriptions) { dialog, which ->
                val selectedDay = days[which]
                highlightedDayId = selectedDay.id
                workoutAdapter.updateData(days, which)

                dialog.dismiss()

                navigateToExercises(selectedDay)
            }
            .setOnDismissListener { isDialogShowing = false }
            .setCancelable(false) // Не даем закрыть диалог без выбора
            .show()
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

        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
    }
}