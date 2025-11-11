package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Uplata;
import org.raflab.studsluzba.model.UpisGodine;
import org.raflab.studsluzba.model.dto.CreateUplataRequest;
import org.raflab.studsluzba.model.dto.UplataDto;
import org.raflab.studsluzba.repositories.UpisGodineRepository;
import org.raflab.studsluzba.repositories.UplataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UplataService {

    private final UplataRepository repository;
    private final UpisGodineRepository upisGodineRepository;

    public Uplata create(Uplata entity) {
        return repository.save(entity);
    }

    public Uplata findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Uplata not found: " + id));
    }

    public UplataDto findDtoById(Long id) {
        Uplata uplata = findById(id);
        return toDto(uplata);
    }

    public Uplata update(Long id, Uplata entity) {
        Uplata existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public UplataDto createWithCurrentRate(Long studentId, CreateUplataRequest request) {
        UpisGodine upis = upisGodineRepository.findActiveByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Aktivan upis nije pronađen za studenta: " + studentId));

        if (request.getIznosUDinarima() == null || request.getIznosUDinarima().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Iznos u dinarima mora biti veći od nule");
        }
        if (request.getSrednjiKurs() == null || request.getSrednjiKurs().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Srednji kurs mora biti veći od nule");
        }

        LocalDate datumUplate = request.getDatumUplate() != null ? request.getDatumUplate() : LocalDate.now();

        Uplata uplata = Uplata.builder()
                .upisGodine(upis)
                .datumUplate(datumUplate)
                .iznosUDinarima(request.getIznosUDinarima())
                .srednjiKurs(request.getSrednjiKurs())
                .build();

        Uplata saved = repository.save(uplata);
        return toDto(saved);
    }

    private UplataDto toDto(Uplata entity) {
        return UplataDto.builder()
                .id(entity.getId())
                .upisGodineId(entity.getUpisGodine() != null ? entity.getUpisGodine().getId() : null)
                .datumUplate(entity.getDatumUplate())
                .iznosUDinarima(entity.getIznosUDinarima())
                .srednjiKurs(entity.getSrednjiKurs())
                .build();
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Uplata not found: " + id);
        }
        repository.deleteById(id);
    }
}
