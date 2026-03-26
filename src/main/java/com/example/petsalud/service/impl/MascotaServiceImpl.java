package com.example.petsalud.service.impl;

import com.example.petsalud.model.Mascota;
import com.example.petsalud.repository.MascotaRepository;
import com.example.petsalud.service.MascotaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mascota> search(String q, Integer idEspecie, String sexo, Boolean activo) {
        return mascotaRepository.search(q, idEspecie, sexo, activo);
    }

    @Override
    @Transactional(readOnly = true)
    public Mascota findById(Integer id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mascota no encontrada con id: " + id));
    }

    @Override
    public void save(Mascota mascota) {
        mascotaRepository.save(mascota);
    }

    @Override
    public void toggleActivo(Integer id) {
        Mascota mascota = findById(id);
        mascota.setActivo(!mascota.isActivo());
        mascotaRepository.save(mascota);
    }
}
