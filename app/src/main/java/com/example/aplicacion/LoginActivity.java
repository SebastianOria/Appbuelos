package com.example.aplicacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText numero, codigo, rut;
    private Button enviar_numero, enviar_codigo;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerification;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String phoneNumber;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        numero = (EditText)findViewById(R.id.nombre);
        rut = (EditText) findViewById(R.id.rut);
        codigo = (EditText) findViewById(R.id.codigo);
        enviar_numero = (Button) findViewById(R.id.siguiente);
        enviar_codigo = (Button) findViewById(R.id.enviarCodigo);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        enviar_numero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    phoneNumber  = rut.getText().toString();
                    if(TextUtils.isEmpty(phoneNumber)){

                        Toast.makeText(LoginActivity.this, "ingrese el numero", Toast.LENGTH_LONG).show();

                    }else{
                        loadingBar.setTitle("Enviando el codigo");
                        loadingBar.setMessage("Por favor espere...");
                        loadingBar.show();
                        loadingBar.setCancelable(true);
                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber(phoneNumber)
                                        .setTimeout(60L, TimeUnit.SECONDS)
                                        .setActivity(LoginActivity.this)
                                        .setCallbacks(callbacks)
                                        .build();
                                PhoneAuthProvider.verifyPhoneNumber(options);
                    }
            }
        });

        enviar_codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numero.setVisibility(View.GONE);
                rut.setVisibility(View.GONE);
                enviar_numero.setVisibility(View.GONE);
                String verificationCode = codigo.getText().toString();
                if(TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(LoginActivity.this, "Ingrese el codigo recibido", Toast.LENGTH_SHORT).show();
                }else{

                    loadingBar.setTitle("ingresando");
                    loadingBar.setMessage(".............");
                    loadingBar.show();
                    loadingBar.setCancelable(true);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerification, verificationCode);
                    signInPhoneAuthCredential(credential);

                }
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Numero invalido, Intente denuevo", Toast.LENGTH_SHORT).show();
                numero.setVisibility(View.VISIBLE);
                enviar_numero.setVisibility((View.VISIBLE));
                rut.setVisibility(View.VISIBLE);
                codigo.setVisibility(View.GONE);
                enviar_codigo.setVisibility(View.GONE);
            }
            @Override
            public void onCodeSent(String verificacionid, PhoneAuthProvider.ForceResendingToken token){
            mVerification = verificacionid;
            mResendingToken = token;
            loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Codigo invalido, Intente denuevo", Toast.LENGTH_SHORT).show();
                numero.setVisibility(View.GONE);
                enviar_numero.setVisibility((View.GONE));
                rut.setVisibility(View.GONE);
                codigo.setVisibility(View.VISIBLE);
                enviar_codigo.setVisibility(View.VISIBLE);
            }



        };




    }

private void signInPhoneAuthCredential(PhoneAuthCredential credential){

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Ingresado Con Exito", Toast.LENGTH_SHORT).show();
                    EnviarAlInicio();

                }else{

                    String mensaje = task.getException().toString();
                    Toast.makeText(LoginActivity.this, "Error"+ mensaje, Toast.LENGTH_SHORT).show();
                }

            }
        });


}
@Override
    protected void onStart(){
        super.onStart();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    if(currentUser != null ){
        EnviarAlInicio();

    }

}
private void EnviarAlInicio(){
        Intent intent = new Intent(LoginActivity.this, VentanaPrincipalActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

}

}
