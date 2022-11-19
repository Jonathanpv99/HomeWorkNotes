package com.jonathanpea.homeworknotes

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jonathanpea.homeworknotes.Adapter.NotesAdapter
import com.jonathanpea.homeworknotes.Database.NoteDatabase
import com.jonathanpea.homeworknotes.Models.Note
import com.jonathanpea.homeworknotes.Models.NoteViewModel
import com.jonathanpea.homeworknotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    lateinit var viewModel : NoteViewModel
    lateinit var adapter: NotesAdapter
    lateinit var selectedNote : Note

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


                }
            }
        }
    }
}