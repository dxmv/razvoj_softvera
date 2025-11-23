package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.*;
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
    private final VSURepository vsuRepository;
    private final PredmetRepository predmetRepository;
    private final NastavnikPredmetRepository nastavnikPredmetRepository;

    public StudentDto create(StudentDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student body is required");
        }
        if (dto.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student ID must not be provided when creating");
        }

        Student entity = new Student();
        applyStudentDto(dto, entity);
        Student saved = repository.save(entity);
        return EntityMapper.toDto(saved);
    }

    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id));
    }

    public StudentDto update(Long id, StudentDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student body is required");
        }
        Student existing = findById(id);
        applyStudentDto(dto, existing);
        Student saved = repository.save(existing);
        return EntityMapper.toDto(saved);
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

        // naci indeks za studenta
        Indeks indx = findIndeksOrThrow(indeksValue);
        // naci aktivnu godinu studija
        SkolskaGodina skolskaGodina = skolskaGodinaRepository.findAktivnaSkolskaGodina()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nema aktivne skolske fodine"));
        // da li upis vec postoji
        if(upisGodineRepository.existsByStudentskiIndeksAndSkolskaGodina(indx, skolskaGodina)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "godina je vec upisana");
        }
        // napravi novi upis
        UpisGodine upis = UpisGodine.builder()
        .studentskiIndeks(indx)
        .skolskaGodina(skolskaGodina)
        .godinaStudija(request.getGodinaStudija())
        .datumUpisa(request.getDatumUpisa() == null ? LocalDate.now() : request.getDatumUpisa()) //default danas
        .napomena(request.getNapomena())
        .build();

        if (request.getGodinaStudija() == null || request.getGodinaStudija() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Godina studija mora biti pozitivan broj");
        }

        // nepolozeni predmeti automatski postaju preneti predmeti
        List<Predmet> nepolozeni = studentPredmetRepository
                .findUnpassedSubjectsByIndeks(indx, Pageable.unpaged())
                .getContent();
        upis.setPredmeti(new HashSet<>(nepolozeni));

        // predmeti za narednu godinu na osnovu aktivne skolske godine i studijskog programa
        List<Integer> targetSemesters = resolveSemestersForYear(request.getGodinaStudija());
        List<NastavnikPredmet> predmetiZaSlusanje = nastavnikPredmetRepository
                .findBySkolskaGodinaIdAndPredmetStudijskiProgramIdAndPredmetSemestarIn(
                        skolskaGodina.getId(),
                        indx.getStudijskiProgram().getId(),
                        targetSemesters);
        if (predmetiZaSlusanje.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nema dostupnih predmeta za trazenu godinu studija");
        }

        UpisGodine saved = upisGodineRepository.save(upis);
        createStudentPredmeti(indx, skolskaGodina, predmetiZaSlusanje);
        return EntityMapper.toDto(saved);
    }

    @Transactional
    public ObnovaGodineDto repeatYear(String indeksValue, ObnovaGodineRequest request) {
        // naci indeks za studenta
        Indeks indx = findIndeksOrThrow(indeksValue);
        // naci aktivnu godinu studija
        SkolskaGodina skolskaGodina = skolskaGodinaRepository.findAktivnaSkolskaGodina()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nema aktivne skolske fodine"));
        // da li obnova vec postoji
        if(obnovaGodineRepository.existsByStudentskiIndeksAndSkolskaGodina(indx, skolskaGodina)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "godina je vec upisana");
        }
        if (request.getPredmetIds() == null || request.getPredmetIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Morate navesti bar jedan predmet za obnovu");
        }
        // napravi novu obnovu
        ObnovaGodine obnova = ObnovaGodine.builder()
        .studentskiIndeks(indx)
        .skolskaGodina(skolskaGodina)
        .godinaStudija(request.getGodinaStudija())
        .datumObnove(request.getDatumObnove() == null ? LocalDate.now() : request.getDatumObnove()) //default danas
        .napomena(request.getNapomena())
        .build();

        // Check if any requested subject is already passed
        List<PolozenPredmet> polozeni = polozenPredmetRepository.findByStudentskiIndeks(indx, Pageable.unpaged()).getContent();
        Set<Long> polozeniIds = polozeni.stream()
                .map(pp -> pp.getPredmet().getId())
                .collect(Collectors.toSet());

        Set<Long> trazeniPredmetIds = request.getPredmetIds();
        for (Long id : trazeniPredmetIds) {
            if (polozeniIds.contains(id)) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Predmet je vec polozen: " + id);
            }
        }

        List<Predmet> izabraniPredmeti = predmetRepository.findAllById(trazeniPredmetIds);
        if (izabraniPredmeti.size() != trazeniPredmetIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Jedan ili vise trazenih predmeta ne postoji");
        }
        obnova.setPredmeti(new HashSet<>(izabraniPredmeti));

        List<NastavnikPredmet> nastavniciZaObnovu = nastavnikPredmetRepository
                .findBySkolskaGodinaIdAndPredmetIdIn(skolskaGodina.getId(), trazeniPredmetIds);
        Set<Long> pronadjeniPredmeti = nastavniciZaObnovu.stream()
                .map(np -> np.getPredmet().getId())
                .collect(Collectors.toSet());
        if (!pronadjeniPredmeti.containsAll(trazeniPredmetIds)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Odabrani predmeti nisu dostupni u aktivnoj skolskoj godini");
        }

        validateEspbLimit(nastavniciZaObnovu);
        ObnovaGodine saved = obnovaGodineRepository.save(obnova);
        createStudentPredmeti(indx, skolskaGodina, nastavniciZaObnovu);
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

    private void applyStudentDto(StudentDto dto, Student target) {
        target.setIme(dto.getIme());
        target.setPrezime(dto.getPrezime());
        target.setSrednjeIme(dto.getSrednjeIme());
        target.setJmbg(dto.getJmbg());
        target.setDatumRodjenja(dto.getDatumRodjenja());
        target.setMestoRodjenja(dto.getMestoRodjenja());
        target.setDrzavaRodjenja(dto.getDrzavaRodjenja());
        target.setDrzavljanstvo(dto.getDrzavljanstvo());
        target.setNacionalnost(dto.getNacionalnost());
        target.setPol(dto.getPol());
        target.setMestoPrebivalista(dto.getMestoPrebivalista());
        target.setUlicaPrebivalista(dto.getUlicaPrebivalista());
        target.setBrojPrebivalista(dto.getBrojPrebivalista());
        target.setBrojTelefona(dto.getBrojTelefona());
        target.setFakultetskiEmail(dto.getFakultetskiEmail());
        target.setPrivatniEmail(dto.getPrivatniEmail());
        target.setBrojLicneKarte(dto.getBrojLicneKarte());
        target.setIzdavalacLicneKarte(dto.getIzdavalacLicneKarte());
        target.setUspehSrednjaSkola(dto.getUspehSrednjaSkola());
        target.setUspehPrijemni(dto.getUspehPrijemni());
        target.setZavrsenaSkola(resolveSrednjaSkola(dto.getZavrsenaSkolaId()));
        target.setPrethodnaVisokoskolskaUstanova(resolveVisokoskolska(dto.getPrethodnaVisokoskolskaUstanovaId()));
    }

    private SrednjaSkola resolveSrednjaSkola(Long skolaId) {
        if (skolaId == null) {
            return null;
        }
        return srednjasSkolaRepository.findById(skolaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Srednja skola not found: " + skolaId));
    }

    private VSU resolveVisokoskolska(Long vsuId) {
        if (vsuId == null) {
            return null;
        }
        return vsuRepository.findById(vsuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "VSU not found: " + vsuId));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<Integer> resolveSemestersForYear(Integer godinaStudija) {
        int firstSemester = (godinaStudija - 1) * 2 + 1;
        return List.of(firstSemester, firstSemester + 1);
    }

}
