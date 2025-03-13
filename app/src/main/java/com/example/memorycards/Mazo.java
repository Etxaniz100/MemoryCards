package com.example.memorycards;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Mazo
{
    private String nombre;
    private String color;
    public ArrayList<Carta> preguntasNuevas;
    public ArrayList<Carta> preguntasEstudiadas;
    public ArrayList<Carta> preguntasEstudiando;


    public Mazo(String pNombre, String pColor)
    {
        nombre = pNombre;
        color = pColor;
        preguntasNuevas = new ArrayList<Carta>();
        preguntasEstudiadas = new ArrayList<Carta>();
        preguntasEstudiando = new ArrayList<Carta>();
    }

    public String getNombre() {
        return nombre;
    }

    public String getColor() {
        return color;
    }

    public ArrayList<Carta> getPreguntasNuevas()
    {
        return preguntasNuevas;
    }

    public ArrayList<Carta> getPreguntasEstudiadas()
    {
        return preguntasEstudiadas;
    }

    public ArrayList<Carta> getPreguntasEstudiando()
    {
        return preguntasEstudiando;
    }

    public void anadirCarta(Carta c, Context context)
    {
        // TODO: Comprobar si la carta ya existe, si existe lanzar un pop up de aviso
        preguntasNuevas.add(c);
        GestorMazos.getMiGestorMazos().guardarCartaEnBd(this, c, context);

    }

    public int getTamanoLista()
    {
        return preguntasNuevas.size() + preguntasEstudiadas.size() + preguntasEstudiando.size();
    }

    public boolean hayCartasParaEstudiar()
    {
        return (preguntasNuevas.size() + preguntasEstudiando.size() > 0 || obtenerRepaso() != null);
    }
    public Carta siguienteCarta()
    {
        Carta ret = null;
        Random rand = new Random();
        int tipo = rand.nextInt(3);


        switch (tipo)
        {
            case 0: //Pregunta nueva
                if (preguntasNuevas.size() > 0)
                {
                    ret = preguntasNuevas.get(rand.nextInt(preguntasNuevas.size()));
                    break;
                }

            case 1: //Pregunta estudiando
                if (preguntasEstudiando.size() > 0)
                {
                    ret = preguntasEstudiando.get(rand.nextInt(preguntasEstudiando.size()));
                    break;
                }

            case 2: //Pregunta repaso

                    ret = obtenerRepaso();
                    if(ret != null)
                    {
                        break;
                    }

            default:
                // Si la que habia tocado estaba vacia y las siguientes también, se hace una vuelta más
                if (preguntasNuevas.size() > 0)
                {
                    ret = preguntasNuevas.get(rand.nextInt(preguntasNuevas.size()));
                    break;
                }

                if (preguntasEstudiando.size() > 0)
                {
                    ret = preguntasEstudiando.get(rand.nextInt(preguntasEstudiando.size()));
                    break;
                }

                ret = obtenerRepaso();
                break;
        }

        return ret;
    }

    public void quitarCarta(Carta c)
    {
        preguntasEstudiadas.remove(c);
        preguntasEstudiando.remove(c);
        preguntasNuevas.remove(c);
    }

    public void cartaAcertada(Carta c, boolean acertada, Context context)
    {
        switch (c.getEstado())
        {
            case 0: // Carta nueva ------------------------------------------+
                if(!preguntasNuevas.contains(c)) { /*Error*/ break;}

                preguntasNuevas.remove(c);
                preguntasEstudiando.add(c);
                c.unaVezCorrecto = acertada;
                c.setEstado(1);
                break;

            case 1: // Carta estudiando -------------------------------------+
                if(!preguntasEstudiando.contains(c)) { /*Error*/ break;}

                if(acertada && c.unaVezCorrecto)  // Pasa a largo plazo
                {
                    c.setEstado(2);
                    preguntasEstudiando.remove(c);
                    preguntasEstudiadas.add(c);
                    c.diasEntreEstudio = 0;
                    c.calcularProximoEstudio();
                }
                else
                {
                    c.setEstado(1);
                    c.unaVezCorrecto = acertada;
                }
                break;

            case 2: // Carta repaso -----------------------------------------+
                if(!preguntasEstudiadas.contains(c)) { /*Error*/ break;}

                if(acertada)
                {
                    c.setEstado(2);
                    c.calcularProximoEstudio();

                }
                else
                {
                    c.setEstado(1);
                    preguntasEstudiadas.remove(c);
                    preguntasEstudiando.add(c);
                    c.unaVezCorrecto = false;
                    c.diasEntreEstudio = 0;
                }
                break;
        }

        //TODO : Actualizar BD
        GestorMazos.getMiGestorMazos().actualizarCartaEnBd(this, c, context);

        GestorMazos.getMiGestorMazos().getHuevo().preguntaContestada(acertada, context);

    }

    private Carta obtenerRepaso()
    {
        Date ahora = Calendar.getInstance().getTime();

        if(ahora == null)
        {
            return null;
        }

        for (Carta c: preguntasEstudiadas)
        {
            if (c.proximoEstudio == null)
            {
                return c;
            }
            if(c.proximoEstudio.before(ahora) || c.proximoEstudio.equals(ahora))
            {
                return c;
            }
        }
        return null;
    }


    public int getNumeroPreguntasHoy()
    {
        return preguntasNuevas.size() + preguntasEstudiadas.size() + numeroPreguntasRepaso();
    }

    public int numeroPreguntasRepaso()
    {

        Date ahora = Calendar.getInstance().getTime();

        int ret = 0;

        if(ahora == null)
        {
            return ret;
        }

        for (Carta c: preguntasEstudiadas)
        {
            if(c.proximoEstudio != null && (c.proximoEstudio.before(ahora) || c.proximoEstudio.equals(ahora)))
            {
                ret += 1;
            }
        }

        return ret;
    }
}
