package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "student_predispitna_obaveza")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPredispitnaObaveza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_predmet_id", nullable = false)
    private StudentPredmet studentPredmet;

    @ManyToOne
    @JoinColumn(name = "predispitna_obaveza_id", nullable = false)
    private PredispitnaObaveza predispitnaObaveza;

    @Column(name = "osvojeni_poeni")
    private Double osvojeniPoeni;
}
