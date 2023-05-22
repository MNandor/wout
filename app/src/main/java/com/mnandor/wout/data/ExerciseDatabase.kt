package com.mnandor.wout.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mnandor.wout.data.entities.*


@Database(entities = [Exercise::class, Completion::class, Location::class, KeyValue::class, ScheduleDay::class], version = 3, exportSchema = false)
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
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE exercise " +
                        "ADD notes TEXT DEFAULT NULL")

            }
        }
    }
}