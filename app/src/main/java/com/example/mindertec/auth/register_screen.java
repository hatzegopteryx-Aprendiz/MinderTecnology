package com.example.mindertec.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mindertec.MainActivity;
import com.example.mindertec.R;
import com.example.mindertec.menu.menu_screen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class register_screen extends AppCompatActivity {

    private EditText edText_Nombre, edText_Correo, edText_Contrasena;

    private Button btn_Registrar;

    private String nombre, correo, contrasena;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edText_Nombre = findViewById(R.id.edText_Nombre);
        edText_Correo = findViewById(R.id.edText_Correo);
        edText_Contrasena = findViewById(R.id.edText_Contrasena);
        btn_Registrar = findViewById(R.id.btn_Registrar);

        btn_Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre =  edText_Nombre.getText().toString();
                correo =  edText_Correo.getText().toString();
                contrasena =  edText_Contrasena.getText().toString();

                if (!nombre.isEmpty() && !correo.isEmpty() &&  !contrasena.isEmpty()){

                    if (contrasena.length()>=6){
                        registrarUsuario();
                    }
                    else{
                        Toast.makeText(register_screen.this,"La contraseña debe tener minimo 6 caracteres.",Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(register_screen.this, "Debe completar los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button btn_Volver = findViewById(R.id.btn_Volver);
        btn_Volver.setOnClickListener(v ->startActivity(new Intent(this, login_screen.class)));

    }

    private void registrarUsuario(){
        mAuth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Map<String,Object> map = new HashMap<>();
                    map.put("nombre",nombre);
                    map.put("correo", correo);
                    map.put("contrasena", contrasena);

                    String id = mAuth.getCurrentUser().getUid();

                    mDatabase.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){
                                startActivity(new Intent(register_screen.this,login_screen.class));
                                finish();
                            }else {
                                Toast.makeText(register_screen.this,"No se pudo crear la cuenta",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(register_screen.this, "Cuenta creada correctamente",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}