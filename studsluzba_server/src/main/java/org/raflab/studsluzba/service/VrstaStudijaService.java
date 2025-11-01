package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.VrstaStudija;
import org.raflab.studsluzba.repositories.VrstaStudijaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class VrstaStudijaService {

    private final VrstaStudijaRepository repository;

    public VrstaStudija create(VrstaStudija entity) {
        return repository.save(entity);
    }

    public VrstaStudija findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "VrstaStudija not found: " + id));
    }

    public VrstaStudija update(Long id, VrstaStudija entity) {
        VrstaStudija existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "VrstaStudija not found: " + id);
        }
        repository.deleteById(id);
    }
}
