package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Predmet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.*;

public interface PredmetRepository extends JpaRepository<Predmet, Long> {
    @Query("SELECT p FROM Predmet p WHERE p.studijskiProgram.id = :studProgramId")
    Page<Predmet> findByStudijskiProgramId(@Param("studProgramId") Long studProgramId, Pageable pageable);

    Page<Predmet> findByStudijskiProgram_Id(Long studijskiProgramId, Pageable pageable);

    boolean existsBySifra(String sifra);
}
