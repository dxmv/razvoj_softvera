package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(value = "SELECT * FROM student s " +
            "WHERE (:ime IS NULL OR s.ime ILIKE CONCAT('%', :ime, '%')) " +
            "AND (:prezime IS NULL OR s.prezime ILIKE CONCAT('%', :prezime, '%'))",
            nativeQuery = true)
    Page<Student> searchByImeAndPrezime(@Param("ime") String ime, @Param("prezime") String prezime, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Student s " +
            "JOIN s.indeksi i " +
            "JOIN UpisGodine ug ON ug.studentskiIndeks = i " +
            "WHERE (:srednjaSkolaId IS NULL OR s.zavrsenaSkola.id = :srednjaSkolaId)")
    List<Student> findEnrolledByHighSchool(@Param("srednjaSkolaId") Long srednjaSkolaId);
}
