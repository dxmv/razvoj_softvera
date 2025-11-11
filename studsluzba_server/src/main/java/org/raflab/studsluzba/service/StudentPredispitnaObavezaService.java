package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.StudentPredispitnaObaveza;
import org.raflab.studsluzba.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudentPredispitnaObavezaService {

    private final StudentPredispitnaObavezaRepository repository;
    private final StudentskiIndeksRepository studentskiIndeksRepository;
    private final PredmetRepository predmetRepository;
    private final SkolskaGodinaRepository skolskaGodinaRepository;

    public StudentPredispitnaObaveza create(StudentPredispitnaObaveza entity) {
        return repository.save(entity);
    }

    public StudentPredispitnaObaveza findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "StudentPredispitnaObaveza not found: " + id));
    }

    public StudentPredispitnaObaveza update(Long id, StudentPredispitnaObaveza entity) {
        StudentPredispitnaObaveza existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "StudentPredispitnaObaveza not found: " + id);
        }
        repository.deleteById(id);
    }
    public Double getUkupniPredispitniPoeni(Long indeksId, Long predmetId, Long skolskaGodinaId) {
        if (!studentskiIndeksRepository.existsById(indeksId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indeks nije pronadjen: " + indeksId);
        }
        if (!predmetRepository.existsById(predmetId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Predmet nije pronadjen: " + predmetId);
        }
        if (!skolskaGodinaRepository.existsById(skolskaGodinaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Skolska godina nije pronadjena: " + skolskaGodinaId);
        }

        Double sum = repository.findUkupniPredispitniPoeni(indeksId, predmetId, skolskaGodinaId);

        return sum != null ? sum : 0.0;

    }
}
