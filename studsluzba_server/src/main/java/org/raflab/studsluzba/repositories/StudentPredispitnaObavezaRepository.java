package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.StudentPredispitnaObaveza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentPredispitnaObavezaRepository extends JpaRepository<StudentPredispitnaObaveza, Long> {
    @Query("SELECT spo FROM StudentPredispitnaObaveza spo WHERE spo.studentPredmet.id = :studentPredmetId")
    List<StudentPredispitnaObaveza> findByStudentPredmetId(@Param("studentPredmetId") Long studentPredmetId);

    @Query("SELECT SUM(spo.osvojeniPoeni) " +
            "FROM StudentPredispitnaObaveza spo " +
            "JOIN spo.studentPredmet sp " +
            "JOIN sp.nastavnikPredmet np " +
            "WHERE sp.indeks.id = :indeksId " +
            "AND np.predmet.id = :predmetId " +
            "AND sp.skolskaGodina.id = :skolskaGodinaId")
    Double findUkupniPredispitniPoeni(@Param("indeksId") Long indeksId,
                                      @Param("predmetId") Long predmetId,
                                      @Param("skolskaGodinaId") Long skolskaGodinaId);
}
