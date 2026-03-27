package com.example.petsalud.service.impl;

import com.example.petsalud.model.Page;
import com.example.petsalud.model.Veterinario;
import com.example.petsalud.repository.VeterinarioRepository;
import com.example.petsalud.service.VeterinarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioServiceImpl(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Veterinario> findAllActivos() {
        return veterinarioRepository.findAllActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Veterinario> search(String q, Integer idEspecialidad, Boolean activo,
                                     int page, int pageSize, String sortBy, String sortDir) {
        return veterinarioRepository.search(q, idEspecialidad, activo, page, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public Veterinario findById(Integer id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Veterinario no encontrado con id: " + id));
    }

    @Override
    public void save(Veterinario veterinario) {
        veterinarioRepository.save(veterinario);
    }

    @Override
    public void toggleActivo(Integer id) {
        Veterinario veterinario = findById(id);
        veterinario.setActivo(!veterinario.isActivo());
        veterinarioRepository.save(veterinario);
    }
}
