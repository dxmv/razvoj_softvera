package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Student;
import org.raflab.studsluzba.model.Uplata;
import org.raflab.studsluzba.model.dto.CreateUplataRequest;
import org.raflab.studsluzba.repositories.StudentRepository;
import org.raflab.studsluzba.repositories.UplataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UplataService {

    private final UplataRepository repository;
    private final StudentRepository studentRepository;
    private final String KURS_URL = "https://kurs.resenje.org/api/v1/currencies/eur/rates/today";

    public Uplata create(Uplata entity) {
        return repository.save(entity);
    }

    public Uplata findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Uplata not found: " + id));
    }

    public Uplata update(Long id, Uplata entity) {
        Uplata existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public Uplata createWithCurrentRate(Long studentId, CreateUplataRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + studentId));
        // getovanje kursa

        // kreiranje uplate
        Uplata uplata = new Uplata();
        uplata.setStudent(student);
        uplata.setDatumUplate(request.getDatumUplate());
        uplata.setIznosUDinarima(request.getIznosUDinarima());
        uplata.setSrednjiKurs(BigDecimal.valueOf(10.2));
        return repository.save(uplata);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Uplata not found: " + id);
        }
        repository.deleteById(id);
    }
}
