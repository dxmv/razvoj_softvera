package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.SkolskaGodina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SkolskaGodinaRepository extends JpaRepository<SkolskaGodina, Long> {
    @Query("SELECT sg FROM SkolskaGodina sg WHERE sg.aktivna = true")
    Optional<SkolskaGodina> findAktivnaSkolskaGodina();
}
