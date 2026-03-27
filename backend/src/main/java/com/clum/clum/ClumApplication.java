package com.clum.clum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación CLUM.
 * Los datos iniciales (roles base) se gestionan en las migraciones
 * Flyway (V1__init_schema.sql), no aquí, para evitar errores
 * de clave duplicada al reiniciar la aplicación.
 */
@SpringBootApplication
public class ClumApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClumApplication.class, args);
    }
}
