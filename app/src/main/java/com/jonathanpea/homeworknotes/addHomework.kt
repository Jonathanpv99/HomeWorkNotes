package com.jonathanpea.homeworknotes

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.jonathanpea.homeworknotes.Models.HomeWork
import com.jonathanpea.homeworknotes.databinding.ActivityAddHomeBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class addHomework : AppCompatActivity(), View.OnTouchListener{

    private lateinit var binding: ActivityAddHomeBinding
    private lateinit var home : HomeWork
    private lateinit var old_home : HomeWork
    var isUpdate = false

    private lateinit var calendar : Calendar
    private lateinit var picker : MaterialTimePicker
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    private val REQUEST_VIDEO_CAPTURE: Int = 1001
    lateinit var currentVideoPath: String
    var photoURI: Uri ="".toUri()
    var urif: String=""
    var uriv: String=""
    private val REQUEST_IMAGE_CAPTURE: Int = 1000
    lateinit var mediaController: MediaController
    //lateinit var mediaPlayer: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotifiChanel()
        try {
            old_home = intent.getSerializableExtra("current_home") as HomeWork
            binding.edtTitle.setText(old_home.title)
            binding.edtNote.setText(old_home.desc)
            binding.foto.setImageURI(old_home.uriF?.toUri())
            binding.videoView.setVideoURI(old_home.uriV?.toUri())
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
                    home = HomeWork(
                        old_home.id,title,note_desc,formatter.format(Date()),urif,uriv
                    )
                }else{
                    home = HomeWork(
                        null,title,note_desc,formatter.format(Date()),urif,uriv
                    )
                }

                val intent = Intent()
                intent.putExtra("homework",home)
                setResult(Activity.RESULT_OK,intent)
                finish()

            }else{
                Toast.makeText(this@addHomework,"Porfavor ingresa los datos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val notificacion = NotificationCompat.Builder(this, channelID).also { noti->
                noti.setContentTitle("Nueva Tarea Agragada: "+binding.edtTitle.text.toString())
                noti.setContentText(binding.edtNote.text.toString())
                noti.setSmallIcon(R.drawable.ic_notifi)
            }.build()
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            notificationManager.notify(1,notificacion)
        }



        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        //Notificaciones
        binding.btnCalendar.setOnClickListener{
            val note_desc = binding.edtNote.text.toString()
            val title = binding.edtTitle.text.toString()

            if(title.isNotEmpty() && note_desc.isNotEmpty()){
                generarAlarma()
            }else{
                Toast.makeText(this@addHomework,"Ingresa los datos antes de la nitificacion", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


        }

        binding.btnNotifi.setOnClickListener {
            setAlarm()
        }


        //binding.root.setOnTouchListener(this)
        binding.videoView.setOnClickListener { mediaController.show() }
        mediaController = MediaController(this)
        mediaController.setAnchorView(
            binding.root);
        binding.videoView.setMediaController(mediaController)

        binding.btnVideo.setOnClickListener {
            Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
                takeVideoIntent.resolveActivity(packageManager)?.also {

                    // Create the File where the photo should go
                    val videoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File

                        null
                    }

                    // Continue only if the File was successfully created
                    videoFile?.also {
                        photoURI = FileProvider.getUriForFile(
                            this,
                            "net.JonathanP.HomeWorkNotes.fileProvider",
                            it
                        )
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
                    }
                }
            }
        }



        binding.btnFoto.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {

                    // Create the File where the photo should goh
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File

                        null
                    }

                    // Continue only if the File was successfully created
                    photoFile?.also {
                        photoURI = FileProvider.getUriForFile(
                            this,
                            "net.JonathanP.HomeWorkNotes.fileProvider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }

            }
        }

        binding.btnFotoG.setOnClickListener{
            val intent :Intent
            if(Build.VERSION.SDK_INT<19){
                intent =Intent()
                intent.setAction(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                    startActivityForResult(intent,111)
            }else{
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("image/*")
                startActivityForResult(Intent.createChooser(intent, "foto"), 111)
            }
        }


        binding.btnVideoG.setOnClickListener{
            val intent :Intent
            if(Build.VERSION.SDK_INT<19){
                intent =Intent()
                intent.setAction(Intent.ACTION_GET_CONTENT)
                intent.setType("video/*")
                startActivityForResult(intent,111)
            }else{
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("video/*")
                startActivityForResult(Intent.createChooser(intent, "video"), 222)
            }
        }


    }





   //------------------------------Alarma-----------------------------------------------------------



    private fun generarAlarma(){
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Selecciona la alarma")
            .build()

        picker.show(supportFragmentManager,"Alarma")

        picker.addOnPositiveButtonClickListener {

             calendar = Calendar.getInstance()
                calendar[Calendar.HOUR_OF_DAY] = picker.hour
                calendar[Calendar.MINUTE] = picker.minute
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0


        }


    }

    private fun createNotifiChanel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val name : CharSequence = "AlarmaChanel"
            val description = "chanel for alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("Alarma",name,importance)
            channel.description =description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setAlarm(){
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_HOUR,pendingIntent
        )

        Toast.makeText(this,"Alarma exitosa",Toast.LENGTH_SHORT).show()
    }
//------------------Alarma2--------------------------------------------------

    private fun setAlarmChingon(callback: (Long) -> Unit){
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND,0)
            this.set(Calendar.MILLISECOND,0)
            DatePickerDialog(
                this@addHomework,
                0,
                { _, year, month, dayOfMonth ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH,  month)
                    this.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    TimePickerDialog(
                        this@addHomework,
                        0,
                        {_,hour,min ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, min)
                            callback(this.timeInMillis)
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()

                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()

        }
    }




    //---------------------------------------------------------------------------media-----------------------------------------------
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val storageDir: File? = filesDir
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            currentVideoPath = absolutePath
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            /*val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageViewFotoMiniatura.setImageBitmap(imageBitmap)*/

            //Carga la imagen de manera escalada
            //setPic()

            //Carga la imagen a partir de URI
            binding.foto.setImageURI(
                photoURI
            )
            urif=photoURI.toString()
        }else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){



            //binding.videoView.setMediaController(mediaController)
            binding.videoView.setVideoURI(photoURI)
            uriv=photoURI.toString()

           // binding.videoView.start()
            //mediaController.show()



            /*mediaController.setEnabled(true);
            mediaController.show();*/
        }else if(requestCode == 111 && resultCode == Activity.RESULT_OK){
            applicationContext.grantUriPermission(applicationContext.packageName,photoURI,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            data?.data?.also { uri ->

                val takeflags : Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                photoURI=uri

                applicationContext.contentResolver.takePersistableUriPermission(photoURI,takeflags)


            }
            binding.foto.setImageURI(photoURI)
            urif=photoURI.toString()

        }else if(requestCode == 222 && resultCode == Activity.RESULT_OK){
            applicationContext.grantUriPermission(applicationContext.packageName,photoURI,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            data?.data?.also { uri ->

                val takeflags : Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                photoURI=uri
                uriv=photoURI.toString()

                applicationContext.contentResolver.takePersistableUriPermission(photoURI,takeflags)


            }
            binding.videoView.setVideoURI(photoURI)
        }

    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = binding.foto .width
        val targetH: Int = binding.foto.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            binding.foto.setImageBitmap(bitmap)

        }
    }



    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        mediaController.show()
        return false
    }


}