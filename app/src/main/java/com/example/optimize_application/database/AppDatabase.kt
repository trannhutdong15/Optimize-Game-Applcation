package com.example.optimize_application.database

import android.content.Context


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


//Annotation TypeConverters to connect Converters class to Appdatabase
@Database(entities = [UserEntity::class, GameEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    }
