package com.example.totrain

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val allDays: LiveData<List<WorkoutDay>> =
        repository.getDays().asLiveData()

    fun getExercisesForDay(dayId: Int): LiveData<List<Exercise>> =
        repository.getExercisesForDay(dayId).asLiveData()

    fun getAllExerciseSetsForExercise(exerciseId: Int): LiveData<List<ExerciseSet>> =
        repository.getAllExerciseSetsForExercise(exerciseId).asLiveData()

    fun insertExercise(exercise: Exercise) = viewModelScope.launch {
        repository.insertExercise(exercise)
    }

    fun updateExercise(exercise: Exercise) = viewModelScope.launch {
        repository.updateExercise(exercise)
    }

    fun deleteExercise(exercise: Exercise) = viewModelScope.launch {
        repository.deleteExercise(exercise)
    }

    fun updateDay(day: WorkoutDay) = viewModelScope.launch {
        repository.updateDay(day)
    }
    fun updateExerciseSets(sets: List<ExerciseSet>) = viewModelScope.launch {
        repository.updateExerciseSets(sets)
    }

    fun deleteExerciseSets(sets: List<ExerciseSet>) = viewModelScope.launch {
        repository.deleteExerciseSets(sets)
    }

    fun insertExerciseSet(exerciseSet: ExerciseSet) = viewModelScope.launch {
        repository.insertExerciseSet(exerciseSet)
    }
}

class WorkoutViewModelFactory(
    private val repository: WorkoutRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
