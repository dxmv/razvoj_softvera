package org.raflab.studsluzba.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.model.dto.IzlazakIspitDto;
import org.raflab.studsluzba.repositories.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IzlazakIspitServiceTest {

    @Mock
    private IzlazakIspitRepository repository;
    @Mock
    private StudentPredispitnaObavezaRepository studentPredispitnaObavezaRepository;
    @Mock
    private PolozenPredmetRepository polozenPredmetRepository;
    @Mock
    private PrijavaIspitaRepository prijavaIspitaRepository;
    @Mock
    private SkolskaGodinaRepository skolskaGodinaRepository;
    @Mock
    private StudentskiIndeksRepository studentskiIndeksRepository;
    @Mock
    private PredmetRepository predmetRepository;

    @InjectMocks
    private IzlazakIspitService izlazakIspitService;

    private PrijavaIspita prijava;
    private Indeks indeks;
    private Predmet predmet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        predmet = new Predmet();
        predmet.setId(11L);
        Ispit ispit = new Ispit();
        ispit.setPredmet(predmet);
        indeks = new Indeks();
        indeks.setId(1L);
        prijava = PrijavaIspita.builder()
                .id(5L)
                .ispit(ispit)
                .studentskiIndeks(indeks)
                .build();
    }

    @Test
    void testDodajIzlazak_SavesAndMarksPassed() {
        when(prijavaIspitaRepository.findById(5L)).thenReturn(Optional.of(prijava));
        when(polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet)).thenReturn(false);
        SkolskaGodina godina = new SkolskaGodina();
        godina.setId(2L);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(godina));
        when(studentPredispitnaObavezaRepository.findUkupniPredispitniPoeni(1L, 11L, 2L)).thenReturn(30.0);
        when(repository.save(any(IzlazakIspit.class))).thenAnswer(invocation -> {
            IzlazakIspit saved = invocation.getArgument(0);
            saved.setId(70L);
            return saved;
        });

        IzlazakIspitDto request = IzlazakIspitDto.builder()
                .prijavaIspitaId(5L)
                .poeniSaIspita(25.0)
                .ponistavanIspit(false)
                .build();

        IzlazakIspitDto result = izlazakIspitService.dodajIzlazak(request);

        assertEquals(70L, result.getId());
        assertEquals(55.0, result.getUkupnoPoena());

        ArgumentCaptor<PolozenPredmet> captor = ArgumentCaptor.forClass(PolozenPredmet.class);
        verify(polozenPredmetRepository).save(captor.capture());
        assertEquals(6, captor.getValue().getOcena());
    }

    @Test
    void testDodajIzlazak_AlreadyPassedThrows() {
        when(prijavaIspitaRepository.findById(5L)).thenReturn(Optional.of(prijava));
        when(polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet)).thenReturn(true);

        IzlazakIspitDto request = IzlazakIspitDto.builder()
                .prijavaIspitaId(5L)
                .poeniSaIspita(20.0)
                .ponistavanIspit(false)
                .build();

        assertThrows(ResponseStatusException.class,
                () -> izlazakIspitService.dodajIzlazak(request));
        verify(repository, never()).save(any());
    }

    @Test
    void testDodajIzlazak_NoActiveYear() {
        when(prijavaIspitaRepository.findById(5L)).thenReturn(Optional.of(prijava));
        when(polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet)).thenReturn(false);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.empty());

        IzlazakIspitDto request = IzlazakIspitDto.builder()
                .prijavaIspitaId(5L)
                .poeniSaIspita(20.0)
                .ponistavanIspit(false)
                .build();

        assertThrows(ResponseStatusException.class,
                () -> izlazakIspitService.dodajIzlazak(request));
    }

    @Test
    void testDodajIzlazak_NotEnoughPointsDoesNotSavePassed() {
        when(prijavaIspitaRepository.findById(5L)).thenReturn(Optional.of(prijava));
        when(polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet)).thenReturn(false);
        SkolskaGodina godina = new SkolskaGodina();
        godina.setId(2L);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(godina));
        when(studentPredispitnaObavezaRepository.findUkupniPredispitniPoeni(1L, 11L, 2L)).thenReturn(20.0);
        when(repository.save(any(IzlazakIspit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IzlazakIspitDto request = IzlazakIspitDto.builder()
                .prijavaIspitaId(5L)
                .poeniSaIspita(25.0)
                .ponistavanIspit(false)
                .build();

        IzlazakIspitDto result = izlazakIspitService.dodajIzlazak(request);

        assertEquals(45.0, result.getUkupnoPoena());
        verify(polozenPredmetRepository, never()).save(any());
    }

    @Test
    void testDodajIzlazak_PonistavanSkipSavingPassed() {
        when(prijavaIspitaRepository.findById(5L)).thenReturn(Optional.of(prijava));
        when(polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet)).thenReturn(false);
        SkolskaGodina godina = new SkolskaGodina();
        godina.setId(2L);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(godina));
        when(studentPredispitnaObavezaRepository.findUkupniPredispitniPoeni(1L, 11L, 2L)).thenReturn(40.0);
        when(repository.save(any(IzlazakIspit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IzlazakIspitDto request = IzlazakIspitDto.builder()
                .prijavaIspitaId(5L)
                .poeniSaIspita(20.0)
                .ponistavanIspit(true)
                .build();

        izlazakIspitService.dodajIzlazak(request);

        verify(polozenPredmetRepository, never()).save(any());
    }

    @Test
    void testGetBrojIzlasakaNaPredmet_Success() {
        when(studentskiIndeksRepository.existsById(1L)).thenReturn(true);
        when(predmetRepository.existsById(11L)).thenReturn(true);
        when(repository.countByStudentAndPredmet(1L, 11L)).thenReturn(4L);

        Long count = izlazakIspitService.getBrojIzlasakaNaPredmet(1L, 11L);
        assertEquals(4L, count);
    }

    @Test
    void testGetBrojIzlasaka_IndexMissing() {
        when(studentskiIndeksRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> izlazakIspitService.getBrojIzlasakaNaPredmet(1L, 11L));
    }

    @Test
    void testGetBrojIzlasaka_PredmetMissing() {
        when(studentskiIndeksRepository.existsById(1L)).thenReturn(true);
        when(predmetRepository.existsById(11L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> izlazakIspitService.getBrojIzlasakaNaPredmet(1L, 11L));
        verify(repository, never()).countByStudentAndPredmet(anyLong(), anyLong());
    }
}
