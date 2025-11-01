package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.IzlazakIspit;
import org.raflab.studsluzba.repositories.IzlazakIspitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class IzlazakIspitService {

    private final IzlazakIspitRepository repository;

    public IzlazakIspit create(IzlazakIspit entity) {
        return repository.save(entity);
    }

    public IzlazakIspit findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "IzlazakIspit not found: " + id));
    }

    public IzlazakIspit update(Long id, IzlazakIspit entity) {
        IzlazakIspit existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "IzlazakIspit not found: " + id);
        }
        repository.deleteById(id);
    }
}
