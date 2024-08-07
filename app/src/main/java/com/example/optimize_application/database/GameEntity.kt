package com.example.optimize_application.database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_table")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var gameName: String,
    //For storing image I have to set image varible to ByteArray
    var imageGame: ByteArray,
    var gameUrl: String,
    var isOptimized : Boolean = false
)
