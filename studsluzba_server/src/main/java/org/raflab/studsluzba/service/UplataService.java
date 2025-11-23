package org.raflab.studsluzba.service;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.client.ExchangeRateClient;
import org.raflab.studsluzba.mapper.EntityMapper;
import org.raflab.studsluzba.model.SkolskaGodina;
import org.raflab.studsluzba.model.Student;
import org.raflab.studsluzba.model.Uplata;
import org.raflab.studsluzba.model.dto.CreateUplataRequest;
import org.raflab.studsluzba.model.dto.ExchangeRateDto;
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
    private final ExchangeRateClient exchangeRateClient;

    public Uplata create(Uplata entity) {
        return repository.save(entity);
    }

    public Uplata findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Uplata not found: " + id));
    }

    public UplataDto findDtoById(Long id) {
        Uplata uplata = findById(id);
        return EntityMapper.toDto(uplata);
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

        SkolskaGodina skolskaGodina = skolskaGodinaRepository.findAktivnaSkolskaGodina()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nema aktivne skolske fodine"));

        if (request.getIznosUDinarima() == null || request.getIznosUDinarima().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Iznos u dinarima mora biti veći od nule");
        }

        BigDecimal rate = fetchTodayMiddleRate();

        // datum uplate je opcionalan
        LocalDate datumUplate = request.getDatumUplate() != null ? request.getDatumUplate() : LocalDate.now();

        Uplata uplata = Uplata.builder()
                .student(student)
                .skolskaGodina(skolskaGodina)
                .datumUplate(datumUplate)
                .iznosUDinarima(request.getIznosUDinarima())
                .srednjiKurs(rate)
                .build();

        Uplata saved = repository.save(uplata);
        return EntityMapper.toDto(saved);
    }

    public RemainingTuitionDto getRemainingTuition(Long studentId) {
        // prvo trazimo studenta
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Student not found: " + studentId));
        // pa trenutnu aktivnu godinu
        SkolskaGodina skolskaGodina = skolskaGodinaRepository.findAktivnaSkolskaGodina()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nema aktivne skolske fodine"));

        // suma svih uplata u evrima
        BigDecimal paidInEur = sumUplataEur(studentId,skolskaGodina.getId());

        BigDecimal remainingEur = TUITION_EUR.subtract(paidInEur);
        if (remainingEur.compareTo(BigDecimal.ZERO) < 0) {
            remainingEur = BigDecimal.ZERO;
        }

        BigDecimal appliedRate = fetchTodayMiddleRate();

        BigDecimal remainingRsd = remainingEur.multiply(appliedRate).setScale(2, RoundingMode.HALF_UP);

        return RemainingTuitionDto.builder()
                .preostaloEur(remainingEur.setScale(2, RoundingMode.HALF_UP))
                .preostaloRsd(remainingRsd)
                .primenjeniKurs(appliedRate)
                .build();
    }

    private BigDecimal sumUplataEur(Long studentId, Long skolskaGodinaId){
        // sve uplate ove godine za studenta
        List<Uplata> uplate = repository.findByStudentIdAndSkolskaGodinaId(studentId,skolskaGodinaId);
        
        return uplate.stream()
                .map(u -> convertToEur(u.getIznosUDinarima(), u.getSrednjiKurs()))
                .reduce(BigDecimal.ZERO, (prev, current) -> prev.add(current));
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

    public ExchangeRateDto getTodayExchangeRate() {
        return exchangeRateClient.fetchTodayEurRate();
    }

    private BigDecimal fetchTodayMiddleRate() {
        ExchangeRateDto rateDto = exchangeRateClient.fetchTodayEurRate();
        BigDecimal middle = rateDto.getExchangeMiddle();
        if (middle == null || middle.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Greska sa kursom");
        }
        return middle;
    }
}
