package com.example.aplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AjustesActivity extends AppCompatActivity {
    private TextView Perfil;
    private SeekBar sk;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_ajustar);
        sk = (SeekBar) findViewById(R.id.seekBar);
        Perfil = (TextView)findViewById(R.id.ventanaAjustar);
        Perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirAjustesPerfil();
            }
        });
    }

    private void abrirAjustesPerfil() {
        Intent intent = new Intent(AjustesActivity.this, AjustesPerfilActivity.class);
        startActivity(intent);
    }


}
