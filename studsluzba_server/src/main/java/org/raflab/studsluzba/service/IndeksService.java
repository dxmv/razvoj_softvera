package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.repositories.StudentskiIndeksRepository;
import org.raflab.studsluzba.utils.ParseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class IndeksService {

    private final StudentskiIndeksRepository repository;

    public Indeks create(Indeks entity) {
        return repository.save(entity);
    }

    public Indeks findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Indeks not found: " + id));
    }

    public Indeks update(Long id, Indeks entity) {
        Indeks existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indeks not found: " + id);
        }
        repository.deleteById(id);
    }

    public Indeks findByShort(String indeks) {
        String[] arr = ParseUtils.parseIndeks(indeks);
        if (arr == null || arr.length != 3 || arr[0].isBlank() || arr[1].isBlank() || arr[2].isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid indeks format: " + indeks);
        }
        try {
            Integer broj = Integer.parseInt(arr[1]);
            Integer godina = Integer.parseInt(arr[2]);
            return repository.findByShort(arr[0], broj, godina);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid indeks numeric format: " + indeks, ex);
        }
    }
}
