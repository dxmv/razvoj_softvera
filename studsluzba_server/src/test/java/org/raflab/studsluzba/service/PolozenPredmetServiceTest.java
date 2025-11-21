package org.raflab.studsluzba.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.model.PolozenPredmet;
import org.raflab.studsluzba.repositories.PolozenPredmetRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolozenPredmetServiceTest {

    @Mock
    PolozenPredmetRepository repository;

    @InjectMocks
    PolozenPredmetService service;

    private PolozenPredmet polozenPredmet(Long id, Integer ocena) {
        PolozenPredmet pp = new PolozenPredmet();
        pp.setId(id);
        pp.setOcena(ocena);
        return pp;
    }

    // ---------- create ----------

    @Nested
    class CreateTests {

        @Test
        @DisplayName("Creates and returns saved PolozenPredmet")
        void createsPolozenPredmet() {
            PolozenPredmet input = polozenPredmet(null, 9);
            PolozenPredmet saved = polozenPredmet(1L, 9);

            when(repository.save(input)).thenReturn(saved);

            PolozenPredmet result = service.create(input);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(9, result.getOcena());
            verify(repository).save(input);
        }
    }

    // ---------- findById ----------

    @Nested
    class FindByIdTests {

        @Test
        @DisplayName("Returns PolozenPredmet when found")
        void returnsPolozenPredmet() {
            Long id = 5L;
            PolozenPredmet polozenPredmet = polozenPredmet(id, 10);

            when(repository.findById(id)).thenReturn(Optional.of(polozenPredmet));

            PolozenPredmet result = service.findById(id);

            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals(10, result.getOcena());
            verify(repository).findById(id);
        }

        @Test
        @DisplayName("Throws exception when PolozenPredmet not found")
        void throwsWhenNotFound() {
            Long id = 99L;

            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class, () -> service.findById(id));
            verify(repository).findById(id);
        }
    }

    // ---------- update ----------

    @Nested
    class UpdateTests {

        @Test
        @DisplayName("Updates existing PolozenPredmet successfully")
        void updatesExistingPolozenPredmet() {
            Long id = 5L;
            PolozenPredmet existing = polozenPredmet(id, 8);
            PolozenPredmet updated = polozenPredmet(null, 10);
            PolozenPredmet saved = polozenPredmet(id, 10);

            when(repository.findById(id)).thenReturn(Optional.of(existing));
            when(repository.save(any(PolozenPredmet.class))).thenReturn(saved);

            PolozenPredmet result = service.update(id, updated);

            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals(10, result.getOcena());
            verify(repository).findById(id);
            verify(repository).save(any(PolozenPredmet.class));
        }

        @Test
        @DisplayName("Throws exception when PolozenPredmet not found")
        void throwsWhenNotFound() {
            Long id = 99L;
            PolozenPredmet updated = polozenPredmet(null, 10);

            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class, () -> service.update(id, updated));
            verify(repository).findById(id);
            verify(repository, never()).save(any());
        }
    }

    // ---------- delete ----------

    @Nested
    class DeleteTests {

        @Test
        @DisplayName("Deletes existing PolozenPredmet")
        void deletesExistingPolozenPredmet() {
            Long id = 7L;

            when(repository.existsById(id)).thenReturn(true);
            doNothing().when(repository).deleteById(id);

            service.delete(id);

            verify(repository).existsById(id);
            verify(repository).deleteById(id);
        }

        @Test
        @DisplayName("Throws exception when trying to delete non-existing PolozenPredmet")
        void throwsWhenDeletingNonExisting() {
            Long id = 99L;

            when(repository.existsById(id)).thenReturn(false);

            assertThrows(ResponseStatusException.class, () -> service.delete(id));
            verify(repository).existsById(id);
            verify(repository, never()).deleteById(any());
        }
    }

    // ---------- getProsecnaOcenaZaPredmetUGodinama ----------

    @Nested
    class GetProsecnaOcenaZaPredmetUGodinamaTests {

        @Test
        @DisplayName("Returns average grade for predmet in given years")
        void returnsAverageGrade() {
            Long predmetId = 1L;
            int godinaOd = 2015;
            int godinaDo = 2018;
            Double expectedAverage = 8.5;

            when(repository.findProsecnaOcenaZaPredmetUGodinama(predmetId, godinaOd, godinaDo))
                    .thenReturn(expectedAverage);

            Double result = service.getProsecnaOcenaZaPredmetUGodinama(predmetId, godinaOd, godinaDo);

            assertNotNull(result);
            assertEquals(expectedAverage, result);
            verify(repository).findProsecnaOcenaZaPredmetUGodinama(predmetId, godinaOd, godinaDo);
        }

        @Test
        @DisplayName("Throws exception when no passed predmeti found for period")
        void throwsWhenNoPredmetiFound() {
            Long predmetId = 1L;
            int godinaOd = 2015;
            int godinaDo = 2018;

            when(repository.findProsecnaOcenaZaPredmetUGodinama(predmetId, godinaOd, godinaDo))
                    .thenReturn(null);

            assertThrows(ResponseStatusException.class,
                    () -> service.getProsecnaOcenaZaPredmetUGodinama(predmetId, godinaOd, godinaDo));
            verify(repository).findProsecnaOcenaZaPredmetUGodinama(predmetId, godinaOd, godinaDo);
        }

        @Test
        @DisplayName("Handles single year period")
        void handlesSingleYearPeriod() {
            Long predmetId = 1L;
            int godina = 2020;
            Double expectedAverage = 9.0;

            when(repository.findProsecnaOcenaZaPredmetUGodinama(predmetId, godina, godina))
                    .thenReturn(expectedAverage);

            Double result = service.getProsecnaOcenaZaPredmetUGodinama(predmetId, godina, godina);

            assertNotNull(result);
            assertEquals(expectedAverage, result);
            verify(repository).findProsecnaOcenaZaPredmetUGodinama(predmetId, godina, godina);
        }
    }
}