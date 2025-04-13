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

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.Data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
            String parametros = "user=" + URLEncoder.encode("Eneko", "UTF-8") + "&clave=" + URLEncoder.encode("E", "UTF-8");
            String resultado = conexionGenerica("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/eetxaniz006/WEB/checkpassword.php", parametros);
            Data resultados = new Data.Builder()
                    .putString("resultado",resultado)
                    .build();
            return Result.success(resultados);
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
}
