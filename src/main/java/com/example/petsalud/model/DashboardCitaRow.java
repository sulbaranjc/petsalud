package com.example.petsalud.model;

import java.time.LocalDateTime;

public class DashboardCitaRow {

    private int id;
    private Integer idConsulta;
    private String nombreMascota;
    private String nombreEspecie;
    private String nombrePropietario;
    private LocalDateTime fechaHora;
    private String nombreEstadoCita;

    public DashboardCitaRow() {}

    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }

    public Integer getIdConsulta()               { return idConsulta; }
    public void setIdConsulta(Integer v)         { this.idConsulta = v; }

    public String getNombreMascota()             { return nombreMascota; }
    public void setNombreMascota(String v)       { this.nombreMascota = v; }

    public String getNombreEspecie()             { return nombreEspecie; }
    public void setNombreEspecie(String v)       { this.nombreEspecie = v; }

    public String getNombrePropietario()         { return nombrePropietario; }
    public void setNombrePropietario(String v)   { this.nombrePropietario = v; }

    public LocalDateTime getFechaHora()          { return fechaHora; }
    public void setFechaHora(LocalDateTime v)    { this.fechaHora = v; }

    public String getNombreEstadoCita()          { return nombreEstadoCita; }
    public void setNombreEstadoCita(String v)    { this.nombreEstadoCita = v; }

    private String fotoUrlMascota;
    public String getFotoUrlMascota()            { return fotoUrlMascota; }
    public void setFotoUrlMascota(String v)      { this.fotoUrlMascota = v; }
}
