package com.example.studybuddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "plans")
data class Plan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String = "",
    val date: String = "",
    val time: String = "",
    val note: String = "",
    val firebaseId: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {

    constructor() : this(0, "", "", "", "", "", 0)
}