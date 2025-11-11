package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.SkolskaGodina;
import org.raflab.studsluzba.model.Student;
import org.raflab.studsluzba.model.Uplata;
import org.raflab.studsluzba.model.dto.CreateUplataRequest;
import org.raflab.studsluzba.model.dto.RemainingTuitionDto;
import org.raflab.studsluzba.model.dto.UplataDto;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.raflab.studsluzba.repositories.StudentRepository;
import org.raflab.studsluzba.repositories.UplataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UplataService {

    private static final BigDecimal TUITION_EUR = new BigDecimal("3000");

    private final UplataRepository repository;
    private final StudentRepository studentRepository;
    private final SkolskaGodinaRepository skolskaGodinaRepository;

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
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Student not found: " + studentId));

        if (request.getSkolskaGodinaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Skolska godina je obavezna");
        }

        SkolskaGodina skolskaGodina = skolskaGodinaRepository.findById(request.getSkolskaGodinaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Skolska godina not found: " + request.getSkolskaGodinaId()));

        if (Boolean.FALSE.equals(skolskaGodina.getAktivna())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Skolska godina mora biti aktivna za evidentiranje uplate");
        }

        if (request.getIznosUDinarima() == null || request.getIznosUDinarima().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Iznos u dinarima mora biti veći od nule");
        }
        if (request.getSrednjiKurs() == null || request.getSrednjiKurs().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Srednji kurs mora biti veći od nule");
        }

        LocalDate datumUplate = request.getDatumUplate() != null ? request.getDatumUplate() : LocalDate.now();

        Uplata uplata = Uplata.builder()
                .student(student)
                .skolskaGodina(skolskaGodina)
                .datumUplate(datumUplate)
                .iznosUDinarima(request.getIznosUDinarima())
                .srednjiKurs(request.getSrednjiKurs())
                .build();

        Uplata saved = repository.save(uplata);
        return toDto(saved);
    }

    public RemainingTuitionDto getRemainingTuition(Long studentId, Long skolskaGodinaId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Student not found: " + studentId));

        SkolskaGodina skolskaGodina = skolskaGodinaRepository.findById(skolskaGodinaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Skolska godina not found: " + skolskaGodinaId));

        if (Boolean.FALSE.equals(skolskaGodina.getAktivna())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Skolska godina mora biti aktivna");
        }

        List<Uplata> uplate = repository.findByStudentIdAndSkolskaGodinaId(student.getId(), skolskaGodina.getId());

        BigDecimal paidInEur = uplate.stream()
                .map(u -> convertToEur(u.getIznosUDinarima(), u.getSrednjiKurs()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingEur = TUITION_EUR.subtract(paidInEur);
        if (remainingEur.compareTo(BigDecimal.ZERO) < 0) {
            remainingEur = BigDecimal.ZERO;
        }

        BigDecimal appliedRate = uplate.stream()
                .max(Comparator.comparing(Uplata::getDatumUplate).thenComparing(Uplata::getId))
                .map(Uplata::getSrednjiKurs)
                .orElse(BigDecimal.ONE);

        BigDecimal remainingRsd = remainingEur.multiply(appliedRate).setScale(2, RoundingMode.HALF_UP);

        return RemainingTuitionDto.builder()
                .preostaloEur(remainingEur.setScale(2, RoundingMode.HALF_UP))
                .preostaloRsd(remainingRsd)
                .primenjeniKurs(appliedRate)
                .build();
    }

    private UplataDto toDto(Uplata entity) {
        return UplataDto.builder()
                .id(entity.getId())
                .studentId(entity.getStudent() != null ? entity.getStudent().getId() : null)
                .skolskaGodinaId(entity.getSkolskaGodina() != null ? entity.getSkolskaGodina().getId() : null)
                .datumUplate(entity.getDatumUplate())
                .iznosUDinarima(entity.getIznosUDinarima())
                .srednjiKurs(entity.getSrednjiKurs())
                .build();
    }

    private BigDecimal convertToEur(BigDecimal iznosRsd, BigDecimal kurs) {
        if (kurs == null || kurs.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Srednji kurs mora biti pozitivan");
        }
        return iznosRsd.divide(kurs, 4, RoundingMode.HALF_UP);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Uplata not found: " + id);
        }
        repository.deleteById(id);
    }
}
