# 💰 PocketBook - Gestor de Gastos Personales

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-blue)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.2-purple)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Descripción

**PocketBook** es una aplicación web moderna para la gestión de gastos personales desarrollada con Spring Boot. Permite a los usuarios registrar, visualizar y analizar sus gastos de manera intuitiva y eficiente, con formato de moneda en pesos chilenos (CLP).

## 🚀 Características Principales

- ✅ **Autenticación de usuarios** con validación completa
- 💰 **Registro de gastos** con formularios intuitivos
- 📊 **Visualización de datos** en tablas dinámicas
- 🔍 **Filtros y búsqueda** en tiempo real
- 📈 **Estadísticas automáticas** (total mensual, promedio diario, categoría principal)
- 💵 **Formato de moneda** en pesos chilenos (CLP)
- 🎨 **Interfaz moderna** con Bootstrap 5 y animaciones
- 📱 **Diseño responsive** para móviles y tablets
- 🔐 **Gestión de perfiles** y configuración de usuario

## 🛠️ Tecnologías Utilizadas

### Backend
- **Spring Boot 3.x** - Framework principal
- **Spring MVC** - Arquitectura Modelo-Vista-Controlador
- **Thymeleaf** - Motor de plantillas del lado del servidor
- **Spring Data JPA** - Persistencia de datos
- **H2 Database** - Base de datos en memoria (desarrollo)
- **Maven** - Gestión de dependencias

### Frontend
- **HTML5** - Estructura
- **CSS3** - Estilos personalizados con animaciones
- **JavaScript (Vanilla)** - Validaciones y lógica del cliente
- **Bootstrap 5.3.2** - Framework de UI
- **Bootstrap Icons 1.11.1** - Iconografía

## 📦 Estructura del Proyecto

```
proyectspring/
├── src/
│   ├── main/
│   │   ├── java/com/example/proyectspring/
│   │   │   ├── controllers/          # Controladores MVC
│   │   │   │   ├── LoginController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── GastosController.java
│   │   │   │   ├── PerfilController.java
│   │   │   │   ├── ConfiguracionController.java
│   │   │   │   └── GestorUsuarios.java
│   │   │   ├── entity/                # Entidades JPA

│   │   │   └── ProyectspringApplication.java
│   │   └── resources/
│   │       │   ├── css/               # Estilos CSS
│   │       │   │   ├── dashboard/
│   │       │   │   ├── login/
│   │       │   │   ├── register/
│   │       │   │   ├── gastos/
│   │       │   │   └── perfil/
│   │       │   ├── js/                # Scripts JavaScript
│   │       │   │   └── gastos/
│   │       │   ├── scripts/           # Validaciones
│   │       │   │   ├── login/
│   │       │   │   └── register/
│   │       │   └── image/             # Recursos gráficos
│   │       ├── templates/             # Plantillas Thymeleaf
│   │       │   ├── login.html
│   │       │   ├── crearcuenta.html
│   │       │   ├── dashboard.html
│   │       │   ├── perfil/
│   │       │   │   └── perfil.html
│   │       │   ├── configuracion/
│   │       │   │   └── configuracion.html
│   │       │   ├── gastos/
│   │       │   │   └── gastos.html
│   │       │   ├── gestorUsuarios/
│   │       │   │   └── gestorUsuarios.html
│   │       │   └── fragments/
│   │       │       └── ingresoGastos.html
│   │       └── application.properties # Configuración
│   └── test/                          # Tests unitarios
├── pom.xml                            # Dependencias Maven
└── README.md
```

## 📚 Versiones del Proyecto

### 🔷 **v1.0 - Base y Autenticación** (Primera versión estable)

**Fecha de lanzamiento:** Noviembre 2024

#### Características implementadas:
- ✅ **Sistema de Login**
  - Formulario de inicio de sesión con validación
  - Validación de email con regex
  - Validación de contraseña (mínimo 6 caracteres)
  - Mensajes de error dinámicos con Bootstrap
  - Animaciones CSS en formularios
  - Estilos modernos con gradientes y sombras

- ✅ **Sistema de Registro**
  - Formulario de creación de cuenta
  - Validación de nombre (solo letras y espacios)
  - Validación de email única
  - Requisitos de contraseña:
    - 6-20 caracteres
    - Al menos una mayúscula
    - Al menos un número
    - Al menos un caracter especial
  - Confirmación de contraseña
  - Checkbox de términos y condiciones
  - Validación en tiempo real (JavaScript)

- ✅ **Dashboard Principal**
  - Navbar responsive con dropdown de usuario
  - Sidebar con navegación
  - Cards de resumen (gastos, presupuesto, ahorros, transacciones)
  - Secciones de actividad reciente y categorías
  - Estados vacíos con iconos y mensajes
  - Animaciones de hover en elementos

- ✅ **Perfil de Usuario**
  - Página de perfil con tabs (Información Personal, Seguridad)
  - Sección de avatar
  - Formulario de datos personales
  - Opciones de cambio de contraseña
  - Modal de confirmación para eliminar cuenta

- ✅ **Configuración**
  - Configuración de apariencia (tema claro/oscuro/auto)
  - Selector de idioma y moneda
  - Configuración de notificaciones
  - Opciones de privacidad
  - Exportación de datos

- ✅ **Gestión de Usuarios (Admin)**
  - Vista de tabla de usuarios
  - Columnas: ID, Nombre, Apellido, Email, Contraseña, Fecha de Creación
  - Preparado para operaciones CRUD

#### Mejoras técnicas:
- 📁 Reorganización de templates en carpetas por módulo
- 🎨 CSS modular y organizado por secciones
- 🔧 Controladores actualizados para rutas de carpetas
- 🔗 Navegación con Thymeleaf (`th:href="@{/ruta}"`)
- 🎭 Estilos consistentes en toda la aplicación

---

### 🔷 **v2.0 - Sistema de Gastos** (Versión actual)

**Fecha de lanzamiento:** Noviembre 2024

#### Nuevas características:

- 💰 **Módulo Completo de Gastos**
  - Página dedicada para gestión de gastos
  - Modal Bootstrap para registro de nuevos gastos
  - Formulario con campos:
    - Descripción del gasto
    - Monto (sin decimales, formato CLP)
    - Fecha (no permite fechas futuras)
    - Categoría (9 categorías disponibles)
    - Método de pago (5 opciones)
    - Notas adicionales (opcional)

- 📊 **Tabla Dinámica de Gastos**
  - Visualización en tabla responsive
  - Columnas: #, Fecha, Descripción, Categoría, Método de Pago, Monto, Acciones
  - Badges de colores por categoría:
    - 🍔 Alimentación (amarillo)
    - 🚗 Transporte (celeste)
    - 🏠 Vivienda (verde)
    - 🎬 Entretenimiento (rojo)
    - 💊 Salud (morado)
    - 📚 Educación (azul)
    - 💡 Servicios (naranja)
    - 👔 Ropa y Calzado (rosado)
    - 📦 Otros (gris)
  - Badges de método de pago con íconos
  - Ordenamiento automático por fecha (más reciente primero)
  - Estado vacío cuando no hay gastos
  - Animaciones al agregar filas

- 🔍 **Sistema de Filtros**
  - Búsqueda en tiempo real por descripción
  - Filtro por categoría (dropdown)
  - Filtro por mes (dropdown)
  - Actualización instantánea de resultados

- 📈 **Estadísticas en Tiempo Real**
  - **Total general**: Suma de todos los gastos
  - **Gastos del mes**: Total del mes actual
  - **Promedio diario**: Cálculo automático
  - **Categoría principal**: Categoría más utilizada
  - Actualización automática al agregar/eliminar gastos

- 💵 **Formato de Moneda Chilena**
  - Montos sin decimales (números enteros)
  - Separadores de miles con punto: `$15.000`, `$1.234.567`
  - Función `formatearPesosChilenos()` con `toLocaleString('es-CL')`
  - Validación de montos enteros (sin centavos)
  - Sufijo "CLP" en displays

- ⚡ **Validaciones Completas**
  - Descripción: 3-100 caracteres
  - Monto: entero positivo, máximo 999.999.999
  - Fecha: obligatoria, no permite futuro
  - Categoría y método de pago: obligatorios
  - Feedback visual con clases Bootstrap (is-valid/is-invalid)
  - Mensajes de error específicos por campo

- 🎬 **Acciones sobre Gastos**
  - 👁️ **Ver detalles**: Modal con información completa
  - 🗑️ **Eliminar**: Confirmación antes de borrar
  - Notificaciones toast al guardar/eliminar
  - Confirmación visual de acciones

- 🎨 **Mejoras de UI/UX**
  - Cards de resumen con hover effects
  - Scrollbar personalizado en formularios largos
  - Animación `slideIn` para nuevas filas
  - Gradientes y sombras en elementos
  - Icons de Bootstrap para mejor visualización
  - Diseño responsive optimizado

#### Archivos nuevos en v2:
```
static/
├── css/gastos/gastos.css              # Estilos de módulo de gastos
└── js/gastos/nuevoGasto.js            # Lógica y validaciones

templates/
├── gastos/gastos.html                 # Página principal de gastos
└── fragments/ingresoGastos.html       # Modal de formulario

controllers/
└── GastosController.java              # Controlador de gastos
```

#### Mejoras técnicas en v2:
- 🎯 Lógica de negocio separada en JavaScript modular
- 🎨 CSS específico para componentes de gastos
- 🔄 Actualización reactiva de UI sin recargar página
- 💾 Almacenamiento en memoria (preparado para backend)
- 🎭 Animaciones CSS con @keyframes
- 📱 Optimización mobile-first

---

## 🚦 Instalación y Ejecución

### Prerrequisitos
- Java 17 o superior
- Maven 3.6+
- Git

### Pasos de instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/Frederickiribarren/proyectoGestorDeGastos.git
cd proyectoGestorDeGastos/proyectspring
```

2. **Compilar el proyecto**
```bash
./mvnw clean install
```

3. **Ejecutar la aplicación**

**Opción 1: Con Maven Wrapper**
```bash
./mvnw spring-boot:run
```

**Opción 2: Con JAR compilado**
```bash
./mvnw package
java -jar target/proyectspring-0.0.1-SNAPSHOT.jar
```

4. **Acceder a la aplicación**
```
http://localhost:8080
```

### Credenciales de prueba
- **Email:** usuario@email.com
- **Contraseña:** 123456

---

## 📖 Guía de Uso

### 1. Inicio de Sesión
1. Accede a `http://localhost:8080/login`
2. Ingresa tus credenciales
3. Si no tienes cuenta, haz clic en "Crear cuenta"

### 2. Registrar un Gasto
1. Navega a "Mis Gastos" desde el sidebar
2. Haz clic en el botón "Nuevo Gasto"
3. Completa el formulario:
   - Descripción del gasto
   - Monto en pesos chilenos (sin decimales)
   - Fecha del gasto
   - Selecciona una categoría
   - Elige el método de pago
   - (Opcional) Agrega notas adicionales
4. Haz clic en "Guardar Gasto"

### 3. Visualizar y Filtrar Gastos
- **Buscar:** Escribe en la barra de búsqueda para filtrar por descripción
- **Filtrar por categoría:** Usa el dropdown de categorías
- **Filtrar por mes:** Selecciona un mes específico
- **Ver detalles:** Haz clic en el ícono 👁️ para ver información completa
- **Eliminar:** Haz clic en el ícono 🗑️ y confirma la acción

### 4. Estadísticas
- Las estadísticas se actualizan automáticamente
- **Total general:** Suma de todos los gastos registrados
- **Gastos del mes:** Total del mes actual
- **Promedio diario:** Gasto promedio por día del mes
- **Categoría principal:** La categoría más utilizada

---

## 🎯 Roadmap

### v2.1 (Próximamente)
- [ ] Integración con base de datos PostgreSQL
- [ ] Persistencia real de gastos
- [ ] API REST para operaciones CRUD
- [ ] Edición de gastos existentes
- [ ] Exportación de gastos a CSV/PDF

### v2.2
- [ ] Gráficos interactivos (Chart.js)
- [ ] Análisis de gastos por categoría
- [ ] Comparativas mensuales
- [ ] Predicciones de gastos

### v3.0
- [ ] Sistema de presupuestos
- [ ] Alertas y notificaciones
- [ ] Múltiples monedas
- [ ] Modo oscuro persistente
- [ ] PWA (Progressive Web App)

### v3.1
- [ ] Autenticación con JWT
- [ ] Roles de usuario (Admin, Usuario)
- [ ] Gestión de usuarios completa
- [ ] Auditoría de cambios

---

## 🤝 Contribución

Las contribuciones son bienvenidas. Para contribuir:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📝 Convenciones de Código

- **Java:** Seguir convenciones de Spring Boot
- **JavaScript:** ES6+ con funciones arrow
- **CSS:** BEM methodology para clases
- **HTML:** Semantic HTML5
- **Commits:** Conventional Commits (feat, fix, docs, style, refactor, test)

---

## 🐛 Reporte de Bugs

Si encuentras un bug, por favor abre un issue con:
- Descripción del problema
- Pasos para reproducir
- Comportamiento esperado
- Screenshots (si aplica)
- Versión del proyecto

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

## 👤 Autor

**Frederick Iribarren**
- GitHub: [@Frederickiribarren](https://github.com/Frederickiribarren)
- Proyecto: [proyectoGestorDeGastos](https://github.com/Frederickiribarren/proyectoGestorDeGastos)

---

## 🙏 Agradecimientos

- Spring Boot Team por el excelente framework
- Bootstrap Team por los componentes de UI
- Comunidad de desarrolladores de Spring
- Iconos por Bootstrap Icons

---

## 📞 Soporte

Si tienes preguntas o necesitas ayuda:
- 📧 Abre un issue en GitHub
- 💬 Revisa la documentación en el README
- 🔍 Consulta el código fuente (bien comentado)

---

**⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub!**

---

*Última actualización: Noviembre 2024*
