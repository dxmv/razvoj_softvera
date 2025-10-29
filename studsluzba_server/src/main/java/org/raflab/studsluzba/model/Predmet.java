package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "predmeti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Predmet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sifra;

    @Column(nullable = false)
    private String naziv;

    @Column(length = 1000)
    private String opis;

    @Column(name = "espb_bodovi", nullable = false)
    private Integer espbBodovi;

    @Column(nullable = false)
    private Integer semestar;

    @Column(name = "broj_predavanja")
    private Integer brPredavanja; // broj casova predavanja

    @Column(name = "broj_vezbi")
    private Integer brVezbi; // broj casova vezbi

    @ManyToOne
    @JoinColumn(name = "studijski_program_id", nullable = false)
    private StudProgram studijskiProgram;
}
