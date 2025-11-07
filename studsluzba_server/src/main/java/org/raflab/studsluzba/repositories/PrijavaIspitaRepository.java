package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Ispit;
import org.raflab.studsluzba.model.PrijavaIspita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrijavaIspitaRepository extends JpaRepository<PrijavaIspita, Long> {
    List<PrijavaIspita> findByIspit(Ispit ispit);
}
