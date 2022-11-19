package com.jonathanpea.homeworknotes.Adapter

import android.content.Context
import android.location.GnssAntennaInfo.Listener
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jonathanpea.homeworknotes.Models.Note
import com.jonathanpea.homeworknotes.R
import kotlin.random.Random

class NotesAdapter(private val context : Context, val listener: NotesItemclickListener) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val NotesList = ArrayList<Note>()
    private val fullList = ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item,parent,false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = NotesList[position]
        holder.title.text = currentNote.title
        holder.title.isSelected = true

        holder.note_tv.text = currentNote.note
        holder.date.text = currentNote.date
        holder.date.isSelected = true

        holder.notes_layout.setCardBackgroundColor(holder.itemView.resources.getColor(RandomColor(),null))

        holder.notes_layout.setOnClickListener {
            listener.onItemClicked(NotesList[holder.adapterPosition])
        }

        holder.notes_layout.setOnLongClickListener {
            listener.onLongitemClicked(NotesList[holder.adapterPosition],holder.notes_layout)
            true
        }
    }

    override fun getItemCount(): Int {
        return NotesList.size

    }

    fun updateList(newList : List<Note>){

        fullList.clear()
        fullList.addAll(newList)

        NotesList.clear()
        NotesList.addAll(fullList)
        notifyDataSetChanged()

    }

    fun filterList(search : String){

        NotesList.clear()

        for(item in fullList){

            if(item.title?.lowercase()?.contains(search.lowercase())== true ||
                    item.note?.lowercase()?.contains(search.lowercase()) == true){
                NotesList.add(item)
            }
        }
        notifyDataSetChanged()

    }



    fun RandomColor() : Int{

        val List = ArrayList<Int>()
        List.add(R.color.Color3)
        List.add(R.color.Color4)
        List.add(R.color.Color5)

        val seed = System.currentTimeMillis().toInt()
        val randomIndex = Random(seed).nextInt(List.size)
        return List[randomIndex]
    }


    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val notes_layout = itemView.findViewById<CardView>(R.id.card_layout)

        val title = itemView.findViewById<TextView>(R.id.textView_title)

        val note_tv = itemView.findViewById<TextView>(R.id.textView_notes)
        val date = itemView.findViewById<TextView>(R.id.textView_date)
    }

    interface NotesItemclickListener{
        fun onItemClicked(note: Note)
        fun onLongitemClicked(note: Note, cardView: CardView)

    }
}