package com.example.totrain

import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    fun getDays(): Flow<List<WorkoutDay>> =
        workoutDao.getWorkoutDays()

    fun getExercisesForDay(dayId: Int): Flow<List<Exercise>> =
        workoutDao.getExercisesForDay(dayId)

    fun getAllExerciseSetsForExercise(exerciseId: Int): Flow<List<ExerciseSet>> =
        workoutDao.getAllExerciseSetsForExercise(exerciseId)

    suspend fun insertExercise(exercise: Exercise) {
        workoutDao.insertExercise(exercise)
    }

    suspend fun updateExercise(exercise: Exercise) {
        workoutDao.updateExercise(exercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        workoutDao.deleteExercise(exercise)
    }

    suspend fun updateDay(day: WorkoutDay) {
        workoutDao.updateWorkoutDay(day)
    }

    suspend fun insertExerciseSet(exerciseSet: ExerciseSet) {
        workoutDao.insertExerciseSet(exerciseSet)
    }

    suspend fun updateExerciseSets(sets: List<ExerciseSet>) {
        workoutDao.updateExerciseSets(sets)
    }

    suspend fun deleteExerciseSets(sets: List<ExerciseSet>) {
        workoutDao.deleteExerciseSets(sets)
    }
}
