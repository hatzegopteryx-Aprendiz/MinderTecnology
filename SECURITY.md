# 🔒 Seguridad y Credenciales

## ⚠️ IMPORTANTE: Archivos que NO deben subirse a GitHub

### Archivos Críticos (YA ESTÁN EN .gitignore):
- ✅ `app/google-services.json` - Contiene API keys y configuración de Firebase
- ✅ `local.properties` - Contiene rutas del SDK de Android
- ✅ `*.keystore` / `*.jks` - Archivos de firma de la aplicación
- ✅ Archivos con credenciales: `*.p12`, `*.p8`, `*.pem`, `*.key`

## 📋 Configuración para Nuevos Desarrolladores

### 1. Obtener `google-services.json`:

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona el proyecto: **mindertecnology**
3. Ve a **Configuración del proyecto** (⚙️)
4. En la sección **Tus aplicaciones**, selecciona la app Android
5. Descarga el archivo `google-services.json`
6. Colócalo en: `app/google-services.json`

### 2. Verificar que el archivo esté en .gitignore:

```bash
# Verificar que google-services.json NO aparece en git
git status
# No debe aparecer app/google-services.json
```

### 3. Si accidentalmente se subió a GitHub:

**ACCIÓN INMEDIATA REQUERIDA:**

1. **Rotar las credenciales en Firebase:**
   - Ve a Firebase Console → Configuración del proyecto
   - Regenera las API keys
   - Actualiza las reglas de seguridad de la base de datos

2. **Eliminar del historial de Git:**
   ```bash
   # Eliminar del historial (CUIDADO: esto reescribe el historial)
   git filter-branch --force --index-filter \
     "git rm --cached --ignore-unmatch app/google-services.json" \
     --prune-empty --tag-name-filter cat -- --all
   
   # O usar BFG Repo-Cleaner (más seguro)
   ```

3. **Forzar actualización en GitHub:**
   ```bash
   git push origin --force --all
   ```

## 🔐 Mejores Prácticas

1. **Nunca commits credenciales:**
   - Revisa `git status` antes de hacer commit
   - Usa `git diff` para ver qué cambios vas a subir

2. **Usa variables de entorno para desarrollo:**
   - Considera usar BuildConfig para valores no sensibles
   - Para valores sensibles, usa un archivo local no versionado

3. **Revisa el .gitignore regularmente:**
   - Asegúrate de que todos los archivos sensibles estén listados

4. **Si trabajas en equipo:**
   - Comparte `google-services.json` por un canal seguro (no por GitHub)
   - Usa un gestor de secretos como GitHub Secrets (para CI/CD)

## 📝 Archivo de Ejemplo

Se incluye `app/google-services.json.example` como plantilla.
Los desarrolladores deben copiarlo y reemplazar los valores con sus credenciales reales.

```bash
# Para nuevos desarrolladores:
cp app/google-services.json.example app/google-services.json
# Luego editar con las credenciales reales
```

