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
}