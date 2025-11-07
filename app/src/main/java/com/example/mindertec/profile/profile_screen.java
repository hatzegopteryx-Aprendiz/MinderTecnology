package com.example.mindertec.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.menu.menu_screen;

import org.w3c.dom.Text;

public class profile_screen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Button btn_back = findViewById(R.id.btn_volver_prf);
        btn_back.setOnClickListener(v ->startActivity(new Intent(this, menu_screen.class)));

    }
}
