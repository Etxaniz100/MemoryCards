package com.example.memorycards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MostrarMazo extends Fragment {


    private String nombreMazo;

    private Mazo mazo;

    private ListenerFragmentMostrarMazo listener;


    public MostrarMazo() {
        // Required empty public constructor
    }

    public static MostrarMazo newInstance(String nombreMazo) {
        MostrarMazo fragment = new MostrarMazo();
        Bundle args = new Bundle();
        args.putString("nombreMazo", nombreMazo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mostrar_mazo, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Boton
        Button boton;

        boton = (Button) view.findViewById(R.id.boton_estudiar);
        boton.setOnClickListener(v -> pulsarEstudiar(view));

        boton = (Button) view.findViewById(R.id.boton_anadir_preguntas);
        boton.setOnClickListener(v -> pulsarAnadirPregunta(view));

        boton = (Button) view.findViewById(R.id.boton_ver_preguntas);
        boton.setOnClickListener(v -> pulsarVerPreguntas(view));

        boton = (Button) view.findViewById(R.id.boton_ajustes_mazo);
        boton.setOnClickListener(v -> pulsarAjustesMazo(view));

        // Obtener mazo
        nombreMazo = requireArguments().getString("nombreMazo");
        if(nombreMazo != null)
        {
            mazo = GestorMazos.getMiGestorMazos().getMazo(nombreMazo);
        }

        listener.mostrarMazoIniciado();
    }

    // -------------------------------- Botones ------------------------------------------

    public void pulsarEstudiar(View v)
    {
        if(mazo.hayCartasParaEstudiar())
        {
            listener.pulsarEstudiar(mazo);
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getContext().getResources().getString(R.string.no_mas_cartas_tit));

            TextView aux = new TextView(getContext());

            TextView descripcion = new TextView(getContext());
            String textoDes = "  " + getContext().getResources().getString(R.string.no_mas_cartas_des);
            descripcion.setText(textoDes);

            LinearLayout layoutName = new LinearLayout(getContext());
            layoutName.setOrientation(LinearLayout.VERTICAL);

            layoutName.addView(aux);
            layoutName.addView(descripcion);

            builder.setView(layoutName);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });


            builder.show();
        }
    }

    public void pulsarAnadirPregunta(View v)
    {
        listener.pulsarAnadirPregunta(mazo);
    }


    public void pulsarVerPreguntas(View v)
    {
        listener.pulsarVerPreguntas();
    }

    public void pulsarAjustesMazo(View v)
    {
        listener.pulsarAjustesMazo();
    }


    // ------------------------- Funciones para concectarse con la actividad ---------------------------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (MostrarMazo.ListenerFragmentMostrarMazo) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString() + "debe implementar ListenerFragmentMostrarMazo");
        }
    }

    public interface ListenerFragmentMostrarMazo
    {
        void pulsarEstudiar(Mazo m);
        void pulsarAnadirPregunta(Mazo m);
        void pulsarVerPreguntas();
        void pulsarAjustesMazo();
        void mostrarMazoIniciado();
    }



}