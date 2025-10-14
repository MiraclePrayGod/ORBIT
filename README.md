# Gestión de Pedidos - App Android

Una aplicación móvil para la gestión de pedidos desarrollada con **Jetpack Compose** y **Kotlin**, recreada con un diseño moderno estilo Apple.

## 📱 Características

- **Pantalla de Inicio**: Dashboard con resumen de pedidos y ventas
- **Tarjetas de Resumen**: Pedidos de hoy y venta total
- **Navegación Rápida**: Acceso directo a Pedidos, Inventario, Mapa y Reportes
- **Selector de Fechas**: Navegación por días de la semana
- **Lista de Pedidos**: Vista de pedidos recientes con detalles completos
- **Diseño Moderno**: Interfaz limpia con esquinas redondeadas y colores suaves

## 🛠️ Tecnologías Utilizadas

- **Kotlin** 1.9.20
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Componentes de diseño
- **OkHttp3** - Comunicación HTTP
- **Kotlin Coroutines** - Programación asíncrona
- **Gradle** 8.13 - Sistema de construcción
- **Java 17** - Compatibilidad

## 📦 Estructura del Proyecto

```
app/src/main/java/com/pedidosapp/
├── MainActivity.kt                 # Actividad principal
├── data/
│   └── Order.kt                   # Modelos de datos
└── ui/
    ├── components/
    │   ├── OrderCard.kt           # Tarjeta de pedido
    │   ├── SummaryCard.kt         # Tarjeta de resumen
    │   ├── QuickActionButton.kt   # Botón de acción rápida
    │   └── DateSelector.kt        # Selector de fechas
    └── theme/
        ├── Color.kt               # Paleta de colores
        ├── Theme.kt               # Tema de la aplicación
        └── Type.kt                # Tipografía
```

## 🎨 Diseño

La aplicación sigue un diseño **estilo Apple** con:
- Esquinas redondeadas en todos los elementos
- Espaciado generoso y tipografía clara
- Colores suaves y minimalistas
- Iconografía simple y reconocible
- Jerarquía visual clara

## 🚀 Cómo Ejecutar

1. Abre el proyecto en **Android Studio**
2. Sincroniza las dependencias de Gradle
3. Ejecuta la aplicación en un emulador o dispositivo físico

## 📋 Funcionalidades Implementadas

### Pantalla Principal
- ✅ Header con título y botones de configuración/agregar
- ✅ Tarjetas de resumen (Pedidos de hoy, Venta Total)
- ✅ Botones de navegación rápida (4 opciones)
- ✅ Selector de fechas horizontal
- ✅ Lista de pedidos recientes con scroll horizontal

### Componentes UI
- ✅ Tarjetas con elevación y esquinas redondeadas
- ✅ Estados de pedidos (En Proceso, Pagado, etc.)
- ✅ Métodos de pago (Efectivo, Por Partes, etc.)
- ✅ Iconos Material Design
- ✅ Colores consistentes con el diseño original

## 🔄 Próximas Mejoras

- [ ] Implementar navegación entre pantallas
- [ ] Agregar funcionalidad de red con OkHttp3
- [ ] Implementar base de datos local
- [ ] Agregar animaciones y transiciones
- [ ] Implementar modo oscuro
- [ ] Agregar notificaciones push

## 📱 Capturas de Pantalla

La aplicación recrea fielmente el diseño original mostrado en la captura de pantalla, incluyendo:
- Layout exacto de elementos
- Colores y tipografías
- Espaciado y proporciones
- Estados de los componentes

---

**Desarrollado con ❤️ usando Jetpack Compose y Kotlin**
