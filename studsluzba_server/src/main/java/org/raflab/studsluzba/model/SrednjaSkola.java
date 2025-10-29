package org.raflab.studsluzba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "srednje_skole")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SrednjaSkola {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String naziv;

    @Column(nullable = false)
    private String mesto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VrstaSkole vrsta;
}
