package com.example.memorycards;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CancellationSignal;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


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

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 31);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 32);
        }

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
                            huevo.setNombre(nombreHuevo, getContext(), getActivity());
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

        // ------------------------- MAPA --------------------------------
        ImageView iconoMapa = (ImageView) view.findViewById(R.id.imagen_ir_mapa);

        iconoMapa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.irMapa();
            }
        });

        // ------------------------- COMIDA --------------------------------
        ImageView iconoGalleta = (ImageView) view.findViewById(R.id.imagen_dar_comida);
        TextView cantidaComida = (TextView) view.findViewById(R.id.texto_cantidad_comida);
        cantidaComida.setText(""+GestorMazos.getCantidadComida());
        iconoGalleta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                darComida(view);
            }
        });


        // ------------------------- ABRIR HUEVO ------------------------------------

        Button botonAbrir = view.findViewById(R.id.button_abrir);

        if(huevo != null && huevo.getProgreso() >= 100)
        {
            botonAbrir.setVisibility(View.VISIBLE);
            botonAbrir.setOnClickListener(v ->
                                            {
                                                abrirHuevo();
                                            });
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

        // ----------------------------- Poner alarma --------------------------------------------
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED)
        {
            Date fechaAlarma = huevo.calcularFechaTriste();
            huevo.actualizarAlarma(fechaAlarma, getContext());
        }



        listener.huevoIniciado();

    }



    // ------------------------ COmida ----------

    private void darComida(View v)
    {
        TextView cantidaComida = (TextView) v.findViewById(R.id.texto_cantidad_comida);
        int comida = GestorMazos.getCantidadComida();
        if(comida <= 0 || huevo == null || huevo.getProgreso() >= 100 || huevo.getEstadoFelicidad().equals("caducado"))
        {
            return;
        }

        GestorMazos.setCantidadComida(comida-1, getContext(), getActivity());
        cantidaComida.setText(""+GestorMazos.getCantidadComida());

        huevo.alimentar(getContext(), getActivity());

        ImageView imagenHuevo = (ImageView) v.findViewById(R.id.imagen_huevo);
        setImagenHuevo(imagenHuevo);

        ImageView imagenEstadoHuevo = (ImageView) v.findViewById(R.id.imagen_estado);
        setEstado(imagenEstadoHuevo);

        ProgressBar barraProgreso = (ProgressBar) v.findViewById(R.id.progreso_huevo);
        barraProgreso.setProgress((int)huevo.getProgreso());

        Button botonAbrir = v.findViewById(R.id.button_abrir);

        if(huevo != null && huevo.getProgreso() >= 100)
        {
            botonAbrir.setVisibility(View.VISIBLE);
            botonAbrir.setOnClickListener(va -> abrirHuevo());
        }
        else if(huevo != null && huevo.getEstadoFelicidad().equals("caducado"))
        {
            botonAbrir.setVisibility(View.VISIBLE);
            botonAbrir.setText(getContext().getResources().getString(R.string.nuevo_huevo));
            botonAbrir.setOnClickListener(va -> nuevoHuevo());
        }
        else
        {
            botonAbrir.setVisibility(View.INVISIBLE);
        }

        ImageView iconoGalleta = (ImageView) v.findViewById(R.id.imagen_dar_comida);
        iconoGalleta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
        iconoGalleta.setAlpha(0.5f);


        iconoGalleta.postDelayed(() ->
                {
                    iconoGalleta.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            darComida(v);
                        }
                    }) ;
                    iconoGalleta.setAlpha(1f);
                }, 1000);
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
        //anadirEventoHuevoAbierto();

        String nombreH = huevo.getNombre();
        //GestorMazos.getMiGestorMazos().borrarHuevo(this.getContext(), huevo);

        huevo =  GestorMazos.getMiGestorMazos().nuevoHuevo(this.getContext(), getActivity());

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
        //GestorMazos.getMiGestorMazos().borrarHuevo(this.getContext(), huevo);

        huevo =  GestorMazos.getMiGestorMazos().nuevoHuevo(this.getContext(), getActivity());

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
        void irMapa();
    }
}