package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Nastavnik;
import org.raflab.studsluzba.repositories.NastavnikRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NastavnikService {

    private final NastavnikRepository repository;

    public Nastavnik create(Nastavnik entity) {
        return repository.save(entity);
    }

    public Nastavnik findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nastavnik not found: " + id));
    }

    public Nastavnik update(Long id, Nastavnik entity) {
        Nastavnik existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nastavnik not found: " + id);
        }
        repository.deleteById(id);
    }
}
