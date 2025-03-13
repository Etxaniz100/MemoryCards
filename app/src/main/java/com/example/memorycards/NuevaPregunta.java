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
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuevaPregunta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuevaPregunta extends Fragment {


    private String nombreMazo;
    private Mazo mazo;
    private EditText textoPregunta, textoRespuesta;
    private ListenerFragmentNuevaCarta listener;

    public NuevaPregunta() {
        // Required empty public constructor
    }

    public static NuevaPregunta newInstance(String param1, String param2) {
        NuevaPregunta fragment = new NuevaPregunta();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nueva_pregunta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener mazo
        nombreMazo = requireArguments().getString("nombreMazo");
        if(nombreMazo != null)
        {
            mazo = GestorMazos.getMiGestorMazos().getMazo(nombreMazo);
        }

        textoPregunta = (EditText) view.findViewById(R.id.texto_pregunta);
        textoRespuesta = (EditText) view.findViewById(R.id.texto_respuesta);

        Button botonAceptar = view.findViewById(R.id.boton_aceptar_nueva_pregunta);
        botonAceptar.setOnClickListener(v -> anadirCarta());

        Button botonCancelar = view.findViewById(R.id.boton_cancelar_nueva_pregunta);
        botonCancelar.setOnClickListener(v -> cancelar());

        listener.nuevaPreguntaIniciado();
    }

    public void anadirCarta()
    {
        if(mazo == null)
        {
            return;
        }
        String pregunta = (String) textoPregunta.getText().toString();
        String respuesta = (String) textoRespuesta.getText().toString();
        mazo.anadirCarta(new Carta(pregunta, respuesta), this.getContext());
        listener.nuevaCartaAnadida();
    }

    public void cancelar()
    {
        listener.cancelarNuevaCarta();
    }


    // ------------------------- Funciones para concectarse con la actividad ---------------------------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (NuevaPregunta.ListenerFragmentNuevaCarta) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString() + "debe implementar ListenerFragmentMostrarMazo");
        }
    }

    public interface ListenerFragmentNuevaCarta
    {
        void cancelarNuevaCarta();
        void nuevaCartaAnadida();

        void nuevaPreguntaIniciado();
    }
}