package org.raflab.studsluzba.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "obnovev_godine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObnovaGodine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "studentski_indeks_id", nullable = false)
    private Indeks studentskiIndeks;

    @ManyToOne
    @JoinColumn(name = "skolska_godina_id", nullable = false)
    private SkolskaGodina skolskaGodina;

    @Column(name = "godina_studija", nullable = false)
    private Integer godinaStudija;

    @Column(name = "datum_obnove", nullable = false)
    private LocalDate datumObnove;

    @Column(length = 500)
    private String napomena;

    // Predmeti koje upisuje u obnovljenu godinu
    @ManyToMany
    @JoinTable(
            name = "obnova_godine_predmeti",
            joinColumns = @JoinColumn(name = "obnova_godine_id"),
            inverseJoinColumns = @JoinColumn(name = "predmet_id")
    )
    @Builder.Default
    private Set<Predmet> predmeti = new HashSet<>();
}
