package com.example.petsalud.model;

public class ConsultaMedicamento {

    private Integer id;
    private Integer idConsulta;
    private Integer idMedicamento;
    private String dosis;
    private String frecuencia;
    private String observaciones;

    // ── Campo de solo lectura (populado desde JOIN) ───────────────────────────
    private String nombreMedicamento;

    public ConsultaMedicamento() {}

    public Integer getId()                          { return id; }
    public void setId(Integer id)                   { this.id = id; }

    public Integer getIdConsulta()                  { return idConsulta; }
    public void setIdConsulta(Integer idConsulta)   { this.idConsulta = idConsulta; }

    public Integer getIdMedicamento()                       { return idMedicamento; }
    public void setIdMedicamento(Integer idMedicamento)     { this.idMedicamento = idMedicamento; }

    public String getDosis()                        { return dosis; }
    public void setDosis(String dosis)              { this.dosis = dosis; }

    public String getFrecuencia()                   { return frecuencia; }
    public void setFrecuencia(String frecuencia)    { this.frecuencia = frecuencia; }

    public String getObservaciones()                    { return observaciones; }
    public void setObservaciones(String observaciones)  { this.observaciones = observaciones; }

    public String getNombreMedicamento()                         { return nombreMedicamento; }
    public void setNombreMedicamento(String nombreMedicamento)   { this.nombreMedicamento = nombreMedicamento; }
}
