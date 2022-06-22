package com.example.aplicacion;

import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clases.Mensajes;
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

        public MensajesViewHolder(@NonNull View itemView){
            super(itemView);

            enviarMensajeTexto=(TextView) itemView.findViewById(R.id.enviar_mensaje);
            recibirMensajeTexto=(TextView) itemView.findViewById(R.id.recibir_mensaje);
            recibirImagenPerfil=(CircleImageView) itemView.findViewById(R.id.mensaje_image_perfil);

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



        if (TipoMensaje.equals(texto)) {

                    holder.recibirMensajeTexto.setVisibility(View.GONE);
                    holder.recibirImagenPerfil.setVisibility(View.GONE);

                    if (DeUsuarioId.equals(mensajeEnviadoID)) {
                        holder.enviarMensajeTexto.setBackgroundResource(R.drawable.enviar_mensaje_layout);
                        holder.enviarMensajeTexto.setTextColor(Color.WHITE);
                        holder.enviarMensajeTexto.setText(mensajes.getMensaje());

                    } else {
                        holder.enviarMensajeTexto.setVisibility(View.GONE);
                        holder.recibirImagenPerfil.setVisibility(View.VISIBLE);
                        holder.recibirMensajeTexto.setVisibility(View.VISIBLE);

                        holder.recibirMensajeTexto.setBackgroundResource(R.drawable.recibir_mensaje_layout);
                        holder.recibirMensajeTexto.setTextColor(Color.BLACK);
                        holder.recibirMensajeTexto.setText(mensajes.getMensaje());


                    }

                }

    }

    @Override
    public int getItemCount() {
        return usuarioMensajes.size();
    }



}
