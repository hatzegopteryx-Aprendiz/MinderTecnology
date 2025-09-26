package com.example.mindertec;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.auth.login_screen;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Redirige al login_screen y cierra MainActivity

        Intent login_screen = new Intent(this, login_screen.class);
        startActivity(login_screen);
        finish();
        
    }
}