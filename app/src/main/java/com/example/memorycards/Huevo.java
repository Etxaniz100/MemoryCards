package com.example.memorycards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.system.StructTimespec;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.nio.BufferUnderflowException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Huevo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Huevo extends Fragment {

    private ListenerFragmentHuevo listener;
    private GestorHuevo huevo;

    public Huevo() {
        // Required empty public constructor
    }

    public static Huevo newInstance(String param1, String param2) {
        Huevo fragment = new Huevo();
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
        return inflater.inflate(R.layout.fragment_huevo, container, false);
    }


    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Obtener mazo
        huevo = GestorMazos.getMiGestorMazos().getHuevo();


        ImageView imagenHuevo = (ImageView) view.findViewById(R.id.imagen_huevo);
        setImagenHuevo(imagenHuevo);

        ImageView imagenEstadoHuevo = (ImageView) view.findViewById(R.id.imagen_estado);
        setEstado(imagenEstadoHuevo);

        ProgressBar barraProgreso = (ProgressBar) view.findViewById(R.id.progreso_huevo);
        barraProgreso.setProgress((int)huevo.getProgreso());

        // ------------------------- NOMBRE --------------------------------
        TextView textoNombre = (TextView) view.findViewById(R.id.text_nombre_huevo);
        textoNombre.setText(huevo.getNombre());

        textoNombre.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getContext().getResources().getString(R.string.nombre_huevo));
                final EditText editTextNombreHuevo = new EditText(getContext());

                builder.setView(editTextNombreHuevo);
                LinearLayout layoutName = new LinearLayout(getContext());
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editTextNombreHuevo);
                builder.setView(layoutName);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String nombreHuevo = editTextNombreHuevo.getText().toString();
                        if(!nombreHuevo.isEmpty() && !nombreHuevo.isBlank())
                        {
                            huevo.setNombre(nombreHuevo, getContext());
                            textoNombre.setText(huevo.getNombre());
                        }
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
        });


        // ------------------------- ABRIR HUEVO ------------------------------------

        Button botonAbrir = view.findViewById(R.id.button_abrir);

        if(huevo != null && huevo.getProgreso() >= 100)
        {
            botonAbrir.setVisibility(View.VISIBLE);
            botonAbrir.setOnClickListener(v -> abrirHuevo());
        }
        else if(huevo != null && huevo.getEstadoFelicidad().equals("caducado"))
        {
            botonAbrir.setVisibility(View.VISIBLE);
            botonAbrir.setText(getContext().getResources().getString(R.string.nuevo_huevo));
            botonAbrir.setOnClickListener(v -> nuevoHuevo());
        }
        else
        {
            botonAbrir.setVisibility(View.INVISIBLE);
        }




        listener.huevoIniciado();

    }

    // ------------------------- Huevo ------------------------

    private void setEstado(ImageView imagen)
    {
        String estado = "";
        if(huevo != null)
        {
            estado = huevo.getEstadoFelicidad();
        }

        switch (estado)
        {
            case "caducado":
                imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sentiment_very_dissatisfied_24px, this.getContext().getTheme()));
                break;

            case "m_triste":
                imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sentiment_sad_24px, this.getContext().getTheme()));
                break;

            case "triste":
                imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sentiment_dissatisfied_24px, this.getContext().getTheme()));
                break;

            case "feliz":
                imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sentiment_satisfied_24px, this.getContext().getTheme()));
                break;

            case "m_feliz":
                imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sentiment_very_satisfied_24px, this.getContext().getTheme()));
                break;

            case "neutral":
            default:
                imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sentiment_neutral_24px, this.getContext().getTheme()));
                break;

        }

        imagen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String estado = "";
                if(huevo != null)
                {
                    estado = huevo.getEstadoFelicidad();
                }

                String titulo = "";

                switch (estado)
                {
                    case "caducado":
                        titulo = getContext().getResources().getString(R.string.caducado);
                        break;

                    case "m_triste":
                        titulo = getContext().getResources().getString(R.string.muy_triste);
                        break;

                    case "triste":
                        titulo = getContext().getResources().getString(R.string.triste);
                        break;

                    case "feliz":
                        titulo = getContext().getResources().getString(R.string.feliz);
                        break;

                    case "m_feliz":
                        titulo = getContext().getResources().getString(R.string.muy_feliz);
                        break;

                    case "neutral":
                    default:
                        titulo = getContext().getResources().getString(R.string.neutral);
                        break;

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(titulo);

                TextView aux = new TextView(getContext());


                TextView felicidad = new TextView(getContext());
                String textoFelicidad = "  " + getContext().getResources().getString(R.string.felicidad);
                textoFelicidad += " : "+huevo.getFelicidad() + "%";
                felicidad.setText(textoFelicidad);

                LinearLayout layoutName = new LinearLayout(getContext());
                layoutName.setOrientation(LinearLayout.VERTICAL);

                layoutName.addView(aux);
                layoutName.addView(felicidad);

                builder.setView(layoutName);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });


                builder.show();
                listener.huevoAbierto();
            }
        });
    }


    private void setImagenHuevo(ImageView imagen)
    {
        float progreso = 0;
        if(huevo != null)
        {
            progreso = huevo.getProgreso();
        }

        String color = "";
        color = huevo.getColor();

        switch (color)
        {
            case "rojo":
                if(progreso < 25)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_rojo_0, this.getContext().getTheme()));
                }
                else if(progreso < 50)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_rojo_1, this.getContext().getTheme()));
                }
                else if(progreso < 75)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_rojo_2, this.getContext().getTheme()));
                }
                else if(progreso < 100)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_rojo_3, this.getContext().getTheme()));
                }
                else
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_rojo_4, this.getContext().getTheme()));
                }
                break;

            case "verde":
                if(progreso < 25)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_verde_0, this.getContext().getTheme()));
                }
                else if(progreso < 50)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_verde_1, this.getContext().getTheme()));
                }
                else if(progreso < 75)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_verde_2, this.getContext().getTheme()));
                }
                else if(progreso < 100)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_verde_3, this.getContext().getTheme()));
                }
                else
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_verde_4, this.getContext().getTheme()));
                }
                break;

            case "gris":
            default:
                if(progreso < 25)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_gris_0, this.getContext().getTheme()));
                }
                else if(progreso < 50)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_gris_1, this.getContext().getTheme()));
                }
                else if(progreso < 75)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_gris_2, this.getContext().getTheme()));
                }
                else if(progreso < 100)
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_gris_3, this.getContext().getTheme()));
                }
                else
                {
                    imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.huevo_gris_4, this.getContext().getTheme()));
                }
                break;
        }
    }

    private void abrirHuevo()
    {
        String nombreH = huevo.getNombre();
        GestorMazos.getMiGestorMazos().borrarHuevo(this.getContext(), huevo);

        huevo =  GestorMazos.getMiGestorMazos().nuevoHuevo(this.getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String titulo = getContext().getResources().getString(R.string.eclosionar);
        titulo = titulo.replace("nombre", nombreH);
        builder.setTitle(titulo);

        LinearLayout layoutName = new LinearLayout(getContext());
        layoutName.setOrientation(LinearLayout.VERTICAL);
        builder.setView(layoutName);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                listener.huevoAbierto();
            }
        });


        builder.show();
        listener.huevoAbierto();
    }

    private void nuevoHuevo()
    {
        GestorMazos.getMiGestorMazos().borrarHuevo(this.getContext(), huevo);

        huevo =  GestorMazos.getMiGestorMazos().nuevoHuevo(this.getContext());

        listener.huevoCaducado();

    }

    // ------------------------- Funciones para concectarse con la actividad ---------------------------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (Huevo.ListenerFragmentHuevo) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString() + "debe implementar ListenerFragmentMostrarMazo");
        }
    }

    public interface ListenerFragmentHuevo
    {
        void huevoIniciado();

        void huevoAbierto();

        void huevoCaducado();
    }
}