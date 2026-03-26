package com.example.petsalud.model.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Raza {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "La especie es obligatoria")
    private Integer idEspecie;

    private boolean activo = true;

    /** Campo de solo lectura, poblado desde JOIN con especie. */
    private String nombreEspecie;

    public Raza() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getIdEspecie() { return idEspecie; }
    public void setIdEspecie(Integer idEspecie) { this.idEspecie = idEspecie; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getNombreEspecie() { return nombreEspecie; }
    public void setNombreEspecie(String nombreEspecie) { this.nombreEspecie = nombreEspecie; }
}
