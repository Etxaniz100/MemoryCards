package com.example.memorycards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements  MostrarListaMazos.ListenerFragmentListaMazos,
                                                                MostrarMazo.ListenerFragmentMostrarMazo,
                                                                Estudiar.ListenerFragmentEstudiar,
                                                                NuevaPregunta.ListenerFragmentNuevaCarta,
                                                                Huevo.ListenerFragmentHuevo
{
    // En main se tiene la funcionalidad de la toolbar, cajon desplegable y fragmentView
    // Se compone de dos layouts:
    //      - activity_main.xml, contiene el visor de fragments y la toolbar
    //      - navigation_drawer.xml, contiene el navigation drawer y contiene activity main

    // Atributos
    private DrawerLayout menuDesplegable;
    private GestorMazos gestorMazos;


    private String fragmentoActual = "";
    private Mazo mazoActual;
    private String idioma = "es";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        // -------------------- GESTOR MAZOS -----------------------
        gestorMazos = GestorMazos.getMiGestorMazos();


        // ---------------------- Abrir bundle -------------------------------

        if(savedInstanceState != null)
        {
            fragmentoActual = savedInstanceState.getString("fragmentoActual");
            String nombreMazoActual = savedInstanceState.getString("mazoActual");
            mazoActual = GestorMazos.getMiGestorMazos().getMazo(nombreMazoActual);
            idioma = savedInstanceState.getString("idioma");
        }
        else
        {
            fragmentoActual = "";
            mazoActual = null;
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            idioma = sharedPref.getString("idioma", "es");
        }

        cambiarIdioma(idioma, false);

        // ----------------------------- Lo que ya venia -----------------------------

        setContentView(R.layout.navigation_drawer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // -------------------- TOOLBAR -------------------------------
        setSupportActionBar(findViewById(R.id.toolbar));

        // Para que no aparezca el título de la aplicación
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Para poner el icono de hamburguesa
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_24px);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // ------------------- NAVIGATION DRAWER ----------------------

        menuDesplegable = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.drawer_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_principal:
                        abrirFragmentoListaMazos();
                        break;
                    case R.id.menu_huevo:
                        abrirFragmentoHuevo();
                        break;


                    case R.id.cambiar_idioma:
                        abrirOpcionesIdioma();
                        break;
                }
                menuDesplegable.closeDrawers();
                return false;
            }
        });

        // Para que se cierre el menu desplegable al pulsar atras
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                if (menuDesplegable.isDrawerOpen(GravityCompat.START))
                {
                    menuDesplegable.closeDrawer(GravityCompat.START);
                }
                else
                {
                    if(!volverAtras())
                    {
                        finish();
                    }
                }
            }
        });

        // ----------------------------------- Notificaciones ----------------------------------------


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 30);
            }
        }


        // --------------------------------- Abrir fragmento ------------------------------------

        // Añadir fragmento

        switch (fragmentoActual)
        {
            case "estudiar":
                getSupportFragmentManager().popBackStack();
                abrirFragmentoEstudiar(mazoActual);
                break;

            case "mostrarMazo":
                getSupportFragmentManager().popBackStack();
                abrirFragmentoMostrarMazo(mazoActual);
                break;

            case "mostrarPreguntas":
                getSupportFragmentManager().popBackStack();
                break;

            case "nuevaPregunta":
                getSupportFragmentManager().popBackStack();
                abrirFragmentoNuevaCarta(mazoActual);
                break;

            case "huevo":
                getSupportFragmentManager().popBackStack();
                abrirFragmentoHuevo();
                break;


            case "":
            case "listaMazos":
            default:
                getSupportFragmentManager().popBackStack();
                abrirFragmentoListaMazos();
                break;
        }
    }

    // ---------------------------------------------------------- IDIOMA ------------------------------------------------------------

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

    // ----------------------------------------------------------- TOOLBAR ---------------------------------------------------------------------------
    // Para la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_puntitos,menu);
        return true;
    }

    // Que opción se selecciona en el menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            // Para abrir el menu con el icono hamburguesa
            case android.R.id.home:
                menuDesplegable.openDrawer(GravityCompat.START);
                return true;

            case R.id.resetear_aplicacion:
                gestorMazos.reset(getBaseContext());
                gestorMazos.inicializarMazos(getBaseContext(), true);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }





    // ------------------------------------- Metodos de gestión de fragmentos ------------------------------------

    public void vaciarBackStack()
    {
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public boolean volverAtras()
    {
        return getSupportFragmentManager().popBackStackImmediate();
    }
    public void abrirFragmentoListaMazos()
    {
        fragmentoActual = "listaMazos";
        mazoActual = null;
        cambiarTitulo(null);

        vaciarBackStack();
        // Lanzar el fragmento
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, MostrarListaMazos.class, null)
                //.addToBackStack(null)
                .commit();
    }

    public void abrirFragmentoMostrarMazo(Mazo m)
    {
        fragmentoActual = "mostrarMazo";
        mazoActual = m;
        cambiarTitulo(m.getNombre());

        Bundle bundle = new Bundle();
        bundle.putString("nombreMazo", m.getNombre());

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, MostrarMazo.class, bundle)
                .addToBackStack(null)
                .commit();
    }

    public void abrirFragmentoEstudiar(Mazo m)
    {
        fragmentoActual = "estudiar";
        mazoActual = m;
        cambiarTitulo(m.getNombre());

        Bundle bundle = new Bundle();
        bundle.putString("nombreMazo", m.getNombre());

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, Estudiar.class, bundle)
                .addToBackStack(null)
                .commit();
    }

    public void abrirFragmentoNuevaCarta(Mazo m)
    {
        fragmentoActual = "nuevaPregunta";
        mazoActual = m;
        cambiarTitulo(m.getNombre());

        Bundle bundle = new Bundle();
        bundle.putString("nombreMazo", m.getNombre());

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, NuevaPregunta.class, bundle)
                .addToBackStack(null)
                .commit();
    }



    public void abrirFragmentoHuevo()
    {
        fragmentoActual = "nuevaPregunta";
        cambiarTitulo("");

        Bundle bundle = new Bundle();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, Huevo.class, bundle)
                .addToBackStack(null)
                .commit();
    }

    // -------------------------------------- Recuperacion de fragmentos -------------------------------------------

    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);

        bundle.putString("fragmentoActual", fragmentoActual);

        if(mazoActual == null)
        {
            bundle.putString("mazoActual", "");
        }
        else
        {
            bundle.putString("mazoActual", mazoActual.getNombre());
        }

        bundle.putString("idioma", idioma);



    }

    public void onRestoreInstanceState (Bundle bundle)
    {

    }

    // -------------------------------------- Fragment MostrarListaMazos --------------------------------------------

    public void cambiarTitulo(String titulo)
    {
        if(titulo == null || titulo.isBlank() || titulo.isEmpty())
        {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        else
        {
            getSupportActionBar().setTitle(titulo);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

    }
    @Override
    public void selecionarMazo(Mazo m)
    {
        // Lanzar el fragmento
        abrirFragmentoMostrarMazo(m);
    }

    @Override
    public void listaMazosIniciado()
    {
        fragmentoActual = "listaMazos";
        cambiarTitulo("");
    }


    // -------------------------------------- Fragment MostrarMazo --------------------------------------------
    public void pulsarEstudiar(Mazo m)
    {
        abrirFragmentoEstudiar(m);
    }
    public void pulsarAnadirPregunta(Mazo m)
    {
        abrirFragmentoNuevaCarta(m);
    }
    public void pulsarVerPreguntas()
    {

    }
    public void pulsarAjustesMazo()
    {

    }

    public void mostrarMazoIniciado()
    {
        fragmentoActual = "mostrarMazo";
    }

    // -------------------------------------- Fragment Estudiar --------------------------------------------
    @Override
    public void finEstudio()
    {
        // Vuelve a mostrar el mazo
        volverAtras();
    }

    @Override
    public void estudiarIniciado()
    {
        fragmentoActual = "estudiar";
    }

    // -------------------------------------- Fragment Nueva Carta --------------------------------------------
    public void cancelarNuevaCarta()
    {
        volverAtras();
    }
    public void nuevaCartaAnadida()
    {
        volverAtras();
    }

    public void nuevaPreguntaIniciado()
    {
        fragmentoActual = "nuevaPregunta";
    }

    // -------------------------------------- Fragment HUEVO --------------------------------------------

    @Override
    public void huevoIniciado()
    {
        fragmentoActual = "huevo";
    }

    @Override
    public void huevoAbierto()
    {
        getSupportFragmentManager().popBackStack();
        abrirFragmentoHuevo();
    }

    @Override
    public void huevoCaducado()
    {
        getSupportFragmentManager().popBackStack();
        abrirFragmentoHuevo();
    }





}