package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.model.PolozenPredmet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolozenPredmetRepository extends JpaRepository<PolozenPredmet, Long> {
    Page<PolozenPredmet> findByStudentskiIndeks(Indeks studentskiIndeks, Pageable pageable);
}
