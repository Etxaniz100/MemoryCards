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

        Button boton = (Button) view.findViewById(R.id.boton_registrar_usuario);
        boton.setOnClickListener(v -> registrarUsuario(view));

        listener.registroUsuarioInicado();
    }

    private void registrarUsuario(View v)
    {
        EditText editTextUsuario = (EditText) v.findViewById(R.id.editRegistroNombre);
        EditText editTextClave = (EditText) v.findViewById(R.id.editRegistroPass);
        EditText editTextClaveRe = (EditText) v.findViewById(R.id.editRegistroPassRe);

        String usuario = editTextUsuario.getText().toString();
        String clave = editTextClave.getText().toString();
        String claveRe = editTextClaveRe.getText().toString();

        TextView tituloUsuario = (TextView) v.findViewById(R.id.tituloeditRegistroNombre);
        tituloUsuario.setText("");
        TextView tituloClave = (TextView) v.findViewById(R.id.tituloeditRegistroPass);
        tituloClave.setText("");
        TextView tituloClaveRe = (TextView) v.findViewById(R.id.tituloeditRegistroPassRe);
        tituloClaveRe.setText("");

        if(usuario.equals(""))
        {
            tituloUsuario.setText("Este campo es obligatorio");
            return;
        }
        if(clave.equals(""))
        {
            tituloClave.setText("Este campo es obligatorio");
            return;
        }
        if(claveRe.equals(""))
        {
            tituloClaveRe.setText("Este campo es obligatorio");
            return;
        }
        if(!clave.equals(claveRe))
        {
            tituloClaveRe.setText("Las contraseñas no coinciden");
            return;
        }

        Data datosEntrada = new Data.Builder()
                .putString("usuario", usuario)
                .putString("clave", clave)
                .putString("funcion", "registro")
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

                                tituloUsuario.setText("Error insperado");
                                return;
                            }
                            String tipoResultado = workInfo.getOutputData().getString("tipo");

                            if(tipoResultado != null && tipoResultado.equals("success"))
                            {
                                listener.usuarioRegistrado();
                                return;
                            }

                            if(tipoResultado != null && tipoResultado.equals("error"))
                            {
                                String mensajeResultado = workInfo.getOutputData().getString("mensaje");
                                if(mensajeResultado != null && mensajeResultado.equals("existe"))
                                {
                                    tituloUsuario.setText("Usuario existente");
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
            listener = (RegistroUsuario.ListenerRegistroUsuario) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString() + "debe implementar ListenerRegistroUsuario");
        }
    }

    public interface ListenerRegistroUsuario
    {
        void registroUsuarioInicado();
        void usuarioRegistrado();
    }
}