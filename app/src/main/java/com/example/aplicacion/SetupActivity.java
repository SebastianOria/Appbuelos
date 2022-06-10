package com.example.aplicacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private StorageReference UserProfileImagen;
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
        UserProfileImagen = FirebaseStorage.getInstance().getReference().child("ImagesPerfil");

        guardarinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GuardarInformacionDB();
            }
        });
        image_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_PICK);

            }

        });

        UserRef.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("imagen")){
                        
                        String imagen = snapshot.child("imagen").getValue().toString();
                        Picasso.get()
                                .load(imagen)
                                .placeholder(R.drawable.foto)
                                .error(R.drawable.foto)
                                .into(image_setup);
                                                
                    }else{

                        Toast.makeText(SetupActivity.this, "Puede cargar una imagen", Toast.LENGTH_SHORT).show();
                    }
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
                @Override

                protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
                super.onActivityResult(requestCode, resultCode, data);
                if(requestCode==Gallery_PICK && resultCode== RESULT_OK && data != null){
                    Uri imageUri = data.getData();
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio( 1, 1)
                            .start(this);

                }

                if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if(resultCode == RESULT_OK){
                        dialog.setTitle("Imagen de perfil");
                        dialog.setMessage("Estamos guardando tu foto de perfil");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                        final Uri resultUri = result.getUri();
                        StorageReference filePath = UserProfileImagen.child(CurrentUserID+" .jpg");
                        final File url = new File(resultUri.getPath());
                        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(SetupActivity.this, "imagen guardada", Toast.LENGTH_SHORT).show();
                                    UserProfileImagen.child(CurrentUserID).child(CurrentUserID+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String downloadUri = uri.toString();
                                            UserRef.child(CurrentUserID).child("imagen").setValue(downloadUri)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Picasso.get()
                                                                            .load(downloadUri)
                                                                            .error(R.drawable.foto)
                                                                            .into(image_setup);
                                                                    Toast.makeText(SetupActivity.this, "Imagen se guardo", Toast.LENGTH_SHORT).show();
                                                                    dialog.dismiss();
                                                                }else{
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(SetupActivity.this, "Imagen no guardada", Toast.LENGTH_SHORT).show();
                                                                    dialog.dismiss();
                                                                }
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        });
                    }else{
                        Toast.makeText(this, "imagen no soportada intenelo de nuevo", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

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

            HashMap map = new HashMap();
            map.put("nombre", nom);

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

    private void EnviarALInicio() {
        Intent intent = new Intent(SetupActivity.this, VentanaPrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
