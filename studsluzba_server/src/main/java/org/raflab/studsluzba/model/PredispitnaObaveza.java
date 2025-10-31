package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;


@Entity
@Table(name = "predispitne_obaveze")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredispitnaObaveza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nastavnik_predmet_id", nullable = false)
    private NastavnikPredmet nastavnikPredmet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VrstaPredispitneObaveze vrsta;

    @Column(name = "maksimalan_broj_poena", nullable = false)
    private Double maksimalanBrojPoena;
}
