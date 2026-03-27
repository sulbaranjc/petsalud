package com.example.petsalud.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Veterinario {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    @NotBlank(message = "La matrícula profesional es obligatoria")
    @Size(max = 50, message = "La matrícula no puede superar los 50 caracteres")
    private String matricula;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;

    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    /** FK nullable: NULL indica veterinario general sin especialidad asignada. */
    private Integer idEspecialidad;

    private boolean activo = true;

    /** Nombre de la especialidad; se popula con LEFT JOIN, no se persiste. */
    private String nombreEspecialidad;

    public Veterinario() {}

    /** Devuelve "Apellido, Nombre" para ordenar y mostrar de forma natural. */
    public String getNombreCompleto() {
        return apellido + ", " + nombre;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(Integer idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getNombreEspecialidad() { return nombreEspecialidad; }
    public void setNombreEspecialidad(String nombreEspecialidad) { this.nombreEspecialidad = nombreEspecialidad; }
}
