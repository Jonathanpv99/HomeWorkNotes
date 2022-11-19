package com.jonathanpea.homeworknotes.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jonathanpea.homeworknotes.Models.Note

@Dao
interface NoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)
    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM notes_table ORDER BY id ASC")
    fun getAllNotes() : LiveData<List<Note>>

    @Query("UPDATE notes_table set title = :title, note = :note WHERE id= :id")
    fun upgrade(id : Int?, title : String?, note : String?)

}