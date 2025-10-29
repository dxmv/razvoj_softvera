package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "studentski_indeksi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Indeks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "godina_upisa", nullable = false)
    private Integer godinaUpisa;

    @Column(name = "broj_indeksa", nullable = false)
    private Integer brojIndeksa;

    @ManyToOne
    @JoinColumn(name = "studijski_program_id", nullable = false)
    private StudProgram studijskiProgram;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusIndeksa status = StatusIndeksa.AKTIVAN;

    @Column(name = "datum_aktivacije", nullable = false)
    private LocalDate datumAktivacije;

    @Column(name = "datum_deaktivacije")
    private LocalDate datumDeaktivacije;
}
