# Rapientrega API

API backend desarrollada en **Spring Boot** para un sistema de entregas a domicilio estilo delivery (similar a plataformas de pedidos de comida y productos). Permite gestionar clientes, restaurantes, productos, pedidos, repartidores, pagos, facturación y calificaciones.

---

## 🚀 Características Principales

* **Gestión de Clientes:** Administración de usuarios y perfiles que realizan los pedidos (`ClienteController`, `ClienteService`).
* **Gestión de Restaurantes y Productos:** Control de establecimientos afiliados, su menú y artículos disponibles (`RestauranteController`, `ProductoController`, etc.).
* **Gestión de Pedidos:** Creación, seguimiento, detalles de pedidos y tareas automatizadas o programadas (`PedidoController`, `PedidoService`, `PedidoSchedulerService`).
* **Pagos y Pasarela:** Procesamiento de pagos, integración con pasarelas y control de estados financieros (`PagoController`, `PasarelaPagosService`).
* **Facturación:** Emisión y control de facturas asociadas a las transacciones (`FacturaController`, `FacturacionService`).
* **Repartidores y Calificaciones:** Administración de la flota de repartidores y el sistema de valoraciones/reseñas de los servicios (`RepartidorController`, `CalificacionController`).

---

## 🛠️ Tecnologías Utilizadas

* **Java** (versión compatible con Spring Boot)
* **Spring Boot**
* **Maven** (incluye Maven Wrapper `mvnw`)

---

## ⚙️ Requisitos Previos

Antes de ejecutar el proyecto de forma local, asegúrate de tener instalado:

1. **Java JDK** (versión 17 o superior recomendada).
2. Un sistema de gestión de base de datos configurado según los parámetros establecidos en el archivo de propiedades del proyecto.

---

## 📥 Instrucciones de Ejecución Local

1. **Clona o descarga** este repositorio en tu computadora.
2. Abre tu terminal y dirígete al directorio raíz del proyecto (`rapientrega`).
3. Configura las variables de entorno o los parámetros de conexión en el archivo de configuración:
   `src/main/resources/application.properties` (o el archivo `.env` según corresponda).
4. Ejecuta la aplicación utilizando el **Maven Wrapper**:

   * **En Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```

   * **En Windows (CMD / PowerShell):**
     ```cmd
     mvnw.cmd spring-boot:run
     ```

5. La API se iniciará y estará disponible localmente en el puerto predeterminado (por defecto `http://localhost:8080`).
```[cite: 2]
