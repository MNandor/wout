package com.mnandor.wout

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DataAccessObject {
    // return Flow means suspend is not required
    @Query("SELECT * FROM exercise_template ORDER BY isDisabled ASC, name ASC")
    fun getTemplates(): Flow<List<ExerciseTemplate>>

    @Query("SELECT exercise_template.* FROM exercise_template LEFT JOIN day_template ON exercise == name WHERE (template = :filter or :filter = 'All') AND isDisabled != 1 ORDER BY name ASC")
    fun getNonhiddenTemplates(filter: String): Flow<List<ExerciseTemplate>>

    // todo suspend should work in this function
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addExerciseTemplate(exerciseTemplate: ExerciseTemplate)

    @Query("SELECT * FROM exercise_log ORDER BY timestamp ASC")
    fun getLogs(): Flow<List<ExerciseLog>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addExerciseLog(exerciseLog: ExerciseLog)

    @Delete()
    fun deleteLog(exerciseLog: ExerciseLog)

    @Update()
    fun updateLog(exerciseLog: ExerciseLog)

    @Query("SELECT reps FROM exercise_log WHERE exercise = :exerciseTemplate and reps is not null ORDER BY timestamp DESC LIMIT :count")
    fun getTrendlinePoints(exerciseTemplate: String, count:Int): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addDayTemplate(template: TemplateItem)

    @Delete()
    fun deleteDayTemplate(template: TemplateItem)

    @Query("SELECT * FROM day_template ORDER BY template DESC, exercise ASC")
    fun getDayTemplates(): Flow<List<TemplateItem>>

    @Query("SELECT DISTINCT template FROM day_template")
    fun getDayTemplateNames(): Flow<List<String>>

}