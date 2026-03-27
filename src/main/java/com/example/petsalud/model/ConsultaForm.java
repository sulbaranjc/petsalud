package com.example.petsalud.model;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Command object que agrupa todos los datos de una consulta médica
 * (datos clínicos + listas dinámicas) para el binding del formulario.
 */
public class ConsultaForm {

    @NotNull(message = "La cita es obligatoria")
    private Integer idCita;

    // Signos vitales (todos opcionales)
    private BigDecimal pesoKg;
    private BigDecimal temperaturaC;
    private Integer    frecuenciaCardiaca;
    private Integer    frecuenciaResp;

    // Narrativa clínica (opcional, pero diagnóstico es importante)
    private String anamnesis;
    private String examenFisico;
    private String diagnostico;
    private String observaciones;

    // Listas dinámicas — Spring MVC las vincula por índice: tratamientos[0].descripcion
    private List<TratamientoInput>      tratamientos     = new ArrayList<>();
    private List<ConsultaMedInput>      medicamentos     = new ArrayList<>();
    private List<ConsultaVacInput>      vacunas          = new ArrayList<>();

    // ── Inner DTOs ────────────────────────────────────────────────────────────

    public static class TratamientoInput {
        private String descripcion;
        private String fechaInicio;   // String para binding simple desde el form (yyyy-MM-dd)
        private String fechaFin;
        private String observaciones;

        public String getDescripcion()                  { return descripcion; }
        public void setDescripcion(String descripcion)  { this.descripcion = descripcion; }
        public String getFechaInicio()                  { return fechaInicio; }
        public void setFechaInicio(String fechaInicio)  { this.fechaInicio = fechaInicio; }
        public String getFechaFin()                     { return fechaFin; }
        public void setFechaFin(String fechaFin)        { this.fechaFin = fechaFin; }
        public String getObservaciones()                    { return observaciones; }
        public void setObservaciones(String observaciones)  { this.observaciones = observaciones; }
    }

    public static class ConsultaMedInput {
        private Integer idMedicamento;
        private String  dosis;
        private String  frecuencia;
        private String  observaciones;

        public Integer getIdMedicamento()                       { return idMedicamento; }
        public void setIdMedicamento(Integer idMedicamento)     { this.idMedicamento = idMedicamento; }
        public String getDosis()                                { return dosis; }
        public void setDosis(String dosis)                      { this.dosis = dosis; }
        public String getFrecuencia()                           { return frecuencia; }
        public void setFrecuencia(String frecuencia)            { this.frecuencia = frecuencia; }
        public String getObservaciones()                        { return observaciones; }
        public void setObservaciones(String obs)                { this.observaciones = obs; }
    }

    public static class ConsultaVacInput {
        private Integer idVacuna;
        private String  proximaDosis;   // yyyy-MM-dd como String
        private String  lote;
        private String  observaciones;

        public Integer getIdVacuna()                    { return idVacuna; }
        public void setIdVacuna(Integer idVacuna)       { this.idVacuna = idVacuna; }
        public String getProximaDosis()                 { return proximaDosis; }
        public void setProximaDosis(String proximaDosis){ this.proximaDosis = proximaDosis; }
        public String getLote()                         { return lote; }
        public void setLote(String lote)                { this.lote = lote; }
        public String getObservaciones()                    { return observaciones; }
        public void setObservaciones(String obs)            { this.observaciones = obs; }
    }

    // ── Getters / Setters principales ─────────────────────────────────────────

    public Integer getIdCita()                      { return idCita; }
    public void setIdCita(Integer idCita)           { this.idCita = idCita; }

    public BigDecimal getPesoKg()                   { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg)        { this.pesoKg = pesoKg; }

    public BigDecimal getTemperaturaC()                    { return temperaturaC; }
    public void setTemperaturaC(BigDecimal temperaturaC)   { this.temperaturaC = temperaturaC; }

    public Integer getFrecuenciaCardiaca()                    { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Integer fc)             { this.frecuenciaCardiaca = fc; }

    public Integer getFrecuenciaResp()                    { return frecuenciaResp; }
    public void setFrecuenciaResp(Integer fr)             { this.frecuenciaResp = fr; }

    public String getAnamnesis()                    { return anamnesis; }
    public void setAnamnesis(String anamnesis)      { this.anamnesis = anamnesis; }

    public String getExamenFisico()                 { return examenFisico; }
    public void setExamenFisico(String ef)          { this.examenFisico = ef; }

    public String getDiagnostico()                  { return diagnostico; }
    public void setDiagnostico(String diagnostico)  { this.diagnostico = diagnostico; }

    public String getObservaciones()                    { return observaciones; }
    public void setObservaciones(String observaciones)  { this.observaciones = observaciones; }

    public List<TratamientoInput> getTratamientos()              { return tratamientos; }
    public void setTratamientos(List<TratamientoInput> t)        { this.tratamientos = t; }

    public List<ConsultaMedInput> getMedicamentos()              { return medicamentos; }
    public void setMedicamentos(List<ConsultaMedInput> m)        { this.medicamentos = m; }

    public List<ConsultaVacInput> getVacunas()                   { return vacunas; }
    public void setVacunas(List<ConsultaVacInput> v)             { this.vacunas = v; }
}
