package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "upisi_godine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpisGodine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "studentski_indeks_id", nullable = false)
    private Indeks studentskiIndeks;

    @ManyToOne
    @JoinColumn(name = "skolska_godina_id", nullable = false)
    private SkolskaGodina skolskaGodina;

    @Column(name = "godina_studija", nullable = false)
    private Integer godinaStudija;

    @Column(name = "datum_upisa", nullable = false)
    private LocalDate datumUpisa;

    @Column(length = 500)
    private String napomena;

    // preneti predmeti sa prethodne godine
    @ManyToMany
    @JoinTable(
            name = "upis_godine_preneti_predmeti",
            joinColumns = @JoinColumn(name = "upis_godine_id"),
            inverseJoinColumns = @JoinColumn(name = "predmet_id")
    )
    @Builder.Default
    private Set<Predmet> predmeti = new HashSet<>();
}
