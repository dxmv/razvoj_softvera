package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.StudProgram;
import org.raflab.studsluzba.repositories.StudProgramRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudProgramService {

    private final StudProgramRepository repository;

    public StudProgram create(StudProgram entity) {
        return repository.save(entity);
    }

    public StudProgram findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "StudProgram not found: " + id));
    }

    public StudProgram update(Long id, StudProgram entity) {
        StudProgram existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "StudProgram not found: " + id);
        }
        repository.deleteById(id);
    }
}
