package com.example.memorycards;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistroUsuario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistroUsuario extends Fragment {

    private ListenerRegistroUsuario listener;
    public RegistroUsuario() {
        // Required empty public constructor
    }

    public static RegistroUsuario newInstance(String param1, String param2) {
        RegistroUsuario fragment = new RegistroUsuario();
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
        return inflater.inflate(R.layout.fragment_registro_usuario, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);


        listener.registroUsuarioInicado();
    }

    // ------------------------- Funciones para concectarse con la actividad ---------------------------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (RegistroUsuario.ListenerRegistroUsuario) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString() + "debe implementar ListenerRegistroUsuario");
        }
    }

    public interface ListenerRegistroUsuario
    {
        void registroUsuarioInicado();
    }
}