package org.raflab.studsluzbadesktopclient.model.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamDetails {
    private String sifraPredmeta;
    private String nazivPredmeta;
    private Integer ocena;
    private Integer espbBodovi;
    private String datumPolaganja;
}
