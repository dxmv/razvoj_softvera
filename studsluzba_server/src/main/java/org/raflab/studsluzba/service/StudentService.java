package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.model.NastavnikPredmet;
import org.raflab.studsluzba.model.ObnovaGodine;
import org.raflab.studsluzba.model.Predmet;
import org.raflab.studsluzba.model.SkolskaGodina;
import org.raflab.studsluzba.model.Student;
import org.raflab.studsluzba.model.StudentPredmet;
import org.raflab.studsluzba.model.UpisGodine;
import org.raflab.studsluzba.model.dto.ObnovaGodineDto;
import org.raflab.studsluzba.model.dto.ObnovaGodineRequest;
import org.raflab.studsluzba.model.dto.PolozenPredmetDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzba.model.dto.UpisGodineDto;
import org.raflab.studsluzba.model.dto.UpisGodineEnrollmentRequest;
import org.raflab.studsluzba.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;
    private final IndeksService indeksService;
    private final PolozenPredmetRepository polozenPredmetRepository;
    private final StudentPredmetRepository studentPredmetRepository;
    private final UpisGodineRepository upisGodineRepository;
    private final SkolskaGodinaRepository skolskaGodinaRepository;
    private final ObnovaGodineRepository obnovaGodineRepository;
    private final SrednjasSkolaRepository srednjasSkolaRepository;
    private final PredmetRepository predmetRepository;
    private final NastavnikPredmetRepository nastavnikPredmetRepository;

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

    public Page<StudentDto> search(String ime, String prezime, Pageable pageable) {
        String normalizedIme = normalize(ime);
        String normalizedPrezime = normalize(prezime);
        return repository.searchByImeAndPrezime(normalizedIme, normalizedPrezime, pageable)
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

    public List<StudentDto> findEnrolledByHighSchool(Long srednjaSkolaId) {
        if (srednjaSkolaId != null && !srednjasSkolaRepository.existsById(srednjaSkolaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Srednja skola not found: " + srednjaSkolaId);
        }
        return repository.findEnrolledByHighSchool(srednjaSkolaId).stream()
                .map(EntityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UpisGodineDto enroll(String indeksValue, UpisGodineEnrollmentRequest request) {
        validateEnrollmentRequest(request);
        Indeks indeks = findIndeksOrThrow(indeksValue);
        SkolskaGodina skolskaGodina = skolskaGodinaRepository.findById(request.getSkolskaGodinaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Skolska godina not found: " + request.getSkolskaGodinaId()));

        if (upisGodineRepository.existsByStudentskiIndeksAndSkolskaGodina(indeks, skolskaGodina)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Student je već upisan u zadatu školsku godinu");
        }

        UpisGodine upis = UpisGodine.builder()
                .studentskiIndeks(indeks)
                .skolskaGodina(skolskaGodina)
                .godinaStudija(request.getGodinaStudija())
                .datumUpisa(request.getDatumUpisa() != null ? request.getDatumUpisa() : LocalDate.now())
                .napomena(request.getNapomena())
                .predmeti(fetchPrenetiPredmeti(request.getPrenetiPredmetIds()))
                .build();

        UpisGodine saved = upisGodineRepository.save(upis);
        return EntityMapper.toDto(saved);
    }

    @Transactional
    public ObnovaGodineDto repeatYear(String indeksValue, ObnovaGodineRequest request) {
        validateRepeatRequest(request);
        Indeks indeks = findIndeksOrThrow(indeksValue);
        SkolskaGodina skolskaGodina = skolskaGodinaRepository.findById(request.getSkolskaGodinaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Skolska godina not found: " + request.getSkolskaGodinaId()));

        if (obnovaGodineRepository.existsByStudentskiIndeksAndSkolskaGodina(indeks, skolskaGodina)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Student je već obnovio zadatu školsku godinu");
        }

        List<NastavnikPredmet> nastavnici = fetchNastavnikPredmeti(request.getNastavnikPredmetIds(), skolskaGodina);
        validateEspbLimit(nastavnici);

        ObnovaGodine obnova = ObnovaGodine.builder()
                .studentskiIndeks(indeks)
                .skolskaGodina(skolskaGodina)
                .godinaStudija(request.getGodinaStudija())
                .datumObnove(request.getDatumObnove() != null ? request.getDatumObnove() : LocalDate.now())
                .napomena(request.getNapomena())
                .predmeti(nastavnici.stream()
                        .map(NastavnikPredmet::getPredmet)
                        .collect(Collectors.toSet()))
                .build();

        ObnovaGodine saved = obnovaGodineRepository.save(obnova);
        createStudentPredmeti(indeks, skolskaGodina, nastavnici);

        return EntityMapper.toDto(saved);
    }

    // pomcna funkcija
    private Indeks findIndeksOrThrow(String index) {
        Indeks indeks = indeksService.findByShort(index);
        if (indeks == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Index not found: " + index);
        }
        return indeks;
    }

    private void validateEnrollmentRequest(UpisGodineEnrollmentRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is required");
        }
        if (request.getSkolskaGodinaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Skolska godina je obavezna");
        }
        if (request.getGodinaStudija() == null || request.getGodinaStudija() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Godina studija mora biti pozitivan broj");
        }
    }

    private void validateRepeatRequest(ObnovaGodineRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is required");
        }
        if (request.getSkolskaGodinaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Skolska godina je obavezna");
        }
        if (request.getGodinaStudija() == null || request.getGodinaStudija() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Godina studija mora biti pozitivan broj");
        }
        if (request.getNastavnikPredmetIds() == null || request.getNastavnikPredmetIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Potrebno je izabrati bar jedan predmet");
        }
    }

    private Set<Predmet> fetchPrenetiPredmeti(Set<Long> prenetiPredmetIds) {
        if (prenetiPredmetIds == null || prenetiPredmetIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Predmet> predmeti = predmetRepository.findAllById(prenetiPredmetIds);
        if (predmeti.size() != prenetiPredmetIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jedan ili više predmeta ne postoji");
        }
        return new HashSet<>(predmeti);
    }

    private List<NastavnikPredmet> fetchNastavnikPredmeti(Set<Long> nastavnikPredmetIds, SkolskaGodina skolskaGodina) {
        List<NastavnikPredmet> nastavnici = nastavnikPredmetRepository.findAllById(nastavnikPredmetIds);
        if (nastavnici.size() != nastavnikPredmetIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jedan ili više nastavnik/predmet parova ne postoji");
        }
        boolean mismatchedYear = nastavnici.stream()
                .anyMatch(np -> np.getSkolskaGodina() == null || !np.getSkolskaGodina().getId().equals(skolskaGodina.getId()));
        if (mismatchedYear) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Svi izabrani predmeti moraju pripadati unetoj školskoj godini");
        }
        return nastavnici;
    }

    private void validateEspbLimit(List<NastavnikPredmet> nastavnici) {
        int totalEspb = nastavnici.stream()
                .map(NastavnikPredmet::getPredmet)
                .filter(predmet -> predmet != null && predmet.getEspbBodovi() != null)
                .mapToInt(Predmet::getEspbBodovi)
                .sum();
        if (totalEspb > 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ukupan broj ESPB poena ne može biti veći od 60");
        }
    }

    private void createStudentPredmeti(Indeks indeks, SkolskaGodina skolskaGodina, List<NastavnikPredmet> nastavnici) {
        List<StudentPredmet> noviZapisi = nastavnici.stream()
                .map(np -> StudentPredmet.builder()
                        .skolskaGodina(skolskaGodina)
                        .indeks(indeks)
                        .nastavnikPredmet(np)
                        .build())
                .collect(Collectors.toList());
        studentPredmetRepository.saveAll(noviZapisi);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}
