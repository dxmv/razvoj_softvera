package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SrednjaSkolaDTO {
    private int id;
    private String naziv;
    private String mesto;
    private String vrstaSkole;
    private List<StudentDTO> bivsiUcenici;

    @Override
    public String toString() {
        return  vrstaSkole + " " + naziv + " " + mesto;
    }
}
