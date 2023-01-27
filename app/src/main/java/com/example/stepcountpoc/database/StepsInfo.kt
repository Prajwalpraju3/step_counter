package com.example.stepcountpoc.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_count_details")
data class StepsInfo(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "steps") val steps: Int?,
)
