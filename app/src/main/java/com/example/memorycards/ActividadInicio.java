package com.example.memorycards;

import android.content.Intent;
import android.os.Bundle;
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

public class ActividadInicio extends AppCompatActivity implements RegistroUsuario.ListenerRegistroUsuario, InicioSesion.ListenerInicioSesion, GestorMazos.ListenerBaseDatos
{

    private String fragmentoActual = "";
    private String usuario = "";

    // Datos de DB cargados
    private boolean preguntasCargadas = false;
    private boolean huevoCargado = false;
    private boolean imagenCargada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //bundle.putString("sdfsdf", dfsdfsdf);

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

        //bundle.putString("sdfsdf", dfsdfsdf);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_inicio, RegistroUsuario.class, bundle)
                .addToBackStack(null)
                .commit();
    }

    // -------------------------------------- Recuperacion de fragmentos -------------------------------------------


    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        //TODO
        /*
        bundle.putString("fragmentoActual", fragmentoActual);

        if(mazoActual == null)
        {
            bundle.putString("mazoActual", "");
        }
        else
        {
            bundle.putString("mazoActual", mazoActual.getNombre());
        }

        if(fragmentoActual.equals("estudiar") && cartaActual != null)
        {
            bundle.putString("cartaActual", cartaActual.pregunta);
            bundle.putBoolean("respuestaMostrada", respuestaMostrada);
        } else if (fragmentoActual.equals("nuevaPregunta"))
        {
            bundle.putString("preguntaAMedias", preguntaAMedias);
            bundle.putString("respuestaAMedias", respuestaAMedias);
        }

        bundle.putString("idioma", idioma);*/
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
        Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
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
            iniciarAplicacion();
        }
    }










}