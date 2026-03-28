package com.example.petsalud.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Consulta {

    private Integer id;
    private Integer idCita;
    private LocalDateTime fechaHora;
    private BigDecimal pesoKg;
    private BigDecimal temperaturaC;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaResp;
    private String anamnesis;
    private String examenFisico;
    private String diagnostico;
    private String observaciones;

    // ── Campos de solo lectura (populados desde JOINs) ────────────────────────

    private String nombreMascota;
    private String nombreEspecie;
    private String nombrePropietario;
    private String nombreVeterinario;
    private LocalDateTime fechaHoraCita;
    private String fotoUrlMascota;

    public Consulta() {}

    public Integer getId()                          { return id; }
    public void setId(Integer id)                   { this.id = id; }

    public Integer getIdCita()                      { return idCita; }
    public void setIdCita(Integer idCita)           { this.idCita = idCita; }

    public LocalDateTime getFechaHora()                    { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora)      { this.fechaHora = fechaHora; }

    public BigDecimal getPesoKg()                   { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg)        { this.pesoKg = pesoKg; }

    public BigDecimal getTemperaturaC()                    { return temperaturaC; }
    public void setTemperaturaC(BigDecimal temperaturaC)   { this.temperaturaC = temperaturaC; }

    public Integer getFrecuenciaCardiaca()                    { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }

    public Integer getFrecuenciaResp()                    { return frecuenciaResp; }
    public void setFrecuenciaResp(Integer frecuenciaResp) { this.frecuenciaResp = frecuenciaResp; }

    public String getAnamnesis()                    { return anamnesis; }
    public void setAnamnesis(String anamnesis)      { this.anamnesis = anamnesis; }

    public String getExamenFisico()                 { return examenFisico; }
    public void setExamenFisico(String examenFisico){ this.examenFisico = examenFisico; }

    public String getDiagnostico()                  { return diagnostico; }
    public void setDiagnostico(String diagnostico)  { this.diagnostico = diagnostico; }

    public String getObservaciones()                    { return observaciones; }
    public void setObservaciones(String observaciones)  { this.observaciones = observaciones; }

    public String getNombreMascota()                         { return nombreMascota; }
    public void setNombreMascota(String nombreMascota)       { this.nombreMascota = nombreMascota; }

    public String getNombreEspecie()                         { return nombreEspecie; }
    public void setNombreEspecie(String nombreEspecie)       { this.nombreEspecie = nombreEspecie; }

    public String getNombrePropietario()                         { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario)   { this.nombrePropietario = nombrePropietario; }

    public String getNombreVeterinario()                         { return nombreVeterinario; }
    public void setNombreVeterinario(String nombreVeterinario)   { this.nombreVeterinario = nombreVeterinario; }

    public LocalDateTime getFechaHoraCita()                      { return fechaHoraCita; }
    public void setFechaHoraCita(LocalDateTime fechaHoraCita)    { this.fechaHoraCita = fechaHoraCita; }

    public String getFotoUrlMascota()                            { return fotoUrlMascota; }
    public void setFotoUrlMascota(String fotoUrlMascota)         { this.fotoUrlMascota = fotoUrlMascota; }
}
