package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "ispiti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ispit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "datum_odrzavanja", nullable = false)
    private LocalDate datum;

    @ManyToOne
    @JoinColumn(name = "predmet_id", nullable = false)
    private Predmet predmet;

    @ManyToOne
    @JoinColumn(name = "nastavnik_id", nullable = false)
    private Nastavnik nastavnik;

    @Column(name = "vreme_pocetka", nullable = false)
    private LocalTime vremePocetka;

    @Column(name = "zakljucen", nullable = false)
    @Builder.Default
    private Boolean zakljucen = false;

    @ManyToOne
    @JoinColumn(name = "ispitni_rok_id", nullable = false)
    private IspitniRok ispitniRok;
}

