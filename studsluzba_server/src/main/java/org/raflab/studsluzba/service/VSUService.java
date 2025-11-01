package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.VSU;
import org.raflab.studsluzba.repositories.VSURepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class VSUService {

    private final VSURepository repository;

    public VSU create(VSU entity) {
        return repository.save(entity);
    }

    public VSU findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "VSU not found: " + id));
    }

    public VSU update(Long id, VSU entity) {
        VSU existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "VSU not found: " + id);
        }
        repository.deleteById(id);
    }
}
