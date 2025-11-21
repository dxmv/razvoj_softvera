package org.raflab.studsluzba.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.raflab.studsluzba.repositories.StudentPredispitnaObavezaRepository;
import org.raflab.studsluzba.repositories.StudentskiIndeksRepository;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentPredispitnaObavezaServiceTest {

    @Mock
    private StudentPredispitnaObavezaRepository repository;
    @Mock
    private StudentskiIndeksRepository studentskiIndeksRepository;
    @Mock
    private PredmetRepository predmetRepository;
    @Mock
    private SkolskaGodinaRepository skolskaGodinaRepository;

    @InjectMocks
    private StudentPredispitnaObavezaService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetUkupniPredispitniPoeni_Success() {
        when(studentskiIndeksRepository.existsById(1L)).thenReturn(true);
        when(predmetRepository.existsById(2L)).thenReturn(true);
        when(skolskaGodinaRepository.existsById(3L)).thenReturn(true);
        when(repository.findUkupniPredispitniPoeni(1L, 2L, 3L)).thenReturn(42.5);

        Double result = service.getUkupniPredispitniPoeni(1L, 2L, 3L);
        assertEquals(42.5, result);
    }

    @Test
    void testGetUkupniPredispitniPoeni_ReturnsZeroWhenNull() {
        when(studentskiIndeksRepository.existsById(1L)).thenReturn(true);
        when(predmetRepository.existsById(2L)).thenReturn(true);
        when(skolskaGodinaRepository.existsById(3L)).thenReturn(true);
        when(repository.findUkupniPredispitniPoeni(1L, 2L, 3L)).thenReturn(null);

        Double result = service.getUkupniPredispitniPoeni(1L, 2L, 3L);
        assertEquals(0.0, result);
    }

    @Test
    void testGetUkupniPredispitniPoeni_IndexMissing() {
        when(studentskiIndeksRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> service.getUkupniPredispitniPoeni(1L, 2L, 3L));
    }

    @Test
    void testGetUkupniPredispitniPoeni_PredmetMissing() {
        when(studentskiIndeksRepository.existsById(1L)).thenReturn(true);
        when(predmetRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> service.getUkupniPredispitniPoeni(1L, 2L, 3L));
    }

    @Test
    void testGetUkupniPredispitniPoeni_SkolskaGodinaMissing() {
        when(studentskiIndeksRepository.existsById(1L)).thenReturn(true);
        when(predmetRepository.existsById(2L)).thenReturn(true);
        when(skolskaGodinaRepository.existsById(3L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> service.getUkupniPredispitniPoeni(1L, 2L, 3L));
    }
}
