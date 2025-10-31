package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ispitni_rokovi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IspitniRok {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "datum_pocetka", nullable = false)
    private LocalDate datumPocetka;

    @Column(name = "datum_zavrsetka", nullable = false)
    private LocalDate datumZavrsetka;

    @OneToMany(mappedBy = "ispitniRok", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Ispit> ispiti = new HashSet<>();
}

