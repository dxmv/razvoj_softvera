package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.NastavnikZvanje;
import org.raflab.studsluzba.repositories.NastavnikZvanjeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NastavnikZvanjeService {

    private final NastavnikZvanjeRepository repository;

    public NastavnikZvanje create(NastavnikZvanje entity) {
        return repository.save(entity);
    }

    public NastavnikZvanje findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NastavnikZvanje not found: " + id));
    }

    public NastavnikZvanje update(Long id, NastavnikZvanje entity) {
        NastavnikZvanje existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NastavnikZvanje not found: " + id);
        }
        repository.deleteById(id);
    }
}
