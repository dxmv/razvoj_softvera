package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.NastavnikObrazovanje;
import org.raflab.studsluzba.repositories.NastavnikObrazovanjeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NastavnikObrazovanjeService {

    private final NastavnikObrazovanjeRepository repository;

    public NastavnikObrazovanje create(NastavnikObrazovanje entity) {
        return repository.save(entity);
    }

    public NastavnikObrazovanje findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "NastavnikObrazovanje not found: " + id));
    }

    public NastavnikObrazovanje update(Long id, NastavnikObrazovanje entity) {
        NastavnikObrazovanje existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "NastavnikObrazovanje not found: " + id);
        }
        repository.deleteById(id);
    }
}
