package com.example.petsalud.service.catalogo.impl;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Especialidad;
import com.example.petsalud.repository.catalogo.EspecialidadRepository;
import com.example.petsalud.service.catalogo.EspecialidadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadServiceImpl(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Especialidad> findAllActivas() {
        return especialidadRepository.findAllActivas();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Especialidad> search(String q, Boolean activo, int page, int pageSize,
                                      String sortBy, String sortDir) {
        return especialidadRepository.search(q, activo, page, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public Especialidad findById(Integer id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Especialidad no encontrada con id: " + id));
    }

    @Override
    public void save(Especialidad especialidad) {
        especialidadRepository.save(especialidad);
    }

    @Override
    public void toggleActivo(Integer id) {
        Especialidad especialidad = findById(id);
        especialidad.setActivo(!especialidad.isActivo());
        especialidadRepository.save(especialidad);
    }
}
