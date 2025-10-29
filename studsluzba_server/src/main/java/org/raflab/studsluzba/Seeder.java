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
    private final VrstaStudijaRepository vrstaStudijaRepository;
    private final StudProgramRepository studijskiProgramRepository;
    private final PredmetRepository predmetRepository;

    @Override
    public void run(String... args) throws Exception {
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
                .naziv("Prva kragujevacka gimnazija")
                .mesto("Kragujevac")
                .vrsta(VrstaSkole.GIMNAZIJA)
                .build();
        srednjaSkolaRepository.save(gimnazijaKg);

        System.out.println("Kreirano " + srednjaSkolaRepository.count() + " srednjih skola");
        // Kreiramo vrste studija
        VrstaStudija oas = VrstaStudija.builder()
                .skraceniNaziv("OAS")
                .punNaziv("Osnovne akademske studije")
                .build();
        vrstaStudijaRepository.save(oas);

        VrstaStudija mas = VrstaStudija.builder()
                .skraceniNaziv("MAS")
                .punNaziv("Master akademske studije")
                .build();
        vrstaStudijaRepository.save(mas);

        System.out.println("Kreirano " + vrstaStudijaRepository.count() + " vrsta studija");

        // Kreiramo studijske programe
        StudProgram ri = StudProgram.builder()
                .oznaka("RI")
                .naziv("Nz")
                .godinaAkreditacije(2020)
                .zvanje("Diplomirani inženjer računarstva i informatike")
                .trajanjeSemestara(8)
                .vrstaStudija(oas)
                .espbBodovi(240)
                .build();
        studijskiProgramRepository.save(ri);

        StudProgram si = StudProgram.builder()
                .oznaka("SI")
                .naziv("Softversko inženjerstvo")
                .godinaAkreditacije(2021)
                .zvanje("Diplomirani inženjer softverskog inženjerstva")
                .trajanjeSemestara(8)
                .vrstaStudija(oas)
                .espbBodovi(240)
                .build();
        studijskiProgramRepository.save(si);

        StudProgram rn = StudProgram.builder()
                .oznaka("RN")
                .naziv("Računarske nauke")
                .godinaAkreditacije(2019)
                .zvanje("Diplomirani inženjer računarskih nauka")
                .trajanjeSemestara(8)
                .vrstaStudija(oas)
                .espbBodovi(240)
                .build();
        studijskiProgramRepository.save(rn);

        System.out.println("Kreirano " + studijskiProgramRepository.count() + " studijskih programa");

        // Kreiramo predmete za RI program
        Predmet prog1 = Predmet.builder()
                .sifra("RI-101")
                .naziv("Programiranje 1")
                .opis("Uvod u programiranje, osnovne strukture podataka i algoritmi")
                .espbBodovi(8)
                .semestar(1)
                .brPredavanja(45)
                .brVezbi(45)
                .studijskiProgram(ri)
                .build();
        predmetRepository.save(prog1);

        Predmet mat1 = Predmet.builder()
                .sifra("RI-102")
                .naziv("Matematika 1")
                .opis("Diferencijalni i integralni račun")
                .espbBodovi(7)
                .semestar(1)
                .brPredavanja(60)
                .brVezbi(30)
                .studijskiProgram(ri)
                .build();
        predmetRepository.save(mat1);

        Predmet prog2 = Predmet.builder()
                .sifra("RI-201")
                .naziv("Programiranje 2")
                .opis("Objektno-orijentisano programiranje, nasleđivanje, polimorfizam")
                .espbBodovi(8)
                .semestar(2)
                .brPredavanja(45)
                .brVezbi(45)
                .studijskiProgram(ri)
                .build();
        predmetRepository.save(prog2);

        Predmet bp = Predmet.builder()
                .sifra("RI-301")
                .naziv("Baze podataka")
                .opis("Relacione baze podataka, SQL, normalizacija")
                .espbBodovi(7)
                .semestar(3)
                .brPredavanja(45)
                .brVezbi(30)
                .studijskiProgram(ri)
                .build();
        predmetRepository.save(bp);

        // Predmeti za SI program
        Predmet web = Predmet.builder()
                .sifra("SI-401")
                .naziv("Web programiranje")
                .opis("Frontend i backend razvoj web aplikacija")
                .espbBodovi(8)
                .semestar(4)
                .brPredavanja(45)
                .brVezbi(45)
                .studijskiProgram(si)
                .build();
        predmetRepository.save(web);

        Predmet mobile = Predmet.builder()
                .sifra("SI-501")
                .naziv("Mobilne aplikacije")
                .opis("Razvoj Android i iOS aplikacija")
                .espbBodovi(7)
                .semestar(5)
                .brPredavanja(30)
                .brVezbi(45)
                .studijskiProgram(si)
                .build();
        predmetRepository.save(mobile);

        System.out.println("Kreirano " + predmetRepository.count() +  " predmeta");

        // Kreiramo studente
        Student student1 = Student.builder()
                .ime("Marko")
                .prezime("Petrovic")
                .srednjeIme("Jovanovic")
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
                .fakultetskiEmail("marko.petrovic@kg.ac.rs")
                .privatniEmail("marko.petrovic@gmail.com")
                .brojLicneKarte("123456789")
                .izdavalacLicneKarte("PU Beograd")
                .zavrsenaSkola(gimnazijaBg)
                .uspehSrednjaSkola(4.85)
                .uspehPrijemni(89.5)
                .build();
        studentRepository.save(student1);

        Student student2 = Student.builder()
                .ime("Ana")
                .prezime("Nikolic")
                .jmbg("0505001805002")
                .datumRodjenja(LocalDate.of(2001, 5, 5))
                .mestoRodjenja("Nis")
                .drzavaRodjenja("Srbija")
                .drzavljanstvo("Srpsko")
                .nacionalnost("Srpska")
                .pol(Pol.ZENSKI)
                .mestoPrebivalista("Nis")
                .ulicaPrebivalista("Cara Dusana")
                .brojPrebivalista("23")
                .brojTelefona("+381652345678")
                .fakultetskiEmail("ana.nikolic@kg.ac.rs")
                .privatniEmail("ana.nikolic@yahoo.com")
                .brojLicneKarte("987654321")
                .izdavalacLicneKarte("PU Nis")
                .zavrsenaSkola(tehnicka)
                .uspehSrednjaSkola(4.50)
                .uspehPrijemni(75.0)
                .build();
        studentRepository.save(student2);

        Student student3 = Student.builder()
                .ime("Stefan")
                .prezime("Jovanovic")
                .srednjeIme("Petrovic")
                .jmbg("1212999800003")
                .datumRodjenja(LocalDate.of(1999, 12, 12))
                .mestoRodjenja("Kragujevac")
                .drzavaRodjenja("Srbija")
                .drzavljanstvo("Srpsko")
                .nacionalnost("Srpska")
                .pol(Pol.MUSKI)
                .mestoPrebivalista("Kragujevac")
                .ulicaPrebivalista("Svetozara Markovica")
                .brojPrebivalista("7")
                .brojTelefona("+381663456789")
                .fakultetskiEmail("stefan.jovanovic@kg.ac.rs")
                .privatniEmail("stefan.j@outlook.com")
                .brojLicneKarte("456789123")
                .izdavalacLicneKarte("PU Kragujevac")
                .zavrsenaSkola(gimnazijaKg)
                .uspehSrednjaSkola(4.95)
                .uspehPrijemni(95.0)
                .build();
        studentRepository.save(student3);

        System.out.println("Kreirano " + studentRepository.count() + " studenata");

        // Kreiramo indekse
        Indeks indeks1 = Indeks.builder()
                .student(student1)
                .godinaUpisa(2020)
                .brojIndeksa(1)
                .studijskiProgram(ri)
                .status(StatusIndeksa.AKTIVAN)
                .datumAktivacije(LocalDate.of(2020, 10, 1))
                .build();
        indeksRepository.save(indeks1);

        Indeks indeks2 = Indeks.builder()
                .student(student2)
                .godinaUpisa(2021)
                .brojIndeksa(15)
                .studijskiProgram(si)
                .status(StatusIndeksa.AKTIVAN)
                .datumAktivacije(LocalDate.of(2021, 10, 1))
                .build();
        indeksRepository.save(indeks2);

        // Student 3 je presao sa RN na RI
        Indeks indeks3a = Indeks.builder()
                .student(student3)
                .godinaUpisa(2019)
                .brojIndeksa(5)
                .studijskiProgram(rn)
                .status(StatusIndeksa.NEAKTIVAN)
                .datumAktivacije(LocalDate.of(2019, 10, 1))
                .datumDeaktivacije(LocalDate.of(2020, 10, 1))
                .build();
        indeksRepository.save(indeks3a);

        Indeks indeks3b = Indeks.builder()
                .student(student3)
                .godinaUpisa(2020)
                .brojIndeksa(50)
                .studijskiProgram(ri)
                .status(StatusIndeksa.AKTIVAN)
                .datumAktivacije(LocalDate.of(2020, 10, 1))
                .build();
        indeksRepository.save(indeks3b);

        System.out.println("Kreirano " + indeksRepository.count() + " indeksa");


    }
}