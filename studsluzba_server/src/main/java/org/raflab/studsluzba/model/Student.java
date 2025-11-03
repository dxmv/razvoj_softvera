package org.raflab.studsluzba.model;

import lombok.*;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entitet Student predstavlja osnovne podatke o studentu.
 * Student se jednom unosi u sistem, a može biti upisan na različite studijske programe.
 */
@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ime;

    @Column(nullable = false)
    private String prezime;

    @Column(name = "srednje_ime")
    private String srednjeIme;

    @Column(nullable = false, unique = true, length = 13)
    private String jmbg;

    @Column(name = "datum_rodjenja", nullable = false)
    private LocalDate datumRodjenja;

    @Column(name = "mesto_rodjenja", nullable = false)
    private String mestoRodjenja;

    @Column(name = "drzava_rodjenja", nullable = false)
    private String drzavaRodjenja;

    @Column(nullable = false)
    private String drzavljanstvo;

    @Column(nullable = false)
    private String nacionalnost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Pol pol;

    // Adresa prebivališta
    @Column(name = "mesto_prebivalista")
    private String mestoPrebivalista;

    @Column(name = "ulica_prebivalista")
    private String ulicaPrebivalista;

    @Column(name = "broj_prebivalista")
    private String brojPrebivalista;

    @Column(name = "broj_telefona")
    private String brojTelefona;

    @Column(name = "fakultetski_email", unique = true)
    private String fakultetskiEmail;

    @Column(name = "privatni_email")
    private String privatniEmail;

    @Column(name = "broj_licne_karte")
    private String brojLicneKarte;

    @Column(name = "izdavalac_licne_karte")
    private String izdavalacLicneKarte;

    // Veza sa srednjom školom
    @ManyToOne
    @JoinColumn(name = "srednja_skola_id")
    private SrednjaSkola zavrsenaSkola;

    @Column(name = "uspeh_srednja_skola")
    private Double uspehSrednjaSkola;

    @Column(name = "uspeh_prijemni")
    private Double uspehPrijemni;

    // Indeksi studenta
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Indeks> indeksi = new HashSet<>();

    // TODO: prosle VSU

}
