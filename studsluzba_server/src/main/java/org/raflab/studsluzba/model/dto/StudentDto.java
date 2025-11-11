package org.raflab.studsluzba.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.raflab.studsluzba.model.Pol;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private Long id;
    private String ime;
    private String prezime;
    private String srednjeIme;
    private String jmbg;
    private LocalDate datumRodjenja;
    private String mestoRodjenja;
    private String drzavaRodjenja;
    private String drzavljanstvo;
    private String nacionalnost;
    private Pol pol;
    private String mestoPrebivalista;
    private String ulicaPrebivalista;
    private String brojPrebivalista;
    private String brojTelefona;
    private String fakultetskiEmail;
    private String privatniEmail;
    private String brojLicneKarte;
    private String izdavalacLicneKarte;
    private Long zavrsenaSkolaId;
    private Double uspehSrednjaSkola;
    private Double uspehPrijemni;
    private Long prethodnaVisokoskolskaUstanovaId;
}
