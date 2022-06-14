package com.example.aplicacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AjustesPerfilActivity extends AppCompatActivity {

    private EditText nombre;
    private Button actualizar;
    private String rut, telefono;
    private CircleImageView imageperfil;
    private Toolbar toolbar;
    private String  CurrentUserID;
    private FirebaseAuth auth;
    private DatabaseReference RootRef;
    final static int Gallery_PICK = 1;
    private ProgressDialog dialog;
    private static final int COD_SEL_IMAGE= 300;
    private Uri image_url;
    StorageReference  storageReference;
    String storagepath = "imgperfil/*";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustar_perfil);

        Componentes();
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarInformacion();
            }
        });
        RootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        CurrentUserID= auth.getCurrentUser().getUid();
        RootRef.child("Usuarios").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nom1 = snapshot.child("nombre").getValue().toString();
                    String imagen1 = snapshot.child("Foto").getValue().toString();
                    String rut1 = snapshot.child("Rut").getValue().toString();
                    String telefono1 = snapshot.child("Telefono").getValue().toString();

                    telefono = telefono1;
                    rut = rut1;


                    nombre.setText(nom1);
                    Picasso.with(AjustesPerfilActivity.this)
                            .load(imagen1)
                            .placeholder(R.drawable.foto)
                            .into(imageperfil);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }

        });
    }

    private void uploadPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, COD_SEL_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        Log.d("image_url", "requestCode - RESULT:OK: " + requestCode + " " + RESULT_OK);
        if(resultCode == RESULT_OK){
            if(requestCode == COD_SEL_IMAGE){
                image_url = data.getData();
                subirPhoto(image_url);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void subirPhoto(Uri image_url) {
        dialog.setMessage("Actualizando foto");
        dialog.show();
        String rute_storage_photo = storagepath + "" + "photo" + "" + auth.getUid() + CurrentUserID;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) {
                    if (uriTask.isSuccessful()) {
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download_Uri = uri.toString();
                                String url ="Foto"+ download_Uri;
                                HashMap map = new HashMap();
                                map.put("Foto", download_Uri);
                                RootRef.child(CurrentUserID).updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        dialog.dismiss();
                                    }
                                });
                                Picasso.with(AjustesPerfilActivity.this)
                                        .load(url)
                                        .placeholder(R.drawable.foto)
                                        .into(imageperfil);

                            }


                        });
                    }
                }
            }
        });
    }


    private void ActualizarInformacion() {
    String nom = nombre.getText().toString();
    String tel = telefono;
    String rt = rut;

    if(TextUtils.isEmpty(nom)){
        Toast.makeText(this, "Ingrese su nombre", Toast.LENGTH_SHORT).show();
    }else{
        HashMap  profile = new HashMap();
        profile.put("uid", CurrentUserID);
        profile.put("nombre", nom);

        RootRef.child("Usuarios").child(CurrentUserID).updateChildren(profile);

    }

    }

    private void Componentes() {
        nombre = (EditText) findViewById(R.id.nombre_perfil);
       actualizar = (Button)findViewById(R.id.boton_perfil) ;
        imageperfil = (CircleImageView) findViewById(R.id.imagen_perfil);
        toolbar = (Toolbar) findViewById(R.id.toolbar_setup);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Actualizar Perfil");
        dialog = new ProgressDialog(this);
    }

}
