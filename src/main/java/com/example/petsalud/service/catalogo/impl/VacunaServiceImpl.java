package com.example.petsalud.service.catalogo.impl;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Vacuna;
import com.example.petsalud.repository.catalogo.VacunaRepository;
import com.example.petsalud.service.catalogo.VacunaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class VacunaServiceImpl implements VacunaService {

    private final VacunaRepository vacunaRepository;

    public VacunaServiceImpl(VacunaRepository vacunaRepository) {
        this.vacunaRepository = vacunaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vacuna> findAllActivas() {
        return vacunaRepository.findAllActivas();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Vacuna> search(String q, Boolean activo,
                                int page, int pageSize,
                                String sortBy, String sortDir) {
        return vacunaRepository.search(q, activo, page, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public Vacuna findById(Integer id) {
        return vacunaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vacuna no encontrada con id: " + id));
    }

    @Override
    public void save(Vacuna vacuna) {
        vacunaRepository.save(vacuna);
    }

    @Override
    public void toggleActivo(Integer id) {
        Vacuna vacuna = findById(id);
        vacuna.setActivo(!vacuna.isActivo());
        vacunaRepository.save(vacuna);
    }
}
