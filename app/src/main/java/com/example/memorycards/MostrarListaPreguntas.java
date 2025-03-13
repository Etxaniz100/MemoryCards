package com.example.memorycards;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MostrarListaPreguntas extends Fragment {

    private ListenerFragmentListaPreguntas listener;
    
    public MostrarListaPreguntas() {
        // Required empty public constructor
    }


    public static MostrarListaPreguntas newInstance(String param1, String param2) {
        MostrarListaPreguntas fragment = new MostrarListaPreguntas();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mostrar_lista_preguntas, container, false);
    }
    // ------------------------- Funciones para concectarse con la actividad ---------------------------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (MostrarListaPreguntas.ListenerFragmentListaPreguntas) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " + context.toString() + "debe implementar ListenerFragmentListaPreguntas");
        }
    }

    public interface ListenerFragmentListaPreguntas
    {
        void selecionarPregunta(Mazo m);
    }
}