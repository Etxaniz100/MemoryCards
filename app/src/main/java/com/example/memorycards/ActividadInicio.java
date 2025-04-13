package com.example.memorycards;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
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

    }

    // -------------------------------------- Fragment REGISTRO USUARIO --------------------------------------------

    @Override
    public void registroUsuarioInicado()
    {
        fragmentoActual = "registro";
    }
}