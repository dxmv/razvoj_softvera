package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.model.ObnovaGodine;
import org.raflab.studsluzba.model.Student;
import org.raflab.studsluzba.model.UpisGodine;
import org.raflab.studsluzba.model.dto.ObnovaGodineDto;
import org.raflab.studsluzba.model.dto.PolozenPredmetDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.UpisGodineDto;
import org.raflab.studsluzba.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;
    private final IndeksService indeksService;
    private final PolozenPredmetRepository polozenPredmetRepository;
    private final StudentPredmetRepository studentPredmetRepository;
    private final UpisGodineRepository upisGodineRepository;
    private final ObnovaGodineRepository obnovaGodineRepository;

    public Student create(Student entity) {
        return repository.save(entity);
    }

    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id));
    }

    public Student update(Long id, Student entity) {
        Student existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id);
        }
        repository.deleteById(id);
    }

    public Student findByIndex(String index) {
        Indeks indeks = findIndeksOrThrow(index);
        return indeks.getStudent();
    }

    public Page<PolozenPredmetDto> findPassedExamsByIndex(String index, Pageable pageable) {
        Indeks indeks = findIndeksOrThrow(index);
        return polozenPredmetRepository
                .findByStudentskiIndeks(indeks, pageable)
                .map(EntityMapper::toDto);
    }

    public Page<PredmetDto> findFailedExamsByIndex(String index, Pageable pageable) {
        Indeks indeks = findIndeksOrThrow(index);
        return studentPredmetRepository
                .findUnpassedSubjectsByIndeks(indeks, pageable)
                .map(EntityMapper::toDto);
    }

    public List<UpisGodineDto> findEnrolledYearsByIndex(String index) {
        Indeks indeks = findIndeksOrThrow(index);
        List<UpisGodine> upisi = upisGodineRepository.findByStudentskiIndeks(indeks);
        return upisi.stream().map(EntityMapper::toDto).collect(Collectors.toList());
    }

    public List<ObnovaGodineDto> findRepeatedYearsByIndex(String index) {
        Indeks indeks = findIndeksOrThrow(index);
        List<ObnovaGodine> obnove = obnovaGodineRepository.findByStudentskiIndeks(indeks);
        return obnove.stream().map(EntityMapper::toDto).collect(Collectors.toList());
    }

    // pomcna funkcija
    private Indeks findIndeksOrThrow(String index) {
        Indeks indeks = indeksService.findByShort(index);
        if (indeks == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Index not found: " + index);
        }
        return indeks;
    }


}
