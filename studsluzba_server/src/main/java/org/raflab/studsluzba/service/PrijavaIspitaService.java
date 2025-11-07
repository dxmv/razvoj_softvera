package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.Ispit;
import org.raflab.studsluzba.model.PrijavaIspita;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzba.repositories.IspitRepository;
import org.raflab.studsluzba.repositories.PrijavaIspitaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrijavaIspitaService {

    private final PrijavaIspitaRepository repository;
    private final IspitRepository ispitRepository;


    public PrijavaIspita create(PrijavaIspita entity) {
        return repository.save(entity);
    }

    public PrijavaIspita findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PrijavaIspita not found: " + id));
    }

    public PrijavaIspita update(Long id, PrijavaIspita entity) {
        PrijavaIspita existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PrijavaIspita not found: " + id);
        }
        repository.deleteById(id);
    }
    public List<StudentDto> findPrijavljeniZaIspit(Long ispitId) {
        Ispit ispit = ispitRepository.findById(ispitId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"Ispit with id: "+ ispitId +" not found."));

        List<PrijavaIspita> prijave = repository.findByIspit(ispit);

        return prijave.stream()
                .map(p -> EntityMapper.toDto(p.getStudentskiIndeks().getStudent()))
                .collect(Collectors.toList());
    }
}
