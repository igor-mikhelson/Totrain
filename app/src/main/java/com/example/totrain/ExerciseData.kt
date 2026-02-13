package com.example.totrain

class ExerciseData {

    fun getExercisesForDay(dayId: Int): List<Exercise> {
        return when (dayId) {
            1 -> listOf(
                Exercise(dayId = 1, name = "Жим штанги"),
                Exercise(dayId = 1, name = "Жим лёжа, гантели"),
                Exercise(dayId = 1, name = "Бабочка"),
                Exercise(dayId = 1, name = "Отжимания"),
                Exercise(dayId = 1, name = "Разводка гантелей")
            )
            2 -> listOf(
                Exercise(dayId = 2, name = "Тяга, верхний блок"),
                Exercise(dayId = 2, name = "Тяга, сидя к поясу"),
                Exercise(dayId = 2, name = "Тяга гантели к бедру"),
                Exercise(dayId = 2, name = "Гиперэкстензия"),
                Exercise(dayId = 2, name = "Тяга штанги в наклоне")

            )
            3 -> listOf(
                Exercise(dayId = 3, name = "Подтягивания"),
                Exercise(dayId = 3, name = "Брусья"),
                Exercise(dayId = 3, name = "Кардио"),
                Exercise(dayId = 3, name = "Приседания"),
                Exercise(dayId = 3, name = "Планка")
            )
            else -> emptyList()
        }
    }
}