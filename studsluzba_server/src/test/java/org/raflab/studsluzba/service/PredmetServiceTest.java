package org.raflab.studsluzba.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raflab.studsluzba.model.Predmet;
import org.raflab.studsluzba.model.StudProgram;
import org.raflab.studsluzba.model.dto.PredmetCreateDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.repositories.PolozenPredmetRepository;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.raflab.studsluzba.repositories.StudProgramRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PredmetServiceTest {

    @Mock
    private PredmetRepository predmetRepository;
    @Mock
    private StudProgramRepository studProgramRepository;
    @Mock
    private PolozenPredmetRepository polozenPredmetRepository;

    @InjectMocks
    private PredmetService predmetService;

    private StudProgram studProgram;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        studProgram = new StudProgram();
        studProgram.setId(5L);
    }

    // predmeti i studijski program

    @Test
    void testFindPredmetiByStudProgram_Success() {
        Predmet predmet = Predmet.builder()
                .id(1L)
                .sifra("MAT101")
                .naziv("Matematika")
                .opis("Opis")
                .espbBodovi(8)
                .semestar(1)
                .brPredavanja(30)
                .brVezbi(30)
                .studijskiProgram(studProgram)
                .build();

        Page<Predmet> page = new PageImpl<>(Collections.singletonList(predmet));
        Pageable pageable = Pageable.unpaged();

        when(predmetRepository.findByStudijskiProgramId(5L, pageable)).thenReturn(page);

        Page<PredmetDto> result = predmetService.findPredmetiByStudijskiProgram(5L, pageable);

        assertEquals(1, result.getTotalElements());
        PredmetDto dto = result.getContent().get(0);
        assertEquals("MAT101", dto.getSifra());
        assertEquals(5L, dto.getStudijskiProgramId());
    }

    // svi predmeti

    @Test
    void testGetAllPredmeti_Success() {
        Predmet predmet = Predmet.builder()
                .id(2L)
                .sifra("PHY201")
                .naziv("Fizika")
                .espbBodovi(7)
                .semestar(2)
                .studijskiProgram(studProgram)
                .build();

        Page<Predmet> page = new PageImpl<>(Collections.singletonList(predmet));
        Pageable pageable = Pageable.unpaged();
        when(predmetRepository.findAll(pageable)).thenReturn(page);

        Page<PredmetDto> result = predmetService.getAllPredmeti(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Fizika", result.getContent().get(0).getNaziv());
        verify(predmetRepository).findAll(pageable);
    }

    // dodavanje

    @Test
    void testCreatePredmet_Success() {
        PredmetCreateDto dto = PredmetCreateDto.builder()
                .sifra("INF101")
                .naziv("Informatika")
                .opis("Osnove")
                .espbBodovi(6)
                .semestar(1)
                .brPredavanja(30)
                .brVezbi(30)
                .studijskiProgramId(5L)
                .build();

        when(predmetRepository.existsBySifra("INF101")).thenReturn(false);
        when(studProgramRepository.findById(5L)).thenReturn(Optional.of(studProgram));
        when(predmetRepository.save(any(Predmet.class))).thenAnswer(invocation -> {
            Predmet saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        PredmetDto created = predmetService.createPredmet(dto);

        assertEquals(10L, created.getId());
        assertEquals("INF101", created.getSifra());
        assertEquals(5L, created.getStudijskiProgramId());
    }

    @Test
    void testCreatePredmet_DuplicateCodeThrows() {
        PredmetCreateDto dto = PredmetCreateDto.builder()
                .sifra("INF101")
                .studijskiProgramId(5L)
                .build();

        when(predmetRepository.existsBySifra("INF101")).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> predmetService.createPredmet(dto));
    }

    @Test
    void testCreatePredmet_StudProgramMissing() {
        PredmetCreateDto dto = PredmetCreateDto.builder()
                .sifra("INF101")
                .studijskiProgramId(5L)
                .build();

        when(predmetRepository.existsBySifra("INF101")).thenReturn(false);
        when(studProgramRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> predmetService.createPredmet(dto));
    }

    // procena ocena

    @Test
    void testGetAverageGrade_SubjectNotFound() {
        when(predmetRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> predmetService.getAverageGradeForSubjectInYearRange(1L, 2015, 2018));
    }

    @Test
    void testGetAverageGrade_ReturnsZeroWhenNull() {
        when(predmetRepository.existsById(1L)).thenReturn(true);
        when(polozenPredmetRepository.findProsecnaOcenaZaPredmetUGodinama(1L, 2015, 2018)).thenReturn(null);

        Double avg = predmetService.getAverageGradeForSubjectInYearRange(1L, 2015, 2018);
        assertEquals(0.0, avg);
    }

    @Test
    void testGetAverageGrade_ReturnsValue() {
        when(predmetRepository.existsById(2L)).thenReturn(true);
        when(polozenPredmetRepository.findProsecnaOcenaZaPredmetUGodinama(2L, 2015, 2018))
                .thenReturn(8.75);

        Double avg = predmetService.getAverageGradeForSubjectInYearRange(2L, 2015, 2018);
        assertEquals(8.75, avg);
    }
}
