package com.mnandor.wout.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mnandor.wout.data.entities.Exercise
import com.mnandor.wout.data.entities.Completion
import com.mnandor.wout.data.entities.KeyValue
import com.mnandor.wout.data.entities.Location


@Database(entities = [Exercise::class, Completion::class, Location::class, KeyValue::class], version = 2, exportSchema = false)
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
                ).addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance

                instance
            }
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE day_template (" +
                        "itemID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "exercise TEXT NOT NULL," +
                        "template TEXT NOT NULL," +
                        "UNIQUE (exercise, template))")
                database.execSQL("CREATE UNIQUE INDEX index_day_template_exercise_template ON day_template (exercise ASC, template ASC)")
            }
        }
    }
}