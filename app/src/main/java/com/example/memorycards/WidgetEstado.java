package com.example.memorycards;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.widget.ImageView;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WidgetEstado  extends AppWidgetProvider
{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        for (int appWidgetId : appWidgetIds)
        {
            actualizarWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void actualizarWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        SharedPreferences sharedPref = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        if(sharedPref.contains("nombre_huevo"))
        {
            String nombre = sharedPref.getString("nombre_huevo", "???");
            views.setTextViewText(R.id.widget_nombre_huevo, nombre);

            float felicidadActual = sharedPref.getFloat("felicidad_huevo", 0);
            float progresoActual = sharedPref.getFloat("progreso_huevo", 0);

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String ultimaVezAbierto = sharedPref.getString("ultima_vez_abierto", "");

            Date fecha = null;
            try
            {
                if (ultimaVezAbierto != null && !ultimaVezAbierto.equals("null") && !ultimaVezAbierto.isEmpty())
                {
                    fecha = formato.parse(ultimaVezAbierto);
                }
            }
            catch (Exception e){}

            GestorHuevo huevo = new GestorHuevo(nombre, progresoActual, felicidadActual, fecha, "rojo");

            int idEstado = R.drawable.sentiment_sad_24px;

            String estado = "";
            if(huevo != null)
            {
                estado = huevo.getEstadoFelicidad();
            }

            switch (estado)
            {
                case "caducado":
                    idEstado = R.drawable.sentiment_very_dissatisfied_24px;
                    break;

                case "m_triste":
                    idEstado = R.drawable.sentiment_sad_24px;
                    break;

                case "triste":
                    idEstado = R.drawable.sentiment_dissatisfied_24px;
                    break;

                case "feliz":
                    idEstado = R.drawable.sentiment_satisfied_24px;
                    break;

                case "m_feliz":
                    idEstado = R.drawable.sentiment_very_satisfied_24px;
                    break;

                case "neutral":
                default:
                    idEstado = R.drawable.sentiment_neutral_24px;
                    break;

            }

            views.setImageViewResource(R.id.imagen_widget_estado, idEstado);
        }
        else
        {
            views.setTextViewText(R.id.widget_nombre_huevo, "???");
            views.setImageViewResource(R.id.imagen_widget_estado, R.drawable.sentiment_neutral_24px);
        }

        views.setImageViewResource(R.id.imagen_widget_actualizar, R.drawable.refresh_24px);

        Intent intent = new Intent(context, WidgetEstado.class);
        intent.setAction("com.example.ACTION_ACTUALIZAR_WIDGET");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pi = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        views.setOnClickPendingIntent(R.id.imagen_widget_actualizar, pi);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        if ("com.example.ACTION_ACTUALIZAR_WIDGET".equals(intent.getAction()))
        {
            int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                actualizarWidget(context, manager, id);
            }
        }
    }
}
