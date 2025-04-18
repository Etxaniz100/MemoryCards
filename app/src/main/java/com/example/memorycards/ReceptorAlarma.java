package com.example.memorycards;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReceptorAlarma extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {

        SharedPreferences sharedPref = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        String nombre = sharedPref.getString("nombre_huevo", "???");

        NotificationManager managerNotificacion = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builderNotificacion = null;
        builderNotificacion = new NotificationCompat.Builder(context, "felicidadHuevo")
                        .setSmallIcon(R.drawable.sentiment_dissatisfied_24px)
                        .setContentTitle(context.getResources().getString(R.string.notificacion_triste).replace("nombre", nombre))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);


        // Crear un canal de notificación si la versión de Android es Oreo (API 26) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel canal = new NotificationChannel(
                    "felicidadHuevo", // ID del canal
                    "Huevo",      // Nombre del canal
                    NotificationManager.IMPORTANCE_DEFAULT // Importancia
            );
            if (managerNotificacion != null && managerNotificacion.getNotificationChannel("felicidadHuevo") == null)
            {
                managerNotificacion.createNotificationChannel(canal); // Registrar el canal
            }

        }

        // Lanzar la notificación
        if(managerNotificacion != null && builderNotificacion != null)
        {
            managerNotificacion.notify(1, builderNotificacion.build()); // El número 1 es el ID único para la notificación
        }


    }

}