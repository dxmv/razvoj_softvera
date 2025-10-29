package org.raflab.studsluzba;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {


    private final SrednjasSkolaRepository srednjaSkolaRepository;
    private final StudentRepository studentRepository;
    private final StudentskiIndeksRepository indeksRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Dodavanje srednjih skola...");
        // Kreiramo srednje skole
        SrednjaSkola gimnazijaBg = SrednjaSkola.builder()
                .naziv("Matematicka gimnazija")
                .mesto("Beograd")
                .vrsta(VrstaSkole.GIMNAZIJA)
                .build();
        srednjaSkolaRepository.save(gimnazijaBg);

        SrednjaSkola tehnicka = SrednjaSkola.builder()
                .naziv("Tehnicka skola Nikola Tesla")
                .mesto("Nis")
                .vrsta(VrstaSkole.STRUCNA)
                .build();
        srednjaSkolaRepository.save(tehnicka);

        SrednjaSkola gimnazijaKg = SrednjaSkola.builder()
                .naziv("Gimnazija Mrgud Branko")
                .mesto("Kragujevac")
                .vrsta(VrstaSkole.GIMNAZIJA)
                .build();
        srednjaSkolaRepository.save(gimnazijaKg);
        System.out.println("Broj dodatih skola: " + srednjaSkolaRepository.findAll().size());
        // studenti
        Student student1 = Student.builder()
                .ime("Zoran")
                .prezime("Francuz")
                .jmbg("0101000800001")
                .datumRodjenja(LocalDate.of(2000, 1, 1))
                .mestoRodjenja("Beograd")
                .drzavaRodjenja("Srbija")
                .drzavljanstvo("Srpsko")
                .nacionalnost("Srpska")
                .pol(Pol.MUSKI)
                .mestoPrebivalista("Beograd")
                .ulicaPrebivalista("Kneza Milosa")
                .brojPrebivalista("15")
                .brojTelefona("+381641234567")
                .fakultetskiEmail("zoki@gmail.com")
                .privatniEmail("zoki_boss@gmail.com")
                .brojLicneKarte("123456789")
                .izdavalacLicneKarte("PU Beograd")
                .zavrsenaSkola(gimnazijaBg)
                .uspehSrednjaSkola(4.85)
                .uspehPrijemni(89.5)
                .build();
        studentRepository.save(student1);
        System.out.println(student1);
    }
}