package com.jonathanpea.homeworknotes.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jonathanpea.homeworknotes.Models.HomeWork
import com.jonathanpea.homeworknotes.Models.Note
import com.jonathanpea.homeworknotes.utilies.DATABASE_NAME

@Database(entities = arrayOf(Note::class,HomeWork::class), version =1)
abstract class NoteDatabase : RoomDatabase(){

    abstract fun getNoteDao() : NoteDAO
    abstract fun getHomeDao() : HomeWorkDAO
    companion object{
        @Volatile
        private var INSTANCE : NoteDatabase? = null

        fun getDatabase(context: Context) : NoteDatabase{
            return INSTANCE ?: synchronized(this){

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance
                instance
            }


        }
    }

}