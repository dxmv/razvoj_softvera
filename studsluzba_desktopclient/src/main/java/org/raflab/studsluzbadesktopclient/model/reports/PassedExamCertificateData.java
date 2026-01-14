package org.raflab.studsluzbadesktopclient.model.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassedExamCertificateData {
    private String ime;
    private String prezime;
    private String srednjeIme;
    private String indeks;
    private List<ExamByYearGroup> examsByYear;
}
