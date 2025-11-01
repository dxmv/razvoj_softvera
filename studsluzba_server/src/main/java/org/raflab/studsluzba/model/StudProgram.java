package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "studijski_programi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String oznaka; // RI, RN, SI...

    @Column(nullable = false)
    private String naziv;

    @Column(name = "godina_akreditacije")
    private Integer godinaAkreditacije;

    @Column(name = "trajanje_semestara", nullable = false)
    private Integer trajanjeSemestara;

    @ManyToOne
    @JoinColumn(name = "vrsta_studija_id", nullable = false)
    private VrstaStudija vrstaStudija;

    @Column(name = "espb_bodovi")
    private Integer espbBodovi;

    @Column(nullable = false)
    private String zvanje;

    // TODO: Studijski program sadrži predmete
}
