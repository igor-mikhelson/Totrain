package com.example.totrain

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class ExerciseDetailActivity : AppCompatActivity() {

    private val workoutViewModel: WorkoutViewModel by viewModels {
        WorkoutViewModelFactory((application as WorkoutApplication).repository)
    }

    private lateinit var adapter: ExerciseSetAdapter
    private var exerciseId: Int = -1
    private var historicMaxWeight: Int = 0

    private var actionMode: ActionMode? = null
    private var timer: CountDownTimer? = null
    private var timerDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        exerciseId = intent.getIntExtra("EXERCISE_ID", -1)
        val exerciseName = intent.getStringExtra("EXERCISE_NAME")
        supportActionBar?.title = exerciseName

        val textViewExerciseName = findViewById<TextView>(R.id.textViewExerciseName)
        textViewExerciseName.text = exerciseName

        val listView = findViewById<ListView>(R.id.listViewSets)
        val layoutLastWorkout = findViewById<LinearLayout>(R.id.layoutLastWorkout)
        val textViewLastWorkoutSets = findViewById<TextView>(R.id.textViewLastWorkoutSets)
        val textViewLastWorkoutLabel = findViewById<TextView>(R.id.textViewLastWorkoutLabel)

        val footer = layoutInflater.inflate(R.layout.add_set_footer, listView, false)
        listView.addFooterView(footer, null, false)
        val repsEditText = footer.findViewById<EditText>(R.id.editTextNewReps)
        val weightEditText = footer.findViewById<EditText>(R.id.editTextNewWeight)
        val addButton = footer.findViewById<Button>(R.id.buttonAddSet)

        adapter = ExerciseSetAdapter(this, mutableListOf(), 0)
        listView.adapter = adapter

        workoutViewModel.getAllExerciseSetsForExercise(exerciseId).observe(this) { allSets ->
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val todaySets = allSets.filter { it.timestamp >= todayStart }
            val pastSets = allSets.filter { it.timestamp < todayStart }

            adapter.updateData(todaySets, pastSets.maxByOrNull { it.weight }?.weight ?: 0)

            if (pastSets.isEmpty()) {
                layoutLastWorkout.visibility = View.GONE
                return@observe
            }

// группируем по дате (обнуляем время)
            val groupedByDate = pastSets.groupBy { set ->
                Calendar.getInstance().apply {
                    timeInMillis = set.timestamp
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

            val lastWorkoutDate = groupedByDate.keys.maxOrNull()

            if (lastWorkoutDate != null) {
                val formattedDate = formatLastWorkoutDate(lastWorkoutDate)
                val fullText = getString(R.string.last_workout_label, formattedDate)
                val spannable = SpannableStringBuilder(fullText)
                val dateColor = Color.parseColor("#2E7D32")

                val startIndex = fullText.indexOf(formattedDate)
                if (startIndex != -1) {
                    spannable.setSpan(
                        ForegroundColorSpan(dateColor),
                        startIndex,
                        startIndex + formattedDate.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                textViewLastWorkoutLabel.text = spannable

                val lastWorkoutSets = groupedByDate[lastWorkoutDate]
                    ?.sortedBy { it.timestamp }
                    ?: emptyList()

                // максимум ДО этой тренировки
                val setsBeforeLastWorkout = pastSets.filter { it.timestamp < lastWorkoutDate }
                val maxWeightBeforeLastWorkout =
                    setsBeforeLastWorkout.maxByOrNull { it.weight }?.weight ?: 0

                val spannableSets = buildSpannableForLastWorkout(
                    lastWorkoutSets,
                    maxWeightBeforeLastWorkout
                )

                textViewLastWorkoutSets.text = spannableSets
                layoutLastWorkout.visibility = View.VISIBLE
            } else {
                layoutLastWorkout.visibility = View.GONE
            }
        }

        val fabStartTimer = findViewById<FloatingActionButton>(R.id.fab_start_timer)
        fabStartTimer.setOnClickListener {
            startTimer()
        }

        val addOrUpdateAction = {
            val newRepsStr = repsEditText.text.toString()
            val newWeightStr = weightEditText.text.toString()

            if (actionMode == null) {
                if (newRepsStr.isNotBlank() || newWeightStr.isNotBlank()) {
                    val set = ExerciseSet(
                        exerciseId = exerciseId,
                        reps = newRepsStr.toIntOrNull() ?: 0,
                        weight = newWeightStr.toIntOrNull() ?: 0,
                        timestamp = System.currentTimeMillis()
                    )
                    workoutViewModel.insertExerciseSet(set)
                    //startTimer()
                    hideKeyboard()
                }
            } else {
                val selectedItems = adapter.getSelectedItems().toList()
                if (selectedItems.isNotEmpty() && (newRepsStr.isNotBlank() || newWeightStr.isNotBlank())) {
                    val updatedSets = selectedItems.map {
                        it.copy(
                            reps = newRepsStr.toIntOrNull() ?: it.reps,
                            weight = newWeightStr.toIntOrNull() ?: it.weight
                        )
                    }
                    workoutViewModel.updateExerciseSets(updatedSets)
                    hideKeyboard()
                    actionMode?.finish()
                } else {
                    hideKeyboard()
                    actionMode?.finish()
                }
            }

            repsEditText.text.clear()
            weightEditText.text.clear()
        }

        addButton.setOnClickListener { addOrUpdateAction() }
        weightEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addOrUpdateAction()
                true
            } else false
        }

        setupMultiChoiceModeListener(listView, addButton, repsEditText)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_exercise_detail, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history -> {
                val intent = Intent(this, ExerciseHistoryActivity::class.java)
                intent.putExtra("EXERCISE_ID", exerciseId)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun buildSpannableForLastWorkout(
        lastWorkoutSets: List<ExerciseSet>,
        maxWeightBefore: Int
    ): SpannableStringBuilder {

        val spannable = SpannableStringBuilder()
        val redColor = ContextCompat.getColor(this, R.color.record_red)

        lastWorkoutSets.forEach { set ->
            val lineStart = spannable.length
            val line = "- ${set.reps} повт. x ${set.weight} кг\n"
            spannable.append(line)

            if (set.weight > maxWeightBefore) {
                val weightString = "${set.weight}"
                val weightStartInLine = line.indexOf(weightString)
                if (weightStartInLine != -1) {
                    val spanStart = lineStart + weightStartInLine
                    val spanEnd = spanStart + weightString.length
                    spannable.setSpan(
                        ForegroundColorSpan(redColor),
                        spanStart,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        spanStart,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        if (spannable.isNotEmpty()) {
            spannable.delete(spannable.length - 1, spannable.length)
        }

        return spannable
    }

    private fun setupMultiChoiceModeListener(
        listView: ListView,
        addButton: Button,
        repsEditText: EditText
    ) {
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        listView.setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                actionMode = mode
                mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
                adapter.setSelectionMode(true)
                addButton.text = "Применить"
                repsEditText.requestFocus()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                menu.findItem(R.id.action_delete).isVisible =
                    adapter.getSelectedCount() > 0
                return true
            }

            override fun onActionItemClicked(
                mode: ActionMode,
                item: MenuItem
            ): Boolean {
                return when (item.itemId) {
                    R.id.action_delete -> {
                        showDeleteConfirmationDialog(
                            adapter.getSelectedItems(),
                            mode
                        )
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                actionMode = null
                adapter.setSelectionMode(false)
                addButton.text = "Добавить"
            }

            override fun onItemCheckedStateChanged(
                mode: ActionMode,
                position: Int,
                id: Long,
                checked: Boolean
            ) {
                if (position < adapter.count) {
                    adapter.toggleSelection(position)
                    mode.title = "Выбрано: ${adapter.getSelectedCount()}"
                    mode.invalidate()
                }
            }
        })
    }

    private fun showDeleteConfirmationDialog(
        setsToDelete: List<ExerciseSet>,
        mode: ActionMode
    ) {
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Удалить подходы?")
            .setMessage("Вы уверены, что хотите удалить ${setsToDelete.size} выбранных подхода(ов)?")
            .setPositiveButton("Да") { _, _ ->
                workoutViewModel.deleteExerciseSets(setsToDelete)
                mode.finish()
            }
            .setNegativeButton("Нет") { _, _ ->
                mode.finish()
            }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        timerDialog?.dismiss()
        timer?.cancel()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun startTimer() {
        timerDialog?.dismiss()
        timer?.cancel()

        timerDialog = Dialog(
            this,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        ).apply {

            setContentView(R.layout.dialog_timer)
            val timerTextView =
                findViewById<TextView>(R.id.dialog_timer_text)
            val dismissButton =
                findViewById<ImageButton>(R.id.button_dismiss_timer)

            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            timer = object : CountDownTimer(120000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutes = (millisUntilFinished / 1000) / 60
                    val seconds = (millisUntilFinished / 1000) % 60
                    timerTextView.text =
                        String.format("%02d:%02d", minutes, seconds)
                }

                override fun onFinish() {
                    dismiss()
                }
            }.start()

            dismissButton.setOnClickListener { dismiss() }
            setOnDismissListener { timer?.cancel() }
        }

        timerDialog?.show()
    }

    private fun formatLastWorkoutDate(timestamp: Long): String {
        val lastWorkoutLocalDate = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val today = LocalDate.now(ZoneId.systemDefault())

        val pattern = if (lastWorkoutLocalDate.year == today.year) {
            "d MMMM"
        } else {
            "d MMMM yyyy"
        }
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale("ru"))

        return lastWorkoutLocalDate.format(formatter)
    }
}
