package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "vsu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VSU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String naziv;

    @Column(nullable = false)
    private String mesto;

    @Column
    private String drzava;
}
