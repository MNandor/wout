package com.mnandor.wout

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExerciseTemplate::class, ExerciseLog::class], version = 1, exportSchema = false)
public abstract class ExerciseDatabase : RoomDatabase() {

    abstract fun dao(): DataAccessObject

    companion object {
        @Volatile
        private var INSTANCE: ExerciseDatabase? = null

        fun getDatabase(context: Context): ExerciseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExerciseDatabase::class.java,
                    "exercise_database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}