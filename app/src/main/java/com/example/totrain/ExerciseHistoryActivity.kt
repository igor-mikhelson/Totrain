package com.example.totrain

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseHistoryActivity : AppCompatActivity() {

    private var exerciseId: Int = -1

    private val workoutViewModel: WorkoutViewModel by viewModels {
        WorkoutViewModelFactory((application as WorkoutApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_history)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        exerciseId = intent.getIntExtra("EXERCISE_ID", -1)

        val lineChart = findViewById<LineChart>(R.id.lineChart)

        workoutViewModel
            .getAllExerciseSetsForExercise(exerciseId)
            .observe(this) { allSets ->

                val entries = mutableListOf<Entry>()

                allSets.groupBy { it.timestamp.toSimpleDate() }
                    .forEach { (_, sets) ->

                        val maxWeight =
                            sets.maxByOrNull { it.weight }?.weight ?: 0

                        if (maxWeight > 0) {
                            val timestamp =
                                sets.minOfOrNull { it.timestamp } ?: 0L

                            entries.add(
                                Entry(
                                    timestamp.toFloat(),
                                    maxWeight.toFloat()
                                )
                            )
                        }
                    }

                entries.sortBy { it.x }

                val dataSet = LineDataSet(entries, "Max Weight")
                dataSet.color = Color.RED
                dataSet.valueTextColor = Color.GRAY

                lineChart.data = LineData(dataSet)
                setupChart(lineChart)
                lineChart.invalidate()
            }
    }

    private fun setupChart(lineChart: LineChart) {
        lineChart.xAxis.valueFormatter = DateAxisFormatter()
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.textColor = Color.GRAY
        lineChart.axisLeft.textColor = Color.GRAY
        lineChart.axisRight.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.legend.textColor = Color.GRAY
    }

    private fun Long.toSimpleDate(): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        return sdf.format(Date(this))
    }

    class DateAxisFormatter : ValueFormatter() {
        private val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        override fun getFormattedValue(value: Float): String {
            return sdf.format(Date(value.toLong()))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
