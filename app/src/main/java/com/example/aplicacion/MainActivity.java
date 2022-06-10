package com.example.aplicacion;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public class MainActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState){
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
      Thread thread  = new Thread(){

          @Override
          public void run() {
              try {

                  FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
                  firebaseAppCheck.installAppCheckProviderFactory(
                          PlayIntegrityAppCheckProviderFactory.getInstance());
                  sleep(2000);


              } catch (Exception e) {

                  e.printStackTrace();

              } finally {
                  Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                  startActivity(intent);
              }
          }

          };
          thread.start();

      }
      @Override
    protected void onPause(){
        super.onPause();
        finish();

      }

  }






