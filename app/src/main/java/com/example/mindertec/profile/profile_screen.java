package com.example.mindertec.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.mindertec.R;
import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.menu.menu_screen;
import com.example.mindertec.repositories.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class profile_screen extends AppCompatActivity {

    private session_manager_screen sessionManager;
    private UserRepository userRepository;
    private TextView tvNombre, tvCorreo;
    private ImageView imgPerfil;
    private MaterialButton btnCamera;
    private Uri photoUri;
    private Bitmap currentPhotoBitmap;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    // ActivityResultLauncher para la cámara
    private ActivityResultLauncher<Uri> takePictureLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        sessionManager = new session_manager_screen(this);
        userRepository = new UserRepository(this);

        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        imgPerfil = findViewById(R.id.imgPerfil);
        btnCamera = findViewById(R.id.btnCamera);

        loadUserData();
        setupCameraLauncher();
        setupCameraButton();

        // Botón volver
        Button btn_back = findViewById(R.id.btn_volver_prf);
        btn_back.setOnClickListener(v ->
                startActivity(new Intent(this, menu_screen.class))
        );
    }

    private void setupCameraLauncher() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        if (photoUri != null) {
                            try {
                                // Obtener bitmap desde la URI
                                if (Build.VERSION.SDK_INT < 28) {
                                    currentPhotoBitmap = MediaStore.Images.Media.getBitmap(
                                            getContentResolver(), photoUri);
                                } else {
                                    currentPhotoBitmap = null; // Opcional: si no necesitas el Bitmap en sí
                                }

                                // Mostrar la imagen en el ImageView
                                imgPerfil.setImageURI(photoUri);

                                // Subir la foto a Firebase Storage + guardar URL en BD
                                uploadPhotoToFirebase(photoUri);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Error al cargar la foto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void uploadPhotoToFirebase(Uri photoUri) {
        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show();
        btnCamera.setEnabled(false);

        userRepository.uploadProfilePhoto(photoUri, new UserRepository.UploadPhotoCallback() {
            @Override
            public void onSuccess(String photoUrl) {
                runOnUiThread(() -> {
                    // Guardar URL en la sesión para acceso rápido
                    sessionManager.saveUserPhoto(photoUrl);

                    btnCamera.setEnabled(true);
                    Toast.makeText(profile_screen.this,
                            "Foto guardada exitosamente",
                            Toast.LENGTH_SHORT).show();

                    // Volver a cargar con Picasso desde la URL por si cambia de dispositivo
                    Picasso.get()
                            .load(photoUrl)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(imgPerfil);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    btnCamera.setEnabled(true);
                    Toast.makeText(profile_screen.this,
                            "Error: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onProgress(double progress) {
                // Opcional: podrías mostrar un ProgressBar si quieres
            }
        });
    }

    private void setupCameraButton() {
        btnCamera.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this,
                        "Se necesita permiso de cámara para tomar fotos",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        photoFile
                );
                takePictureLauncher.launch(photoUri);
            } else {
                Toast.makeText(this,
                        "Error al crear archivo de imagen",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error al abrir la cámara",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "profile_photo_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    private void loadUserData() {
        // Obtener datos del usuario desde SessionManager
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userId = sessionManager.getUserId();
        String localPhotoUrl = sessionManager.getUserPhoto();

        if (tvNombre != null) {
            tvNombre.setText(userName);
        }

        if (tvCorreo != null) {
            tvCorreo.setText(userEmail);
        }

        // 1. Cargar primero desde sesión (más rápido)
        if (localPhotoUrl != null && !localPhotoUrl.isEmpty()) {
            Picasso.get()
                    .load(localPhotoUrl)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(imgPerfil);
        }

        // 2. Refrescar desde Firebase por si hay algo más actualizado
        if (userId != null && !userId.isEmpty()) {
            loadProfilePhoto(userId);
        }
    }

    private void loadProfilePhoto(String userId) {
        userRepository.getUserData(userId, new UserRepository.UserDataCallback() {
            @Override
            public void onDataLoaded(com.example.mindertec.models.User user) {
                runOnUiThread(() -> {
                    String photoUrl = user.getFotoUrl();
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        // Actualizar también en sesión para próximas veces
                        sessionManager.saveUserPhoto(photoUrl);

                        Picasso.get()
                                .load(photoUrl)
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                                .into(imgPerfil);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Mantener imagen por defecto si falla
            }
        });
    }
}
