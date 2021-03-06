package com.example.aplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VentanaPrincipalActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private ViewPager myviewPager;
    private TabLayout mytabLayout;
    private AdapterTabs adapter;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private RecyclerView messagesRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_principal);
        toolbar=(Toolbar)findViewById(R.id.app_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AppBuelos");

        myviewPager =(ViewPager)findViewById(R.id.main_tabs_pager);
        adapter = new AdapterTabs(getSupportFragmentManager());
        myviewPager.setAdapter(adapter);

        mytabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mytabLayout.setupWithViewPager(myviewPager);

        databaseReference  = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
    }
    @Override
    protected void onStart(){
    super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();
        if(curUser == null){
            EnviarALogin();
        }else{
            VerificarUsuario();
        }
    }
    private void VerificarUsuario() {
        final String currentUserID = mAuth.getCurrentUser().getUid();

        databaseReference.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("")) {
                    CompletarDatosUsuario();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

        private void CompletarDatosUsuario() {
            Intent intent = new Intent(VentanaPrincipalActivity.this, SetupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
        private void EnviarALogin(){
            Intent intent = new Intent(VentanaPrincipalActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        @Override
            public boolean onCreateOptionsMenu(Menu menu){
                super.onCreateOptionsMenu(menu);
                getMenuInflater().inflate(R.menu.menu_opciones, menu);
                return true;

        }

        @Override
            public boolean onOptionsItemSelected(@NonNull MenuItem item){
                super.onOptionsItemSelected(item);
                    if(item.getItemId() == R.id.ajustes) AjustesActivity();
                return true;

        }

    private void AjustesActivity() {
        Intent intent = new Intent(VentanaPrincipalActivity.this, AjustesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }



}



