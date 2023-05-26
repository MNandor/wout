package com.mnandor.wout.data

import androidx.room.*
import com.mnandor.wout.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DataAccessObject {
    // return Flow means suspend is not required
    @Query("SELECT * FROM exercise ORDER BY isDisabled ASC, name ASC")
    fun getTemplates(): Flow<List<Exercise>>

    @Query("SELECT exercise.* FROM exercise LEFT JOIN location ON exercise == name WHERE (location = :filter or :filter = 'All') AND isDisabled != 1 GROUP BY exercise.name ORDER BY name ASC")
    fun getNonhiddenTemplates(filter: String): Flow<List<Exercise>>

    // todo suspend should work in this function
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addExerciseTemplate(exercise: Exercise)

    @Query("SELECT * FROM completion ORDER BY timestamp ASC")
    fun getLogs(): Flow<List<Completion>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addExerciseLog(completion: Completion)

    @Delete()
    fun deleteLog(completion: Completion)

    @Update()
    fun updateLog(completion: Completion)

    @Query("SELECT reps FROM completion WHERE exercise = :exerciseTemplate and reps is not null ORDER BY timestamp DESC LIMIT :count")
    fun getTrendlinePoints(exerciseTemplate: String, count:Int): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addDayTemplate(template: Location)

    @Delete()
    fun deleteDayTemplate(template: Location)

    @Query("SELECT * FROM location ORDER BY location DESC, exercise ASC")
    fun getDayTemplates(): Flow<List<Location>>

    @Query("SELECT * FROM location GROUP BY location ORDER BY location DESC, exercise ASC")
    fun getUniqueDayTemplates(): Flow<List<Location>>

    @Query("SELECT DISTINCT location FROM location")
    fun getDayTemplateNames(): Flow<List<String>>

    @Query("SELECT value FROM kvpairs WHERE `key` = :key")
    fun getValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setValue(keyValue: KeyValue)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setDaySchedule(dayScheduleDay: ScheduleDay)

    @Query("SELECT * from scheduleday ORDER BY day")
    fun getDaySchedules(): Flow<List<ScheduleDay>>

    @Query("SELECT * from scheduleday WHERE day = :day")
    fun getDayByNumber(day: Int): ScheduleDay

    @Query("UPDATE scheduleday SET location = :location WHERE day = :day")
    fun updateScheduleDay(location: Int, day: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addScheduleDay(scheduleDay: ScheduleDay)

    @Query("DELETE FROM scheduleday WHERE day = :day")
    fun removeScheduleDayData(day: Int)

    @Query("SELECT * FROM location WHERE itemID = :id")
    fun getLocationByID(id: Int): Location

    @Query("UPDATE exercise SET name = :newName WHERE name = :oldName")
    fun updateExerciseName(oldName: String, newName: String)

    @Query("UPDATE exercise SET notes = :notes WHERE name = :name")
    fun updateExerciseNotes(name:String, notes:String)

    @Query("UPDATE completion SET exercise = :newName WHERE exercise = :oldName")
    fun updateExerciseNameInCompletions(oldName: String, newName: String)

    @Delete
    fun deleteExerciseTemplate(exercise: Exercise)

    @Query("SELECT * FROM location WHERE location = :name")
    fun getLocationByName(name:String):Location

    @Query("DELETE FROM scheduleday WHERE day = :id")
    fun deleteScheduleDayByID(id: Int)

    @Update
    fun updateExercise(exercise: Exercise)

}