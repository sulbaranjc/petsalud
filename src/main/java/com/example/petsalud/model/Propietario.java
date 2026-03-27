package com.example.petsalud.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Propietario {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20)
    private String documento;

    @Size(max = 20)
    private String telefono;

    @Size(max = 150)
    private String email;

    @Size(max = 255)
    private String direccion;

    private boolean activo = true;

    /** Solo lectura — poblado desde subquery COUNT en la lista. */
    private int totalMascotas;

    public Propietario() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public int getTotalMascotas() { return totalMascotas; }
    public void setTotalMascotas(int totalMascotas) { this.totalMascotas = totalMascotas; }

    /** Nombre completo para mostrar en dropdowns y listas. */
    public String getNombreCompleto() {
        return apellido + ", " + nombre;
    }
}
