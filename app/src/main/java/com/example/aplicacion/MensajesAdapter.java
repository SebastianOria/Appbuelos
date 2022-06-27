package com.example.aplicacion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clases.Mensajes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.MensajesViewHolder> {

    private List<Mensajes> usuarioMensajes;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    final private String  texto = "texto";
    public MensajesAdapter(List<Mensajes> usuarioMensajes){
        this.usuarioMensajes = usuarioMensajes;

    }
    public class MensajesViewHolder extends RecyclerView.ViewHolder{
        public TextView enviarMensajeTexto, recibirMensajeTexto;
        public CircleImageView recibirImagenPerfil;
        public ImageView mensajeImagenEnviar, mensajeImagenRecibir;


        public MensajesViewHolder(@NonNull View itemView){
            super(itemView);

            enviarMensajeTexto=(TextView) itemView.findViewById(R.id.enviar_mensaje);
            recibirMensajeTexto=(TextView) itemView.findViewById(R.id.recibir_mensaje);
            recibirImagenPerfil=(CircleImageView) itemView.findViewById(R.id.mensaje_image_perfil);
            mensajeImagenEnviar=(ImageView) itemView.findViewById(R.id.mensaje_enviar_imagen);
            mensajeImagenRecibir=(ImageView) itemView.findViewById(R.id.mensaje_recibir_imagen);

        }

    }

    @NonNull
    @Override
    public MensajesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuario_mensaje_layout, parent, false);
        auth = FirebaseAuth.getInstance();
        return new MensajesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajesViewHolder holder, int position) {
        String mensajeEnviadoID = auth.getCurrentUser().getUid();
        Mensajes mensajes = usuarioMensajes.get(position);
        String DeUsuarioId = mensajes.getDe();
        String TipoMensaje = mensajes.getTipo();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(DeUsuarioId);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Foto")){
                    String ImageRecibido = snapshot.child("Foto").getValue().toString();
                    Picasso.get().load(ImageRecibido).placeholder(R.drawable.foto).into(holder.recibirImagenPerfil);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.enviarMensajeTexto.setVisibility(View.GONE);
        holder.recibirMensajeTexto.setVisibility(View.GONE);
        holder.recibirImagenPerfil.setVisibility(View.GONE);
        holder.mensajeImagenEnviar.setVisibility(View.GONE);
        holder.mensajeImagenRecibir.setVisibility(View.GONE);

        if (TipoMensaje.equals(texto)) {

                    if (DeUsuarioId.equals(mensajeEnviadoID)) {

                        holder.enviarMensajeTexto.setVisibility(View.VISIBLE);

                        holder.enviarMensajeTexto.setBackgroundResource(R.drawable.enviar_mensaje_layout);
                        holder.enviarMensajeTexto.setTextColor(Color.WHITE);
                        holder.enviarMensajeTexto.setText(mensajes.getMensaje());

                    } else {
                        holder.recibirImagenPerfil.setVisibility(View.VISIBLE);
                        holder.recibirMensajeTexto.setVisibility(View.VISIBLE);

                        holder.recibirMensajeTexto.setBackgroundResource(R.drawable.recibir_mensaje_layout);
                        holder.recibirMensajeTexto.setTextColor(Color.BLACK);
                        holder.recibirMensajeTexto.setText(mensajes.getMensaje());


                    }

                }else if(TipoMensaje.equals("imagen")) {
            if (DeUsuarioId.equals(mensajeEnviadoID)) {
                holder.mensajeImagenEnviar.setVisibility(View.VISIBLE);
                Picasso.get().load(mensajes.getMensaje()).into(holder.mensajeImagenEnviar);
            } else {
                holder.recibirImagenPerfil.setVisibility(View.VISIBLE);
                holder.mensajeImagenRecibir.setVisibility(View.VISIBLE);
                Picasso.get().load(mensajes.getMensaje()).into(holder.mensajeImagenRecibir);

            }


        }

        if(DeUsuarioId.equals(mensajeEnviadoID)){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(usuarioMensajes.get(position).getTipo().equals("texto")){
                        CharSequence opciones[] = new CharSequence[]{
                            "Elminar Mensaje",

                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    EliminarMensajesEnviados(position, holder);
                                    Intent intent = new Intent(holder.itemView.getContext(), MensajesAdapter.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();

                    }else if(usuarioMensajes.get(position).getTipo().equals("imagen")){
                        CharSequence opciones[] = new CharSequence[]{
                                "Elminar Mensjae",

                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    EliminarMensajesEnviados(position, holder);
                                    Intent intent = new Intent(holder.itemView.getContext(), MensajesAdapter.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }
                            }
                        });
                        builder.show();

                    }
                }
            });

        }else{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(usuarioMensajes.get(position).getTipo().equals("texto")){
                        CharSequence opciones[] = new CharSequence[]{
                                "Elminar Mensaje",

                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){

                                    EliminarMensajesEnviados(position, holder);
                                    Intent intent = new Intent(holder.itemView.getContext(), MensajesAdapter.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();

                    }else if(usuarioMensajes.get(position).getTipo().equals("imagen")){
                        CharSequence opciones[] = new CharSequence[]{
                                "Elminar Mensaje",

                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    EliminarMensajesEnviados(position, holder);
                                    Intent intent = new Intent(holder.itemView.getContext(), MensajesAdapter.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();

                    }
                }
            });



        }

    }

    @Override
    public int getItemCount() {
        return usuarioMensajes.size();
    }

    private void EliminarMensajesEnviados(final int position, final MensajesViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Mensajes").child(usuarioMensajes.get(position).getDe())
                .child(usuarioMensajes.get(position).getMensaje())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }
                    }
                });

    }



}
