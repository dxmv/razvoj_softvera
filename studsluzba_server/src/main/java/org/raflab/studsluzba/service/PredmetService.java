package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.Predmet;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.raflab.studsluzba.repositories.StudProgramRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredmetService {

    private final PredmetRepository repository;


    public Predmet create(Predmet entity) {
        return repository.save(entity);
    }

    public Predmet findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Predmet not found: " + id));
    }

    public Predmet update(Long id, Predmet entity) {
        Predmet existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Predmet not found: " + id);
        }
        repository.deleteById(id);
    }
    public Page<PredmetDto> findPredmetiByStudijskiProgram(Long studProgramId, Pageable pageable) {
        return repository
                .findByStudijskiProgramId(studProgramId, pageable)
                .map(EntityMapper::toDto);
    }
    public Page<PredmetDto> getAllPredmeti(Pageable pageable) {
        return repository.findAll(pageable).map(EntityMapper::toDto);
    }
}
