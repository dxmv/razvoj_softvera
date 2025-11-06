package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.model.PolozenPredmet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PolozenPredmetRepository extends JpaRepository<PolozenPredmet, Long> {
    Page<PolozenPredmet> findByStudentskiIndeks(Indeks studentskiIndeks, Pageable pageable);

    @Query("SELECT AVG(p.ocena) " +
            "FROM PolozenPredmet p " +
            "WHERE p.predmet.id = :predmetId " +
            "AND p.izlazakNaIspit IS NOT NULL " +
            "AND FUNCTION('YEAR', p.izlazakNaIspit.datumIzlaska) BETWEEN :godinaOd AND :godinaDo")
    Double findProsecnaOcenaZaPredmetUGodinama(
            @Param("predmetId") Long predmetId,
            @Param("godinaOd") int godinaOd,
            @Param("godinaDo") int godinaDo);

}
