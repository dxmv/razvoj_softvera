package org.raflab.studsluzba.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.model.dto.PrijavaIspitaDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzba.repositories.IspitRepository;
import org.raflab.studsluzba.repositories.PrijavaIspitaRepository;
import org.raflab.studsluzba.repositories.StudentPredmetRepository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PrijavaIspitaServiceTest {

    @Mock
    private PrijavaIspitaRepository prijavaIspitaRepository;
    @Mock
    private IspitRepository ispitRepository;
    @Mock
    private StudentPredmetRepository studentPredmetRepository;

    @InjectMocks
    private PrijavaIspitaService prijavaIspitaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFindPrijavljeniZaIspit_ReturnsStudents() {
        Ispit ispit = new Ispit();
        when(ispitRepository.findById(10L)).thenReturn(Optional.of(ispit));

        Student student = new Student();
        student.setIme("Ana");
        student.setPrezime("Jovanovic");
        Indeks indeks = new Indeks();
        indeks.setStudent(student);

        PrijavaIspita prijava = PrijavaIspita.builder()
                .studentskiIndeks(indeks)
                .ispit(ispit)
                .datumPrijave(LocalDateTime.now())
                .build();

        when(prijavaIspitaRepository.findByIspit(ispit)).thenReturn(Collections.singletonList(prijava));

        List<StudentDto> result = prijavaIspitaService.findPrijavljeniZaIspit(10L);

        assertEquals(1, result.size());
        assertEquals("Ana", result.get(0).getIme());
        verify(prijavaIspitaRepository).findByIspit(ispit);
    }

    @Test
    void testFindPrijavljeniZaIspit_IspitMissing() {
        when(ispitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> prijavaIspitaService.findPrijavljeniZaIspit(99L));
    }

    @Test
    void testPrijaviIspit_Success() {
        Predmet predmet = new Predmet();
        predmet.setId(77L);
        Ispit ispit = new Ispit();
        ispit.setId(15L);
        ispit.setPredmet(predmet);

        when(ispitRepository.findById(15L)).thenReturn(Optional.of(ispit));

        Indeks indeks = new Indeks();
        indeks.setId(3L);
        StudentPredmet studentPredmet = StudentPredmet.builder()
                .indeks(indeks)
                .build();

        when(studentPredmetRepository
                .findByIndeksIdAndNastavnikPredmetPredmetIdAndSkolskaGodinaAktivna(3L, 77L))
                .thenReturn(Optional.of(studentPredmet));
        when(prijavaIspitaRepository.existsByIspitAndStudentskiIndeks(ispit, indeks)).thenReturn(false);
        when(prijavaIspitaRepository.save(any(PrijavaIspita.class))).thenAnswer(invocation -> {
            PrijavaIspita saved = invocation.getArgument(0);
            saved.setId(200L);
            return saved;
        });

        PrijavaIspitaDto request = PrijavaIspitaDto.builder()
                .ispitId(15L)
                .studentskiIndeksId(3L)
                .build();

        PrijavaIspitaDto created = prijavaIspitaService.prijaviIspit(request);

        assertEquals(200L, created.getId());
        assertEquals(15L, created.getIspitId());
        assertEquals(3L, created.getStudentskiIndeksId());
        verify(prijavaIspitaRepository).save(any(PrijavaIspita.class));
    }

    @Test
    void testPrijaviIspit_IspitNotFound() {
        when(ispitRepository.findById(1L)).thenReturn(Optional.empty());

        PrijavaIspitaDto request = PrijavaIspitaDto.builder()
                .ispitId(1L)
                .studentskiIndeksId(3L)
                .build();

        assertThrows(ResponseStatusException.class,
                () -> prijavaIspitaService.prijaviIspit(request));
    }

    @Test
    void testPrijaviIspit_StudentNotAttendingSubject() {
        Predmet predmet = new Predmet();
        predmet.setId(77L);
        Ispit ispit = new Ispit();
        ispit.setId(15L);
        ispit.setPredmet(predmet);
        when(ispitRepository.findById(15L)).thenReturn(Optional.of(ispit));

        when(studentPredmetRepository
                .findByIndeksIdAndNastavnikPredmetPredmetIdAndSkolskaGodinaAktivna(3L, 77L))
                .thenReturn(Optional.empty());

        PrijavaIspitaDto request = PrijavaIspitaDto.builder()
                .ispitId(15L)
                .studentskiIndeksId(3L)
                .build();

        assertThrows(ResponseStatusException.class,
                () -> prijavaIspitaService.prijaviIspit(request));
    }

    @Test
    void testPrijaviIspit_AlreadyRegistered() {
        Predmet predmet = new Predmet();
        predmet.setId(77L);
        Ispit ispit = new Ispit();
        ispit.setId(15L);
        ispit.setPredmet(predmet);
        when(ispitRepository.findById(15L)).thenReturn(Optional.of(ispit));

        Indeks indeks = new Indeks();
        indeks.setId(3L);
        StudentPredmet studentPredmet = StudentPredmet.builder()
                .indeks(indeks)
                .build();
        when(studentPredmetRepository
                .findByIndeksIdAndNastavnikPredmetPredmetIdAndSkolskaGodinaAktivna(3L, 77L))
                .thenReturn(Optional.of(studentPredmet));
        when(prijavaIspitaRepository.existsByIspitAndStudentskiIndeks(ispit, indeks)).thenReturn(true);

        PrijavaIspitaDto request = PrijavaIspitaDto.builder()
                .ispitId(15L)
                .studentskiIndeksId(3L)
                .build();

        assertThrows(ResponseStatusException.class,
                () -> prijavaIspitaService.prijaviIspit(request));
        verify(prijavaIspitaRepository, never()).save(any());
    }
}
