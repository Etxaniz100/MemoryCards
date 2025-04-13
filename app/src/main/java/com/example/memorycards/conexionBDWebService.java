package com.example.memorycards;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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

                case "cargaMazos":
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/obtenermazos.php", parametros);

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


                case "cargaPreguntas":
                    Log.d("MIO", "Cargando preguntas...");
                    parametros = "user=" + URLEncoder.encode(usuario, "UTF-8") + "&mazo=" + URLEncoder.encode(nombreMazo, "UTF-8");
                    resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/obtenerpreguntas.php", parametros);
                    Log.d("MIO", resultado);
                    resultadoData = new Data.Builder()
                            .putString("resultado", resultado)
                            .build();
                    break;
                    /*
                    json = new JSONObject(resultado);

                    if(json != null)
                    {

                        JSONArray arrayMazos = json.getJSONArray("mazos");
                        String[] lista = new String[arrayMazos.length()];

                        for(int i = 0; i < arrayMazos.length(); i++)
                        {
                            lista[i] = arrayMazos.getString(i);

                        }

                        resultadoData = new Data.Builder()
                                .putStringArray("mazos", lista)
                                .putString("resultado", resultado)
                                .build();
                        break;
                    }*/


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
