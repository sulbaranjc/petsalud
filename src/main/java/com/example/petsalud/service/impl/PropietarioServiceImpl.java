package com.example.petsalud.service.impl;

import com.example.petsalud.model.Propietario;
import com.example.petsalud.repository.PropietarioRepository;
import com.example.petsalud.service.PropietarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class PropietarioServiceImpl implements PropietarioService {

    private final PropietarioRepository propietarioRepository;

    public PropietarioServiceImpl(PropietarioRepository propietarioRepository) {
        this.propietarioRepository = propietarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Propietario> findAll() {
        return propietarioRepository.findAllByOrderByApellidoNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Propietario findById(Integer id) {
        return propietarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Propietario no encontrado con id: " + id));
    }

    @Override
    public void save(Propietario propietario) {
        propietarioRepository.save(propietario);
    }

    @Override
    public void toggleActivo(Integer id) {
        Propietario propietario = findById(id);
        propietario.setActivo(!propietario.isActivo());
        propietarioRepository.save(propietario);
    }
}
