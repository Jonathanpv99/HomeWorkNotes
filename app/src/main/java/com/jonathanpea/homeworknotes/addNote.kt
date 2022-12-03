package com.jonathanpea.homeworknotes

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jonathanpea.homeworknotes.Models.Note
import com.jonathanpea.homeworknotes.databinding.ActivityAddNoteBinding
import com.jonathanpea.homeworknotes.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

class addNote : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note : Note
    private lateinit var old_note : Note
    var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            old_note = intent.getSerializableExtra("current_note") as Note
            binding.edtTitle.setText(old_note.title)
            binding.edtNote.setText(old_note.note)
            isUpdate = true

        }catch (e : Exception){
            e.printStackTrace()
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val chanel = NotificationChannel(channelID,"canal1",importancia)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chanel)
        }

        binding.imgAcep.setOnClickListener{

            val title = binding.edtTitle.text.toString()
            val note_desc = binding.edtNote.text.toString()

            if(title.isNotEmpty() && note_desc.isNotEmpty()){

                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")

                if(isUpdate){
                    note = Note(
                        old_note.id,title,note_desc,formatter.format(Date())
                    )
                }else{
                    note = Note(
                        null,title,note_desc,formatter.format(Date())
                    )
                }

                val intent = Intent()
                intent.putExtra("note",note)
                setResult(Activity.RESULT_OK,intent)
                finish()

            }else{
                Toast.makeText(this@addNote,"Porfavor ingresa los datos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val notificacion = NotificationCompat.Builder(this, channelID).also { noti->
                noti.setContentTitle("Nueva Nota agregada: "+binding.edtTitle.text.toString())
                noti.setContentText(binding.edtNote.text.toString())
                noti.setSmallIcon(R.drawable.ic_notifi)
            }.build()
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            notificationManager.notify(1,notificacion)
        }

        

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }
}