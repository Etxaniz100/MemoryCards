package com.example.memorycards;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class Mapa extends Fragment {


    private ListenerFragmentMapa listener;
    private MapView mapa;
    private FusedLocationProviderClient locationClient;

    public Mapa() {
        // Required empty public constructor
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

        GeoPoint startPoint = new GeoPoint(42.8467, -2.6731);
        mapa.getController().setZoom(12.0);
        mapa.getController().setCenter(startPoint);

        Marker startMarker = new Marker(mapa);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(getResources().getDrawable(R.drawable.flag_24px));
        startMarker.setTitle("Huevo");

        startMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                // Acción al hacer clic en el marcador
                Toast.makeText(getContext(), "Marcador clicado: " + marker.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mapa.getOverlays().add(startMarker);


        obtenerPersmisos();


        // Inicializar cliente de ubicación
        locationClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Obtener y mostrar ubicación
        obtenerUbicacionActual();
        listener.mapaIniciado();
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
            return;
        }

        locationClient.getLastLocation()

                        .addOnSuccessListener(getActivity(), location ->
                        {
                            if (location != null)
                            {
                                double lat = location.getLatitude();
                                double lon = location.getLongitude();

                                GeoPoint miPosicion = new GeoPoint(lat, lon);
                                mapa.getController().setZoom(16.0);
                                mapa.getController().setCenter(miPosicion);

                                // Agregar marcador en tu ubicación
                                Marker marker = new Marker(mapa);
                                marker.setPosition(miPosicion);
                                marker.setIcon(getResources().getDrawable(R.drawable.location_on_24px));
                                //myMarker.setTitle("Estás aquí");
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                mapa.getOverlays().add(marker);
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Ubicación desconocida", Toast.LENGTH_SHORT).show();
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
    }
}