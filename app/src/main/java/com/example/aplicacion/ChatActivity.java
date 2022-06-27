package com.example.aplicacion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
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
        private ProgressDialog dialog;
        private ImageView botonenviar, botonarchivo;
        private DatabaseReference RootRef;
        private FirebaseAuth auth;
        private String EnviarUserID;
        private String myUrl="";
        private StorageTask uploadTask;
        private Uri fileUri;
        private String check;

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
        dialog = new ProgressDialog(this);
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

        RootRef.child("Mensajes").child(EnviarUserID).child(RecibirUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Mensajes mensajes = snapshot.getValue(Mensajes.class);
                mensajesList.add(mensajes);
                mensajesAdapter.notifyDataSetChanged();

                UsuariosrecRecyclerView.smoothScrollToPosition(UsuariosrecRecyclerView.getAdapter().getItemCount());


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

    @Override
    protected void onStart(){
      super.onStart();

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
        botonarchivo = (ImageView) findViewById(R.id.enviar_archivos_boton);
        botonarchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Enviando Imagen");
                dialog.setMessage("Estamos enviando tu imagen");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();


                check ="imagen";
                Intent intent  = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 349);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 349 && resultCode==RESULT_OK && data!=null && data.getData() != null ){

            fileUri = data.getData();
            if(!check.equals("imagen")){


            }else if(check.equals("imagen")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Archivos");

                String mensajeEnviadoRef = "Mensajes/" + EnviarUserID + "/" + RecibirUserID;
                String mensajeRecibidoRef = "Mensajes/" + RecibirUserID + "/" + EnviarUserID;

                DatabaseReference usuarioMensajeRef = RootRef.child("Mensajes").child(EnviarUserID).child(RecibirUserID).push();

                String mensajePushID = usuarioMensajeRef.getKey();

                StorageReference filepath = storageReference.child(mensajePushID+"."+"jpg");

                uploadTask = filepath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();

                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUri = task.getResult();
                            myUrl = downloadUri.toString();

                            Map mensajetxt = new HashMap();

                            mensajetxt.put("mensaje", myUrl);
                            mensajetxt.put("tipo", check);
                            mensajetxt.put("de", EnviarUserID);
                            mensajetxt.put("para", RecibirUserID);

                            Map mensajeTxtfull = new HashMap();
                            mensajeTxtfull.put(mensajeEnviadoRef + "/" + mensajePushID, mensajetxt);
                            mensajeTxtfull.put(mensajeRecibidoRef + "/" + mensajePushID, mensajetxt);

                            RootRef.updateChildren(mensajeTxtfull).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        dialog.dismiss();

                                    }else{
                                        dialog.dismiss();

                                    }

                                    mensaje.setText("");
                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });


            }

        }

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
