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
public class ExamByYearGroup {
    private Integer godinaStudija;
    private List<ExamDetails> ispiti;
}
