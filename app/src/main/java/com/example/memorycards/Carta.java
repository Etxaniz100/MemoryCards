package com.example.memorycards;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Carta
{
    String pregunta;
    String respuesta;

    Date proximoEstudio;
    int diasEntreEstudio;

    boolean unaVezCorrecto = false;
    private int estado = 0;  // 0 = nuevo // 1 = estudiando // 2 = repasando


    /*
    En sql :

    key String pregunta
    key String nombreMazo
    String respuesta;
    Date proximoEstudio;
    int ultimosDiasEstudio;
    boolean unaVezCorrecto;
    */

    public Carta(String pPregunta, String pRespuesta)
    {
        pregunta = pPregunta;
        respuesta = pRespuesta;
        diasEntreEstudio = 0;
        estado = 0;
    }

    public Carta(String pPregunta, String pRespuesta, Date pproximoEstudio, int uUltimos, boolean uUna, int eEstado)
    {
        pregunta = pPregunta;
        respuesta = pRespuesta;

        proximoEstudio = pproximoEstudio;
        diasEntreEstudio = uUltimos;
        unaVezCorrecto = uUna;
        estado = eEstado;

        String prueba = pPregunta;
        prueba += " -> ";
        prueba += unaVezCorrecto?"true":"false";
        Log.i("miTag", prueba);

    }


    public void SetProximoEstudio(Date fecha)
    {
        proximoEstudio = fecha;
    }

    public void calcularProximoEstudio()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        diasEntreEstudio += 1;
        cal.add(Calendar.DATE, diasEntreEstudio);
        SetProximoEstudio(cal.getTime());

    }

    public int getEstado()
    {
        return estado;
    }

    public void setEstado(int i)
    {
        if(i == 0 || i == 1 | i == 2)
        {
            estado = i;
        }
    }
}
