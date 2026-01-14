package org.raflab.studsluzbadesktopclient.model.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.raflab.studsluzba.model.dto.ObnovaGodineDto;
import org.raflab.studsluzba.model.dto.UpisGodineDto;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentCertificateData {
    private String ime;
    private String prezime;
    private String srednjeIme;
    private String indeks;
    private String jmbg;
    private LocalDate datumRodjenja;
    private String mestoRodjenja;
    private String drzavaRodjenja;
    private String studijskiProgram;
    private String fakultet;
    private List<UpisGodineDto> upisaneGodine;
    private List<ObnovaGodineDto> obnovljeneGodine;
    private String trenutniStatus;
}
