package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.UpisGodine;
import org.raflab.studsluzba.repositories.UpisGodineRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UpisGodineService {

    private final UpisGodineRepository repository;

    public UpisGodine create(UpisGodine entity) {
        return repository.save(entity);
    }

    public UpisGodine findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UpisGodine not found: " + id));
    }

    public UpisGodine update(Long id, UpisGodine entity) {
        UpisGodine existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UpisGodine not found: " + id);
        }
        repository.deleteById(id);
    }
}
