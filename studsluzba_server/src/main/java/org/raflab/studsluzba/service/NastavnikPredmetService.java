package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.NastavnikPredmet;
import org.raflab.studsluzba.repositories.NastavnikPredmetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NastavnikPredmetService {

    private final NastavnikPredmetRepository repository;

    public NastavnikPredmet create(NastavnikPredmet entity) {
        return repository.save(entity);
    }

    public NastavnikPredmet findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NastavnikPredmet not found: " + id));
    }

    public NastavnikPredmet update(Long id, NastavnikPredmet entity) {
        NastavnikPredmet existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NastavnikPredmet not found: " + id);
        }
        repository.deleteById(id);
    }
}
