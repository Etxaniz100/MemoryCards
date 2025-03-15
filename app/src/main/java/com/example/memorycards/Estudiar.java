package com.example.memorycards;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Estudiar extends Fragment {

    private Mazo mazo;
    private String nombreMazo;
    private TextView textPregunta, textRespuesta;
    private Button botonBien, botonMal, botonMostrar;
    private Carta estudiandoAhora;
    private ListenerFragmentEstudiar listener;
    private boolean mostrado;

    public Estudiar() {
        // Required empty public constructor
    }

    public static Estudiar newInstance(String param1, String param2) {
        Estudiar fragment = new Estudiar();
        Bundle args = new Bundle();
        args.putString("NombreMazo", fragment.nombreMazo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_estudiar, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // ---------------- Recuperar datos del bundle
        Bundle bundle = requireArguments();
        nombreMazo = bundle.getString("nombreMazo");
        if(nombreMazo != null)
        {
            mazo = GestorMazos.getMiGestorMazos().getMazo(nombreMazo);
        }

        String pregunta = bundle.getString("pregunta");
        if(pregunta.isEmpty() || pregunta.isBlank())
        {
            estudiandoAhora = null;
        }
        else
        {
            estudiandoAhora = mazo.obtenerCarta(pregunta);
        }

        mostrado = bundle.getBoolean("respuestaMostrada");

        //String aaa = mostrado?"t":"f";
        //Toast.makeText(getContext(), aaa, Toast.LENGTH_SHORT).show();

        // ---------------- Obtener views

        textPregunta = view.findViewById(R.id.texto_pregunta);
        textRespuesta = view.findViewById(R.id.texto_respuesta);

        textPregunta.setText("...");
        textRespuesta.setText("...");

        botonBien = view.findViewById(R.id.boton_bien);
        botonBien.setOnClickListener(v -> respuestaCorrecta(view));
        botonMal = view.findViewById(R.id.boton_mal);
        botonMal.setOnClickListener(v -> respuestaIncorrecta(view));
        botonMostrar = view.findViewById(R.id.boton_mostrar);
        botonMostrar.setOnClickListener(v -> mostrarRespuesta(view));

        if(estudiandoAhora == null)
        {
            siguientePregunta();
        } else if (mostrado)
        {
            mostrarRespuesta(view);
        }
        else
        {
            textPregunta.setText(estudiandoAhora.pregunta);
            textRespuesta.setText("...");
            botonMostrar.setVisibility(View.VISIBLE);
            botonBien.setVisibility(View.INVISIBLE);
            botonMal.setVisibility(View.INVISIBLE);
        }


        listener.estudiarIniciado();

    }


    public void siguientePregunta()
    {
        Carta siguienteCarta = mazo.siguienteCarta();

        if(siguienteCarta == null)
        {
            listener.finEstudio();
            return;
        }

        estudiandoAhora = siguienteCarta;


        textPregunta.setText(estudiandoAhora.pregunta);
        textRespuesta.setText("...");
        botonMostrar.setVisibility(View.VISIBLE);
        botonBien.setVisibility(View.INVISIBLE);
        botonMal.setVisibility(View.INVISIBLE);

        mostrado = false;
    }

    public void mostrarRespuesta(View v)
    {
        textPregunta.setText(estudiandoAhora.pregunta);
        textRespuesta.setText(estudiandoAhora.respuesta);
        botonMostrar.setVisibility(View.INVISIBLE);
        botonBien.setVisibility(View.VISIBLE);
        botonMal.setVisibility(View.VISIBLE);

        String texto = getContext().getResources().getString(R.string.bien);
        if(estudiandoAhora.getEstado() != 0 && estudiandoAhora.unaVezCorrecto)
        {
            texto += " +"+(estudiandoAhora.diasEntreEstudio +1);
            texto += " " +getContext().getResources().getString(R.string.dias);
        }
        botonBien.setText(texto);

        mostrado = true;
    }

    public void respuestaCorrecta(View v)
    {
        if(estudiandoAhora != null)
        {
            mazo.cartaAcertada(estudiandoAhora, true, getContext());
        }
        siguientePregunta();
    }

    public void respuestaIncorrecta(View v)
    {
        if(estudiandoAhora != null)
        {
            mazo.cartaAcertada(estudiandoAhora, false, getContext());
        }

        siguientePregunta();
    }

    // --------------------------------- Recuperación de información

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);

        listener.guardarCartaActual(estudiandoAhora,  mostrado);

    }

    // ------------------------- Funciones para concectarse con la actividad ---------------------------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (Estudiar.ListenerFragmentEstudiar) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString() + "debe implementar ListenerFragmentMostrarMazo");
        }
    }

    public interface ListenerFragmentEstudiar
    {
        void finEstudio();
        void estudiarIniciado();
        void guardarCartaActual(Carta c, boolean mostrado);
    }
}