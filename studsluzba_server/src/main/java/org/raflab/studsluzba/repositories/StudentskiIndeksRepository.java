package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Indeks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentskiIndeksRepository extends JpaRepository<Indeks, Long> {
    @Query("SELECT i FROM Indeks i JOIN i.studijskiProgram sp WHERE sp.oznaka = :smer AND i.brojIndeksa = :broj AND i.godinaUpisa = :godina")
    Indeks findByShort(@Param("smer") String smer, @Param("broj") Integer broj, @Param("godina") Integer godina);
}
