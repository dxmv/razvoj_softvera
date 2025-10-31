package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prijave_ispita")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrijavaIspita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "studentski_indeks_id", nullable = false)
    private Indeks studentskiIndeks;

    @ManyToOne
    @JoinColumn(name = "ispit_id", nullable = false)
    private Ispit ispit;

    @Column(name = "datum_prijave", nullable = false)
    private LocalDateTime datumPrijave;

}
