package com.example.petsalud.model;

import java.time.LocalDate;

public class ConsultaVacuna {

    private Integer id;
    private Integer idConsulta;
    private Integer idVacuna;
    private LocalDate proximaDosis;
    private String lote;
    private String observaciones;

    // ── Campo de solo lectura (populado desde JOIN) ───────────────────────────
    private String nombreVacuna;

    public ConsultaVacuna() {}

    public Integer getId()                          { return id; }
    public void setId(Integer id)                   { this.id = id; }

    public Integer getIdConsulta()                  { return idConsulta; }
    public void setIdConsulta(Integer idConsulta)   { this.idConsulta = idConsulta; }

    public Integer getIdVacuna()                    { return idVacuna; }
    public void setIdVacuna(Integer idVacuna)       { this.idVacuna = idVacuna; }

    public LocalDate getProximaDosis()                      { return proximaDosis; }
    public void setProximaDosis(LocalDate proximaDosis)     { this.proximaDosis = proximaDosis; }

    public String getLote()                         { return lote; }
    public void setLote(String lote)                { this.lote = lote; }

    public String getObservaciones()                    { return observaciones; }
    public void setObservaciones(String observaciones)  { this.observaciones = observaciones; }

    public String getNombreVacuna()                      { return nombreVacuna; }
    public void setNombreVacuna(String nombreVacuna)     { this.nombreVacuna = nombreVacuna; }
}
