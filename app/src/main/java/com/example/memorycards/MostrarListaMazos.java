package com.example.memorycards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class MostrarListaMazos extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Lista
    ArrayList<Mazo> listaMazos;
    AdaptadorListaMazos adaptadorListaMazos;

    // Listener
    private ListenerFragmentListaMazos listener;

    public MostrarListaMazos() {
        // Required empty public constructor
    }

    public static MostrarListaMazos newInstance(String param1, String param2) {
        MostrarListaMazos fragment = new MostrarListaMazos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        listaMazos = GestorMazos.getMiGestorMazos().getListaMazos();

        GestorMazos.inicializarTodo(this.getContext(), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mostrar_lista_mazos, container, false);

    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {

        // Obtiene el recyclerView
        RecyclerView recyclerViewMazos = (RecyclerView) view.findViewById(R.id.listaMazos);
        recyclerViewMazos.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // Añade el evento resultante al clickar un mazo
        adaptadorListaMazos = new AdaptadorListaMazos(listaMazos, new AdaptadorListaMazos.OnItemClickListener()
        {
            @Override
            public void onItemClick(Mazo m)
            {
                listener.selecionarMazo(m);
                //verMazo(m);
            }

            @Override
            public void borrarMazo(Mazo m)
            {
                GestorMazos.getMiGestorMazos().borrarMazo(m, getContext());
                adaptadorListaMazos.notifyDataSetChanged();
            }
        });

        // Pone los elementos de la lista en el recycler view
        recyclerViewMazos.setAdapter(adaptadorListaMazos);
        adaptadorListaMazos.notifyDataSetChanged();

        // Boton nuevo mazo
        Button boton = (Button) view.findViewById(R.id.boton_nuevo_mazo);
        boton.setOnClickListener(v -> pulsarNuevoMazo(view));

        listener.listaMazosIniciado();
    }

    public void pulsarNuevoMazo(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(getContext().getResources().getString(R.string.nombre_mazo));
        final EditText editTextNombreMazo = new EditText(this.getContext());

        builder.setView(editTextNombreMazo);
        LinearLayout layoutName = new LinearLayout(this.getContext());
        layoutName.setOrientation(LinearLayout.VERTICAL);
        layoutName.addView(editTextNombreMazo); // displays the user input bar
        builder.setView(layoutName);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String nombreMazo = editTextNombreMazo.getText().toString();
                //Toast.makeText(getContext(), nombreMazo, Toast.LENGTH_SHORT).show();
                GestorMazos.getMiGestorMazos().crearMazo(nombreMazo, getContext());
                adaptadorListaMazos.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }


    // ------------------------- Funciones para concectarse con la actividad ---------------------------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
             listener = (ListenerFragmentListaMazos) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString() + "debe implementar ListenerFragmentListaMazos");
        }
    }

    public interface ListenerFragmentListaMazos
    {
        void selecionarMazo(Mazo m);
        void listaMazosIniciado();
    }
}