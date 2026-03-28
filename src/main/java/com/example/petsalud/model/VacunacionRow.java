package com.example.petsalud.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VacunacionRow {

    private int id;
    private int idConsulta;
    private String nombreMascota;
    private String nombreEspecie;
    private String nombrePropietario;
    private String nombreVacuna;
    private String nombreVeterinario;
    private LocalDateTime fechaConsulta;
    private LocalDate proximaDosis;
    private String lote;
    private String observaciones;
    private String fotoUrlMascota;

    public VacunacionRow() {}

    /** "vencida" | "proxima" (≤ 30 días) | "vigente" | "sin_fecha" */
    public String getEstadoProximaDosis() {
        if (proximaDosis == null) return "sin_fecha";
        LocalDate hoy = LocalDate.now();
        if (proximaDosis.isBefore(hoy))                        return "vencida";
        if (!proximaDosis.isAfter(hoy.plusDays(30)))           return "proxima";
        return "vigente";
    }

    public int getId()                                 { return id; }
    public void setId(int id)                          { this.id = id; }

    public int getIdConsulta()                         { return idConsulta; }
    public void setIdConsulta(int idConsulta)          { this.idConsulta = idConsulta; }

    public String getNombreMascota()                   { return nombreMascota; }
    public void setNombreMascota(String v)             { this.nombreMascota = v; }

    public String getNombreEspecie()                   { return nombreEspecie; }
    public void setNombreEspecie(String v)             { this.nombreEspecie = v; }

    public String getNombrePropietario()               { return nombrePropietario; }
    public void setNombrePropietario(String v)         { this.nombrePropietario = v; }

    public String getNombreVacuna()                    { return nombreVacuna; }
    public void setNombreVacuna(String v)              { this.nombreVacuna = v; }

    public String getNombreVeterinario()               { return nombreVeterinario; }
    public void setNombreVeterinario(String v)         { this.nombreVeterinario = v; }

    public LocalDateTime getFechaConsulta()            { return fechaConsulta; }
    public void setFechaConsulta(LocalDateTime v)      { this.fechaConsulta = v; }

    public LocalDate getProximaDosis()                 { return proximaDosis; }
    public void setProximaDosis(LocalDate v)           { this.proximaDosis = v; }

    public String getLote()                            { return lote; }
    public void setLote(String v)                      { this.lote = v; }

    public String getObservaciones()                   { return observaciones; }
    public void setObservaciones(String v)             { this.observaciones = v; }

    public String getFotoUrlMascota()                  { return fotoUrlMascota; }
    public void setFotoUrlMascota(String v)            { this.fotoUrlMascota = v; }
}
