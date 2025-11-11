package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.model.Predmet;
import org.raflab.studsluzba.model.StudentPredmet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface StudentPredmetRepository extends JpaRepository<StudentPredmet, Long> {
    @Query("SELECT DISTINCT np.predmet FROM StudentPredmet sp " +
            "JOIN sp.nastavnikPredmet np " +
            "WHERE sp.indeks = :indeks " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM PolozenPredmet pp " +
            "    WHERE pp.studentskiIndeks = :indeks " +
            "      AND pp.predmet = np.predmet" +
            ")")
    Page<Predmet> findUnpassedSubjectsByIndeks(@Param("indeks") Indeks indeks, Pageable pageable);

    @Query("SELECT sp FROM StudentPredmet sp " +
            "WHERE sp.indeks.id = :indeksId " +
            "AND sp.nastavnikPredmet.predmet.id = :predmetId " +
            "AND sp.skolskaGodina.aktivna = true")
    Optional<StudentPredmet> findByIndeksIdAndNastavnikPredmetPredmetIdAndSkolskaGodinaAktivna(
            @Param("indeksId") Long indeksId,
            @Param("predmetId") Long predmetId);
}
