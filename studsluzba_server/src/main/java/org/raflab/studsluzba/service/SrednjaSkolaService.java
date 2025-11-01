package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.SrednjaSkola;
import org.raflab.studsluzba.repositories.SrednjasSkolaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SrednjaSkolaService {

    private final SrednjasSkolaRepository repository;

    public SrednjaSkola create(SrednjaSkola entity) {
        return repository.save(entity);
    }

    public SrednjaSkola findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SrednjaSkola not found: " + id));
    }

    public SrednjaSkola update(Long id, SrednjaSkola entity) {
        SrednjaSkola existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SrednjaSkola not found: " + id);
        }
        repository.deleteById(id);
    }
}
