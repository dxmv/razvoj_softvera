package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.NastavnikPredmet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NastavnikPredmetRepository extends JpaRepository<NastavnikPredmet, Long> {
    List<NastavnikPredmet> findBySkolskaGodinaIdAndPredmetStudijskiProgramIdAndPredmetSemestarIn(
            Long skolskaGodinaId,
            Long studijskiProgramId,
            Collection<Integer> semestri);
}
