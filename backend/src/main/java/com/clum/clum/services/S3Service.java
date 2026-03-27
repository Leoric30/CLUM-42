package com.clum.clum.services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * Servicio para subida de archivos a AWS S3.
 * Centraliza toda la lógica de comunicación con S3: autenticación,
 * construcción del cliente, subida del archivo y retorno de la URL pública.
 *
 * El S3Client se crea UNA sola vez al arrancar el contexto de Spring
 * (@PostConstruct), no en cada llamada a uploadFile. Esto evita:
 *   - Abrir una nueva conexión TCP a AWS por cada subida (~200-500ms extra).
 *   - Crear y desechar objetos de conexión HTTP innecesariamente.
 * S3Client es thread-safe y está diseñado para usarse como singleton.
 */
@Service
public class S3Service {

        @Value("${aws.bucket-name}")
        private String bucket;

        @Value("${aws.region}")
        private String region;

        @Value("${aws.access-key}")
        private String accessKey;

        @Value("${aws.secret-key}")
        private String secretKey;

        private S3Client s3Client;

        /**
         * Se ejecuta una sola vez después de que Spring inyecta los @Value.
         * Construye el cliente S3 que se reutilizará en todas las subidas.
         *
         * No se puede hacer en el constructor porque los @Value aún no están
         * disponibles en ese momento — @PostConstruct garantiza que sí lo están.
         */
        @PostConstruct
        public void init() {
                this.s3Client = S3Client.builder()
                                .region(Region.of(region))
                                .credentialsProvider(StaticCredentialsProvider.create(
                                                AwsBasicCredentials.create(accessKey, secretKey)))
                                .build();
        }

        /**
         * Sube un archivo al bucket de S3 y retorna su URL pública.
         *
         * El nombre del archivo en S3 incluye un timestamp para garantizar unicidad
         * y evitar sobreescribir archivos con el mismo nombre.
         *
         * @param file Archivo temporal en disco a subir.
         * @return URL pública del archivo en S3 (formato:
         *         https://bucket.s3.region.amazonaws.com/key).
         */
        public String uploadFile(File file) {
                // 1. Nombre único en S3: timestamp + nombre original (evita duplicados)
                String fileName = System.currentTimeMillis() + "_" + file.getName();

                // 2. Construir la solicitud de subida
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(fileName)
                                .build();

                try {
                        // 3. Subir el archivo al bucket usando el cliente singleton
                        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
                } finally {
                        // 4. Eliminar el archivo temporal del servidor para evitar fuga de disco
                        if (file.exists()) {
                                file.delete();
                        }
                }

                // 5. Retornar la URL pública para guardar en la base de datos
                return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
        }
}
