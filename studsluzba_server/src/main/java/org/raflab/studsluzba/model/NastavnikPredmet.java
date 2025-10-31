package org.raflab.studsluzba.model;


import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nastavnik_predmet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NastavnikPredmet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "skolska_godina_id", nullable = false)
    private SkolskaGodina skolskaGodina;

    @ManyToOne
    @JoinColumn(name = "nastavnik_id", nullable = false)
    private Nastavnik nastavnik;

    @ManyToOne
    @JoinColumn(name = "predmet_id", nullable = false)
    private Predmet predmet;
}
