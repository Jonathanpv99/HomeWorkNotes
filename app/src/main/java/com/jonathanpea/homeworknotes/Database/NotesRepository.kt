package com.jonathanpea.homeworknotes.Database

import androidx.lifecycle.LiveData
import com.jonathanpea.homeworknotes.Models.Note

class NotesRepository(private val noteDAO: NoteDAO) {

    val allNotes : LiveData<List<Note>> = noteDAO.getAllNotes()

    suspend fun  insert(note: Note){
        noteDAO.insert(note)
    }

    suspend fun delete(note: Note){
        noteDAO.delete(note)
    }

    suspend fun upgrade(note: Note){
        noteDAO.upgrade(note.id,note.title,note.note)
    }
}