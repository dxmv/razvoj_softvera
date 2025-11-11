package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Uplata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UplataRepository extends JpaRepository<Uplata, Long> {

    List<Uplata> findByStudentIdAndSkolskaGodinaId(Long studentId, Long skolskaGodinaId);
}
