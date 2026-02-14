package com.example.totrain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkoutDays(workoutDays: List<WorkoutDay>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercises(exercises: List<Exercise>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExerciseSets(exerciseSets: List<ExerciseSet>)

    @Insert
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert
    suspend fun insertExerciseSet(exerciseSet: ExerciseSet): Long

    @Update
    suspend fun updateExerciseSet(exerciseSet: ExerciseSet)

    @Update
    suspend fun updateExerciseSets(exerciseSets: List<ExerciseSet>)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Update
    suspend fun updateWorkoutDay(day: WorkoutDay)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExerciseSet(exerciseSet: ExerciseSet)

    @Delete
    suspend fun deleteExerciseSets(exerciseSets: List<ExerciseSet>)

    @Query("SELECT * FROM workout_days ORDER BY id ASC")
    fun getWorkoutDays(): Flow<List<WorkoutDay>>

    @Query("SELECT * FROM exercises WHERE dayId = :dayId ORDER BY id ASC")
    fun getExercisesForDay(dayId: Int): Flow<List<Exercise>>

    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY timestamp ASC")
    fun getAllExerciseSetsForExercise(exerciseId: Int): Flow<List<ExerciseSet>>

    @Query("SELECT MAX(timestamp) FROM exercise_sets WHERE exerciseId = :exerciseId")
    suspend fun getLatestTimestampForExercise(exerciseId: Int): Long?

    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId AND timestamp = :timestamp")
    suspend fun getExerciseSetsForTimestamp(exerciseId: Int, timestamp: Long): List<ExerciseSet>

    @Query("SELECT COUNT(*) FROM workout_days")
    suspend fun getWorkoutDayCount(): Int

    @Query("SELECT MAX(weight) FROM exercise_sets WHERE exerciseId = :exerciseId AND timestamp < :currentTimestamp")
    suspend fun getMaxWeightBefore(exerciseId: Int, currentTimestamp: Long): Int?
}