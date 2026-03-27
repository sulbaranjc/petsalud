package com.example.petsalud.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class Cita {

    private Integer id;

    @NotNull(message = "La mascota es obligatoria")
    private Integer idMascota;

    @NotNull(message = "El veterinario es obligatorio")
    private Integer idVeterinario;

    @NotNull(message = "El estado es obligatorio")
    private Integer idEstadoCita;

    @NotNull(message = "La fecha y hora son obligatorias")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar los 255 caracteres")
    private String motivo;

    private String observaciones;

    private LocalDateTime createdAt;

    // ── Campos de solo lectura (populados desde JOINs) ────────────────────────

    /** ID de la consulta asociada; null si la cita aún no fue atendida. */
    private Integer idConsulta;

    private String nombreMascota;
    private String nombreEspecie;
    private String nombrePropietario;
    private String nombreVeterinario;
    private String nombreEstadoCita;

    public Cita() {}

    public Integer getId()                         { return id; }
    public void setId(Integer id)                  { this.id = id; }

    public Integer getIdMascota()                      { return idMascota; }
    public void setIdMascota(Integer idMascota)        { this.idMascota = idMascota; }

    public Integer getIdVeterinario()                  { return idVeterinario; }
    public void setIdVeterinario(Integer idVeterinario){ this.idVeterinario = idVeterinario; }

    public Integer getIdEstadoCita()                   { return idEstadoCita; }
    public void setIdEstadoCita(Integer idEstadoCita)  { this.idEstadoCita = idEstadoCita; }

    public LocalDateTime getFechaHora()                { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora)  { this.fechaHora = fechaHora; }

    public String getMotivo()                          { return motivo; }
    public void setMotivo(String motivo)               { this.motivo = motivo; }

    public String getObservaciones()                   { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getIdConsulta()                         { return idConsulta; }
    public void setIdConsulta(Integer idConsulta)          { this.idConsulta = idConsulta; }

    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }

    public String getNombreMascota()                         { return nombreMascota; }
    public void setNombreMascota(String nombreMascota)       { this.nombreMascota = nombreMascota; }

    public String getNombreEspecie()                         { return nombreEspecie; }
    public void setNombreEspecie(String nombreEspecie)       { this.nombreEspecie = nombreEspecie; }

    public String getNombrePropietario()                         { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario)   { this.nombrePropietario = nombrePropietario; }

    public String getNombreVeterinario()                         { return nombreVeterinario; }
    public void setNombreVeterinario(String nombreVeterinario)   { this.nombreVeterinario = nombreVeterinario; }

    public String getNombreEstadoCita()                          { return nombreEstadoCita; }
    public void setNombreEstadoCita(String nombreEstadoCita)     { this.nombreEstadoCita = nombreEstadoCita; }
}
