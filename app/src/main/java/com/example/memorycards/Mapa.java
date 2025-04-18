package com.example.memorycards;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Random;

public class Mapa extends Fragment {


    private ListenerFragmentMapa listener;
    private MapView mapa;
    private FusedLocationProviderClient locationClient;
    private Marker marcadorPosicionUsuario;
    private double rangoCercaria = 400;
    private double distanciaCercania = 20;
    private boolean inicializado = false;
    private TextView contador;

    public Mapa()
    {
    }


    public static Mapa newInstance(String param1, String param2) {
        Mapa fragment = new Mapa();
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
                             Bundle savedInstanceState)
    {
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }


    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);


        mapa = view.findViewById(R.id.mapa);

        mapa.setTileSource(TileSourceFactory.MAPNIK);
        mapa.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapa.setMultiTouchControls(true);

        obtenerPersmisos();

        // Inicializar cliente de ubicación
        locationClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Obtener y mostrar ubicación
        obtenerUbicacionActual();

        contador = (TextView) view.findViewById(R.id.texto_cantidad_huevos);
        actualizarContador();

        listener.mapaIniciado();
    }

    private void actualizarContador()
    {
        contador.setText(""+GestorMazos.getCantidadComida());
    }

    private void obtenerPersmisos()
    {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 30);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 30);
        }
    }


    private void obtenerUbicacionActual()
    {
        if (    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            listener.salirDelMapa();
            return;
        }

        locationClient  .getLastLocation()
                        .addOnSuccessListener(getActivity(), location ->
                        {
                            GeoPoint miPosicion = null;
                            if (location != null)
                            {
                                double lat = location.getLatitude();
                                double lon = location.getLongitude();

                                miPosicion = new GeoPoint(lat, lon);


                                if(marcadorPosicionUsuario != null)
                                {
                                    mapa.getOverlays().remove(marcadorPosicionUsuario);
                                }

                                // Agregar marcador en tu ubicación
                                marcadorPosicionUsuario = new Marker(mapa);
                                marcadorPosicionUsuario.setPosition(miPosicion);

                                Drawable icono = getResources().getDrawable(R.drawable.location_on_24px);
                                icono = DrawableCompat.wrap(icono);
                                DrawableCompat.setTint(icono, Color.RED);
                                marcadorPosicionUsuario.setIcon(icono);

                                //myMarker.setTitle("Estás aquí");
                                marcadorPosicionUsuario.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                mapa.getOverlays().add(marcadorPosicionUsuario);
                            }
                            else
                            {
                                mostrarMensaje( getContext().getResources().getString(R.string.titulo_aviso_sin_ubicacion),
                                                getContext().getResources().getString(R.string.aviso_sin_ubicacion),
                                                () -> {
                                                        listener.salirDelMapa();
                                                });
                            }

                            if(!inicializado && miPosicion != null)
                            {
                                inicializado = true;
                                mapa.getController().setZoom(16.0);
                                mapa.getController().setCenter(miPosicion);
                                colocarComidas();
                            }

                        });
    }

    private void mostrarMensaje(String titulo, String mensaje, Runnable funcionOk)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(titulo);

        TextView aux = new TextView(getContext());

        TextView textView = new TextView(getContext());
        String textoMensaje = "  " + mensaje;
        textView.setText(textoMensaje);

        LinearLayout layoutName = new LinearLayout(getContext());
        layoutName.setOrientation(LinearLayout.VERTICAL);

        layoutName.addView(aux);
        layoutName.addView(textView);

        builder.setView(layoutName);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                if(funcionOk != null)
                {
                    funcionOk.run();
                }
            }
        });

        builder.show();
    }
    private void colocarComidas()
    {
        GestorMazos gestorMazos = GestorMazos.getMiGestorMazos();
        ArrayList<UbicacionComida> listaPosicionesComidas = gestorMazos.getListaPosicionesComidas();
        int huevosEnRango = 0;
        for (UbicacionComida u: listaPosicionesComidas)
        {
            if(!u.recogido)
            {
                GeoPoint posicionHuevo = new GeoPoint(u.latitud, u.longitud);
                colocarComida(posicionHuevo, u);

                // Comprobar distancia a usuario
                if(marcadorPosicionUsuario != null)
                {
                    double distancia = posicionHuevo.distanceToAsDouble(marcadorPosicionUsuario.getPosition());
                    if (distancia < rangoCercaria)
                    {
                        huevosEnRango += 1;
                    }
                }
            }
        }

        if(huevosEnRango <= 2 && marcadorPosicionUsuario != null)
        {
            for(int i = 0; i < 3-huevosEnRango; i++)
            {
                double latitud = marcadorPosicionUsuario.getPosition().getLatitude() + (new Random().nextFloat()-0.5)/200;
                double longitud = marcadorPosicionUsuario.getPosition().getLongitude() + (new Random().nextFloat()-0.5)/200;
                GeoPoint posicionNuevoHuevo = new GeoPoint(latitud, longitud);

                // Comprobar distancia a usuario
                double distancia = posicionNuevoHuevo.distanceToAsDouble(marcadorPosicionUsuario.getPosition());
                UbicacionComida ubicacionComida = new UbicacionComida(longitud, latitud, false);
                gestorMazos.anadirPosicion(getContext(), ubicacionComida, getActivity());
                colocarComida(posicionNuevoHuevo, ubicacionComida);

            }
        }
    }

    private void colocarComida(GeoPoint posicionComida, UbicacionComida u)
    {
        Marker marker = new Marker(mapa);
        marker.setPosition(posicionComida);

        Drawable icono = null;

        Drawable[] iconos = {   getResources().getDrawable(R.drawable.bakery_dining_24px),
                                getResources().getDrawable(R.drawable.cake_24px),
                                getResources().getDrawable(R.drawable.icecream_24px),
                getResources().getDrawable(R.drawable.local_pizza_24px),
                getResources().getDrawable(R.drawable.lunch_dining_24px),
                getResources().getDrawable(R.drawable.nutrition_24px)
        };

        icono = iconos[new Random().nextInt(iconos.length)];

        icono = DrawableCompat.wrap(icono);
        DrawableCompat.setTint(icono, Color.BLUE);
        marker.setIcon(icono);

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapa.getOverlays().add(marker);

        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                if (    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }

                locationClient  .getLastLocation()
                        .addOnSuccessListener(getActivity(), location ->
                        {
                            GeoPoint miPosicion = null;
                            if (location != null)
                            {
                                double lat = location.getLatitude();
                                double lon = location.getLongitude();

                                miPosicion = new GeoPoint(lat, lon);


                                if(marcadorPosicionUsuario != null)
                                {
                                    mapa.getOverlays().remove(marcadorPosicionUsuario);
                                }

                                // Agregar marcador en tu ubicación
                                marcadorPosicionUsuario = new Marker(mapa);
                                marcadorPosicionUsuario.setPosition(miPosicion);

                                Drawable icono = getResources().getDrawable(R.drawable.location_on_24px);
                                icono = DrawableCompat.wrap(icono);
                                DrawableCompat.setTint(icono, Color.RED);
                                marcadorPosicionUsuario.setIcon(icono);

                                //myMarker.setTitle("Estás aquí");
                                marcadorPosicionUsuario.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                mapa.getOverlays().add(marcadorPosicionUsuario);

                                double distanciaHastaComida = posicionComida.distanceToAsDouble(miPosicion);
                                if(distanciaHastaComida < distanciaCercania)
                                {
                                    GestorMazos.setCantidadComida(GestorMazos.getCantidadComida()+1, getContext(), getActivity());
                                    mapa.getOverlays().remove(marker);
                                    actualizarContador();
                                    GestorMazos.getMiGestorMazos().quitarPosicion(getContext(), u, getActivity());
                                }
                                else
                                {
                                    mostrarMensaje(getContext().getResources().getString(R.string.titulo_aviso_mapa), getContext().getResources().getString(R.string.aviso_distancia)+" : "+ (int)distanciaHastaComida + "m", null);
                                }
                            }

                        });
                return true;
            }
        });
    }




    // ------------------------- Funciones para concectarse con la actividad ---------------------------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (Mapa.ListenerFragmentMapa) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString() + "debe implementar ListenerFragmentMapa");
        }
    }

    public interface ListenerFragmentMapa
    {
        void mapaIniciado();
        void salirDelMapa();
    }
}