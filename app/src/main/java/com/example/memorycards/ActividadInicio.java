package com.example.memorycards;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.Locale;

public class ActividadInicio extends AppCompatActivity implements RegistroUsuario.ListenerRegistroUsuario, InicioSesion.ListenerInicioSesion, GestorMazos.ListenerBaseDatos
{

    private String fragmentoActual = "";
    private String usuario = "";
    private String contrasena = "";
    private String contrasenaRepetida = "";

    // Datos de DB cargados
    private boolean preguntasCargadas = false;
    private boolean huevoCargado = false;
    private boolean imagenCargada = false;
    private String idioma = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        // ---------------------- Abrir bundle -------------------------------

        if(savedInstanceState != null)
        {
            fragmentoActual = savedInstanceState.getString("fragmentoActual");
            idioma = savedInstanceState.getString("idioma");

            usuario = savedInstanceState.getString("usuario");
            contrasena = savedInstanceState.getString("contrasena");
            contrasenaRepetida = savedInstanceState.getString("contrasenaRepetida");
        }
        else
        {
            fragmentoActual = "";
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            idioma = sharedPref.getString("idioma", "es");
        }

        cambiarIdioma(idioma, false);

        // ----------------------- Lo que ya estaba ---------------------------------
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actividad_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inicio), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Para que se cierre el menu desplegable al pulsar atras
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                if(fragmentoActual.equals("inicio") || !volverAtras())
                {
                    finish();
                }
            }
        });



        switch (fragmentoActual)
        {
            case "registro":
                getSupportFragmentManager().popBackStack();
                abrirFragmantoRegistro();
                break;

            case "":
            case "inicio":
            default:
                getSupportFragmentManager().popBackStack();
                abrirFragmantoInicioSesion();
                break;
        }
    }


    public void abrirOpcionesIdioma()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.seleccion_idioma));
        final CharSequence[] opciones = {getResources().getString(R.string.castellano), getResources().getString(R.string.ingles)};
        builder.setSingleChoiceItems(opciones, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                switch (i)
                {

                    case 1: // Ingles
                        //Toast.makeText(getBaseContext(), "Ingles", Toast.LENGTH_SHORT).show();
                        cambiarIdioma("en", true);
                        break;

                    case 0:  // Castellano
                    default: // Castellano
                        //Toast.makeText(getBaseContext(), "Castellano", Toast.LENGTH_SHORT).show();
                        cambiarIdioma("es", true);
                        break;
                }

                dialogInterface.dismiss();
            }
        });

        builder.show();
    }


    private void cambiarIdioma(String nuevoIdioma, boolean reiniciar)
    {
        if(fragmentoActual.equals("mapa"))
        {
            volverAtras();
        }

        idioma = nuevoIdioma;

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("idioma", idioma);
        editor.apply();

        Locale nuevaloc = new Locale(nuevoIdioma);
        Locale.setDefault(nuevaloc);
        Configuration config = new Configuration();
        config.setLocale(nuevaloc);
        config.setLayoutDirection(nuevaloc);

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        if(reiniciar)
        {
            this.recreate();
        }
    }

    public boolean volverAtras()
    {
        return getSupportFragmentManager().popBackStackImmediate();
    }

    public void vaciarBackStack()
    {
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    // ----------------------- Apertura de fragmentos ---------------------------
    public void abrirFragmantoInicioSesion()
    {
        fragmentoActual = "inicio";

        Bundle bundle = new Bundle();

        bundle.putString("usuario", usuario);
        bundle.putString("contrasena", contrasena);


        vaciarBackStack();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_inicio, InicioSesion.class, bundle)
                .addToBackStack(null)
                .commit();
    }


    public void abrirFragmantoRegistro()
    {
        fragmentoActual = "registro";

        Bundle bundle = new Bundle();

        bundle.putString("usuario", usuario);
        bundle.putString("contrasena", contrasena);
        bundle.putString("contrasenaRepetida", contrasenaRepetida);


        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_inicio, RegistroUsuario.class, bundle)
                .addToBackStack(null)
                .commit();
    }


    // -------------------------------------- Recuperacion de fragmentos -------------------------------------------

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);

        bundle.putString("usuario", usuario);
        bundle.putString("fragmentoActual", fragmentoActual);
        bundle.putString("idioma", idioma);
    }

    // -------------------------------------- Carga de base de datos -----------------------------------------

    public void cargarBaseDeDatos()
    {
        GestorMazos gestorMazos = GestorMazos.getMiGestorMazos();
        GestorMazos.limpiar();
        gestorMazos.cargarBaseDeDatos(this, usuario, this);
    }


    public void iniciarAplicacion()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("usuario", usuario);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // -------------------------------------- Fragment INICIO SESION --------------------------------------------

    @Override
    public void inicioSesionIniciado()
    {
        fragmentoActual = "inicio";
    }

    @Override
    public void sesionIniciada(String usr)
    {

        usuario = usr;
        cargarBaseDeDatos();
    }

    @Override
    public void abrirRegistro()
    {
        abrirFragmantoRegistro();
    }

    @Override
    public void abrirIdiomas()
    {
        abrirOpcionesIdioma();
    }

    @Override
    public void guardarInicioSesion(String u)
    {
        usuario = u;
    }




    // -------------------------------------- Fragment REGISTRO USUARIO --------------------------------------------

    @Override
    public void registroUsuarioInicado()
    {
        fragmentoActual = "registro";
    }

    @Override
    public void usuarioRegistrado()
    {
        abrirFragmantoInicioSesion();
        //Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void guardarRegistro(String u)
    {
        usuario = u;
    }





    // ----------------------------------- Listener BD ---------------------

    @Override
    public void preguntasCargadas()
    {
        preguntasCargadas = true;
        intentarIniciarAplicacion();
    }

    @Override
    public void error()
    {
        Toast.makeText(this, "Ha ocurrido un error en la base de datos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void huevoCargado()
    {
        huevoCargado = true;

        try
        {
            GestorHuevo huevo = GestorMazos.getMiGestorMazos().getHuevo();
            SharedPreferences sharedPref = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("nombre_huevo", huevo.getNombre());
            editor.putFloat("felicidad_huevo", huevo.getFelicidad());
            editor.apply();
        }
        catch (Exception e){}

        intentarIniciarAplicacion();
    }

    @Override
    public void imagenCargada()
    {
        imagenCargada = true;
        intentarIniciarAplicacion();
    }


    private void intentarIniciarAplicacion()
    {
        if(preguntasCargadas && huevoCargado && imagenCargada)
        {
            anadirUsuarioADiccionario();

            iniciarAplicacion();
        }
    }

    private void anadirUsuarioADiccionario()
    {
        String[] columnas = { UserDictionary.Words.WORD };
        String seleccion = UserDictionary.Words.WORD + " = ?";
        String[] args = { usuario };

        Cursor cursor = getContentResolver().query(
                UserDictionary.Words.CONTENT_URI,
                columnas,
                seleccion,
                args,
                null
        );

        boolean existe = (cursor != null && cursor.getCount() > 0);

        if (cursor != null)
        {
            cursor.close();
        }

        if(!existe)
        {
            ContentValues values = new ContentValues();
            values.put(UserDictionary.Words.WORD, usuario);
            values.put(UserDictionary.Words.FREQUENCY, 250);

            Uri uri = getContentResolver().insert(UserDictionary.Words.CONTENT_URI, values);
        }
    }










}