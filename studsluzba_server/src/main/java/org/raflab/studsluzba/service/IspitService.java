package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Ispit;
import org.raflab.studsluzba.repositories.IspitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class IspitService {

    private final IspitRepository repository;

    public Ispit create(Ispit entity) {
        return repository.save(entity);
    }

    public Ispit findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit not found: " + id));
    }

    public Ispit update(Long id, Ispit entity) {
        Ispit existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit not found: " + id);
        }
        repository.deleteById(id);
    }
}
