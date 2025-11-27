package com.example.mindertec.menu;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mindertec.R;
import com.example.mindertec.auth.login_screen;
import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.controllers.DeviceController;
import com.example.mindertec.controllers.TaskController;
import com.example.mindertec.devices.devices_screen;
import com.example.mindertec.devices.task_screen;
import com.example.mindertec.models.Task;
import com.example.mindertec.profile.profile_screen;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class menu_screen extends AppCompatActivity {

    private ConstraintLayout slidingMenu;
    private FrameLayout contentContainer;
    private TextView sectionTitle;
    private TextView sectionSubtitle;
    private MaterialCardView dashboardContent;
    private View menuOverlay;
    private MaterialButton btnMenuToggle;
    private MaterialButton btnCloseMenu;
    private String currentSection = "dashboard";
    private boolean isMenuOpen = false;

    // Variables de Firebase
    private FirebaseAuth mAuth;
    private session_manager_screen sessionManager;
    private DatabaseReference mDatabase;
    private TextView tvDeviceCount;
    private TextView tvTaskCount;
    private ValueEventListener deviceCountListener;
    private TaskController taskController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Dispositivos");
        sessionManager = new session_manager_screen(this);
        taskController = new TaskController(this);
        
        // Configurar DeviceController en TaskController para guardar historial
        DeviceController deviceController = new DeviceController(this);
        taskController.setDeviceController(deviceController);

        initializeViews();
        setupListeners();
        initializeMenuPosition();
        showDashboard();
        loadDeviceCount();
        loadTodayTasks();
    }

    private void initializeViews() {
        slidingMenu = findViewById(R.id.sliding_menu);
        contentContainer = findViewById(R.id.content_container);
        sectionTitle = findViewById(R.id.section_title);
        sectionSubtitle = findViewById(R.id.section_subtitle);
        dashboardContent = findViewById(R.id.dashboard_content);
        menuOverlay = findViewById(R.id.menu_overlay);
        btnMenuToggle = findViewById(R.id.btn_menu_toggle);
        btnCloseMenu = findViewById(R.id.btn_close_menu);
        tvDeviceCount = findViewById(R.id.tvDeviceCount);
        tvTaskCount = findViewById(R.id.tvTaskCount);
    }

    private void initializeMenuPosition() {
        // Asegurar que el menú esté oculto al inicio
        slidingMenu.setTranslationX(-1000f); // -1000dp para ocultar completamente
        menuOverlay.setVisibility(View.GONE);
        menuOverlay.setAlpha(0f);
        isMenuOpen = false;
    }

    private void setupListeners() {
        btnMenuToggle.setOnClickListener(v -> toggleMenu());
        btnCloseMenu.setOnClickListener(v -> closeMenu());
        menuOverlay.setOnClickListener(v -> closeMenu());
        MaterialCardView cardDispositivos = findViewById(R.id.card_dispositivos);
        cardDispositivos.setOnClickListener(v -> {
            Intent intent = new Intent(menu_screen.this, devices_screen.class);
            startActivity(intent);
            closeMenu();
        });

        MaterialCardView cardPerfil = findViewById(R.id.card_perfil);
        cardPerfil.setOnClickListener(v -> {
            // Abrir la Activity de perfil
            Intent intent = new Intent(menu_screen.this, profile_screen.class);
            startActivity(intent);
            closeMenu();
        });

        MaterialCardView cardTareas = findViewById(R.id.card_tareas);
        cardTareas.setOnClickListener(v -> {
            Intent intent = new Intent(menu_screen.this, task_screen.class);
            startActivity(intent);
            closeMenu();
        });

        MaterialButton btnVolver = findViewById(R.id.btn_VolverMenu);
        btnVolver.setOnClickListener(v -> {
            // Cerrar sesión y limpiar datos
            sessionManager.logout();
            mAuth.signOut();
            startActivity(new Intent(this, login_screen.class));
            finish();
        });
    }

    private void showDashboard() {
        currentSection = "dashboard";
        sectionTitle.setText("Dashboard");
        sectionSubtitle.setText("Panel principal de control");

        // Mostrar contenido del dashboard
        dashboardContent.setVisibility(View.VISIBLE);

        // Limpiar otros contenidos si existen
        contentContainer.removeAllViews();
        contentContainer.addView(dashboardContent);
    }

    private void showDevicesSection() {
    }

    private void showProfileSection() {
        currentSection = "profile";
        sectionTitle.setText("Perfil");
        sectionSubtitle.setText("Información y configuración del usuario");
        dashboardContent.setVisibility(View.GONE);
        createProfileContent();
    }

    private void showTasksSection() {
        currentSection = "tasks";
        sectionTitle.setText("Tareas");
        sectionSubtitle.setText("Gestiona tus tareas y actividades");

        // Ocultar dashboard
        dashboardContent.setVisibility(View.GONE);

        // Crear contenido dinámico para tareas
        createTasksContent();
    }

    private void createDevicesContent() {
        // Limpiar contenedor
        contentContainer.removeAllViews();

        // Crear contenido para dispositivos
        MaterialCardView devicesCard = new MaterialCardView(this);
        devicesCard.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        devicesCard.setRadius(16);
        devicesCard.setCardElevation(4);
        devicesCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));

        // Aquí puedes agregar el contenido específico de dispositivos
        // Por ahora, agregamos contenido básico
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Gestión de Dispositivos");
        title.setTextColor(getResources().getColor(android.R.color.black));
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("Aquí podrás gestionar todos tus dispositivos conectados");
        subtitle.setTextColor(getResources().getColor(android.R.color.darker_gray));
        subtitle.setTextSize(14);
        subtitle.setPadding(0, 8, 0, 0);

        layout.addView(title);
        layout.addView(subtitle);
        devicesCard.addView(layout);

        contentContainer.addView(devicesCard);
    }

    private void createProfileContent() {
        // Este método ya no se usa, se abre directamente la Activity profile_screen
        // Se mantiene por compatibilidad pero no se llama
    }

    private void createTasksContent() {
        // Limpiar contenedor
        contentContainer.removeAllViews();

        // Inflar el layout de calendario existente
        View calendarView = getLayoutInflater().inflate(R.layout.calendar, contentContainer, false);

        // Configurar el botón volver del calendario para que regrese al dashboard
        com.google.android.material.button.MaterialButton btnVolverCalendar = calendarView.findViewById(R.id.btn_volver_m);
        btnVolverCalendar.setOnClickListener(v -> showDashboard());

        // Agregar la vista del calendario al contenedor
        contentContainer.addView(calendarView);
    }

    private void toggleMenu() {
        if (isMenuOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    private void openMenu() {
        isMenuOpen = true;
        
        // Mostrar overlay
        menuOverlay.setVisibility(View.VISIBLE);
        menuOverlay.setAlpha(0f);
        
        // Animar el menú desde la izquierda (-1000dp a 0dp)
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(slidingMenu, "translationX", -1000f, 0f);
        slideIn.setDuration(300);
        slideIn.start();
        
        // Animar el overlay para que aparezca gradualmente
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(menuOverlay, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        fadeIn.start();
    }

    private void closeMenu() {
        isMenuOpen = false;
        
        // Animar el menú hacia la izquierda (0dp a -1000dp)
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(slidingMenu, "translationX", 0f, -1000f);
        slideOut.setDuration(300);
        slideOut.start();
        
        // Animar el overlay para que desaparezca gradualmente
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(menuOverlay, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.start();
        
        // Ocultar overlay después de la animación
        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                menuOverlay.setVisibility(View.GONE);
                // Asegurar que el menú esté completamente oculto
                slidingMenu.setTranslationX(-1000f);
            }
        });
    }

    private void loadDeviceCount() {
        if (mAuth.getCurrentUser() == null) {
            if (tvDeviceCount != null) {
                tvDeviceCount.setText("0");
            }
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userDevicesRef = mDatabase.child(userId);

        deviceCountListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int deviceCount = 0;
                if (snapshot.exists() && snapshot.hasChildren()) {
                    deviceCount = (int) snapshot.getChildrenCount();
                }
                
                if (tvDeviceCount != null) {
                    tvDeviceCount.setText(String.valueOf(deviceCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // En caso de error, mantener el contador en 0 o el último valor
                if (tvDeviceCount != null) {
                    tvDeviceCount.setText("0");
                }
            }
        };

        userDevicesRef.addValueEventListener(deviceCountListener);
    }

    private void loadTodayTasks() {
        if (mAuth.getCurrentUser() == null) {
            if (tvTaskCount != null) {
                tvTaskCount.setText("0");
            }
            return;
        }

        taskController.getTasksForUser(new TaskController.GetTasksListener() {
            @Override
            public void onSuccess(List<Task> tasks) {
                // Obtener fecha de hoy en formato ISO
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar today = Calendar.getInstance();
                String todayDate = isoFormat.format(today.getTime());

                // Filtrar solo tareas pendientes de hoy
                List<Task> todayTasks = new ArrayList<>();
                for (Task task : tasks) {
                    // Solo incluir tareas pendientes
                    String estado = task.getEstado() != null ? task.getEstado() : "pendiente";
                    if (!"pendiente".equals(estado)) {
                        continue; // Saltar tareas que no están pendientes
                    }
                    
                    String fechaInicio = task.getFechaInicioIso();
                    String fechaTermino = task.getFechaTerminoEstimadaIso();
                    
                    // Verificar si la fecha de inicio es hoy
                    if (fechaInicio != null && fechaInicio.equals(todayDate)) {
                        todayTasks.add(task);
                    }
                    // Verificar si la fecha de término estimada es hoy
                    else if (fechaTermino != null && fechaTermino.equals(todayDate)) {
                        todayTasks.add(task);
                    }
                    // Verificar si hoy está entre la fecha de inicio y término (tarea en curso)
                    else if (fechaInicio != null && fechaTermino != null) {
                        try {
                            Calendar inicio = Calendar.getInstance();
                            inicio.setTime(isoFormat.parse(fechaInicio));
                            
                            Calendar termino = Calendar.getInstance();
                            termino.setTime(isoFormat.parse(fechaTermino));
                            
                            // Si hoy está entre inicio y término, incluir la tarea
                            if (!today.before(inicio) && !today.after(termino)) {
                                todayTasks.add(task);
                            }
                        } catch (Exception e) {
                            // Si hay error al parsear fechas, ignorar esta tarea
                        }
                    }
                }

                // Actualizar contador
                if (tvTaskCount != null) {
                    tvTaskCount.setText(String.valueOf(todayTasks.size()));
                }

                // Mostrar tareas en la lista del dashboard
                android.widget.ListView listView = dashboardContent.findViewById(R.id.listTodayTasks);
                if (listView != null) {
                    com.example.mindertec.devices.TaskAdapter adapter = 
                            new com.example.mindertec.devices.TaskAdapter(todayTasks);
                    
                    // Configurar listener para cambiar estado de tareas
                    adapter.setOnTaskStateChangeListener((taskId, newState) -> {
                        taskController.updateTaskState(taskId, newState, 
                                new TaskController.UpdateTaskStateListener() {
                                    @Override
                                    public void onSuccess() {
                                        android.widget.Toast.makeText(menu_screen.this,
                                                "Tarea marcada como completada",
                                                android.widget.Toast.LENGTH_SHORT).show();
                                        // Recargar tareas para actualizar la lista
                                        loadTodayTasks();
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        android.widget.Toast.makeText(menu_screen.this,
                                                errorMessage,
                                                android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                    
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (tvTaskCount != null) {
                    tvTaskCount.setText("0");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar tareas cuando se vuelve a la pantalla
        loadTodayTasks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remover el listener para evitar memory leaks
        if (deviceCountListener != null && mDatabase != null && mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            if (userId != null) {
                mDatabase.child(userId).removeEventListener(deviceCountListener);
            }
        }
    }
}