package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.Predmet;
import org.raflab.studsluzba.model.StudProgram;
import org.raflab.studsluzba.model.dto.PredmetCreateDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.repositories.PolozenPredmetRepository;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.raflab.studsluzba.repositories.StudProgramRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredmetService {

    private final PredmetRepository repository;
    private final StudProgramRepository studProgramRepository;
    private final PolozenPredmetRepository polozenPredmetRepository;

    public Predmet create(Predmet entity) {
        if (entity.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ID cant be added manually");
        }

        if (repository.existsBySifra(entity.getSifra())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Predmet with id '" + entity.getSifra() + "'already exists");
        }

        return repository.save(entity);    }

    public Predmet findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Predmet not found: " + id));
    }

    public Predmet update(Long id, Predmet entity) {
        Predmet existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Predmet not found: " + id);
        }
        repository.deleteById(id);
    }

    public Page<PredmetDto> findPredmetiByStudijskiProgram(Long studProgramId, Pageable pageable) {
        return repository
                .findByStudijskiProgramId(studProgramId, pageable)
                .map(EntityMapper::toDto);
    }

    public Page<PredmetDto> getAllPredmeti(Pageable pageable) {
        return repository.findAll(pageable).map(EntityMapper::toDto);
    }
    @Transactional
    public PredmetDto createPredmet(PredmetCreateDto predmetDto) {
        if (repository.existsBySifra(predmetDto.getSifra())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Predmet with '" + predmetDto.getSifra() + "' already exists.");
        }

        StudProgram studijskiProgram = studProgramRepository
                .findById(predmetDto.getStudijskiProgramId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "StudProgram not found: " + predmetDto.getStudijskiProgramId()));

        Predmet predmet = Predmet.builder()
                .sifra(predmetDto.getSifra())
                .naziv(predmetDto.getNaziv())
                .opis(predmetDto.getOpis())
                .espbBodovi(predmetDto.getEspbBodovi())
                .semestar(predmetDto.getSemestar())
                .brPredavanja(predmetDto.getBrPredavanja())
                .brVezbi(predmetDto.getBrVezbi())
                .studijskiProgram(studijskiProgram)
                .build();

        Predmet saved = repository.save(predmet);

        return EntityMapper.toDto(saved);
    }

    public Double getAverageGradeForSubjectInYearRange(Long predmetId, int yearFrom, int yearTo) {
        if (!repository.existsById(predmetId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Predmet not found: " + predmetId);
        }
        Double avg = polozenPredmetRepository.findProsecnaOcenaZaPredmetUGodinama(predmetId, yearFrom, yearTo);
        return avg != null ? avg : 0.0;
    }

}