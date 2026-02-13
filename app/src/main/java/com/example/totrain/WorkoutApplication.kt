package com.example.totrain

import android.app.Application

class WorkoutApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val repository: WorkoutRepository by lazy {
        WorkoutRepository(database.workoutDao())
    }
}
