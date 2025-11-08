package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.IzlazakIspit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IzlazakIspitRepository extends JpaRepository<IzlazakIspit, Long> {
    @Query("SELECT iz FROM IzlazakIspit iz " +
            "WHERE iz.prijavaIspita.ispit.id = :ispitId " +
            "ORDER BY iz.prijavaIspita.studentskiIndeks.studijskiProgram.oznaka, " +
            "iz.prijavaIspita.studentskiIndeks.godinaUpisa, " +
            "iz.prijavaIspita.studentskiIndeks.brojIndeksa")
    List<IzlazakIspit> findRezultatiByIspitSorted(@Param("ispitId") Long ispitId);
}
