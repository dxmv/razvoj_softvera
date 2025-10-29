package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;


@Entity
@Table(name = "vrste_studija")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VrstaStudija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String skraceniNaziv; // OAS, MAS, DAS...

    @Column(nullable = false)
    private String punNaziv;
}
