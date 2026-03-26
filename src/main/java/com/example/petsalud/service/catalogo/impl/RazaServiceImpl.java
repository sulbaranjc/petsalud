package com.example.petsalud.service.catalogo.impl;

import com.example.petsalud.model.catalogo.Raza;
import com.example.petsalud.repository.catalogo.RazaRepository;
import com.example.petsalud.service.catalogo.RazaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class RazaServiceImpl implements RazaService {

    private final RazaRepository razaRepository;

    public RazaServiceImpl(RazaRepository razaRepository) {
        this.razaRepository = razaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Raza> findAll() {
        return razaRepository.findAllByOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Raza findById(Integer id) {
        return razaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Raza no encontrada con id: " + id));
    }

    @Override
    public void save(Raza raza) {
        razaRepository.save(raza);
    }

    @Override
    public void toggleActivo(Integer id) {
        Raza raza = findById(id);
        raza.setActivo(!raza.isActivo());
        razaRepository.save(raza);
    }
}
