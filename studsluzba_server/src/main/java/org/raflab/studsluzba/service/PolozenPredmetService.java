package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.PolozenPredmet;
import org.raflab.studsluzba.repositories.PolozenPredmetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PolozenPredmetService {

    private final PolozenPredmetRepository repository;

    public PolozenPredmet create(PolozenPredmet entity) {
        return repository.save(entity);
    }

    public PolozenPredmet findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PolozenPredmet not found: " + id));
    }

    public PolozenPredmet update(Long id, PolozenPredmet entity) {
        PolozenPredmet existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PolozenPredmet not found: " + id);
        }
        repository.deleteById(id);
    }
    public Double getProsecnaOcenaZaPredmetUGodinama(Long predmetId, int godinaOd, int godinaDo) {
        Double prosek = repository.findProsecnaOcenaZaPredmetUGodinama(predmetId, godinaOd, godinaDo);
        if (prosek == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nema položenih predmeta za dati predmet i period godina.");
        }
        return prosek;
    }

}
