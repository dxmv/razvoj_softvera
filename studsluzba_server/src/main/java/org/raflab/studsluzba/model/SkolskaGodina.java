package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skolske_godine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkolskaGodina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String oznaka; // npr. 2023/24

    @Column(name = "datum_pocetka", nullable = false)
    private LocalDate datumPocetka;

    @Column(name = "datum_zavrsetka", nullable = false)
    private LocalDate datumZavrsetka;

    @Column(nullable = false)
    @Builder.Default
    private Boolean aktivna = false;

    @OneToMany(mappedBy = "skolskaGodina", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<NastavnikPredmet> nastavnikPredmeti = new HashSet<>();

    @OneToMany(mappedBy = "skolskaGodina", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<StudentPredmet> studentPredmeti = new HashSet<>();

    @OneToMany(mappedBy = "skolskaGodina", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<IspitniRok> ispitniRokovi = new HashSet<>();
}
