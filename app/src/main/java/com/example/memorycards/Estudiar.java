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

public class Estudiar extends Fragment {

    private Mazo mazo;
    private String nombreMazo;
    private TextView textPregunta, textRespuesta;
    private Button botonBien, botonMal, botonMostrar;
    private Carta estudiandoAhora;
    private ListenerFragmentEstudiar listener;

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

        // Obtener mazo
        nombreMazo = requireArguments().getString("nombreMazo");
        if(nombreMazo != null)
        {
            mazo = GestorMazos.getMiGestorMazos().getMazo(nombreMazo);
        }

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

        siguientePregunta();

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
    }

    public void mostrarRespuesta(View v)
    {
        textRespuesta.setText(estudiandoAhora.respuesta);
        botonMostrar.setVisibility(View.INVISIBLE);
        botonBien.setVisibility(View.VISIBLE);
        botonMal.setVisibility(View.VISIBLE);

        if(estudiandoAhora.getEstado() != 0)
        {
            String texto = getContext().getResources().getString(R.string.bien);
            if(estudiandoAhora.unaVezCorrecto)
            {
                texto += " +"+(estudiandoAhora.diasEntreEstudio +1);
                texto += " " +getContext().getResources().getString(R.string.dias);
            }
            botonBien.setText(texto);
        }
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
    }
}