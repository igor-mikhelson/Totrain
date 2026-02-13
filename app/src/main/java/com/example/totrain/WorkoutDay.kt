package com.example.totrain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_days")
data class WorkoutDay(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String
)
