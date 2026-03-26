package com.example.petsalud.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Mascota {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    private LocalDate fechaNacimiento;

    @NotBlank(message = "El sexo es obligatorio")
    private String sexo = "Desconocido";

    @Size(max = 80, message = "El color no puede superar los 80 caracteres")
    private String color;

    @NotNull(message = "La especie es obligatoria")
    private Integer idEspecie;

    private Integer idRaza;

    @NotNull(message = "El propietario es obligatorio")
    private Integer idPropietario;

    @Size(max = 500)
    private String fotoUrl;

    private boolean activo = true;

    private LocalDateTime createdAt;

    // ── Campos de solo lectura (populados desde JOINs) ────────────────────────

    private String nombreEspecie;
    private String nombreRaza;
    private String nombrePropietario;

    public Mascota() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getIdEspecie() { return idEspecie; }
    public void setIdEspecie(Integer idEspecie) { this.idEspecie = idEspecie; }

    public Integer getIdRaza() { return idRaza; }
    public void setIdRaza(Integer idRaza) { this.idRaza = idRaza; }

    public Integer getIdPropietario() { return idPropietario; }
    public void setIdPropietario(Integer idPropietario) { this.idPropietario = idPropietario; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getNombreEspecie() { return nombreEspecie; }
    public void setNombreEspecie(String nombreEspecie) { this.nombreEspecie = nombreEspecie; }

    public String getNombreRaza() { return nombreRaza; }
    public void setNombreRaza(String nombreRaza) { this.nombreRaza = nombreRaza; }

    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }
}
