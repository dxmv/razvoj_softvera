package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.model.dto.IspitDto;
import org.raflab.studsluzba.model.dto.RezultatIspitaStudentDto;
import org.raflab.studsluzba.repositories.IspitRepository;
import org.raflab.studsluzba.repositories.IzlazakIspitRepository;
import org.raflab.studsluzba.repositories.NastavnikRepository;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.raflab.studsluzba.repositories.IspitniRokRepository;
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
    private final PredmetRepository predmetRepository;
    private final NastavnikRepository nastavnikRepository;
    private final IspitniRokRepository ispitniRokRepository;


    public IspitDto create(IspitDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ispit body is required");
        }
        if (dto.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID must not be provided when creating");
        }
        Ispit entity = new Ispit();
        applyIspitDto(dto, entity);
        Ispit saved = repository.save(entity);
        return EntityMapper.toDto(saved);
    }

    public Ispit findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit not found: " + id));
    }

    public IspitDto update(Long id, IspitDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ispit body is required");
        }
        Ispit existing = findById(id);
        applyIspitDto(dto, existing);
        Ispit saved = repository.save(existing);
        return EntityMapper.toDto(saved);
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
        if (!repository.existsById(ispitId)) {
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
    private void applyIspitDto(IspitDto dto, Ispit target) {
        target.setDatum(dto.getDatum());
        target.setVremePocetka(dto.getVremePocetka());
        if (dto.getZakljucen() != null) {
            target.setZakljucen(dto.getZakljucen());
        } else if (target.getZakljucen() == null) {
            target.setZakljucen(Boolean.FALSE);
        }
        target.setPredmet(resolvePredmet(dto.getPredmetId()));
        target.setNastavnik(resolveNastavnik(dto.getNastavnikId()));
        target.setIspitniRok(resolveIspitniRok(dto.getIspitniRokId()));
    }

    private Predmet resolvePredmet(Long predmetId) {
        if (predmetId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Predmet must be provided");
        }
        return predmetRepository.findById(predmetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Predmet not found: " + predmetId));
    }

    private Nastavnik resolveNastavnik(Long nastavnikId) {
        if (nastavnikId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nastavnik must be provided");
        }
        return nastavnikRepository.findById(nastavnikId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nastavnik not found: " + nastavnikId));
    }

    private IspitniRok resolveIspitniRok(Long rokId) {
        if (rokId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ispitni rok must be provided");
        }
        return ispitniRokRepository.findById(rokId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ispitni rok not found: " + rokId));
    }
}
