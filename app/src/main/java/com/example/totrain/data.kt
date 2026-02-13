package com.example.totrain

import java.util.concurrent.TimeUnit

object data {
    val workoutDays = listOf(
        WorkoutDay(1, "Понедельник", "Грудь и трицепс"),
        WorkoutDay(2, "Среда", "Спина и бицепс"),
        WorkoutDay(3, "Пятница", "Ноги и плечи")
    )

    val exercises = listOf(
        Exercise(dayId = 1, name = "Жим штанги"),
        Exercise(dayId = 1, name = "Жим лёжа, гантели"),
        Exercise(dayId = 1, name = "Бабочка"),
        Exercise(dayId = 1, name = "Отжимания"),
        Exercise(dayId = 1, name = "Разводка гантелей"),
        Exercise(dayId = 2, name = "Тяга, верхний блок"),
        Exercise(dayId = 2, name = "Тяга, сидя к поясу"),
        Exercise(dayId = 2, name = "Тяга гантели к бедру"),
        Exercise(dayId = 2, name = "Становая тяга"),
        Exercise(dayId = 2, name = "Тяга штанги в наклоне"),
        Exercise(dayId = 3, name = "Подтягивания"),
        Exercise(dayId = 3, name = "Брусья"),
        Exercise(dayId = 3, name = "Кардио"),
        Exercise(dayId = 3, name = "Приседания"),
        Exercise(dayId = 3, name = "Планка")
    )

    private val lastWeek = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
    val exerciseSets = listOf(
        // Day 1
        ExerciseSet(exerciseId = 1, reps = 10, weight = 70, timestamp = lastWeek),
        ExerciseSet(exerciseId = 1, reps = 10, weight = 70, timestamp = lastWeek),
        ExerciseSet(exerciseId = 2, reps = 12, weight = 20, timestamp = lastWeek),
        ExerciseSet(exerciseId = 3, reps = 15, weight = 28, timestamp = lastWeek),
        ExerciseSet(exerciseId = 4, reps = 20, weight = 0, timestamp = lastWeek),
        ExerciseSet(exerciseId = 5, reps = 15, weight = 10, timestamp = lastWeek),
        // Day 2
        ExerciseSet(exerciseId = 6, reps = 12, weight = 45, timestamp = lastWeek),
        ExerciseSet(exerciseId = 7, reps = 12, weight = 42, timestamp = lastWeek),
        ExerciseSet(exerciseId = 8, reps = 10, weight = 15, timestamp = lastWeek),
        ExerciseSet(exerciseId = 9, reps = 8, weight = 90, timestamp = lastWeek),
        ExerciseSet(exerciseId = 10, reps = 10, weight = 45, timestamp = lastWeek),
        // Day 3
        ExerciseSet(exerciseId = 11, reps = 10, weight = 0, timestamp = lastWeek),
        ExerciseSet(exerciseId = 12, reps = 15, weight = 0, timestamp = lastWeek),
        ExerciseSet(exerciseId = 14, reps = 12, weight = 55, timestamp = lastWeek)
    )
}