package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.model.SkolskaGodina;
import org.raflab.studsluzba.model.UpisGodine;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UpisGodineRepository extends JpaRepository<UpisGodine, Long> {

    @EntityGraph(attributePaths = "predmeti")
    List<UpisGodine> findByStudentskiIndeks(Indeks indeks);

    boolean existsByStudentskiIndeksAndSkolskaGodina(Indeks studentskiIndeks, SkolskaGodina skolskaGodina);
}
