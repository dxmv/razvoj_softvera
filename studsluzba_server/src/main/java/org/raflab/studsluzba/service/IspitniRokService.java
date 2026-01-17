package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.IspitniRok;
import org.raflab.studsluzba.model.SkolskaGodina;
import org.raflab.studsluzba.model.dto.IspitPrikazDto;
import org.raflab.studsluzba.model.dto.IspitniRokDto;
import org.raflab.studsluzba.repositories.IspitniRokRepository;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IspitniRokService {

    private final IspitniRokRepository repository;
    private final SkolskaGodinaRepository skolskaGodinaRepository;

    public IspitniRok create(IspitniRok entity) {
        return repository.save(entity);
    }
    @Transactional
    public IspitniRokDto create2(IspitniRokDto dto) {
        IspitniRok entity = new IspitniRok();
        entity.setDatumPocetka(dto.getDatumPocetka());
        entity.setDatumZavrsetka(dto.getDatumZavrsetka());

        if (dto.getSkolskaGodinaId() != null) {
            SkolskaGodina sg = skolskaGodinaRepository.findById(dto.getSkolskaGodinaId())
                    .orElseThrow(() -> new RuntimeException("Školska godina nije pronađena"));
            entity.setSkolskaGodina(sg);
        }

        IspitniRok saved = repository.save(entity);

        return IspitniRokDto.builder()
                .id(saved.getId())
                .datumPocetka(saved.getDatumPocetka())
                .datumZavrsetka(saved.getDatumZavrsetka())
                .skolskaGodinaId(saved.getSkolskaGodina().getId())
                .build();
    }
    public IspitniRok findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "IspitniRok not found: " + id));
    }

    public IspitniRok update(Long id, IspitniRok entity) {
        IspitniRok existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "IspitniRok not found: " + id);
        }
        repository.deleteById(id);
    }

    public List<IspitniRokDto> findAll() {
        return repository.findAll().stream()
                .map(rok -> IspitniRokDto.builder()
                        .id(rok.getId())
                        .datumPocetka(rok.getDatumPocetka())
                        .datumZavrsetka(rok.getDatumZavrsetka())
                        .skolskaGodinaId(rok.getSkolskaGodina() != null ? rok.getSkolskaGodina().getId() : null)
                        .build())
                .collect(Collectors.toList());
    }

@Transactional
    public List<IspitPrikazDto> findIspitiByRok(Long rokId) {
        IspitniRok rok = repository.findById(rokId)
                .orElseThrow(() -> new RuntimeException("Rok nije pronađen"));

        return rok.getIspiti().stream()
                .map(ispit -> IspitPrikazDto.builder()
                        .id(ispit.getId())
                        .datum(ispit.getDatum())
                        .vremePocetka(ispit.getVremePocetka())
                        .nazivPredmeta(ispit.getPredmet().getNaziv()) // Čupamo naziv iz povezane entitete
                        .predmetId(ispit.getPredmet().getId())
                        .nastavnikId(ispit.getNastavnik().getId())
                        .zakljucen(ispit.getZakljucen())
                        .build())
                .collect(Collectors.toList());
    }
}
