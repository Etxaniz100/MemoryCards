package com.example.memorycards;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorListaMazos extends RecyclerView.Adapter<AdaptadorListaMazos.ViewHolderMazo>
{

    private ArrayList<Mazo> listaMazos;
    private OnItemClickListener itemClickListener;

    public  AdaptadorListaMazos(ArrayList<Mazo> mazos, OnItemClickListener listener)
    {
        listaMazos = mazos;
        itemClickListener = listener;

    }

    @NonNull
    @Override
    public ViewHolderMazo onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_card_mazo, parent, false);
        return new ViewHolderMazo(itemView, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMazo holder, int position) {
        Mazo mazo = listaMazos.get(position);
        holder.bind(mazo, itemClickListener);

        //holder.nombreMazo.setText(mazo.getNombre());
        //holder.numeroPreguntas.setText("" + mazo.getTamanoLista());
        //holder.nombre = mazo.getNombre();

    }

    @Override
    public int getItemCount() {
        return listaMazos.size();
    }

    public interface OnItemClickListener
    {
        void onItemClick(Mazo m);
        void borrarMazo(Mazo m);
    }

    public class ViewHolderMazo extends RecyclerView.ViewHolder{
        TextView nombreMazo, numeroPreguntas;
        ImageView imgBorrar;
        public String nombre;
        View view;


        public ViewHolderMazo(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            view = itemView;

            nombreMazo = itemView.findViewById(R.id.nombreMazo);
            numeroPreguntas = itemView.findViewById(R.id.numeroPreguntas);
            imgBorrar = itemView.findViewById(R.id.imagen_borrar);
        }

        public void bind(final Mazo mazo, final OnItemClickListener listener) {
            nombreMazo.setText(mazo.getNombre());
            if(mazo == null)
            {
                numeroPreguntas.setText("?/?");
            }
            else
            {
                numeroPreguntas.setText(""+ mazo.getNumeroPreguntasHoy() + "/" + mazo.getTamanoLista());
            }


            imgBorrar.setOnClickListener(new View.OnClickListener()
             {
                 @Override
                 public void onClick(View v)
                 {
                     AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                     builder.setTitle(view.getContext().getResources().getString(R.string.borrar_mazo));

                     LinearLayout layoutName = new LinearLayout(view.getContext());
                     layoutName.setOrientation(LinearLayout.VERTICAL);
                     builder.setView(layoutName);

                     builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                     {
                         @Override
                         public void onClick(DialogInterface dialog, int which)
                         {
                             dialog.dismiss();
                             listener.borrarMazo(mazo);
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

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v) {
                    listener.onItemClick(mazo);
                }
            });
        }






    }
}

