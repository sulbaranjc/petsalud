package com.example.petsalud.model;

import java.time.LocalDateTime;

public class DashboardConsultaRow {

    private int id;
    private String nombreMascota;
    private String nombreEspecie;
    private String nombrePropietario;
    private String nombreVeterinario;
    private LocalDateTime fechaHora;
    private String diagnostico;

    public DashboardConsultaRow() {}

    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }

    public String getNombreMascota()             { return nombreMascota; }
    public void setNombreMascota(String v)       { this.nombreMascota = v; }

    public String getNombreEspecie()             { return nombreEspecie; }
    public void setNombreEspecie(String v)       { this.nombreEspecie = v; }

    public String getNombrePropietario()         { return nombrePropietario; }
    public void setNombrePropietario(String v)   { this.nombrePropietario = v; }

    public String getNombreVeterinario()         { return nombreVeterinario; }
    public void setNombreVeterinario(String v)   { this.nombreVeterinario = v; }

    public LocalDateTime getFechaHora()          { return fechaHora; }
    public void setFechaHora(LocalDateTime v)    { this.fechaHora = v; }

    public String getDiagnostico()               { return diagnostico; }
    public void setDiagnostico(String v)         { this.diagnostico = v; }
}
