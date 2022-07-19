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
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private EditText nombre;
    private Button guardarinfo;
    private CircleImageView image_setup;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserID;
    final static int Gallery_PICK = 1;
    private static final int COD_SEL_IMAGE= 300;
    private Uri image_url;
    StorageReference  storageReference;
    String storagepath = "imgperfil/*";
    private String token;


    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        nombre = (EditText) findViewById(R.id.nombre_setup);
        image_setup = (CircleImageView) findViewById(R.id.imagen_setup);
        toolbar = (Toolbar) findViewById(R.id.app_main_toolbar);

        guardarinfo = (Button) findViewById(R.id.bottom_setup);
        dialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        CurrentUserID = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        storageReference = FirebaseStorage.getInstance().getReference();


        guardarinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GuardarInformacionDB();
            }
        });
        image_setup.setOnClickListener(new View.OnClickListener() {
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
                               final String url ="Foto"+ download_Uri;
                                HashMap map = new HashMap();
                                map.put("Foto", download_Uri);
                                UserRef.child(CurrentUserID).updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {


                                        dialog.dismiss();
                                    }
                                });

                                Picasso.get()
                                        .load(url)
                                        .error(R.drawable.foto)
                                        .placeholder(R.drawable.foto)
                                        .into(image_setup);

                                dialog.dismiss();

                            }


                        });
                    }
                }
            }
        });
    }


    private void GuardarInformacionDB() {
        String nom = nombre.getText().toString();

        if(TextUtils.isEmpty(nom)){
            Toast.makeText(this, "Debe ingresar nombre", Toast.LENGTH_SHORT).show();

        }else {
            dialog.setTitle("Guardando datos");
            dialog.setMessage("Por favor espere a que finalize el procesp");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);

            String token = String.valueOf(FirebaseMessaging.getInstance().getToken());

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                      if(task.isSuccessful()){
                          HashMap map = new HashMap();
                          map.put("nombre", nom);
                          map.put("token",token);

                          UserRef.child(CurrentUserID).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                              @Override
                              public void onComplete(@NonNull Task task) {
                                  if(task.isSuccessful()){
                                      Toast.makeText(SetupActivity.this, "Datos Guardados", Toast.LENGTH_SHORT).show();
                                      dialog.dismiss();
                                      EnviarALInicio();

                                  }else{

                                      String err = task.getException().getMessage();
                                      Toast.makeText(SetupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                  }
                              }
                          });

                      }
                }
            });


        }
    }

    private void EnviarALInicio() {
        Intent intent = new Intent(SetupActivity.this, VentanaPrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
