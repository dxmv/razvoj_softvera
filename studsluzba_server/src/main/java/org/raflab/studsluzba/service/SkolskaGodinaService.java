package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.SkolskaGodina;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SkolskaGodinaService {

    private final SkolskaGodinaRepository repository;

    public SkolskaGodina create(SkolskaGodina entity) {
        return repository.save(entity);
    }

    public SkolskaGodina findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SkolskaGodina not found: " + id));
    }

    public SkolskaGodina update(Long id, SkolskaGodina entity) {
        SkolskaGodina existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SkolskaGodina not found: " + id);
        }
        repository.deleteById(id);
    }
}
