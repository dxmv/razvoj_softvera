package org.raflab.studsluzba.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.model.dto.IzlazakIspitDto;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.service.IzlazakIspitService;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private IzlazakIspitService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    // ------------------------------------------------------------------
    // 1) Prijava ne postoji
    // ------------------------------------------------------------------
    @Test
    void testDodajIzlazak_PrijavaNePostoji() {
        when(prijavaIspitaRepository.findById(1L)).thenReturn(Optional.empty());

        IzlazakIspitDto dto = new IzlazakIspitDto();
        dto.setPrijavaIspitaId(1L);

        assertThrows(ResponseStatusException.class, () -> service.dodajIzlazak(dto));
    }

    // ------------------------------------------------------------------
    // 2) Student već položio predmet
    // ------------------------------------------------------------------
    @Test
    void testDodajIzlazak_VecPolozioPredmet() {

        Predmet predmet = new Predmet();
        predmet.setId(10L);
        predmet.setNaziv("Matematika");

        Indeks indeks = new Indeks();
        indeks.setId(5L);

        Ispit ispit = new Ispit();
        ispit.setPredmet(predmet);

        PrijavaIspita prijava = new PrijavaIspita();
        prijava.setId(1L);
        prijava.setStudentskiIndeks(indeks);
        prijava.setIspit(ispit);

        when(prijavaIspitaRepository.findById(1L)).thenReturn(Optional.of(prijava));
        when(polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet))
                .thenReturn(true);

        IzlazakIspitDto dto = new IzlazakIspitDto();
        dto.setPrijavaIspitaId(1L);

        assertThrows(ResponseStatusException.class, () -> service.dodajIzlazak(dto));
    }

    // ------------------------------------------------------------------
    // 3) Nema aktivne školske godine
    // ------------------------------------------------------------------
    @Test
    void testDodajIzlazak_NemaAktivneSkolskeGodine() {

        Predmet predmet = new Predmet();
        predmet.setId(10L);

        Indeks indeks = new Indeks();
        indeks.setId(5L);

        Ispit ispit = new Ispit();
        ispit.setPredmet(predmet);

        PrijavaIspita prijava = new PrijavaIspita();
        prijava.setStudentskiIndeks(indeks);
        prijava.setIspit(ispit);

        when(prijavaIspitaRepository.findById(1L)).thenReturn(Optional.of(prijava));
        when(polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet))
                .thenReturn(false);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina())
                .thenReturn(Optional.empty());

        IzlazakIspitDto dto = new IzlazakIspitDto();
        dto.setPrijavaIspitaId(1L);

        assertThrows(ResponseStatusException.class, () -> service.dodajIzlazak(dto));
    }

    // ------------------------------------------------------------------
    // 4) Uspešan izlazak (NE POLOŽI predmet)
    // ------------------------------------------------------------------
    @Test
    void testDodajIzlazak_Uspeh_Nepolozio() {

        Predmet predmet = new Predmet();
        predmet.setId(10L);

        Indeks indeks = new Indeks();
        indeks.setId(5L);

        Ispit ispit = new Ispit();
        ispit.setPredmet(predmet);

        PrijavaIspita prijava = new PrijavaIspita();
        prijava.setStudentskiIndeks(indeks);
        prijava.setIspit(ispit);

        SkolskaGodina godina = new SkolskaGodina();
        godina.setId(3L);

        when(prijavaIspitaRepository.findById(1L)).thenReturn(Optional.of(prijava));
        when(polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet))
                .thenReturn(false);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(godina));
        when(studentPredispitnaObavezaRepository.findUkupniPredispitniPoeni(5L, 10L, 3L))
                .thenReturn(20.0);

        IzlazakIspitDto dto = new IzlazakIspitDto();
        dto.setPrijavaIspitaId(1L);
        dto.setPoeniSaIspita(20.0);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        IzlazakIspitDto rezultat = service.dodajIzlazak(dto);

        assertNotNull(rezultat);
        verify(polozenPredmetRepository, never()).save(any());
    }

    // ------------------------------------------------------------------
    // 5) Uspešan izlazak (POLOŽIO predmet → čuva se PolozenPredmet)
    // ------------------------------------------------------------------
    @Test
    void testDodajIzlazak_Uspeh_Polozio() {

        Predmet predmet = new Predmet();
        predmet.setId(10L);

        Indeks indeks = new Indeks();
        indeks.setId(5L);

        Ispit ispit = new Ispit();
        ispit.setPredmet(predmet);

        PrijavaIspita prijava = new PrijavaIspita();
        prijava.setStudentskiIndeks(indeks);
        prijava.setIspit(ispit);

        SkolskaGodina godina = new SkolskaGodina();
        godina.setId(3L);

        when(prijavaIspitaRepository.findById(1L)).thenReturn(Optional.of(prijava));
        when(polozenPredmetRepository.existsByStudentskiIndeksAndPredmet(indeks, predmet))
                .thenReturn(false);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(godina));

        // predispit + ispit >= 51
        when(studentPredispitnaObavezaRepository.findUkupniPredispitniPoeni(5L, 10L, 3L))
                .thenReturn(30.0);

        IzlazakIspitDto dto = new IzlazakIspitDto();
        dto.setPrijavaIspitaId(1L);
        dto.setPoeniSaIspita(30.0);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.dodajIzlazak(dto);

        verify(polozenPredmetRepository, times(1)).save(any());
    }

    // ------------------------------------------------------------------
    // 6) Broj izlasaka — uspeh
    // ------------------------------------------------------------------
    @Test
    void testGetBrojIzlasaka_Uspeh() {
        when(studentskiIndeksRepository.existsById(5L)).thenReturn(true);
        when(predmetRepository.existsById(10L)).thenReturn(true);
        when(repository.countByStudentAndPredmet(5L, 10L)).thenReturn(4L);

        Long result = service.getBrojIzlasakaNaPredmet(5L, 10L);

        assertEquals(4L, result);
    }

    // ------------------------------------------------------------------
    // 7) Broj izlasaka — indeks ne postoji
    // ------------------------------------------------------------------
    @Test
    void testGetBrojIzlasaka_IndeksNotFound() {
        when(studentskiIndeksRepository.existsById(5L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> service.getBrojIzlasakaNaPredmet(5L, 10L));
    }

    // ------------------------------------------------------------------
    // 8) Broj izlasaka — predmet ne postoji
    // ------------------------------------------------------------------
    @Test
    void testGetBrojIzlasaka_PredmetNotFound() {
        when(studentskiIndeksRepository.existsById(5L)).thenReturn(true);
        when(predmetRepository.existsById(10L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> service.getBrojIzlasakaNaPredmet(5L, 10L));
    }
}
