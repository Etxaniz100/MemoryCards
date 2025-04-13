package com.example.memorycards;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InicioSesion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioSesion extends Fragment {

    private ListenerInicioSesion listener;
    public InicioSesion() {
        // Required empty public constructor
    }

    public static InicioSesion newInstance(String param1, String param2) {
        InicioSesion fragment = new InicioSesion();
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
        return inflater.inflate(R.layout.fragment_inicio_sesion, container, false);
    }


    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Button boton = (Button) view.findViewById(R.id.boton_iniciar_sesion);
        boton.setOnClickListener(v -> iniciarSesion(view));

        boton = (Button) view.findViewById(R.id.boton_ir_registro);
        boton.setOnClickListener(v -> listener.abrirRegistro());




        listener.inicioSesionIniciado();
    }

    private void iniciarSesion(View v)
    {
        EditText editTextUsuario = (EditText) v.findViewById(R.id.editInicioSesionNombre);
        EditText editTextClave = (EditText) v.findViewById(R.id.editInicioSesionPass);

        String usuario = editTextUsuario.getText().toString();
        String clave = editTextClave.getText().toString();

        TextView tituloUsuario = (TextView) v.findViewById(R.id.tituloeditInicioSesionNombre);
        tituloUsuario.setText("");
        TextView tituloClave = (TextView) v.findViewById(R.id.tituloeditInicioSesionPass);
        tituloClave.setText("");
        if(usuario.equals(""))
        {
            return;
        }
        if(clave.equals(""))
        {
            return;
        }

        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("clave", clave)
                .putString("funcion", "inicio")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datosEntrada).build();

        WorkManager.getInstance(getContext()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished())
                        {
                            if(workInfo.getOutputData() == null)
                            {

                                TextView textViewResult = v.findViewById(R.id.tituloeditInicioSesionNombre);
                                textViewResult.setText("Error insperado");
                                return;
                            }
                            String tipoResultado = workInfo.getOutputData().getString("tipo");

                            if(tipoResultado != null && tipoResultado.equals("success"))
                            {
                                listener.sesionIniciada(usuario);
                                return;
                            }

                            if(tipoResultado != null && tipoResultado.equals("error"))
                            {
                                String mensajeResultado = workInfo.getOutputData().getString("mensaje");
                                if(mensajeResultado != null && mensajeResultado.equals("nadie"))
                                {
                                    TextView textViewResult = v.findViewById(R.id.tituloeditInicioSesionPass);
                                    textViewResult.setText("Usuario no encontrado");
                                } else if(mensajeResultado != null && mensajeResultado.equals("incorrecto"))
                                {
                                    TextView textViewResult = v.findViewById(R.id.tituloeditInicioSesionPass);
                                    textViewResult.setText("Contraseña incorrecta");
                                }
                            }

                        }
                    }
                });
        WorkManager.getInstance(getContext()).enqueue(otwr);


    }

    // ------------------------- Funciones para concectarse con la actividad ---------------------------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (InicioSesion.ListenerInicioSesion) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString() + "debe implementar ListenerInicioSesion");
        }
    }

    public interface ListenerInicioSesion
    {
        void inicioSesionIniciado();
        void sesionIniciada(String usuario);
        void abrirRegistro();
    }

}