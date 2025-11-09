package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.model.dto.IzlazakIspitDto;
import org.raflab.studsluzba.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IzlazakIspitService {

    private final IzlazakIspitRepository repository;
    private final StudentPredispitnaObavezaRepository studentPredispitnaObavezaRepository;
    private final PolozenPredmetRepository polozenPredmetRepository;
    private final PrijavaIspitaRepository prijavaIspitaRepository;
    private final SkolskaGodinaRepository skolskaGodinaRepository;
    private final StudentskiIndeksRepository studentskiIndeksRepository;
    private final PredmetRepository predmetRepository;


    public IzlazakIspit create(IzlazakIspit entity) {
        return repository.save(entity);
    }

    public IzlazakIspit findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "IzlazakIspit not found: " + id));
    }

    public IzlazakIspit update(Long id, IzlazakIspit entity) {
        IzlazakIspit existing = findById(id);
        entity.setId(existing.getId());
        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "IzlazakIspit not found: " + id);
        }
        repository.deleteById(id);
    }
    @Transactional
    public IzlazakIspitDto dodajIzlazak(IzlazakIspitDto izlazakDto) {
        PrijavaIspita prijava = prijavaIspitaRepository.findById(izlazakDto.getPrijavaIspitaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Prijava ispita nije pronadjena: " + izlazakDto.getPrijavaIspitaId()));

        Indeks indeks = prijava.getStudentskiIndeks();
        Predmet predmet = prijava.getIspit().getPredmet();
        if (polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Student je vec polozio ovaj predmet: " + predmet.getNaziv());
        }
        SkolskaGodina aktivnaGodina = skolskaGodinaRepository.findAktivnaSkolskaGodina()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nema aktivne skolske godine"));

        Double predispitniPoeni = studentPredispitnaObavezaRepository
                .findUkupniPredispitniPoeni(indeks.getId(), predmet.getId(), aktivnaGodina.getId());

        Double ukupnoPoena = predispitniPoeni + izlazakDto.getPoeniSaIspita();

        IzlazakIspit izlazak = IzlazakIspit.builder()
                .prijavaIspita(prijava)
                .poeniSaIspita(izlazakDto.getPoeniSaIspita())
                .ukupnoPoena(ukupnoPoena)
                .napomena(izlazakDto.getNapomena())
                .ponistavanIspit(izlazakDto.getPonistavanIspit())
                .datumIzlaska(LocalDateTime.now())
                .build();

        IzlazakIspit savedIzlazak = repository.save(izlazak);

        if (ukupnoPoena >= 51 && !izlazakDto.getPonistavanIspit()) {
            sacuvajPolozeniPredmet(indeks, predmet, ukupnoPoena, savedIzlazak);
        }

        return EntityMapper.toDto(savedIzlazak);
    }

    private void sacuvajPolozeniPredmet(Indeks indeks, Predmet predmet, Double ukupnoPoena, IzlazakIspit izlazak) {
        Integer ocena = generisiOcenu(ukupnoPoena);

        PolozenPredmet polozeni = PolozenPredmet.builder()
                .studentskiIndeks(indeks)
                .predmet(predmet)
                .ocena(ocena)
                .nacinPolaganja(NacinPolaganja.ISPIT)
                .izlazakNaIspit(izlazak)
                .build();

        polozenPredmetRepository.save(polozeni);
    }
    private Integer generisiOcenu(Double ukupnoPoena) {
        if (ukupnoPoena >= 91) return 10;
        else if (ukupnoPoena >= 81) return 9;
        else if (ukupnoPoena >= 71) return 8;
        else if (ukupnoPoena >= 61) return 7;
        else if (ukupnoPoena >= 51) return 6;
        else return 5;
    }

    public Long getBrojIzlasakaNaPredmet(Long indeksId, Long predmetId) {
        if (!studentskiIndeksRepository.existsById(indeksId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indeks nije pronađen: " + indeksId);
        }
        if (!predmetRepository.existsById(predmetId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Predmet nije pronađen: " + predmetId);
        }

        return repository.countByStudentAndPredmet(indeksId, predmetId);
    }
}
