package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.IzlazakIspit;
import org.raflab.studsluzba.model.NastavnikPredmet;
import org.raflab.studsluzba.model.dto.NastavnikPredmetDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.List;

public interface NastavnikPredmetRepository extends JpaRepository<NastavnikPredmet, Long> {
    List<NastavnikPredmet> findBySkolskaGodinaIdAndPredmetStudijskiProgramIdAndPredmetSemestarIn(
            Long skolskaGodinaId,
            Long studijskiProgramId,
            Collection<Integer> semestri);

    List<NastavnikPredmet> findBySkolskaGodinaIdAndPredmetIdIn(
            Long skolskaGodinaId,
            Collection<Long> predmetIds);


    @Query("SELECT np.nastavnik.id FROM NastavnikPredmet np WHERE np.predmet.id = :predmetId")
    Long findNastavnikIdByPredmetId(@Param("predmetId") Long predmetId);
}
