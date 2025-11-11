package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.model.ObnovaGodine;
import org.raflab.studsluzba.model.SkolskaGodina;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObnovaGodineRepository extends JpaRepository<ObnovaGodine, Long> {
    @EntityGraph(attributePaths = "predmeti")
    List<ObnovaGodine> findByStudentskiIndeks(Indeks indeks);

    boolean existsByStudentskiIndeksAndSkolskaGodina(Indeks studentskiIndeks, SkolskaGodina skolskaGodina);
}
