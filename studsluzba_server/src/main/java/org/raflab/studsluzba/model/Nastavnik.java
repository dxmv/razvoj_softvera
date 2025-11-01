package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nastavnici")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nastavnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ime;

    @Column(nullable = false)
    private String prezime;

    @Column(name = "srednje_ime")
    private String srednjeIme;

    @Column(nullable = false, unique = true)
    private String email;

    // Istorija zvanja
    @OneToMany(mappedBy = "nastavnik", cascade = CascadeType.ALL)
    private Set<NastavnikZvanje> zvanja = new HashSet<>();

    // TODO:  podaci o obrazovanju (visokoškolske ustanove na kojoj je završio sve nivoe studija)
}
