package com.mnandor.wout

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [ExerciseTemplate::class, ExerciseLog::class, TemplateItem::class], version = 2, exportSchema = false)
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
            }
        }
    }
}