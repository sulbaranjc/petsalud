package com.example.petsalud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Gestiona el almacenamiento de archivos subidos por el usuario.
 * Los archivos se guardan en subdirectorios de {@code app.upload.dir}
 * con nombres únicos generados con UUID para evitar colisiones.
 */
@Service
public class FileStorageService {

    private final Path uploadBaseDir;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        this.uploadBaseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadBaseDir);
    }

    /**
     * Guarda {@code file} dentro de {@code uploadBaseDir/subDir} con un nombre único.
     *
     * @param file   archivo recibido del formulario
     * @param subDir subdirectorio destino, p. ej. "mascotas" o "veterinarios"
     * @return ruta URL relativa para acceder a la imagen, p. ej. "/uploads/mascotas/uuid.jpg"
     */
    public String store(MultipartFile file, String subDir) throws IOException {
        String original = file.getOriginalFilename();
        String extension = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.'))
                : "";
        String uniqueName = UUID.randomUUID().toString() + extension.toLowerCase();

        Path targetDir = uploadBaseDir.resolve(subDir);
        Files.createDirectories(targetDir);
        Files.copy(file.getInputStream(), targetDir.resolve(uniqueName));

        return "/uploads/" + subDir + "/" + uniqueName;
    }
}
