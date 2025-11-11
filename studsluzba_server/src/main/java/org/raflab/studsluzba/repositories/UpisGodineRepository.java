package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.model.UpisGodine;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UpisGodineRepository extends JpaRepository<UpisGodine, Long> {

    @EntityGraph(attributePaths = "predmeti")
    List<UpisGodine> findByStudentskiIndeks(Indeks indeks);

    @Query("SELECT ug FROM UpisGodine ug " +
            "WHERE ug.studentskiIndeks.student.id = :studentId " +
            "AND ug.skolskaGodina.aktivna = true")
    Optional<UpisGodine> findActiveByStudentId(@Param("studentId") Long studentId);
}
