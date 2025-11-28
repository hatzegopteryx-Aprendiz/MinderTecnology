package com.example.mindertec.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
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
import com.example.mindertec.utils.LightSensorHelper;
import com.example.mindertec.utils.ThemeHelper;
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
    private LightSensorHelper lightSensorHelper;
    private View rootView;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    // ActivityResultLauncher para la cámara
    private ActivityResultLauncher<Uri> takePictureLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        
        rootView = findViewById(android.R.id.content);

        sessionManager = new session_manager_screen(this);
        userRepository = new UserRepository(this);
        
        // Inicializar sensor de luz
        initializeLightSensor();

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
                                if (Build.VERSION.SDK_INT < 28) {
                                    currentPhotoBitmap = MediaStore.Images.Media.getBitmap(
                                            getContentResolver(), photoUri);
                                }

                                imgPerfil.setImageURI(photoUri);

                                uploadPhotoToFirebase(photoUri);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Error al cargar la foto",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void uploadPhotoToFirebase(Uri photoUri) {
        android.util.Log.d("ProfileScreen", "Iniciando subida de foto a Firebase");
        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show();
        btnCamera.setEnabled(false);

        if (photoUri == null) {
            android.util.Log.e("ProfileScreen", "Error: photoUri es null");
            Toast.makeText(this, "Error: No se pudo obtener la foto", Toast.LENGTH_SHORT).show();
            btnCamera.setEnabled(true);
            return;
        }

        userRepository.uploadProfilePhoto(photoUri, new UserRepository.UploadPhotoCallback() {
            @Override
            public void onSuccess(String photoUrl) {
                android.util.Log.d("ProfileScreen", "Foto subida exitosamente. URL: " + photoUrl);
                runOnUiThread(() -> {
                    sessionManager.saveUserPhoto(photoUrl);

                    btnCamera.setEnabled(true);
                    Toast.makeText(profile_screen.this,
                            "Foto guardada exitosamente",
                            Toast.LENGTH_SHORT).show();

                    Picasso.get()
                            .load(photoUrl)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(imgPerfil);
                });
            }

            @Override
            public void onError(String errorMessage) {
                android.util.Log.e("ProfileScreen", "Error al subir foto: " + errorMessage);
                runOnUiThread(() -> {
                    btnCamera.setEnabled(true);
                    Toast.makeText(profile_screen.this,
                            "Error: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onProgress(double progress) {
                android.util.Log.d("ProfileScreen", "Progreso: " + progress + "%");
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
        // Verificar si el dispositivo tiene cámara
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this,
                    "Este dispositivo no tiene cámara",
                    Toast.LENGTH_SHORT).show();
            return;
        }

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
                    "Error al abrir la cámara: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Carpeta segura en Android 10+ (no requiere permisos de almacenamiento)
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }

        if (storageDir == null) {
            throw new IOException("No se pudo acceder al directorio de imágenes");
        }

        String imageFileName = "profile_photo_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(storageDir, imageFileName);

        return imageFile;
    }

    private void loadUserData() {
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userId = sessionManager.getUserId();
        String localPhotoUrl = sessionManager.getUserPhoto();

        tvNombre.setText(userName);
        tvCorreo.setText(userEmail);

        if (localPhotoUrl != null && !localPhotoUrl.isEmpty()) {
            Picasso.get().load(localPhotoUrl).into(imgPerfil);
        }

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
                // No hacer nada si falla
            }
        });
    }
    
    private void initializeLightSensor() {
        lightSensorHelper = new LightSensorHelper(this);
        
        if (lightSensorHelper.isSensorAvailable()) {
            lightSensorHelper.setLightChangeListener(isDarkMode -> {
                runOnUiThread(() -> {
                    // Cambiar solo el fondo según la luz
                    if (rootView != null) {
                        ThemeHelper.changeBackgroundOnly(rootView, isDarkMode);
                    }
                });
            });
            lightSensorHelper.startListening();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensorHelper != null && lightSensorHelper.isSensorAvailable()) {
            lightSensorHelper.startListening();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (lightSensorHelper != null) {
            lightSensorHelper.stopListening();
        }
    }

}
