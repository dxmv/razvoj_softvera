package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.model.dto.IzlazakIspitDto;
import org.raflab.studsluzba.model.dto.RezultatIspitaStudentDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzba.repositories.IspitRepository;
import org.raflab.studsluzba.repositories.IzlazakIspitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IspitService {

    private final IspitRepository repository;
    private final IzlazakIspitRepository izlazakIspitRepository;
    private final IspitRepository ispitRepository;


    public Ispit create(Ispit entity) {
        return repository.save(entity);
    }

    public Ispit findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit not found: " + id));
    }

    public Ispit update(Long id, Ispit entity) {
        Ispit existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit not found: " + id);
        }
        repository.deleteById(id);
    }
    public Double getProsecnaOcenaNaIspitu(Long ispitId) {
        List<PolozenPredmet> polozeni = repository.findByIspitId(ispitId);
        if (polozeni.isEmpty()) return null;
        return polozeni.stream()
                .mapToInt(PolozenPredmet::getOcena)
                .average().orElse(0);

    }
    public List<RezultatIspitaStudentDto> getRezultatiIspitaSorted(Long ispitId) {
        if (!ispitRepository.existsById(ispitId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit nije pronađen: " + ispitId);
        }

        List<IzlazakIspit> izlasci = izlazakIspitRepository.findRezultatiByIspitSorted(ispitId);

        return izlasci.stream()
                .map(iz -> {
                    Indeks indeks = iz.getPrijavaIspita().getStudentskiIndeks();
                    Student student = indeks.getStudent();
                    StudProgram studProgram = indeks.getStudijskiProgram();

                    return new RezultatIspitaStudentDto(
                            indeks.getGodinaUpisa() + "/" + String.format("%04d", indeks.getBrojIndeksa()),
                            studProgram.getOznaka(),
                            indeks.getGodinaUpisa(),
                            indeks.getBrojIndeksa(),
                            student.getIme(),
                            student.getPrezime(),
                            iz.getUkupnoPoena()
                    );
                })
                .collect(Collectors.toList());

    }
}
