package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.StudentPredmet;
import org.raflab.studsluzba.repositories.StudentPredmetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudentPredmetService {

    private final StudentPredmetRepository repository;

    public StudentPredmet create(StudentPredmet entity) {
        return repository.save(entity);
    }

    public StudentPredmet findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "StudentPredmet not found: " + id));
    }

    public StudentPredmet update(Long id, StudentPredmet entity) {
        StudentPredmet existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "StudentPredmet not found: " + id);
        }
        repository.deleteById(id);
    }
}
