package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;

@Entity
@Table(name = "nastavnicka_zvanja")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NastavnikZvanje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NazivZvanja nazivZvanja;

    @Column(name = "datum_izbora", nullable = false)
    private LocalDate datumIzbora;

    @Column(name = "uza_naucna_oblast", nullable = false)
    private String uzaNaucnaOblast;

    @Column(name = "datum_prestanka")
    private LocalDate datumPrestanka; // null ako je trenutno zvanje

    @ManyToOne
    @JoinColumn(name = "nastavnik_id", nullable = false)
    private Nastavnik nastavnik;
}
