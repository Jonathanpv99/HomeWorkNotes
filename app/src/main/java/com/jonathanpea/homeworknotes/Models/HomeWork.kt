package com.jonathanpea.homeworknotes.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_table")
class HomeWork (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val desc : String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "uriF") val uriF: String?,
    @ColumnInfo(name = "uriV") val uriV: String?
): java.io.Serializable