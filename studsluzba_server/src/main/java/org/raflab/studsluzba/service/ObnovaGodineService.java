package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.ObnovaGodine;
import org.raflab.studsluzba.repositories.ObnovaGodineRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ObnovaGodineService {

    private final ObnovaGodineRepository repository;

    public ObnovaGodine create(ObnovaGodine entity) {
        return repository.save(entity);
    }

    public ObnovaGodine findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ObnovaGodine not found: " + id));
    }

    public ObnovaGodine update(Long id, ObnovaGodine entity) {
        ObnovaGodine existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ObnovaGodine not found: " + id);
        }
        repository.deleteById(id);
    }
}
