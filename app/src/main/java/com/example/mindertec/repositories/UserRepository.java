package com.example.mindertec.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserRepository {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private session_manager_screen sessionManager;

    public UserRepository(Context context) {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.sessionManager = new session_manager_screen(context);
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
}

