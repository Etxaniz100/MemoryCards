package com.example.memorycards;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.work.Data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URLEncoder;

public class conexionBDWebService extends Worker
{
    public conexionBDWebService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork()
    {
        try
        {
            String usuario = getInputData().getString("usuario");
            String clave = getInputData().getString("clave");
            String funcion = getInputData().getString("funcion");
            String nombreMazo = getInputData().getString("mazo");


            String parametros;
            String resultado;

            Data resultadoData = null;

            Log.d("MIO", "do Work Pre Switch");

            switch (funcion)
            {

                // ------------------------- USUARIO ----------------------------------------------------------------------------------------------------------

                case "inicio":
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&clave=" + URLEncoder.encode(clave, "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/checkpassword.php", parametros);

                    JSONObject json = new JSONObject(resultado);

                    if(json != null)
                    {
                        String tipo = json.getString("tipo");
                        String mensaje = json.getString("mensaje");

                        resultadoData = new Data.Builder()
                                .putString("tipo",tipo)
                                .putString("mensaje",mensaje)
                                .build();
                    }

                    break;

                case "registro":
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&clave=" + URLEncoder.encode(clave, "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/registeruser.php", parametros);

                    json = new JSONObject(resultado);

                    if(json != null)
                    {
                        String tipo = json.getString("tipo");
                        String mensaje = json.getString("mensaje");

                        resultadoData = new Data.Builder()
                                .putString("tipo",tipo)
                                .putString("mensaje",mensaje)
                                .build();
                    }
                    break;


                // ------------------------- MAZO ----------------------------------------------------------------------------------------------------------

                case "cargaMazos":
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8")+ "&funcion=" + URLEncoder.encode("obtener", "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarmazo.php", parametros);

                    json = new JSONObject(resultado);


                    if(json != null)
                    {

                        JSONArray arrayMazos = json.getJSONArray("mazos");
                        String[] lista = new String[arrayMazos.length()];

                        for (int i = 0; i < arrayMazos.length(); i++)
                        {
                            lista[i] = arrayMazos.getString(i);

                        }

                        resultadoData = new Data.Builder()
                                .putStringArray("mazos", lista)
                                .putString("resultado", resultado)
                                .build();
                    }
                    break;


                case "subirMazo":
                    Log.d("MIO", "Subiendo mazo...");
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&mazo=" + URLEncoder.encode(nombreMazo, "UTF-8")+ "&funcion=" + URLEncoder.encode("subir", "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarmazo.php", parametros);
                    Log.d("MIO", "Resultado subir mazo : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "borrarMazo":
                    Log.d("MIO", "Borrando mazo...");
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&mazo=" + URLEncoder.encode(nombreMazo, "UTF-8")+ "&funcion=" + URLEncoder.encode("borrar", "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarmazo.php", parametros);
                    Log.d("MIO", "Resultado borrar mazo : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                // ------------------------- CARTA ----------------------------------------------------------------------------------------------------------

                case "cargaPreguntas":
                    Log.d("MIO", "Cargando preguntas...");
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&mazo=" + URLEncoder.encode(nombreMazo, "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/obtenerpreguntas.php", parametros);
                    Log.d("MIO", "Resultado cargarPreguntas : " +resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "subirCarta":
                    Log.d("MIO", "Subiendo carta...");

                    JSONObject jsonSubirCarta = new JSONObject();

                    jsonSubirCarta.put("funcion", "subir");

                    jsonSubirCarta.put("mazo", nombreMazo);
                    jsonSubirCarta.put("usuario", usuario);

                    int idCarta = getInputData().getInt("id", 0);
                    jsonSubirCarta.put("id", idCarta);

                    String pregunta = getInputData().getString("pregunta");
                    jsonSubirCarta.put("pregunta", pregunta);

                    String respuesta = getInputData().getString("respuesta");
                    jsonSubirCarta.put("respuesta", respuesta);

                    String proximoEstudio = getInputData().getString("proximoEstudio");
                    jsonSubirCarta.put("proximoEstudio", proximoEstudio);

                    int estado = getInputData().getInt("estado", 0);
                    jsonSubirCarta.put("estado", estado);


                    int diasEntreEstudio = getInputData().getInt("diasEntreEstudio", 0);
                    jsonSubirCarta.put("diasEntreEstudio", diasEntreEstudio);

                    int unaVezCorrecto = getInputData().getInt("unaVezCorrecto", 0);
                    jsonSubirCarta.put("unaVezCorrecto", unaVezCorrecto);
                    Log.d("MIO", "Subir carta : " + jsonSubirCarta.toString());


                    resultado = conexionJson("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarcarta.php", jsonSubirCarta);
                    Log.d("MIO", "Resultado subir carta : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "actualizarCarta":
                    Log.d("MIO", "Actualizando carta...");

                    jsonSubirCarta = new JSONObject();

                    jsonSubirCarta.put("funcion", "actualizar");

                    jsonSubirCarta.put("mazo", nombreMazo);
                    jsonSubirCarta.put("usuario", usuario);

                    idCarta = getInputData().getInt("id", 0);
                    jsonSubirCarta.put("id", idCarta);

                    pregunta = getInputData().getString("pregunta");
                    jsonSubirCarta.put("pregunta", pregunta);

                    respuesta = getInputData().getString("respuesta");
                    jsonSubirCarta.put("respuesta", respuesta);

                    proximoEstudio = getInputData().getString("proximoEstudio");
                    jsonSubirCarta.put("proximoEstudio", proximoEstudio);

                    estado = getInputData().getInt("estado", 0);
                    jsonSubirCarta.put("estado", estado);


                    diasEntreEstudio = getInputData().getInt("diasEntreEstudio", 0);
                    jsonSubirCarta.put("diasEntreEstudio", diasEntreEstudio);

                    unaVezCorrecto = getInputData().getInt("unaVezCorrecto", 0);
                    jsonSubirCarta.put("unaVezCorrecto", unaVezCorrecto);

                    Log.d("MIO", "Actualizar carta : " + jsonSubirCarta.toString());

                    resultado = conexionJson("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarcarta.php", jsonSubirCarta);
                    Log.d("MIO", "Resultado actualizar carta : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                // ------------------------- HUEVO ----------------------------------------------------------------------------------------------------------
                case "cargaHuevo":
                    Log.d("MIO", "Cargando huevo...");
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&funcion=" + URLEncoder.encode("obtener", "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarhuevo.php", parametros);
                    Log.d("MIO", "Resultado cargarHuevo : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "subirHuevo":
                    Log.d("MIO", "Subiendo huevo...");

                    String nombreHuevo = getInputData().getString("nombre");
                    double progresoHuevo = getInputData().getDouble("progreso", 0);
                    double felicidadHuevo = getInputData().getDouble("felicidad", 0);
                    String colorHuevo = getInputData().getString("color");
                    String ultimaVezAbiertoHuevo = getInputData().getString("ultimaVezAbierto");

                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8")
                                + "&funcion=" + URLEncoder.encode("subir", "UTF-8")
                                + "&nombre=" + URLEncoder.encode(nombreHuevo, "UTF-8")
                                + "&progreso=" + URLEncoder.encode(String.valueOf(progresoHuevo), "UTF-8")
                                + "&felicidad=" + URLEncoder.encode(String.valueOf(felicidadHuevo), "UTF-8")
                                + "&color=" + URLEncoder.encode(colorHuevo, "UTF-8")
                                + "&ultimaVezAbierto=" + URLEncoder.encode(ultimaVezAbiertoHuevo, "UTF-8");

                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarhuevo.php", parametros);
                    Log.d("MIO", "Resultado subir huevo : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "borrarHuevo":
                    Log.d("MIO", "Borrando huevo...");
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&funcion=" + URLEncoder.encode("borrar", "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarhuevo.php", parametros);
                    Log.d("MIO", "Resultado borrar huevo : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                // ------------------------- MAPA y Comida ----------------------------------------------------------------------------------------------------------
                case "cargaPosiciones":
                    Log.d("MIO", "Cargando posiciones comida...");
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&funcion=" + URLEncoder.encode("obtenerposiciones", "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarhuevo.php", parametros);
                    Log.d("MIO", "Resultado Cargando posiciones comida : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "subirPosicion":
                    Log.d("MIO", "Subiendo posicion...");

                    double longitud = getInputData().getDouble("longitud", 0);
                    double latitud = getInputData().getDouble("latitud", 0);


                    parametros =    "user=" + URLEncoder.encode(usuario, "UTF-8")
                                +   "&funcion=" + URLEncoder.encode("comidanueva", "UTF-8")
                                +   "&longitud=" + URLEncoder.encode(""+longitud, "UTF-8")
                                +   "&latitud=" + URLEncoder.encode(""+latitud, "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarhuevo.php", parametros);

                    Log.d("MIO", "Resultado subir posicion : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "borrarPosicion":
                    Log.d("MIO", "Borrando posicion...");

                    longitud = getInputData().getDouble("longitud", 0);
                    latitud = getInputData().getDouble("latitud", 0);

                    parametros =    "user=" + URLEncoder.encode(usuario, "UTF-8")
                            +   "&funcion=" + URLEncoder.encode("comidarecogida", "UTF-8")
                            +   "&longitud=" + URLEncoder.encode(""+longitud, "UTF-8")
                            +   "&latitud=" + URLEncoder.encode(""+latitud, "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarhuevo.php", parametros);
                    Log.d("MIO", "Resultado borrar posicion : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "cargaComida":
                    Log.d("MIO", "Cargando cantidad comida...");
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&funcion=" + URLEncoder.encode("obtenercantidad", "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarhuevo.php", parametros);
                    Log.d("MIO", "Resultado Cargando cantidad comida : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "subirComida":
                    Log.d("MIO", "Subiendo comida...");

                    int cantidad = getInputData().getInt("cantidad", 0);

                    parametros =    "user=" + URLEncoder.encode(usuario, "UTF-8")
                            +   "&funcion=" + URLEncoder.encode("subircantidad", "UTF-8")
                            +   "&cantidad=" + URLEncoder.encode(""+cantidad, "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarhuevo.php", parametros);

                    Log.d("MIO", "Resultado subir comida : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                    // ----------------------- IMAGENES --------------------------------------------

                case "subirFoto":
                    Log.d("MIO", "Subiendo foto...");

                    Bitmap imagen = GestorMazos.getPerfilUsuario();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // No es necesarios guardar toda la calidad de la imagen, que luego se ve en pequeño
                    imagen.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                    String imagenBase64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                    //String imagenBase64 = getInputData().getString("imagen");

                    parametros =    "user=" + URLEncoder.encode(usuario, "UTF-8")
                            +   "&funcion=" + URLEncoder.encode("subir", "UTF-8")
                            +   "&imagen=" + URLEncoder.encode(imagenBase64, "UTF-8");

                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarimagen.php", parametros);

                    Log.d("MIO", "Resultado subir foto : " + resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;

                case "descargarFoto":
                    Log.d("MIO", "Descargando foto...");

                    parametros =    "user=" + URLEncoder.encode(usuario, "UTF-8")
                            +   "&funcion=" + URLEncoder.encode("obtener", "UTF-8");

                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/gestionarimagen.php", parametros);

                    GestorMazos.fotoPerfilTexto = resultado;
                    Log.d("MIO", "Resultado descargar foto : " + resultado.length());
                    resultadoData = new Data.Builder()
                            //.putString("resultado", resultado)
                            .build();
                    break;


                default:
                    return Result.failure();
            }

            return Result.success(resultadoData);
        }
        catch (Exception e)
        {
            return Result.failure();
        }

    }

    private String conexionGenerica(String enlace, String parametros)
    {
        String resultado = "";

        try {
            // Dirección del web service
            URL url = new URL(enlace);


            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Enviar los parámetros
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();
            int statusCode = urlConnection.getResponseCode();


            if (statusCode == 200)
            {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                StringBuilder devuelto = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    devuelto.append(line);
                }

                inputStream.close();

                resultado = devuelto.toString();
            }

            urlConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return resultado;

    }

    private String conexionJson(String enlace, JSONObject json)
    {
        String resultado = "";

        try {
            // Dirección del web service
            URL url = new URL(enlace);


            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Enviar los parámetros
            OutputStream os = urlConnection.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200)
            {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                StringBuilder devuelto = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    devuelto.append(line);
                }

                inputStream.close();

                resultado = devuelto.toString();
            }

            urlConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return resultado;

    }




    /*

Object jsonParsed = new JSONTokener(result.toString()).nextValue();


                if (jsonParsed instanceof JSONObject) {
                    // JSON tipo objeto
                    JSONObject json = (JSONObject) jsonParsed;

                    String nombre = json.optString("nombre", "Sin nombre");
                    String apellido = json.optString("Apellido", "Sin apellido");
                    int edad = json.optInt("Edad", 0);
                    String direccion = json.optString("Direccion", "Sin dirección");

                    resultadoFinal = "Nombre: " + nombre + "\n" +
                                     "Apellido: " + apellido + "\n" +
                                     "Edad: " + edad + "\n" +
                                     "Dirección: " + direccion;

                } else if (jsonParsed instanceof JSONArray) {
                    // JSON tipo array
                    JSONArray jsonArray = (JSONArray) jsonParsed;

                    StringBuilder lista = new StringBuilder("Nombres:\n");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String nombre = obj.optString("Nombre", "Sin nombre");
                        lista.append("- ").append(nombre).append("\n");
                    }

                    resultadoFinal = lista.toString();
                } else {
                    resultadoFinal = "Respuesta inesperada";
                }
     */
}
