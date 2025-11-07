package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Ispit;
import org.raflab.studsluzba.model.PolozenPredmet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IspitRepository extends JpaRepository<Ispit, Long> {
    @Query("SELECT p FROM PolozenPredmet p WHERE p.izlazakNaIspit.prijavaIspita.ispit.id = :ispitId")
    List<PolozenPredmet> findByIspitId(@Param("ispitId") Long ispitId);
}
