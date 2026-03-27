package com.example.petsalud.model;

import java.time.LocalDate;

public class Tratamiento {

    private Integer id;
    private Integer idConsulta;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observaciones;

    public Tratamiento() {}

    public Integer getId()                          { return id; }
    public void setId(Integer id)                   { this.id = id; }

    public Integer getIdConsulta()                  { return idConsulta; }
    public void setIdConsulta(Integer idConsulta)   { this.idConsulta = idConsulta; }

    public String getDescripcion()                  { return descripcion; }
    public void setDescripcion(String descripcion)  { this.descripcion = descripcion; }

    public LocalDate getFechaInicio()                   { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio)   { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin()                  { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin)     { this.fechaFin = fechaFin; }

    public String getObservaciones()                    { return observaciones; }
    public void setObservaciones(String observaciones)  { this.observaciones = observaciones; }
}
