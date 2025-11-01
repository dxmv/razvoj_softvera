package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.PredispitnaObaveza;
import org.raflab.studsluzba.repositories.PredispitnaObavezaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PredispitnaObavezaService {

    private final PredispitnaObavezaRepository repository;

    public PredispitnaObaveza create(PredispitnaObaveza entity) {
        return repository.save(entity);
    }

    public PredispitnaObaveza findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PredispitnaObaveza not found: " + id));
    }

    public PredispitnaObaveza update(Long id, PredispitnaObaveza entity) {
        PredispitnaObaveza existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PredispitnaObaveza not found: " + id);
        }
        repository.deleteById(id);
    }
}
