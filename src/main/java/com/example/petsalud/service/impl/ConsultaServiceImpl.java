package com.example.petsalud.service.impl;

import com.example.petsalud.model.*;
import com.example.petsalud.repository.*;
import com.example.petsalud.service.ConsultaService;
import com.example.petsalud.service.catalogo.EstadoCitaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class ConsultaServiceImpl implements ConsultaService {

    private final ConsultaRepository            consultaRepo;
    private final TratamientoRepository         tratamientoRepo;
    private final ConsultaMedicamentoRepository medicamentoRepo;
    private final ConsultaVacunaRepository      vacunaRepo;
    private final EstadoCitaService             estadoCitaService;

    public ConsultaServiceImpl(ConsultaRepository consultaRepo,
                                TratamientoRepository tratamientoRepo,
                                ConsultaMedicamentoRepository medicamentoRepo,
                                ConsultaVacunaRepository vacunaRepo,
                                EstadoCitaService estadoCitaService) {
        this.consultaRepo     = consultaRepo;
        this.tratamientoRepo  = tratamientoRepo;
        this.medicamentoRepo  = medicamentoRepo;
        this.vacunaRepo       = vacunaRepo;
        this.estadoCitaService = estadoCitaService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Consulta> search(String q, Integer idVeterinario,
                                  int page, int pageSize,
                                  String sortBy, String sortDir) {
        return consultaRepo.search(q, idVeterinario, page, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public Consulta findById(Integer id) {
        return consultaRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Consulta no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Consulta> findByIdCita(Integer idCita) {
        return consultaRepo.findByIdCita(idCita);
    }

    @Override
    public void guardar(ConsultaForm form) {
        // 1. Construir y guardar la consulta; obtener el ID generado
        Consulta consulta = new Consulta();
        consulta.setIdCita(form.getIdCita());
        consulta.setPesoKg(form.getPesoKg());
        consulta.setTemperaturaC(form.getTemperaturaC());
        consulta.setFrecuenciaCardiaca(form.getFrecuenciaCardiaca());
        consulta.setFrecuenciaResp(form.getFrecuenciaResp());
        consulta.setAnamnesis(form.getAnamnesis());
        consulta.setExamenFisico(form.getExamenFisico());
        consulta.setDiagnostico(form.getDiagnostico());
        consulta.setObservaciones(form.getObservaciones());

        Integer idConsulta = consultaRepo.insert(consulta);

        // 2. Tratamientos (filas con descripción no vacía)
        for (ConsultaForm.TratamientoInput ti : form.getTratamientos()) {
            if (ti.getDescripcion() == null || ti.getDescripcion().isBlank()) continue;
            Tratamiento t = new Tratamiento();
            t.setIdConsulta(idConsulta);
            t.setDescripcion(ti.getDescripcion().trim());
            t.setObservaciones(ti.getObservaciones());
            if (ti.getFechaInicio() != null && !ti.getFechaInicio().isBlank())
                t.setFechaInicio(LocalDate.parse(ti.getFechaInicio()));
            if (ti.getFechaFin() != null && !ti.getFechaFin().isBlank())
                t.setFechaFin(LocalDate.parse(ti.getFechaFin()));
            tratamientoRepo.insert(t);
        }

        // 3. Medicamentos administrados (filas con medicamento seleccionado)
        for (ConsultaForm.ConsultaMedInput mi : form.getMedicamentos()) {
            if (mi.getIdMedicamento() == null) continue;
            ConsultaMedicamento cm = new ConsultaMedicamento();
            cm.setIdConsulta(idConsulta);
            cm.setIdMedicamento(mi.getIdMedicamento());
            cm.setDosis(mi.getDosis());
            cm.setFrecuencia(mi.getFrecuencia());
            cm.setObservaciones(mi.getObservaciones());
            medicamentoRepo.insert(cm);
        }

        // 4. Vacunas aplicadas (filas con vacuna seleccionada)
        for (ConsultaForm.ConsultaVacInput vi : form.getVacunas()) {
            if (vi.getIdVacuna() == null) continue;
            ConsultaVacuna cv = new ConsultaVacuna();
            cv.setIdConsulta(idConsulta);
            cv.setIdVacuna(vi.getIdVacuna());
            cv.setLote(vi.getLote());
            cv.setObservaciones(vi.getObservaciones());
            if (vi.getProximaDosis() != null && !vi.getProximaDosis().isBlank())
                cv.setProximaDosis(LocalDate.parse(vi.getProximaDosis()));
            vacunaRepo.insert(cv);
        }

        // 5. Marcar la cita como Completada
        Integer idCompletada = estadoCitaService.findByNombre("Completada")
                .orElseThrow(() -> new IllegalStateException("Estado 'Completada' no encontrado"))
                .getId();
        consultaRepo.actualizarEstadoCita(form.getIdCita(), idCompletada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tratamiento> findTratamientos(Integer idConsulta) {
        return tratamientoRepo.findByIdConsulta(idConsulta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultaMedicamento> findMedicamentos(Integer idConsulta) {
        return medicamentoRepo.findByIdConsulta(idConsulta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultaVacuna> findVacunas(Integer idConsulta) {
        return vacunaRepo.findByIdConsulta(idConsulta);
    }
}
