package org.raflab.studsluzba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "uplate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Uplata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "skolska_godina_id")
    private SkolskaGodina skolskaGodina;

    @Column(name = "datum_uplate", nullable = false)
    private LocalDate datumUplate;

    @Column(name = "iznos_rsd", nullable = false, precision = 15, scale = 2)
    private BigDecimal iznosUDinarima;

    @Column(name = "srednji_kurs", nullable = false, precision = 10, scale = 4)
    private BigDecimal srednjiKurs;
}
