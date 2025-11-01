package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.PrijavaIspita;
import org.raflab.studsluzba.repositories.PrijavaIspitaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PrijavaIspitaService {

    private final PrijavaIspitaRepository repository;

    public PrijavaIspita create(PrijavaIspita entity) {
        return repository.save(entity);
    }

    public PrijavaIspita findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PrijavaIspita not found: " + id));
    }

    public PrijavaIspita update(Long id, PrijavaIspita entity) {
        PrijavaIspita existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PrijavaIspita not found: " + id);
        }
        repository.deleteById(id);
    }
}
