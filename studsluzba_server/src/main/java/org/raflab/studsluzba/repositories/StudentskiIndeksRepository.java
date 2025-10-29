package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.Indeks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentskiIndeksRepository extends JpaRepository<Indeks, Long> {
}
