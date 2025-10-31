package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student_predmet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPredmet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "skolska_godina_id", nullable = false)
    private SkolskaGodina skolskaGodina;

    @ManyToOne
    @JoinColumn(name = "studentski_indeks_id", nullable = false)
    private Indeks indeks;

    @ManyToOne
    @JoinColumn(name = "nastavnik_predmet_id", nullable = false)
    private NastavnikPredmet nastavnikPredmet; // student slusa predmet kod odredjenog nastavnika

}
