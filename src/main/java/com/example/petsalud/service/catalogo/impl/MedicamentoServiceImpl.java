package com.example.petsalud.service.catalogo.impl;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Medicamento;
import com.example.petsalud.repository.catalogo.MedicamentoRepository;
import com.example.petsalud.service.catalogo.MedicamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class MedicamentoServiceImpl implements MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoServiceImpl(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> findAllActivos() {
        return medicamentoRepository.findAllActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Medicamento> search(String q, Boolean activo,
                                     int page, int pageSize,
                                     String sortBy, String sortDir) {
        return medicamentoRepository.search(q, activo, page, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public Medicamento findById(Integer id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicamento no encontrado con id: " + id));
    }

    @Override
    public void save(Medicamento medicamento) {
        medicamentoRepository.save(medicamento);
    }

    @Override
    public void toggleActivo(Integer id) {
        Medicamento medicamento = findById(id);
        medicamento.setActivo(!medicamento.isActivo());
        medicamentoRepository.save(medicamento);
    }
}
