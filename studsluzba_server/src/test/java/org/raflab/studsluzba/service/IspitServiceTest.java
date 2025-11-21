package org.raflab.studsluzba.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.model.dto.RezultatIspitaStudentDto;
import org.raflab.studsluzba.repositories.IspitRepository;
import org.raflab.studsluzba.repositories.IzlazakIspitRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IspitServiceTest {

    @Mock
    private IspitRepository repository;
    @Mock
    private IzlazakIspitRepository izlazakIspitRepository;
    @Mock
    private IspitRepository ispitRepository;
    private IspitService ispitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ispitService = new IspitService(repository, izlazakIspitRepository, ispitRepository);
    }

    @Test
    void testGetProsecnaOcenaNaIspitu_ReturnsAverage() {
        PolozenPredmet prvi = PolozenPredmet.builder().ocena(8).build();
        PolozenPredmet drugi = PolozenPredmet.builder().ocena(10).build();
        when(repository.findByIspitId(5L)).thenReturn(Arrays.asList(prvi, drugi));

        Double avg = ispitService.getProsecnaOcenaNaIspitu(5L);

        assertEquals(9.0, avg);
        verify(repository).findByIspitId(5L);
    }

    @Test
    void testGetProsecnaOcenaNaIspitu_NoResults() {
        when(repository.findByIspitId(6L)).thenReturn(Collections.emptyList());

        assertNull(ispitService.getProsecnaOcenaNaIspitu(6L));
    }

    @Test
    void testGetRezultatiIspitaSorted_Success() {
        when(ispitRepository.existsById(4L)).thenReturn(true);

        StudProgram program = new StudProgram();
        program.setOznaka("RI");

        Student student = new Student();
        student.setIme("Petar");
        student.setPrezime("Petrovic");

        Indeks indeks = new Indeks();
        indeks.setStudent(student);
        indeks.setStudijskiProgram(program);
        indeks.setGodinaUpisa(2020);
        indeks.setBrojIndeksa(12);

        PrijavaIspita prijava = PrijavaIspita.builder()
                .studentskiIndeks(indeks)
                .build();

        IzlazakIspit izlazak = IzlazakIspit.builder()
                .prijavaIspita(prijava)
                .ukupnoPoena(87.5)
                .build();

        when(izlazakIspitRepository.findRezultatiByIspitSorted(4L))
                .thenReturn(Collections.singletonList(izlazak));

        List<RezultatIspitaStudentDto> result = ispitService.getRezultatiIspitaSorted(4L);

        assertEquals(1, result.size());
        RezultatIspitaStudentDto dto = result.get(0);
        assertEquals("RI", dto.getStudProgramOznaka());
        assertEquals("2020/0012", dto.getBrojIndeksa());
        assertEquals(87.5, dto.getUkupnoPoena());
    }

    @Test
    void testGetRezultatiIspitaSorted_IspitMissing() {
        when(ispitRepository.existsById(4L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> ispitService.getRezultatiIspitaSorted(4L));
        verifyNoInteractions(izlazakIspitRepository);
    }
}
