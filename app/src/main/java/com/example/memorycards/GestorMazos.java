package com.example.memorycards;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class GestorMazos
{
    private static GestorMazos miGestorMazos;
    private static ArrayList<Mazo> listaMazos;

    private static SimpleDateFormat formatoFecha;
    private static boolean inicializado = false;
    private static GestorHuevo huevo;

    public static GestorMazos getMiGestorMazos()
    {
        if (miGestorMazos == null)
        {
            miGestorMazos = new GestorMazos();
            listaMazos = new ArrayList<Mazo>();
            formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return miGestorMazos;
    }

    public ArrayList<Mazo> getListaMazos()
    {
        return listaMazos;
    }

    public Mazo getMazo(String nombre)
    {
        if(nombre == null || nombre.isEmpty() || nombre.isBlank())
        {
            return null;
        }
        for (Mazo m: listaMazos)
        {
            if (Objects.equals(m.getNombre(), nombre))
            {
                return m;
            }
        }
        return null;
    }

    public Mazo getMazo(int posicion)
    {
        if (posicion >= 0 && posicion < listaMazos.size())
        {
            return listaMazos.get(posicion);
        }
        return null;
    }

    public static boolean crearMazo(String nombre, Context context)
    {
        if(nombre == null || nombre.equals(""))
        {
            return false;
        }

        for (Mazo m: listaMazos)
        {
            if(m.getNombre().equals(nombre))
            {
                return false;
            }
        }

        Mazo nuevo = new Mazo(nombre, "blue");
        listaMazos.add(nuevo);

        guardarMazoEnBd(nuevo, context);

        return true;
    }

    public static void nuevoMazo(Mazo m, Context context)
    {
        if(m != null && !listaMazos.contains(m)) //TODO: Que no haya otro mazo con el mismo nombre
        {
            listaMazos.add(m);
            guardarMazoEnBd(m, context);
        }

    }

    public static void guardarMazoEnBd(Mazo m, Context context)
    {
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Nombre", m.getNombre());
        bd.insert("Mazo", null, values);

        bd.close();
    }

    public static void guardarCartaEnBd(Mazo m, Carta c, Context context)
    {
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        ContentValues nuevo = getContentValues(c, m);
        bd.insert("Carta", null, nuevo);

        bd.close();
    }

    public static void actualizarMazoEnBd(Mazo m, Context context)
    {
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        //ContentValues values = new ContentValues();
        //values.put("Nombre", m.getNombre());
        //bd.insert("Mazo", null, values);

        bd.close();
    }

    public static void actualizarCartaEnBd(Mazo m, Carta c, Context context)
    {
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        ContentValues nuevo = getContentValues(c, m);
        bd.update("Carta", nuevo, "Pregunta=? and NombreMazo=?", new String[]{c.pregunta, m.getNombre()});

        bd.close();
    }

    public static void inicializarMazos(Context context, boolean forzar)
    {
        try
        {


        if(inicializado && !forzar)
        {
            return;
        }

        // ----------------------------------------------------------------------------------

        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        Cursor c = bd.rawQuery("SELECT * FROM Mazo", null);

        while (c.moveToNext())
        {
            String nombre = c.getString(0);
            Mazo mazo = new Mazo(nombre, "Morado");
            listaMazos.add(mazo);
        }

        for (Mazo mazo: listaMazos)
        {
            String[] argumentos = new String[] {mazo.getNombre()};
            c = bd.query("Carta",null,"NombreMazo==?",argumentos,null,null,null);

            while (c.moveToNext())
            {
                String pregunta = c.getString(0);
                String nombreMazo = c.getString(1);
                String respuesta = c.getString(2);
                int estado = (c.getInt(3));

                String proximoEstudio = c.getString(4); //Fecha
                int diasEntreEstudios = c.getInt(5);
                boolean unaVezCorrecto = (c.getInt(6)==1);
                Carta nuevaCarta;
                try
                {
                    Date fechaProximoEstudio = null;
                    if (!proximoEstudio.isEmpty())
                    {
                        fechaProximoEstudio = formatoFecha.parse(proximoEstudio);
                    }
                    nuevaCarta = new Carta(pregunta, respuesta, fechaProximoEstudio, diasEntreEstudios, unaVezCorrecto, estado);
                }
                catch (Exception e)
                {
                    nuevaCarta = new Carta(pregunta, respuesta, null, 0, unaVezCorrecto, estado);
                }

                switch (estado)
                {
                    case 0:
                        mazo.getPreguntasNuevas().add(nuevaCarta);
                        break;

                    case 1:
                        mazo.getPreguntasEstudiando().add(nuevaCarta);
                        break;

                    case 2:
                        mazo.getPreguntasEstudiadas().add(nuevaCarta);
                        break;
                }
            }
        }


        if(listaMazos.size() <= 0) {
            // Diseño de software avanzado
            Mazo m1 = new Mazo("Desarrollo Avanzado de Software", "Marron");
            listaMazos.add(m1);
            guardarMazoEnBd(m1, context);

            Carta carta = new Carta("¿Quién creó android?", "Andy Rubin y Chris White");
            m1.preguntasNuevas.add(carta);
            guardarCartaEnBd(m1, carta, context);

            carta = new Carta("¿Qué versión de android salió en 2013", "KitKat");
            m1.preguntasNuevas.add(carta);
            guardarCartaEnBd(m1, carta, context);

            carta = new Carta("¿Qué mamíferos ponen huevos?", "Las equidnas y ornitorrincos");
            m1.preguntasNuevas.add(carta);
            guardarCartaEnBd(m1, carta, context);

            carta = new Carta("¿Cómo se llama realmente el Joker?", "Jack Oswald White");
            m1.preguntasNuevas.add(carta);
            guardarCartaEnBd(m1, carta, context);
        }

        /*
            Pregunta             VARCHAR(500)
            NombreMazo           VARCHAR(255)
            Respuesta            VARCHAR(1000)
            Nueva                INT
            ProximoEstudio       DATE
            UltimosDiasEstudio   INTEGER
            UnaVezCorrecto       BOOLEAN
         */

        // Nueva -> Carta nueva
        // UltimosDiasEstudiados == 0 -> Carta estudiando


        //----------------- HUEVO -----------------


        //c.close();
        //bd.close();

        //GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        //bd = GestorDB.getWritableDatabase();
        //c = bd.rawQuery("SELECT * FROM Huevo", null);
          c = bd.query("Huevo", null, null, null, null, null, null);



        huevo = null;
        while (c.moveToNext())
        {
            String nombre = c.getString(0);
            float progreso = c.getInt(1);
            float felicidad = c.getInt(2);
            String color = c.getString(4);

            String ultimaVezAbierto = c.getString(3); //Fecha

            Date fecha = null;
            try {

                if (!ultimaVezAbierto.isEmpty()) {
                    fecha = formatoFecha.parse(ultimaVezAbierto);
                }
            }
            catch (Exception e){}

            huevo = new GestorHuevo(nombre, progreso, felicidad, fecha, color);
            ContentValues nuevo = new ContentValues();
            nuevo.put("Nombre", huevo.getNombre());
            nuevo.put("Progreso", huevo.getProgreso());
            nuevo.put("Felicidad", huevo.getFelicidad());
            nuevo.put("Color", huevo.getColor());
            Date hoy = Calendar.getInstance().getTime();
            nuevo.put("UltimaVezAbierto", formatoFecha.format(hoy));
            bd.update("Huevo", nuevo, "Nombre=?", new String[]{huevo.getNombre()});

        }

        if(huevo == null)
        {
            huevo = new GestorHuevo("Demo", 75f, 75f, Calendar.getInstance().getTime(), "rojo");
            ContentValues nuevo = new ContentValues();
            nuevo.put("Nombre", huevo.getNombre());
            nuevo.put("Progreso", huevo.getProgreso());
            nuevo.put("Felicidad", huevo.getFelicidad());
            nuevo.put("Color", huevo.getColor());
            Date hoy = Calendar.getInstance().getTime();
            nuevo.put("UltimaVezAbierto", formatoFecha.format(hoy));

            bd.insert("Huevo", null, nuevo);
        }


        c.close();
        bd.close();
        inicializado = true;

        }
        catch (Exception e)
        {
            context.deleteDatabase("NombreBD");
            inicializarMazos(context, false);
        }
    }

    public void borrarMazo(Mazo m, Context context)
    {
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        bd.delete("Mazo", "Nombre=?", new String[]{m.getNombre()});
        bd.delete("Carta", "NombreMazo=?", new String[]{m.getNombre()});


        bd.close();
        GestorDB.close();

        listaMazos.remove(m);
    }

    public void borrarCarta(Mazo m, Carta c, Context context, BaseDatos gbd, SQLiteDatabase sbd)
    {
        boolean recibido = false;
        SQLiteDatabase bd;
        BaseDatos GestorDB;
        if(gbd == null && sbd == null)
        {
            GestorDB = new BaseDatos (context, "NombreBD", null, 1);
            bd = GestorDB.getWritableDatabase();
        }
        else
        {
            recibido = true;
            GestorDB = gbd;
            bd = sbd;
        }

        bd.delete("Carta", "Pregunta=? and NombreMazo=?", new String[]{c.pregunta, m.getNombre()});

        if(!recibido)
        {
            bd.close();
            GestorDB.close();
        }

        m.quitarCarta(c);
    }

    public void nuevoHuevoBD(Context context, GestorHuevo h)
    {
        huevo = h;
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        ContentValues nuevo = new ContentValues();
        nuevo.put("Nombre", h.getNombre());
        nuevo.put("Progreso", h.getProgreso());
        nuevo.put("Felicidad", h.getFelicidad());
        nuevo.put("Color", h.getColor());
        Date hoy = Calendar.getInstance().getTime();
        nuevo.put("UltimaVezAbierto", formatoFecha.format(hoy));

        bd.insert("Huevo", null, nuevo);

        bd.close();
        GestorDB.close();
    }


    public GestorHuevo nuevoHuevo(Context context)
    {
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        huevo = new GestorHuevo(context);
        ContentValues nuevo = new ContentValues();
        nuevo.put("Nombre", huevo.getNombre());
        nuevo.put("Progreso", huevo.getProgreso());
        nuevo.put("Felicidad", huevo.getFelicidad());
        nuevo.put("Color", huevo.getColor());
        Date hoy = Calendar.getInstance().getTime();
        nuevo.put("UltimaVezAbierto", formatoFecha.format(hoy));

        bd.insert("Huevo", null, nuevo);
        bd.close();
        GestorDB.close();

        return huevo;
    }

    public void actualizarBDHuevo(Context context, GestorHuevo h)
    {
        huevo = h;
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        ContentValues nuevo = new ContentValues();
        nuevo.put("Nombre", h.getNombre());
        nuevo.put("Progreso", h.getProgreso());
        nuevo.put("Felicidad", h.getFelicidad());
        nuevo.put("Color", h.getColor());
        Date hoy = Calendar.getInstance().getTime();
        nuevo.put("UltimaVezAbierto", formatoFecha.format(hoy));


        bd.update("Huevo", nuevo, "Nombre=?", new String[]{h.getNombre()});


        bd.close();
        GestorDB.close();
    }

    public void borrarHuevo(Context context, GestorHuevo h)
    {
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        bd.delete("Huevo", "Nombre=?", new String[]{h.getNombre()});

        bd.close();
        GestorDB.close();
    }

    private static ContentValues getContentValues(Carta carta, Mazo m)
    {
        ContentValues nuevo = new ContentValues();
        nuevo.put("Pregunta", carta.pregunta);
        nuevo.put("NombreMazo", m.getNombre());
        nuevo.put("Respuesta", carta.respuesta);
        nuevo.put("Estado", carta.getEstado());

        if (carta.proximoEstudio == null)
        {
            nuevo.put("ProximoEstudio", "");
        }
        else
        {
            nuevo.put("ProximoEstudio", formatoFecha.format(carta.proximoEstudio));
        }

        // Nueva -> Carta nueva
        // UltimosDiasEstudiados == 0 -> Carta estudiando
        nuevo.put("DiasEntreEstudio", carta.diasEntreEstudio);
        if(carta.unaVezCorrecto)
        {
            nuevo.put("UnaVezCorrecto", 1);
        }
        else
        {
            nuevo.put("UnaVezCorrecto", 0);
        }
        return nuevo;
    }

    public static void reset(Context context)
    {
        BaseDatos GestorDB = new BaseDatos (context, "NombreBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        bd.delete("Mazo", null, null);
        bd.delete("Carta", null, null);
        bd.delete("Huevo", null, null);

        listaMazos = null;
        listaMazos = new ArrayList<>();
        huevo = null;
    }

    // ------------------------------- HUEVO -------------------------------------------------

    public int getNumeroPreguntasHoy()
    {
        int ret = 0;

        for (Mazo m: listaMazos)
        {
            ret += m.getNumeroPreguntasHoy();
        }

        return ret;
    }
    public GestorHuevo getHuevo()
    {
        return huevo;
    }

}
