package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "izlasci_na_ispit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IzlazakIspit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "prijava_ispita_id", nullable = false)
    private PrijavaIspita prijavaIspita;

    @Column(name = "poeni_sa_ispita")
    private Double poeniSaIspita; // osvojeno na ispitu

    @Column(name = "ukupno_poena")
    private Double ukupnoPoena; // predispitni + ispit

    @Column(length = 500)
    private String napomena;

    @Column(name = "ponistava_ispit", nullable = false)
    @Builder.Default
    private Boolean ponistavanIspit = false;

    @Column(name = "datum_izlaska")
    private LocalDateTime datumIzlaska;
}
