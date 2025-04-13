package com.example.memorycards;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActividadInicio extends AppCompatActivity implements RegistroUsuario.ListenerRegistroUsuario, InicioSesion.ListenerInicioSesion
{

    private String fragmentoActual = "";

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
                if(!volverAtras())
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

    public void abrirFragmantoInicioSesion()
    {
        fragmentoActual = "inicio";

        Bundle bundle = new Bundle();

        //bundle.putString("sdfsdf", dfsdfsdf);

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

    // -------------------------------------- Fragment INICIO SESION --------------------------------------------

    @Override
    public void inicioSesionIniciado()
    {

        fragmentoActual = "inicio";
    }

    @Override
    public void sesionIniciada(String usr)
    {
        Toast.makeText(this, usr, Toast.LENGTH_SHORT).show();
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





}