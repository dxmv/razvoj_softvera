package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "polozeni_predmeti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolozenPredmet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "studentski_indeks_id", nullable = false)
    private Indeks studentskiIndeks;

    @ManyToOne
    @JoinColumn(name = "predmet_id", nullable = false)
    private Predmet predmet;

    @Column(nullable = false)
    private Integer ocena; // 6-10

    @Enumerated(EnumType.STRING)
    @Column(name = "nacin_polaganja", nullable = false)
    private NacinPolaganja nacinPolaganja;

    // Ako je polozen preko ispita
    @OneToOne
    @JoinColumn(name = "izlazak_na_ispit_id")
    private IzlazakIspit izlazakNaIspit;

    // Ako je priznat sa druge VSU
    @ManyToOne
    @JoinColumn(name = "visokoskolska_ustanova_id")
    private VSU visokoskolskaUstanova;

    @Column(name = "naziv_predmeta_sa_druge_vsu")
    private String nazivPredmetaSaDrugeVSU;
}
