package com.example.memorycards;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.util.Log;

public class GestorHuevo
{
    private String nombre;
    private float progreso;
    private float felicidad;
    private String estadoActual;
    private String colorHuevo; // rojo, verde, gris
    private String[] colores = {"rojo", "verde", "gris"};
    private int ratio;
    private float potenciaComida = 5;

    public GestorHuevo()
    {
        nombre = "Huevo";
        progreso = 0f;
        felicidad = 100f;
        estadoActual = "";
        estadoActual = calcularEstado(felicidad);
        ratio = calcularAumentoFelicidad();

        Random rd = new Random();
        int color = rd.nextInt(colores.length);
        colorHuevo = colores[color];
    }

    public GestorHuevo(String name, float progress, float happiness, Date ultimaVezAbierto, String colorH)
    {
        nombre = name;
        progreso = progress;
        felicidad = happiness;
        colorHuevo = colorH;

        ratio = calcularAumentoFelicidad();

        if(ultimaVezAbierto != null)
        {
            calcularProgresoYFelicidadActual(ultimaVezAbierto);
        }
        estadoActual = "";
        estadoActual = calcularEstado(felicidad);
    }

    public String  getColor()
    {
        return colorHuevo;
    }
    public void calcularProgresoYFelicidadActual(Date ultimaVezAbierto)
    {
        Date ahora = Calendar.getInstance().getTime();
        long diff = ahora.getTime() - ultimaVezAbierto.getTime();
        int horas = (int) TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);

        float decadenciaHorariaProceso;
        float felicidadAux = felicidad;

        while (horas > 0 && progreso >= 0 && felicidadAux != 0 && progreso <= 100)
        {
            decadenciaHorariaProceso = calcularCambioProgresoPorHora(felicidadAux);
            felicidadAux = felicidadAux - 1;

            progreso += decadenciaHorariaProceso;
            if(progreso < 0)
            {
                progreso = 0;
            }

            horas -= 1;
        }

        felicidad = felicidadAux;
        if(felicidad < 0)
        {
            felicidad = 0;
        }

        estadoActual = calcularEstado(felicidad);

        if(progreso >= 100)
        {
            //TODO: Eclosionar
        }

    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nuevoNombre, Context context, LifecycleOwner owner)
    {
        try
        {
            SharedPreferences sharedPref = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("nombre_huevo", nuevoNombre);
            editor.apply();
        }
        catch (Exception e){}

        nombre = nuevoNombre;
        GestorMazos.getMiGestorMazos().subirHuevo(context, this, owner);
    }

    public int calcularAumentoFelicidad()
    {
        int ret = 1;

        int numPreguntas = GestorMazos.getMiGestorMazos().getNumeroPreguntasHoy();
        if(numPreguntas > 0)
        {
            ret = 50/numPreguntas;
        }
        Log.d("MIO", "Huevo aumento de felicidad : numePreguntas :" + numPreguntas  + " ret: " + ret);
        return ret;
    }

    public float getProgreso()
    {
        if(progreso < 0)
        {
            return 0;
        }
        return progreso;
    }

    public String getEstadoFelicidad()
    {
        return estadoActual;
    }

    public void preguntaContestada(boolean acertada, Context context, LifecycleOwner owner)
    {
        String estadoAux = estadoActual;
        if(acertada)
        {
            felicidad += ratio;
        }
        else
        {
            felicidad -= ratio;
        }

        if(felicidad > 100)
        {
            felicidad = 100;
        }
        else if (felicidad < 0)
        {
            felicidad = 0;
        }

        estadoActual = calcularEstado(felicidad);
        if(!estadoAux.isEmpty() && !estadoAux.equals(estadoActual))
        {
            NotificationManager managerNotificacion = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builderNotificacion = null;
            switch (estadoActual)
            {
                case "caducado":
                    builderNotificacion = new NotificationCompat.Builder(context, "felicidadHuevo")
                            .setSmallIcon(R.drawable.egg_alt_24px)
                            .setContentTitle(context.getResources().getString(R.string.notificacion_caducado).replace("nombre", nombre))
                            .setContentText("Oh.")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);
                    break;

                case "m_triste":
                    builderNotificacion = new NotificationCompat.Builder(context, "felicidadHuevo")
                            .setSmallIcon(R.drawable.sentiment_sad_24px)
                            .setContentTitle(context.getResources().getString(R.string.notificacion_muy_triste).replace("nombre", nombre))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);
                    break;

                case "triste":
                    builderNotificacion = new NotificationCompat.Builder(context, "felicidadHuevo")
                            .setSmallIcon(R.drawable.sentiment_dissatisfied_24px)
                            .setContentTitle(context.getResources().getString(R.string.notificacion_triste).replace("nombre", nombre))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);
                    break;

                case "feliz":
                    builderNotificacion = new NotificationCompat.Builder(context, "felicidadHuevo")
                            .setSmallIcon(R.drawable.sentiment_satisfied_24px)
                            .setContentTitle(context.getResources().getString(R.string.notificacion_feliz).replace("nombre", nombre))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);
                    break;

                case "m_feliz":
                    builderNotificacion = new NotificationCompat.Builder(context, "felicidadHuevo")
                            .setSmallIcon(R.drawable.sentiment_very_satisfied_24px)
                            .setContentTitle(context.getResources().getString(R.string.notificacion_muy_feliz).replace("nombre", nombre))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);
                    break;

                case "neutral":
                default:
                    builderNotificacion = new NotificationCompat.Builder(context, "felicidadHuevo")
                            .setSmallIcon(R.drawable.sentiment_neutral_24px)
                            .setContentTitle(context.getResources().getString(R.string.notificacion_neutral).replace("nombre", nombre))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);
                    break;

            }


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
        GestorMazos.getMiGestorMazos().subirHuevo(context, this, owner);


    }

    public float getFelicidad() {
        return felicidad;
    }

    private String calcularEstado(float felicidad)
    {
        // caducado -> m_triste -> triste -> neutral -> feliz -> m_feliz
        String ret = "caducado";
        if(felicidad > 0 && felicidad < 20)
        {
            ret = "m_triste";
        }
        else if(felicidad >= 20 && felicidad < 40)
        {
            ret = "triste";
        }
        else if(felicidad >= 40 && felicidad < 60)
        {
            ret = "neutral";
        }
        else if(felicidad >= 60 && felicidad < 80)
        {
            ret = "feliz";
        }
        else if(felicidad >= 80)
        {
            ret = "m_feliz";
        }

        return ret;
    }

    private float calcularCambioProgresoPorHora(float f)
    {
        float cantidadProgresada = 0;
        if(f > 0 && f < 20)
        {
            cantidadProgresada = -1;
        }
        else if(f >= 20 && f < 40)
        {
            cantidadProgresada = -0.5f;
        }
        else if(f >= 40 && f < 60)
        {
            cantidadProgresada = 0;
        }
        else if(f >= 60 && f < 80)
        {
            cantidadProgresada = 0.5f;
        }
        else if(f >= 80)
        {
            cantidadProgresada = 2;
        }

        return cantidadProgresada;
    }

    public void alimentar(Context context, LifecycleOwner owner)
    {
        progreso += potenciaComida;
        felicidad += potenciaComida;


        if(progreso >= 100)
        {
            progreso = 100;
        }

        if(felicidad >= 100)
        {
            felicidad = 100;
        }

        estadoActual = calcularEstado(felicidad);


        GestorMazos.getMiGestorMazos().subirHuevo(context, this, owner);
    }

    // ----------------------------------------------------- ALARMA DE ECLOSION ------------------------------------

    public Date calcularFechaTriste()
    {
        Date ret = Calendar.getInstance().getTime();

        float felicidadActual = felicidad;
        float felicidadTriste = 39;

        if(felicidadActual <= felicidadTriste)
        {
            Log.d("MIO", "calcularFechaTriste huevo ya esta triste");
            return null;
        }

        float horasHastaTriste = felicidadActual - felicidadTriste;

        ret.setTime(ret.getTime() + (long) (horasHastaTriste * 3600000));
        //ret.setTime(ret.getTime() + (long) (1000*30));

        Log.d("MIO", "Horas hasta triste: " + horasHastaTriste + " | fecha notificacion :" + ret.toString());
        return ret;
    }

    @SuppressLint("ScheduleExactAlarm")
    public void actualizarAlarma(Date fecha, Context context)
    {
        if(fecha == null)
        {
            Log.d("MIO", "actualizarAlarma fecha null");
            return;
        }
        AlarmManager gestor = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Intent que será lanzado por la alarma
        Intent intent = new Intent(context, ReceptorAlarma.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Cancelar la alarma anterior si existe
        gestor.cancel(pendingIntent);

        android.icu.util.Calendar calendario = android.icu.util.Calendar.getInstance();
        if(calendario == null)
        {
            Log.d("MIO", "calendario null");
            return;
        }
        calendario.setTime(fecha);

        gestor.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), pendingIntent);
    }
}
