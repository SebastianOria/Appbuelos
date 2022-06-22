package com.example.aplicacion;

import android.content.Context;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clases.Mensajes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;




public class ChatActivity extends AppCompatActivity {
        private String RecibirUserID,nombre, imagen;
        private TextView nombreusuario;
        private CircleImageView usuarioimagen;
        private Toolbar toolbar;
        private EditText mensaje;
        private ImageView botonenviar;
        private DatabaseReference RootRef;
        private FirebaseAuth auth;
        private String EnviarUserID;

        private final List<Mensajes> mensajesList = new ArrayList<>();
        private LinearLayoutManager linearLayoutManager;
        private MensajesAdapter mensajesAdapter;
        private RecyclerView UsuariosrecRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        auth = FirebaseAuth.getInstance();
        EnviarUserID  = auth.getCurrentUser().getUid();
        RecibirUserID = getIntent().getExtras().get("user_id").toString();
        nombre = getIntent().getExtras().get("user_nombre").toString();
        imagen = getIntent().getExtras().get("user_imagen").toString();
        auth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        IniciarelLayout();

        nombreusuario.setText(nombre);

        Picasso.get()
                .load(imagen)
                .placeholder(R.drawable.foto)
                .into(usuarioimagen);

        mensajesAdapter = new MensajesAdapter(mensajesList);
        UsuariosrecRecyclerView = (RecyclerView) findViewById(R.id.listamensajesrecicler);
        linearLayoutManager = new LinearLayoutManager(this);
        UsuariosrecRecyclerView.setLayoutManager(linearLayoutManager);
        UsuariosrecRecyclerView.setAdapter(mensajesAdapter);

    }

    @Override
    protected void onStart(){
      super.onStart();
        RootRef.child("Mensajes").child(EnviarUserID).child(RecibirUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    Mensajes mensajes = snapshot.getValue(Mensajes.class);
                    mensajesList.add(mensajes);
                    mensajesAdapter.notifyDataSetChanged();




            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void IniciarelLayout() {
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_chat_bar, null);
        actionBar.setCustomView(view);
        nombreusuario=(TextView) findViewById(R.id.usuario_nombre);
        usuarioimagen=(CircleImageView) findViewById(R.id.usuario_imagen);
        mensaje = (EditText) findViewById(R.id.mensaje);
        botonenviar = (ImageView) findViewById(R.id.enviar_mensaje_boton);
        botonenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarMensaje();
            }
        });



    }

    private void EnviarMensaje() {
        String mensajeTexto = mensaje.getText().toString();
        if(TextUtils.isEmpty(mensajeTexto)){

        }else{
            String mensajeEnviadoRef = "Mensajes/" + EnviarUserID + "/" + RecibirUserID;
            String mensajeRecibidoRef = "Mensajes/" + RecibirUserID + "/" + EnviarUserID;

            DatabaseReference usuarioMensajeRef = RootRef.child("Mensajes").child(EnviarUserID).child(RecibirUserID).push();

            String mensajePushID = usuarioMensajeRef.getKey();
            Map mensajetxt = new HashMap();

            mensajetxt.put("mensaje", mensajeTexto);
            mensajetxt.put("tipo", "texto");
            mensajetxt.put("de", EnviarUserID);

            Map mensajeTxtfull = new HashMap();
            mensajeTxtfull.put(mensajeEnviadoRef + "/" + mensajePushID, mensajetxt);
            mensajeTxtfull.put(mensajeRecibidoRef + "/" + mensajePushID, mensajetxt);

            RootRef.updateChildren(mensajeTxtfull).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    mensaje.setText("");
                }
            });
        }



    }
}
