package com.example.petsalud.model.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Vacuna {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 100, message = "El laboratorio no puede superar los 100 caracteres")
    private String laboratorio;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    private boolean activo = true;

    public Vacuna() {}

    public Integer getId()           { return id; }
    public void setId(Integer id)    { this.id = id; }

    public String getNombre()              { return nombre; }
    public void setNombre(String nombre)   { this.nombre = nombre; }

    public String getLaboratorio()                   { return laboratorio; }
    public void setLaboratorio(String laboratorio)   { this.laboratorio = laboratorio; }

    public String getDescripcion()                 { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo()             { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
