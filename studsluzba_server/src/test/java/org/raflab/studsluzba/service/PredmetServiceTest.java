package org.raflab.studsluzba.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.model.Predmet;
import org.raflab.studsluzba.model.StudProgram;
import org.raflab.studsluzba.model.dto.PredmetCreateDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.raflab.studsluzba.repositories.StudProgramRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredmetServiceTest {

    @Mock
    PredmetRepository repository;

    @Mock
    StudProgramRepository studProgramRepository;

    @InjectMocks
    PredmetService service;

    private Predmet predmet(Long id, String sifra, String naziv, StudProgram studProgram) {
        return Predmet.builder()
                .id(id)
                .sifra(sifra)
                .naziv(naziv)
                .studijskiProgram(studProgram)
                .build();
    }

    private StudProgram studProgram(Long id, String oznaka) {
        StudProgram sp = new StudProgram();
        sp.setId(id);
        sp.setOznaka(oznaka);
        return sp;
    }

    private PredmetCreateDto createDto(String sifra, String naziv, Long studProgramId) {
        PredmetCreateDto dto = new PredmetCreateDto();
        dto.setSifra(sifra);
        dto.setNaziv(naziv);
        dto.setStudijskiProgramId(studProgramId);
        dto.setEspbBodovi(6);
        dto.setSemestar(1);
        return dto;
    }

    // ---------- create ----------

    @Nested
    class CreateTests {

        @Test
        @DisplayName("Creates and returns saved Predmet")
        void createsPredmet() {
            Predmet input = predmet(null, "MAT1", "Matematika 1", studProgram(1L, "RN"));
            Predmet saved = predmet(10L, "MAT1", "Matematika 1", studProgram(1L, "RN"));

            when(repository.existsBySifra("MAT1")).thenReturn(false);
            when(repository.save(input)).thenReturn(saved);

            Predmet result = service.create(input);

            assertNotNull(result);
            assertEquals(10L, result.getId());
            assertEquals("Matematika 1", result.getNaziv());
            verify(repository).existsBySifra("MAT1");
            verify(repository).save(input);
        }

        @Test
        @DisplayName("Throws exception when ID is provided")
        void throwsWhenIdProvided() {
            Predmet input = predmet(5L, "MAT1", "Matematika 1", studProgram(1L, "RN"));

            assertThrows(ResponseStatusException.class, () -> service.create(input));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Throws exception when Sifra already exists")
        void throwsWhenSifraExists() {
            Predmet input = predmet(null, "MAT1", "Matematika 1", studProgram(1L, "RN"));

            when(repository.existsBySifra("MAT1")).thenReturn(true);

            assertThrows(ResponseStatusException.class, () -> service.create(input));
            verify(repository).existsBySifra("MAT1");
            verify(repository, never()).save(any());
        }
    }

    // ---------- findById ----------

    @Nested
    class FindByIdTests {

        @Test
        @DisplayName("Returns Predmet when found")
        void returnsPredmet() {
            Long predmetId = 5L;
            Predmet predmet = predmet(predmetId, "MAT1", "Matematika 1", studProgram(1L, "RN"));

            when(repository.findById(predmetId)).thenReturn(Optional.of(predmet));

            Predmet result = service.findById(predmetId);

            assertNotNull(result);
            assertEquals(predmetId, result.getId());
            assertEquals("Matematika 1", result.getNaziv());
            verify(repository).findById(predmetId);
        }

        @Test
        @DisplayName("Throws exception when Predmet not found")
        void throwsWhenNotFound() {
            Long predmetId = 99L;

            when(repository.findById(predmetId)).thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class, () -> service.findById(predmetId));
            verify(repository).findById(predmetId);
        }
    }

    // ---------- update ----------

    @Nested
    class UpdateTests {

        @Test
        @DisplayName("Updates existing Predmet successfully")
        void updatesExistingPredmet() {
            Long predmetId = 5L;
            Predmet existing = predmet(predmetId, "MAT1", "Stari naziv", studProgram(1L, "RN"));
            Predmet updated = predmet(null, "MAT1", "Novi naziv", studProgram(1L, "RN"));
            Predmet saved = predmet(predmetId, "MAT1", "Novi naziv", studProgram(1L, "RN"));

            when(repository.findById(predmetId)).thenReturn(Optional.of(existing));
            when(repository.save(any(Predmet.class))).thenReturn(saved);

            Predmet result = service.update(predmetId, updated);

            assertNotNull(result);
            assertEquals(predmetId, result.getId());
            assertEquals("Novi naziv", result.getNaziv());
            verify(repository).findById(predmetId);
            verify(repository).save(any(Predmet.class));
        }

        @Test
        @DisplayName("Throws exception when Predmet not found")
        void throwsWhenNotFound() {
            Long predmetId = 99L;
            Predmet updated = predmet(null, "MAT1", "Novi naziv", studProgram(1L, "RN"));

            when(repository.findById(predmetId)).thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class, () -> service.update(predmetId, updated));
            verify(repository).findById(predmetId);
            verify(repository, never()).save(any());
        }
    }

    // ---------- delete ----------

    @Nested
    class DeleteTests {

        @Test
        @DisplayName("Deletes existing Predmet")
        void deletesExistingPredmet() {
            Long predmetId = 7L;

            when(repository.existsById(predmetId)).thenReturn(true);
            doNothing().when(repository).deleteById(predmetId);

            service.delete(predmetId);

            verify(repository).existsById(predmetId);
            verify(repository).deleteById(predmetId);
        }

        @Test
        @DisplayName("Throws exception when trying to delete non-existing Predmet")
        void throwsWhenDeletingNonExisting() {
            Long predmetId = 99L;

            when(repository.existsById(predmetId)).thenReturn(false);

            assertThrows(ResponseStatusException.class, () -> service.delete(predmetId));
            verify(repository).existsById(predmetId);
            verify(repository, never()).deleteById(any());
        }
    }

    // ---------- findPredmetiByStudijskiProgram ----------

    @Nested
    class FindPredmetiByStudijskiProgramTests {

        @Test
        @DisplayName("Returns paginated list of Predmet DTOs for given StudProgram")
        void returnsPaginatedPredmeti() {
            Long studProgramId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            StudProgram studProgram = studProgram(studProgramId, "RN");

            List<Predmet> predmeti = List.of(
                    predmet(1L, "MAT1", "Matematika 1", studProgram),
                    predmet(2L, "PRG1", "Programiranje 1", studProgram),
                    predmet(3L, "ALG1", "Algoritmi", studProgram)
            );

            Page<Predmet> predmetPage = new PageImpl<>(predmeti, pageable, predmeti.size());

            when(repository.findByStudijskiProgramId(studProgramId, pageable))
                    .thenReturn(predmetPage);

            Page<PredmetDto> result = service.findPredmetiByStudijskiProgram(studProgramId, pageable);

            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
            assertEquals(3, result.getContent().size());

            List<String> nazivi = result.getContent().stream()
                    .map(PredmetDto::getNaziv)
                    .collect(Collectors.toList());

            assertTrue(nazivi.contains("Matematika 1"));
            assertTrue(nazivi.contains("Programiranje 1"));
            assertTrue(nazivi.contains("Algoritmi"));

            verify(repository).findByStudijskiProgramId(studProgramId, pageable);
        }

        @Test
        @DisplayName("Returns empty page when no Predmeti found for StudProgram")
        void returnsEmptyPage() {
            Long studProgramId = 99L;
            Pageable pageable = PageRequest.of(0, 10);

            Page<Predmet> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(repository.findByStudijskiProgramId(studProgramId, pageable))
                    .thenReturn(emptyPage);

            Page<PredmetDto> result = service.findPredmetiByStudijskiProgram(studProgramId, pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());

            verify(repository).findByStudijskiProgramId(studProgramId, pageable);
        }

        @Test
        @DisplayName("Handles pagination correctly (second page)")
        void handlesPaginationCorrectly() {
            Long studProgramId = 1L;
            Pageable pageable = PageRequest.of(1, 2);
            StudProgram studProgram = studProgram(studProgramId, "RN");

            List<Predmet> predmetiPage2 = List.of(
                    predmet(3L, "BP1", "Baze podataka", studProgram),
                    predmet(4L, "WEB1", "Web programiranje", studProgram)
            );

            Page<Predmet> predmetPage = new PageImpl<>(predmetiPage2, pageable, 6);

            when(repository.findByStudijskiProgramId(studProgramId, pageable))
                    .thenReturn(predmetPage);

            Page<PredmetDto> result = service.findPredmetiByStudijskiProgram(studProgramId, pageable);

            assertNotNull(result);
            assertEquals(6, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            assertEquals(1, result.getNumber());
            assertEquals(3, result.getTotalPages());

            verify(repository).findByStudijskiProgramId(studProgramId, pageable);
        }
    }

    // ---------- getAllPredmeti ----------

    @Nested
    class GetAllPredmetiTests {

        @Test
        @DisplayName("Returns paginated list of all Predmet DTOs")
        void returnsAllPredmeti() {
            Pageable pageable = PageRequest.of(0, 10);
            StudProgram studProgram1 = studProgram(1L, "RN");
            StudProgram studProgram2 = studProgram(2L, "SI");

            List<Predmet> predmeti = List.of(
                    predmet(1L, "MAT1", "Matematika 1", studProgram1),
                    predmet(2L, "PRG1", "Programiranje 1", studProgram1),
                    predmet(3L, "BP1", "Baze podataka", studProgram2)
            );

            Page<Predmet> predmetPage = new PageImpl<>(predmeti, pageable, predmeti.size());

            when(repository.findAll(pageable)).thenReturn(predmetPage);

            Page<PredmetDto> result = service.getAllPredmeti(pageable);

            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
            assertEquals(3, result.getContent().size());

            verify(repository).findAll(pageable);
        }

        @Test
        @DisplayName("Returns empty page when no Predmeti exist")
        void returnsEmptyPageWhenNoPredmeti() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Predmet> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(repository.findAll(pageable)).thenReturn(emptyPage);

            Page<PredmetDto> result = service.getAllPredmeti(pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());

            verify(repository).findAll(pageable);
        }
    }

    // ---------- createPredmet ----------

    @Nested
    class CreatePredmetTests {

        @Test
        @DisplayName("Creates Predmet from DTO successfully")
        void createsPredmetFromDto() {
            PredmetCreateDto dto = createDto("MAT1", "Matematika 1", 1L);
            StudProgram studProgram = studProgram(1L, "RN");
            Predmet saved = predmet(10L, "MAT1", "Matematika 1", studProgram);

            when(repository.existsBySifra("MAT1")).thenReturn(false);
            when(studProgramRepository.findById(1L)).thenReturn(Optional.of(studProgram));
            when(repository.save(any(Predmet.class))).thenReturn(saved);

            PredmetDto result = service.createPredmet(dto);

            assertNotNull(result);
            assertEquals("Matematika 1", result.getNaziv());
            assertEquals("MAT1", result.getSifra());

            verify(repository).existsBySifra("MAT1");
            verify(studProgramRepository).findById(1L);
            verify(repository).save(any(Predmet.class));
        }

        @Test
        @DisplayName("Throws exception when Sifra already exists")
        void throwsWhenSifraExists() {
            PredmetCreateDto dto = createDto("MAT1", "Matematika 1", 1L);

            when(repository.existsBySifra("MAT1")).thenReturn(true);

            assertThrows(ResponseStatusException.class, () -> service.createPredmet(dto));
            verify(repository).existsBySifra("MAT1");
            verify(studProgramRepository, never()).findById(any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Throws exception when StudProgram not found")
        void throwsWhenStudProgramNotFound() {
            PredmetCreateDto dto = createDto("MAT1", "Matematika 1", 99L);

            when(repository.existsBySifra("MAT1")).thenReturn(false);
            when(studProgramRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class, () -> service.createPredmet(dto));
            verify(repository).existsBySifra("MAT1");
            verify(studProgramRepository).findById(99L);
            verify(repository, never()).save(any());
        }
    }
}