package com.example.aplicacion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.util.Log;
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

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.MensajesViewHolder> {
    private Context context;
    private List<Mensajes> usuarioMensajes;
    private FirebaseAuth auth;
    public TextToSpeech mtts;
    private DatabaseReference UserRef;
    final private String texto = "texto";

    public MensajesAdapter(List<Mensajes> usuarioMensajes) {
        this.usuarioMensajes = usuarioMensajes;


    }

    public class MensajesViewHolder extends RecyclerView.ViewHolder {

        public TextView enviarMensajeTexto, recibirMensajeTexto;
        public CircleImageView recibirImagenPerfil;
        public ImageView mensajeImagenEnviar, mensajeImagenRecibir, mensaje_recibiraudio;


        public MensajesViewHolder(@NonNull View itemView) {

            super(itemView);

            enviarMensajeTexto = (TextView) itemView.findViewById(R.id.enviar_mensaje);
            recibirMensajeTexto = (TextView) itemView.findViewById(R.id.recibir_mensaje);
            recibirImagenPerfil = (CircleImageView) itemView.findViewById(R.id.mensaje_image_perfil);
            mensajeImagenEnviar = (ImageView) itemView.findViewById(R.id.mensaje_enviar_imagen);
            mensajeImagenRecibir = (ImageView) itemView.findViewById(R.id.mensaje_recibir_imagen);
            mensaje_recibiraudio = (ImageView) itemView.findViewById(R.id.mensaje_recibiraudio);

        }

    }

    @NonNull
    @Override
    public MensajesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuario_mensaje_layout, parent, false);
        auth = FirebaseAuth.getInstance();
        context = parent.getContext();
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
                if (snapshot.hasChild("Foto")) {
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
        holder.mensaje_recibiraudio.setVisibility(View.GONE);

        if (TipoMensaje.equals(texto)) {

            if (DeUsuarioId.equals(mensajeEnviadoID)) {

                holder.enviarMensajeTexto.setVisibility(View.VISIBLE);

                holder.enviarMensajeTexto.setBackgroundResource(R.drawable.enviar_mensaje_layout);
                holder.enviarMensajeTexto.setTextColor(Color.WHITE);
                holder.enviarMensajeTexto.setText(mensajes.getMensaje());

            } else {
                holder.recibirImagenPerfil.setVisibility(View.VISIBLE);
                holder.recibirMensajeTexto.setVisibility(View.VISIBLE);
                holder.mensaje_recibiraudio.setVisibility(View.VISIBLE);
                holder.mensaje_recibiraudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                        public void onClick(View v) {
                              String texto = mensajes.getMensaje();
                          mtts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                           @Override
                           public void onInit(int status) {
                               if (status == TextToSpeech.SUCCESS) {

                                   mtts.setLanguage(Locale.getDefault());}
                               mtts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);

                           }
                          });
                    }
                });

                holder.recibirMensajeTexto.setBackgroundResource(R.drawable.recibir_mensaje_layout);
                holder.recibirMensajeTexto.setTextColor(Color.BLACK);
                holder.recibirMensajeTexto.setText(mensajes.getMensaje());


            }

        } else if (TipoMensaje.equals("imagen")) {
            if (DeUsuarioId.equals(mensajeEnviadoID)) {
                holder.mensajeImagenEnviar.setVisibility(View.VISIBLE);
                Picasso.get().load(mensajes.getMensaje()).into(holder.mensajeImagenEnviar);
            } else {
                holder.recibirImagenPerfil.setVisibility(View.VISIBLE);
                holder.mensajeImagenRecibir.setVisibility(View.VISIBLE);
                Picasso.get().load(mensajes.getMensaje()).into(holder.mensajeImagenRecibir);

            }


        }

        if (DeUsuarioId.equals(mensajeEnviadoID)) {


        } else {



        }

    }

    private void speak(String texto) {

        mtts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
        if (!mtts.isSpeaking()) {
            mtts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = mtts.setLanguage(Locale.ENGLISH);
                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED
                        ) {
                            Log.e("TTS", "Lenguaje no soportado");

                        }
                    } else {
                        Log.e("TTS", "Iniciacion fallida");
                    }
                }
            });

            speak("HELLOW");


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
