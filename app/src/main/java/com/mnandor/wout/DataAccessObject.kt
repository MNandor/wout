package com.mnandor.wout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DataAccessObject {
    // return Flow means suspend is not required
    @Query("SELECT * FROM exercise_template ORDER BY isDisabled ASC, name ASC")
    fun getTemplates(): Flow<List<ExerciseTemplate>>

    // todo suspend should work in this function
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addExerciseTemplate(exerciseTemplate: ExerciseTemplate)
}