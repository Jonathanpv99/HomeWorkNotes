package com.jonathanpea.homeworknotes

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver(){
    var id=1;
    override fun onReceive(context: Context?, intet: Intent?) {
        val i = Intent(context, MainActivity::class.java )
        intet!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent =PendingIntent.getActivity(context,0,i,0)


        val builder = NotificationCompat.Builder(context!!,"Alarma")
            .setSmallIcon(R.drawable.ic_notifi)
            .setContentTitle("alarma tarea")
            .setContentText("Tienes tareas por hacer")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setDefaults(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(id,builder.build())
        id=id+1

    }




}