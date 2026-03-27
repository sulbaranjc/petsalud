package com.example.petsalud.service.catalogo.impl;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.catalogo.Especie;
import com.example.petsalud.repository.catalogo.EspecieRepository;
import com.example.petsalud.service.catalogo.EspecieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class EspecieServiceImpl implements EspecieService {

    private final EspecieRepository especieRepository;

    public EspecieServiceImpl(EspecieRepository especieRepository) {
        this.especieRepository = especieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Especie> findAll() {
        return especieRepository.findAllByOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Especie> search(String q, Boolean activo, int page, int pageSize,
                                 String sortBy, String sortDir) {
        return especieRepository.search(q, activo, page, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public Especie findById(Integer id) {
        return especieRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Especie no encontrada con id: " + id));
    }

    @Override
    public void save(Especie especie) {
        especieRepository.save(especie);
    }

    @Override
    public void toggleActivo(Integer id) {
        Especie especie = findById(id);
        especie.setActivo(!especie.isActivo());
        especieRepository.save(especie);
    }
}
