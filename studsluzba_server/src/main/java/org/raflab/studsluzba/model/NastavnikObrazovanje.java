package org.raflab.studsluzba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "nastavnik_obrazovanje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NastavnikObrazovanje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nastavnik_id")
    private Nastavnik nastavnik;

    @ManyToOne(optional = false)
    @JoinColumn(name = "visokoskolska_ustanova_id")
    private VSU visokaSkola;

    @ManyToOne
    @JoinColumn(name = "vrsta_studija_id")
    private VrstaStudija vrstaStudija;

    @Column(name = "godina_zavrsetka")
    private Integer godinaZavrsetka;
}
