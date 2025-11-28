package com.example.mindertec.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import android.net.Uri;

import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UserRepository {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private session_manager_screen sessionManager;

    public UserRepository(Context context) {
        this.mAuth = FirebaseAuth.getInstance();
        // Inicializar Firebase Realtime Database
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        // Inicializar Firebase Storage (usa el bucket del proyecto desde google-services.json)
        try {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            this.mStorageRef = storage.getReference();
            this.sessionManager = new session_manager_screen(context);
            
            // Verificar que Storage esté correctamente inicializado
            if (mStorageRef != null) {
                android.util.Log.d("UserRepository", "Firebase Storage inicializado correctamente");
                android.util.Log.d("UserRepository", "Storage Bucket: " + mStorageRef.getBucket());
            } else {
                android.util.Log.e("UserRepository", "Error: StorageReference es null");
            }
        } catch (Exception e) {
            android.util.Log.e("UserRepository", "Error al inicializar Firebase Storage: " + e.getMessage());
            e.printStackTrace();
            this.mStorageRef = null;
        }
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public interface RegisterCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public interface UserDataCallback {
        void onDataLoaded(User user);
        void onError(String errorMessage);
    }

    public interface UploadPhotoCallback {
        void onSuccess(String photoUrl);
        void onError(String errorMessage);
        void onProgress(double progress);
    }

    public void loginUser(String correo, String contrasena, LoginCallback callback) {
        mAuth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        String userNameFromEmail = correo.split("@")[0];
                        
                        // Guardar datos básicos en SessionManager inmediatamente
                        sessionManager.saveUserSession(userId, userNameFromEmail, correo);
                        
                        // Crear usuario básico y llamar al callback inmediatamente
                        User user = new User(userNameFromEmail, correo);
                        callback.onSuccess(user);
                        
                        // Intentar obtener datos completos de Firebase Database en segundo plano
                        // (no bloquea el login)
                        try {
                            mDatabase.child("Usuarios").child(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String userName = dataSnapshot.child("nombre").getValue(String.class);
                                                String userEmail = dataSnapshot.child("correo").getValue(String.class);
                                                
                                                // Si hay datos válidos en Firebase, actualizar SessionManager
                                                if (userName != null && !userName.trim().isEmpty() && 
                                                    userEmail != null && !userEmail.trim().isEmpty()) {
                                                    sessionManager.saveUserSession(userId, userName, userEmail);
                                                }
                                            }
                                            // Si no hay datos o están vacíos, se mantienen los datos básicos ya guardados
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Si falla la consulta, los datos básicos ya están guardados
                                            // El login ya funcionó, no hacer nada más
                                        }
                                    });
                        } catch (Exception e) {
                            // Si hay algún error, los datos básicos ya están guardados
                            // Continuar con el login
                        }
                    } else {
                        String errorMessage = translateFirebaseError(task.getException());
                        callback.onError(errorMessage);
                    }
                });
    }

    public void registerUser(String nombre, String correo, String contrasena, RegisterCallback callback) {
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(nombre, correo);
                        
                        // Guardar usuario en Firebase Database
                        mDatabase.child("Usuarios").child(userId).setValue(user)
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        sessionManager.saveUserSession(userId, nombre, correo);
                                        callback.onSuccess(user);
                                    } else {
                                        callback.onError("No se pudo crear la cuenta");
                                    }
                                });
                    } else {
                        String errorMessage = translateFirebaseRegisterError(task.getException());
                        callback.onError(errorMessage);
                    }
                });
    }

    public void getUserData(String userId, UserDataCallback callback) {
        mDatabase.child("Usuarios").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                callback.onDataLoaded(user);
                            } else {
                                callback.onError("Error al obtener datos del usuario");
                            }
                        } else {
                            callback.onError("Usuario no encontrado");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError("Error al obtener datos: " + databaseError.getMessage());
                    }
                });
    }

    private String translateFirebaseError(Exception exception) {
        if (exception == null) {
            return "Correo o contraseña incorrectos";
        }
        
        String exceptionMessage = exception.getMessage();
        if (exceptionMessage == null) {
            return "Correo o contraseña incorrectos";
        }
        
        if (exceptionMessage.contains("password") || exceptionMessage.contains("Password")) {
            return "Contraseña incorrecta";
        } else if (exceptionMessage.contains("user") || exceptionMessage.contains("User")) {
            return "Usuario no encontrado";
        } else if (exceptionMessage.contains("network") || exceptionMessage.contains("Network")) {
            return "Error de conexión. Verifica tu internet";
        } else if (exceptionMessage.contains("invalid") || exceptionMessage.contains("Invalid")) {
            return "Correo o contraseña inválidos";
        } else {
            return "Error al iniciar sesión: " + exceptionMessage;
        }
    }

    private String translateFirebaseRegisterError(Exception exception) {
        if (exception == null) {
            return "Error al crear cuenta";
        }
        
        String exceptionMessage = exception.getMessage();
        if (exceptionMessage == null) {
            return "Error al crear cuenta";
        }
        
        if (exceptionMessage.contains("email") || exceptionMessage.contains("Email")) {
            if (exceptionMessage.contains("already") || exceptionMessage.contains("already in use")) {
                return "Este correo ya está registrado";
            } else if (exceptionMessage.contains("badly") || exceptionMessage.contains("invalid")) {
                return "Correo electrónico inválido";
            } else {
                return "Error con el correo electrónico";
            }
        } else if (exceptionMessage.contains("password") || exceptionMessage.contains("Password")) {
            if (exceptionMessage.contains("weak")) {
                return "La contraseña es muy débil";
            } else {
                return "Error con la contraseña";
            }
        } else if (exceptionMessage.contains("network") || exceptionMessage.contains("Network")) {
            return "Error de conexión. Verifica tu internet";
        } else if (exceptionMessage.contains("too many")) {
            return "Demasiados intentos. Intenta más tarde";
        } else {
            return "Error al crear cuenta: " + exceptionMessage;
        }
    }

    public void uploadProfilePhoto(Uri photoUri, UploadPhotoCallback callback) {
        // Verificar autenticación
        if (mAuth.getCurrentUser() == null) {
            android.util.Log.e("UserRepository", "Usuario no autenticado");
            callback.onError("Usuario no autenticado");
            return;
        }

        // Verificar que Storage esté inicializado
        if (mStorageRef == null) {
            android.util.Log.e("UserRepository", "StorageReference no inicializado");
            callback.onError("Error: Storage no está configurado correctamente");
            return;
        }

        // Verificar que el URI sea válido
        if (photoUri == null) {
            android.util.Log.e("UserRepository", "URI de foto es null");
            callback.onError("Error: URI de foto inválido");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String fileName = "profile_photos/" + userId + "_" + System.currentTimeMillis() + ".jpg";
        
        android.util.Log.d("UserRepository", "Iniciando subida de foto:");
        android.util.Log.d("UserRepository", "- UserId: " + userId);
        android.util.Log.d("UserRepository", "- FileName: " + fileName);
        android.util.Log.d("UserRepository", "- PhotoUri: " + photoUri.toString());
        android.util.Log.d("UserRepository", "- StorageBucket: " + mStorageRef.getBucket());
        
        StorageReference photoRef = mStorageRef.child(fileName);

        UploadTask uploadTask = photoRef.putFile(photoUri);
        
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            android.util.Log.d("UserRepository", "Progreso de subida: " + progress + "%");
            callback.onProgress(progress);
        });

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            android.util.Log.d("UserRepository", "Foto subida exitosamente a Storage");
            photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String photoUrl = uri.toString();
                android.util.Log.d("UserRepository", "URL de descarga obtenida: " + photoUrl);
                // Actualizar la URL de la foto en la base de datos
                updateUserPhotoUrl(userId, photoUrl, callback);
            }).addOnFailureListener(e -> {
                android.util.Log.e("UserRepository", "Error al obtener URL: " + e.getMessage());
                e.printStackTrace();
                callback.onError("Error al obtener URL de la foto: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            android.util.Log.e("UserRepository", "Error al subir foto: " + e.getMessage());
            android.util.Log.e("UserRepository", "Código de error: " + e.getClass().getName());
            e.printStackTrace();
            
            // Mensajes de error más específicos
            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                if (errorMessage.contains("permission") || errorMessage.contains("Permission")) {
                    callback.onError("Error: No tienes permiso para subir archivos. Verifica las reglas de Firebase Storage.");
                } else if (errorMessage.contains("network") || errorMessage.contains("Network")) {
                    callback.onError("Error de conexión. Verifica tu internet.");
                } else if (errorMessage.contains("unauthorized") || errorMessage.contains("Unauthorized")) {
                    callback.onError("Error: No autorizado. Verifica que estés autenticado y las reglas de Storage.");
                } else {
                    callback.onError("Error al subir la foto: " + errorMessage);
                }
            } else {
                callback.onError("Error desconocido al subir la foto");
            }
        });
    }

    private void updateUserPhotoUrl(String userId, String photoUrl, UploadPhotoCallback callback) {
        mDatabase.child("Usuarios").child(userId).child("fotoUrl").setValue(photoUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(photoUrl);
                    } else {
                        callback.onError("Error al actualizar la URL de la foto en la base de datos");
                    }
                });
    }
}

