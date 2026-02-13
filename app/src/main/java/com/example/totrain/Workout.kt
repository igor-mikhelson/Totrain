package com.example.totrain

data class Workout(
    val days: List<WorkoutDay> = listOf(
        //пример
        WorkoutDay(1, "Понедельник", "Грудь и трицепс"),
        WorkoutDay(2, "Среда", "Спина и бицепс"),
        WorkoutDay(3, "Пятница", "Ноги и плечи")
    )
)
