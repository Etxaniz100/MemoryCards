package com.example.memorycards;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class GestorMazos
{
    private static GestorMazos miGestorMazos;
    private static ArrayList<Mazo> listaMazos;

    private static SimpleDateFormat formatoFecha;
    private static boolean inicializado = false;
    private static GestorHuevo huevo;
    private static String usuario;
    private static ArrayList<UbicacionComida> listaPosicionesComidas;
    private static Bitmap fotoPerfilUsuario;
    public static String fotoPerfilTexto;

    private static int cantidadComida = 100;

    public static GestorMazos getMiGestorMazos()
    {
        if (miGestorMazos == null)
        {
            miGestorMazos = new GestorMazos();
            listaMazos = new ArrayList<Mazo>();
            listaPosicionesComidas = new ArrayList<UbicacionComida>();
            formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return miGestorMazos;
    }

    public static void limpiar()
    {
        listaMazos = null;
        listaMazos = new ArrayList<Mazo>();
        huevo = null;

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

    // ------------------------------------- Gestion mazos -----------------------------------------
    public static boolean crearMazo(String nombre, Context context, LifecycleOwner owner)
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

        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("mazo", nuevo.getNombre())
                .putString("funcion", "subirMazo")
                .build();

        funcionGenerica(context, owner, datosEntrada, "Borrar mazo");

        return true;
    }

    public void borrarMazo(Mazo m, Context context, LifecycleOwner owner)
    {
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("mazo", m.getNombre())
                .putString("funcion", "borrarMazo")
                .build();

        funcionGenerica(context, owner, datosEntrada, "Borrar mazo");

        listaMazos.remove(m);
    }


    // ------------------------------------------------ Gestion cartas -------------------------------------------------------

    public static void guardarCartaEnBd(Mazo m, Carta c, Context context, LifecycleOwner owner)
    {
        Data.Builder datosEntradaBulder = new Data.Builder()
                .putString("funcion", "subirCarta")
                .putString("usuario", usuario)
                .putString("mazo", m.getNombre())
                .putString("pregunta", c.pregunta)
                .putString("respuesta", c.respuesta)
                .putInt("estado", c.getEstado())
                .putInt("id", c.id)
                .putInt("diasEntreEstudio", c.diasEntreEstudio)
                .putInt("unaVezCorrecto", (c.unaVezCorrecto)?1:0);

        if (c.proximoEstudio == null)
        {
            datosEntradaBulder.putString("proximoEstudio", "");
        }
        else
        {
            datosEntradaBulder.putString("proximoEstudio", formatoFecha.format(c.proximoEstudio));
        }

        Data datosEntrada = datosEntradaBulder.build();

        funcionGenerica(context, owner, datosEntrada, "Subir carta");
    }

    public static void actualizarCartaEnBd(Mazo m, Carta c, Context context, LifecycleOwner owner)
    {
        Data.Builder datosEntradaBulder = new Data.Builder()
                .putString("funcion", "actualizarCarta")
                .putString("usuario", usuario)
                .putString("mazo", m.getNombre())
                .putString("pregunta", c.pregunta)
                .putString("respuesta", c.respuesta)
                .putInt("estado", c.getEstado())
                .putInt("id", c.id)
                .putInt("diasEntreEstudio", c.diasEntreEstudio);

        if (c.proximoEstudio == null)
        {
            datosEntradaBulder.putString("proximoEstudio", "");
        }
        else
        {
            datosEntradaBulder.putString("proximoEstudio", formatoFecha.format(c.proximoEstudio));
        }

        datosEntradaBulder.putInt("unaVezCorrecto", (c.unaVezCorrecto)?1:0);

        Data datosEntrada = datosEntradaBulder.build();

        funcionGenerica(context, owner, datosEntrada, "Actualizar carta");
    }


    // -------------------------------------- Gestion huevo ----------------------------------------
    public GestorHuevo nuevoHuevo(Context context, LifecycleOwner owner)
    {
        huevo = new GestorHuevo();

        subirHuevo(context, huevo, owner);

        return huevo;
    }

    public void subirHuevo(Context context, GestorHuevo h, LifecycleOwner owner)
    {
        huevo = h;
        if(h == null)
        {
            return;
        }

        // Poner alarma
        Date fechaAlarma = huevo.calcularFechaTriste();
        huevo.actualizarAlarma(fechaAlarma, context);

        Date hoy = Calendar.getInstance().getTime();
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("nombre", huevo.getNombre())
                .putDouble("progreso", huevo.getProgreso())
                .putDouble("felicidad", huevo.getFelicidad())
                .putString("color", huevo.getColor())
                .putString("ultimaVezAbierto", formatoFecha.format(hoy))
                .putString("funcion", "subirHuevo")
                .build();

        funcionGenerica(context, owner, datosEntrada, "Actualizar huevo");
    }

    public void borrarHuevo(Context context, LifecycleOwner owner)
    {
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("funcion", "borrarHuevo")
                .build();

        funcionGenerica(context, owner, datosEntrada, "Borrar huevo");
    }

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


    // -------------------------------------- Gestion Posiciones ----------------------------------------

    public void subirPosicion(Context context, UbicacionComida u, LifecycleOwner owner)
    {
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putDouble("longitud", u.longitud)
                .putDouble("latitud", u.latitud)
                .putString("funcion", "subirPosicion")
                .build();

        funcionGenerica(context, owner, datosEntrada, "Subir posicion");
    }
    public void eliminarPosicion(Context context, UbicacionComida u, LifecycleOwner owner)
    {
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putDouble("longitud", u.longitud)
                .putDouble("latitud", u.latitud)
                .putString("funcion", "borrarPosicion")
                .build();

        funcionGenerica(context, owner, datosEntrada, "Quitar posicion");
    }

    public void anadirPosicion(Context context, UbicacionComida u, LifecycleOwner owner)
    {
        listaPosicionesComidas.add(u);
        subirPosicion(context, u, owner);
    }

    public void quitarPosicion(Context context, UbicacionComida u, LifecycleOwner owner)
    {
        listaPosicionesComidas.remove(u);
        eliminarPosicion(context, u, owner);
    }


    public void reset(Context context, LifecycleOwner owner)
    {

        for (Mazo m: listaMazos)
        {
            borrarMazo(m, context, owner);
        }
        listaMazos = null;

        borrarHuevo(context, owner);

        listaMazos = null;
        listaMazos = new ArrayList<>();
        huevo = null;
    }



    //   +---------------------------------------------------------------------------------------------------------------------------+
    //  /                                                                                                                             \
    //  |                                                         BD REMOTA                                                           |

    public void cargarBaseDeDatos(Context context, String usr, LifecycleOwner owner)
    {
        usuario = usr;
        listener = (ListenerBaseDatos) context;
        cargarMazos(context, owner);
        cargarPosicionesComidas(context, owner);
        cargarCantidadComida(context, owner);
        cargarPerfilUsuario(context, owner);
    }

    private void cargarMazos(Context context, LifecycleOwner owner)
    {
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("funcion", "cargaMazos")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datosEntrada).build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(otwr.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            if(workInfo.getOutputData() == null)
                            {
                                listener.error();
                                return;
                            }
                            String[] listaMazosResultado = workInfo.getOutputData().getStringArray("mazos");
                            if(listaMazosResultado == null)
                            {
                                Log.d("MIO", "Get mazos devuelve vacio");
                                listener.error();
                                return;

                            }
                            for (String mazo: listaMazosResultado)
                            {
                                if(!mazo.isEmpty() && !mazo.isBlank())
                                {
                                    listaMazos.add(new Mazo(mazo, "azul"));
                                }
                            }

                            if(listaMazos.size() == 0)
                            {
                                cargarHuevo(context, owner);
                                listener.preguntasCargadas();
                            }
                            else
                            {
                                cargarPreguntas(context, owner);
                            }

                            //listener.todoCargado();
                        }
                    }
                });
        WorkManager.getInstance(context).enqueue(otwr);
    }

    private void cargarPreguntas(Context context, LifecycleOwner owner)
    {

        int numeroMazos = listaMazos.size();
        int mazosCargados = 0;
        for (Mazo m: listaMazos)
        {
            mazosCargados += 1;
            boolean ultimo = false;
            if(mazosCargados >= numeroMazos)
            {
                ultimo = true;
            }

            Data datosEntrada = new Data.Builder()
                    .putString("usuario", usuario)
                    .putString("mazo", m.getNombre())
                    .putString("funcion", "cargaPreguntas")
                    .build();

            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datosEntrada).build();

            boolean finalUltimo = ultimo;
            WorkManager.getInstance(context).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(owner, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if(workInfo != null && workInfo.getState().isFinished())
                            {
                                if(workInfo.getOutputData() == null)
                                {
                                    listener.error();
                                    return;
                                }
                                String resultado = workInfo.getOutputData().getString("resultado");

                                if(resultado == null)
                                {
                                    listener.error();
                                    return;
                                }

                                //{"preguntas":[{"Pregunta":"\u00bfComo se apellida Mario?","Mazo":"Videojuegos","Usuario":"Eneko","Respuesta":"Mario","Estado":0,"ProximoEstudio":null,"DiasEntreEstudio":0,"UnaVezCorrecto":0}]}

                                try
                                {
                                    JSONObject json = new JSONObject(resultado);

                                    if(json != null) {

                                        JSONArray arrayPreguntas = json.getJSONArray("preguntas");

                                        for (int i = 0; i < arrayPreguntas.length(); i++)
                                        {
                                            int id = arrayPreguntas.getJSONObject(i).getInt("ID");
                                            String pregunta = arrayPreguntas.getJSONObject(i).getString("Pregunta");
                                            String respuesta = arrayPreguntas.getJSONObject(i).getString("Respuesta");
                                            String proximoEstudio = arrayPreguntas.getJSONObject(i).getString("ProximoEstudio");
                                            int estado = 0;
                                            try
                                            {
                                                estado = arrayPreguntas.getJSONObject(i).getInt("Estado");
                                            }
                                            catch (Exception e){}
                                            int diasEntreEstudio = 0;
                                            try
                                            {
                                                diasEntreEstudio = arrayPreguntas.getJSONObject(i).getInt("DiasEntreEstudio");
                                            }
                                            catch (Exception e){}

                                            int unaVezCorrecto = 1;

                                            try
                                            {
                                                unaVezCorrecto = arrayPreguntas.getJSONObject(i).getInt("UnaVezCorrecto");
                                            }
                                            catch (Exception e){}


                                            Carta c = null;
                                            try
                                            {
                                                Date fechaProximoEstudio = null;
                                                if (!proximoEstudio.isEmpty())
                                                {
                                                    fechaProximoEstudio = formatoFecha.parse(proximoEstudio);
                                                }
                                                c = new Carta(pregunta, respuesta, fechaProximoEstudio, diasEntreEstudio, unaVezCorrecto==1, estado, id);
                                            }
                                            catch (Exception e)
                                            {
                                                c = new Carta(pregunta, respuesta, null, 0, unaVezCorrecto==1, estado, id);
                                            }

                                            if(m.idActual <= c.id)
                                            {
                                                m.idActual += 1;
                                            }

                                            switch (estado)
                                            {
                                                case 0:
                                                    m.getPreguntasNuevas().add(c);
                                                    break;

                                                case 1:
                                                    m.getPreguntasEstudiando().add(c);
                                                    break;

                                                case 2:
                                                    m.getPreguntasEstudiadas().add(c);
                                                    break;
                                            }

                                        }
                                    }

                                }
                                catch (Exception e)
                                {
                                    listener.error();
                                    return;
                                }

                                if(finalUltimo == true)
                                {
                                    cargarHuevo(context, owner);
                                    listener.preguntasCargadas();
                                }


                            }
                        }
                    });
            WorkManager.getInstance(context).enqueue(otwr);
        }
    }

    private void cargarHuevo(Context context, LifecycleOwner owner)
    {
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("funcion", "cargaHuevo")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datosEntrada).build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(otwr.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            if(workInfo.getOutputData() == null)
                            {
                                Log.d("MIO", "Huevo getOutputData == null");
                                listener.error();
                                return;
                            }
                            String resultado = workInfo.getOutputData().getString("resultado");

                            if(resultado == null || resultado.isEmpty())
                            {
                                Log.d("MIO", "Huevo resultado == null");
                                listener.error();
                                return;
                            }

                            //{"preguntas":[{"Pregunta":"\u00bfComo se apellida Mario?","Mazo":"Videojuegos","Usuario":"Eneko","Respuesta":"Mario","Estado":0,"ProximoEstudio":null,"DiasEntreEstudio":0,"UnaVezCorrecto":0}]}

                            try
                            {
                                JSONObject json = new JSONObject(resultado);
                                if(json != null)
                                {
                                    String tipo = json.getString("tipo");
                                    if(!(tipo.equals("") || tipo.equals("error")))
                                    {
                                        JSONObject jsonHuevo = json.getJSONObject("huevo");
                                        String nombre = jsonHuevo.getString("Nombre");
                                        float progreso = (float) jsonHuevo.getDouble("Progreso");
                                        float felicidad = (float) jsonHuevo.getDouble("Felicidad");
                                        String ultimaVezAbierto = jsonHuevo.getString("UltimaVezAbierto");
                                        String color = jsonHuevo.getString("Color");

                                        Date fecha = null;
                                        try
                                        {
                                            if (ultimaVezAbierto != null && !ultimaVezAbierto.equals("null") && !ultimaVezAbierto.isEmpty())
                                            {
                                                fecha = formatoFecha.parse(ultimaVezAbierto);
                                            }
                                        }
                                        catch (Exception e){}

                                        huevo = new GestorHuevo(nombre, progreso, felicidad, fecha, color);
                                    }
                                }
                                else
                                {
                                    Log.d("MIO", "Huevo sin json");
                                }
                            }
                            catch (Exception e)
                            {
                                Log.d("MIO", "Huevo excepcion");
                                listener.error();
                                return;
                            }

                            if(huevo == null)
                            {
                                Random rd = new Random();
                                String[] colores = {"rojo", "verde", "gris"};
                                huevo = new GestorHuevo("???", 75, 75, Calendar.getInstance().getTime(), colores[rd.nextInt(colores.length)]);

                                subirHuevo(context, huevo, owner);
                            }
                            listener.huevoCargado();
                        }
                    }
                });
        WorkManager.getInstance(context).enqueue(otwr);
    }

    private void cargarPosicionesComidas(Context context, LifecycleOwner owner)
    {
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("funcion", "cargaPosiciones")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datosEntrada).build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(otwr.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            if(workInfo.getOutputData() == null)
                            {
                                listener.error();
                                return;
                            }
                            String resultado = workInfo.getOutputData().getString("resultado");

                            if(resultado == null)
                            {
                                listener.error();
                                return;
                            }

                            try
                            {
                                JSONObject json = new JSONObject(resultado);

                                if(json != null) {

                                    JSONArray arrayLugares = json.getJSONArray("lugares");

                                    for (int i = 0; i < arrayLugares.length(); i++)
                                    {
                                        double longitud = arrayLugares.getJSONObject(i).getDouble("Longitud");
                                        double latitud = arrayLugares.getJSONObject(i).getDouble("Latitud");

                                        UbicacionComida u = new UbicacionComida(longitud, latitud, false);
                                        listaPosicionesComidas.add(u);
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                listener.error();
                                return;
                            }
                        }
                    }
                });
        WorkManager.getInstance(context).enqueue(otwr);

    }

    private void cargarCantidadComida(Context context, LifecycleOwner owner)
    {
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("funcion", "cargaComida")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datosEntrada).build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(otwr.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            if(workInfo.getOutputData() == null)
                            {
                                listener.error();
                                return;
                            }
                            String resultado = workInfo.getOutputData().getString("resultado");

                            if(resultado == null)
                            {
                                listener.error();
                                return;
                            }

                            try
                            {
                                JSONObject json = new JSONObject(resultado);

                                if(json != null)
                                {

                                    String tipo = json.getString("tipo");

                                    if(tipo == null || tipo.equals("error"))
                                    {
                                        cantidadComida = 0;
                                        return;
                                    }

                                    cantidadComida = json.getInt("cantidad");
                                }
                            }
                            catch (Exception e)
                            {
                                listener.error();
                                return;
                            }
                        }
                    }
                });
        WorkManager.getInstance(context).enqueue(otwr);

    }


    private void cargarPerfilUsuario(Context context, LifecycleOwner owner)
    {
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("funcion", "descargarFoto")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datosEntrada).build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(otwr.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            if(workInfo.getOutputData() == null)
                            {
                                listener.error();
                                return;
                            }
                            //String resultado = workInfo.getOutputData().getString("resultado");
                            String resultado = fotoPerfilTexto;

                            if(resultado == null)
                            {
                                listener.error();
                                return;
                            }

                            try
                            {
                                JSONObject json = new JSONObject(resultado);

                                if(json != null)
                                {
                                    String tipo = json.getString("tipo");

                                    if(tipo == null || tipo.equals("error"))
                                    {
                                        fotoPerfilUsuario = null;
                                        listener.imagenCargada();
                                        return;
                                    }

                                    String imagenBase64 = json.getString("imagen");
                                    Log.d("MIO", "Tamaño Imagen base64 : " + imagenBase64.length());
                                    fotoPerfilUsuario = BitmapFactory.decodeStream(new ByteArrayInputStream(Base64.decode(imagenBase64, Base64.DEFAULT)));
                                    listener.imagenCargada();
                                }
                            }
                            catch (Exception e)
                            {
                                listener.error();
                                return;
                            }
                        }
                    }
                });
        WorkManager.getInstance(context).enqueue(otwr);

    }





    private static void funcionGenerica(Context context, LifecycleOwner owner, Data datosEntrada, String errorLog)
    {
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datosEntrada).build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(otwr.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            if(workInfo.getOutputData() == null)
                            {
                                listener.error();
                                return;
                            }

                            String resultado = workInfo.getOutputData().getString("resultado");

                            if(resultado == null || resultado.isEmpty())
                            {
                                Log.d("MIO", "resultado == null // " + errorLog);
                                listener.error();
                                return;
                            }
                            try
                            {
                                JSONObject json = new JSONObject(resultado);
                                if(json != null)
                                {
                                    String tipo = json.getString("tipo");
                                    if((tipo.equals("") || tipo.equals("error")))
                                    {
                                        Log.d("MIO", "Tipo: "+ tipo + "  // " + errorLog);
                                        listener.error();
                                        return;
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                Log.d("MIO", "Excepcion // " + errorLog);
                                listener.error();
                            }
                        }
                    }
                });
        WorkManager.getInstance(context).enqueue(otwr);
    }

    private static ListenerBaseDatos listener;
    public interface ListenerBaseDatos
    {
        void preguntasCargadas();
        void error();
        void huevoCargado();
        void imagenCargada();
    }

    // ------------------------------------------ Posiciones huevo ---------------------------------------------

    public ArrayList<UbicacionComida> getListaPosicionesComidas()
    {
        return listaPosicionesComidas;
    }

    public static int getCantidadComida()
    {
        return cantidadComida;
    }

    public static void setCantidadComida(int n, Context context, LifecycleOwner owner)
    {
        cantidadComida = n;
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putInt("cantidad", cantidadComida)
                .putString("funcion", "subirComida")
                .build();

        funcionGenerica(context, owner, datosEntrada, "Subir cantidad");

    }

    // ------------------------------ IMAGENES -------------------------------------------------

    public static Bitmap getPerfilUsuario()
    {
        return fotoPerfilUsuario;
    }

    public static void setPerfilUsuario(Bitmap imagen, Context context, LifecycleOwner owner)
    {
        fotoPerfilUsuario = imagen;

        /*
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        String imagenBase64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        */
        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                //.putString("imagen", imagenBase64)
                .putString("funcion", "subirFoto")
                .build();

        funcionGenerica(context, owner, datosEntrada, "Subir imagen perfil usuario");

    }

}










// --------------------------------- OTROS ------------------------------
    /*
    public static void inicializarTodo(Context context, boolean forzar)
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
                subirMazoBd(m1, context);

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
                Random rd = new Random();
                String[] colores = {"rojo", "verde", "gris"};
                huevo = new GestorHuevo("???", 75, 75, Calendar.getInstance().getTime(), colores[rd.nextInt(colores.length)]);
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
            inicializarTodo(context, false);
        }
    }
    */

    /*
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
*/
