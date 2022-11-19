package com.jonathanpea.homeworknotes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jonathanpea.homeworknotes.Adapter.NotesAdapter
import com.jonathanpea.homeworknotes.Database.NoteDatabase
import com.jonathanpea.homeworknotes.Models.Note
import com.jonathanpea.homeworknotes.Models.NoteViewModel
import com.jonathanpea.homeworknotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NotesAdapter.NotesItemclickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    lateinit var viewModel : NoteViewModel
    lateinit var adapter: NotesAdapter
    lateinit var selectedNote : Note
    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if(result.resultCode == Activity.RESULT_OK){

            val note = result.data?.getSerializableExtra("note") as? Note
            if(note != null){
                viewModel.updateNote(note)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ///inicializacion de ui

        initUI()

        viewModel = ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        viewModel.allnotes.observe(this) { list ->
            list?.let {
                adapter.updateList(list)
            }
        }

        database = NoteDatabase.getDatabase(this)

    }

    private fun initUI() {

        binding.recyclerHome.setHasFixedSize(true)
        binding.recyclerHome.layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
        adapter = NotesAdapter(this,this)
        binding.recyclerHome.adapter = adapter

        val getcontent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

            if(result.resultCode == Activity.RESULT_OK){

                val note = result.data?.getSerializableExtra( "note") as Note
                if(note != null){

                 viewModel.insertNote(note)
 
                }
            }
        }

        binding.fabAdd.setOnClickListener{

            val intent = Intent(this,addNote::class.java)
            getcontent.launch(intent)


        }
        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if(newText != null){

                    adapter.filterList(newText)
                }
                return true
            }

        })


    }

    override fun onItemClicked(note: Note) {
        val intent = Intent(this@MainActivity, addNote::class.java)
        intent.putExtra("current_note",note)
        updateNote.launch(intent)
    }

    override fun onLongitemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(this,cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.delete_note){
            viewModel.deleteNote(selectedNote)
            return true
        }
        return false
    }


}