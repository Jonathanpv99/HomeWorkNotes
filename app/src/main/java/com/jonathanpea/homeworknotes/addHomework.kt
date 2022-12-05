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

    private val REQUEST_VIDEO_CAPTURE: Int = 1001
    lateinit var currentVideoPath: String
    lateinit var photoURI: Uri
    private val REQUEST_IMAGE_CAPTURE: Int = 1000
    lateinit var mediaController: MediaController
    //lateinit var mediaPlayer: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            old_home = intent.getSerializableExtra("current_home") as HomeWork
            binding.edtTitle.setText(old_home.title)
            binding.edtNote.setText(old_home.desc)
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
                        old_home.id,title,note_desc,formatter.format(Date())
                    )
                }else{
                    home = HomeWork(
                        null,title,note_desc,formatter.format(Date())
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
        createNotificationChanel()
        binding.btnNotifi.setOnClickListener{
            val note_desc = binding.edtNote.text.toString()
            val title = binding.edtTitle.text.toString()

            if(title.isNotEmpty() && note_desc.isNotEmpty()){
                scheduleNotification()
            }else{
                Toast.makeText(this@addHomework,"Ingresa los datos antes de la nitificacion", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


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
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification() {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = binding.edtTitle.text.toString()
        val message = binding.edtNote.text.toString()
        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val time = getTime()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            progrAlamar(time,pendingIntent)
        }
        showAlert(time,title,message)

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun progrAlamar(time: Long, pendingIntent: PendingIntent?) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact (
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat =  android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Alert Tareas")
            .setMessage("title: " + title+
                    "\n Message: "+message+
                    "\n At: "+dateFormat.format(date)+ " "+timeFormat.format(date))
            .setPositiveButton("aceptar"){_,_->}
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getTime(): Long{
        val minute = binding.timePicker.minute
        val  hour =binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year,month,day,hour,minute)
        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val name = "canal notify"
        val desc = "descripcion del canal"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val chanel = NotificationChannel(channelID, name, importance)
        chanel.description=desc
        val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationmanager.createNotificationChannel(chanel)
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
        }else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){



            //binding.videoView.setMediaController(mediaController)
            binding.videoView.setVideoURI(photoURI)

            binding.videoView.start()
            //mediaController.show()



            /*mediaController.setEnabled(true);
            mediaController.show();*/
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