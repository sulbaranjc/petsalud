package com.example.petsalud.model.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Especialidad {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    private boolean activo = true;

    /** Calculado vía subquery COUNT; no se persiste. */
    private int totalVeterinarios;

    public Especialidad() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public int getTotalVeterinarios() { return totalVeterinarios; }
    public void setTotalVeterinarios(int totalVeterinarios) { this.totalVeterinarios = totalVeterinarios; }
}
