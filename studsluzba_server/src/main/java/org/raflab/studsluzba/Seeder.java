package org.raflab.studsluzba;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final NastavnikRepository nastavnikRepository;
    private final NastavnikZvanjeRepository nastavnikZvanjeRepository;
    private final NastavnikPredmetRepository nastavnikPredmetRepository;
    private final SkolskaGodinaRepository skolskaGodinaRepository;
    private final StudentPredmetRepository studentPredmetRepository;
    private final PredispitnaObavezaRepository predispitnaObavezaRepository;
    private final StudentPredispitnaObavezaRepository studentPredispitnaObavezaRepository;
    private final IspitniRokRepository ispitniRokRepository;
    private final IspitRepository ispitRepository;
    private final PrijavaIspitaRepository prijavaIspitaRepository;
    private final IzlazakIspitRepository izlazakIspitRepository;
    private final PolozenPredmetRepository polozenPredmetRepository;
    private final UpisGodineRepository upisGodineRepository;
    private final ObnovaGodineRepository obnovaGodineRepository;
    private final VSURepository vsuRepository;

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

        // Kreiramo VSU ustanove
        VSU kgf = VSU.builder()
                .naziv("Univerzitet u Kragujevcu - Fakultet inženjerskih nauka")
                .mesto("Kragujevac")
                .drzava("Srbija")
                .build();
        vsuRepository.save(kgf);

        VSU etf = VSU.builder()
                .naziv("Univerzitet u Beogradu - Elektrotehnički fakultet")
                .mesto("Beograd")
                .drzava("Srbija")
                .build();
        vsuRepository.save(etf);

        VSU ftn = VSU.builder()
                .naziv("Univerzitet u Novom Sadu - Fakultet tehničkih nauka")
                .mesto("Novi Sad")
                .drzava("Srbija")
                .build();
        vsuRepository.save(ftn);

        System.out.println("Kreirano " + vsuRepository.count() + " visokoškolskih ustanova");

        // Kreiramo školske godine
        SkolskaGodina skolskaGodina2324 = SkolskaGodina.builder()
                .oznaka("2023/24")
                .datumPocetka(LocalDate.of(2023, 10, 1))
                .datumZavrsetka(LocalDate.of(2024, 9, 30))
                .aktivna(true)
                .build();
        skolskaGodinaRepository.save(skolskaGodina2324);

        SkolskaGodina skolskaGodina2223 = SkolskaGodina.builder()
                .oznaka("2022/23")
                .datumPocetka(LocalDate.of(2022, 10, 1))
                .datumZavrsetka(LocalDate.of(2023, 9, 30))
                .aktivna(false)
                .build();
        skolskaGodinaRepository.save(skolskaGodina2223);

        SkolskaGodina skolskaGodina2122 = SkolskaGodina.builder()
                .oznaka("2021/22")
                .datumPocetka(LocalDate.of(2021, 10, 1))
                .datumZavrsetka(LocalDate.of(2022, 9, 30))
                .aktivna(false)
                .build();
        skolskaGodinaRepository.save(skolskaGodina2122);

        System.out.println("Kreirano " + skolskaGodinaRepository.count() + " školskih godina");

        // Kreiramo nastavnike
        Nastavnik nastavnikMilan = Nastavnik.builder()
                .ime("Milan")
                .prezime("Ilic")
                .srednjeIme("Petrovic")
                .email("milan.ilic@kg.ac.rs")
                .build();
        nastavnikRepository.save(nastavnikMilan);

        Nastavnik nastavnicaJelena = Nastavnik.builder()
                .ime("Jelena")
                .prezime("Jovanovic")
                .email("jelena.jovanovic@kg.ac.rs")
                .build();
        nastavnikRepository.save(nastavnicaJelena);

        Nastavnik nastavnikPetar = Nastavnik.builder()
                .ime("Petar")
                .prezime("Simic")
                .email("petar.simic@kg.ac.rs")
                .build();
        nastavnikRepository.save(nastavnikPetar);

        System.out.println("Kreirano " + nastavnikRepository.count() + " nastavnika");

        // Kreiramo zvanja nastavnika
        NastavnikZvanje zvanjeMilana = NastavnikZvanje.builder()
                .nastavnik(nastavnikMilan)
                .nazivZvanja(NazivZvanja.DOCENT)
                .datumIzbora(LocalDate.of(2021, 9, 1))
                .uzaNaucnaOblast("Računarske nauke")
                .build();
        nastavnikZvanjeRepository.save(zvanjeMilana);

        NastavnikZvanje zvanjeJelene = NastavnikZvanje.builder()
                .nastavnik(nastavnicaJelena)
                .nazivZvanja(NazivZvanja.VANREDNI_PROFESOR)
                .datumIzbora(LocalDate.of(2018, 6, 15))
                .uzaNaucnaOblast("Softversko inženjerstvo")
                .build();
        nastavnikZvanjeRepository.save(zvanjeJelene);

        NastavnikZvanje zvanjePetra = NastavnikZvanje.builder()
                .nastavnik(nastavnikPetar)
                .nazivZvanja(NazivZvanja.ASISTENT)
                .datumIzbora(LocalDate.of(2022, 3, 10))
                .uzaNaucnaOblast("Matematika")
                .build();
        nastavnikZvanjeRepository.save(zvanjePetra);

        System.out.println("Kreirano " + nastavnikZvanjeRepository.count() + " nastavničkih zvanja");

        // Povezujemo nastavnike i predmete
        NastavnikPredmet prog1Milan = NastavnikPredmet.builder()
                .skolskaGodina(skolskaGodina2324)
                .nastavnik(nastavnikMilan)
                .predmet(prog1)
                .build();
        nastavnikPredmetRepository.save(prog1Milan);

        NastavnikPredmet webJelena = NastavnikPredmet.builder()
                .skolskaGodina(skolskaGodina2324)
                .nastavnik(nastavnicaJelena)
                .predmet(web)
                .build();
        nastavnikPredmetRepository.save(webJelena);

        NastavnikPredmet mat1Petar = NastavnikPredmet.builder()
                .skolskaGodina(skolskaGodina2223)
                .nastavnik(nastavnikPetar)
                .predmet(mat1)
                .build();
        nastavnikPredmetRepository.save(mat1Petar);

        System.out.println("Kreirano " + nastavnikPredmetRepository.count() + " nastavnik-predmet zapisa");

        // Evidentiramo studente na predmetima
        StudentPredmet student1Prog1 = StudentPredmet.builder()
                .skolskaGodina(skolskaGodina2324)
                .indeks(indeks1)
                .nastavnikPredmet(prog1Milan)
                .build();
        studentPredmetRepository.save(student1Prog1);

        StudentPredmet student2Web = StudentPredmet.builder()
                .skolskaGodina(skolskaGodina2324)
                .indeks(indeks2)
                .nastavnikPredmet(webJelena)
                .build();
        studentPredmetRepository.save(student2Web);

        StudentPredmet student3Mat1 = StudentPredmet.builder()
                .skolskaGodina(skolskaGodina2223)
                .indeks(indeks3b)
                .nastavnikPredmet(mat1Petar)
                .build();
        studentPredmetRepository.save(student3Mat1);

        System.out.println("Kreirano " + studentPredmetRepository.count() + " evidencija student-predmet");

        // Definišemo predispitne obaveze
        PredispitnaObaveza prog1Kolokvijum = PredispitnaObaveza.builder()
                .nastavnikPredmet(prog1Milan)
                .vrsta(VrstaPredispitneObaveze.KOLOKVIJUM)
                .maksimalanBrojPoena(50.0)
                .build();
        predispitnaObavezaRepository.save(prog1Kolokvijum);

        PredispitnaObaveza webProjekat = PredispitnaObaveza.builder()
                .nastavnikPredmet(webJelena)
                .vrsta(VrstaPredispitneObaveze.PROJEKAT)
                .maksimalanBrojPoena(40.0)
                .build();
        predispitnaObavezaRepository.save(webProjekat);

        PredispitnaObaveza mat1Kolokvijum = PredispitnaObaveza.builder()
                .nastavnikPredmet(mat1Petar)
                .vrsta(VrstaPredispitneObaveze.KOLOKVIJUM)
                .maksimalanBrojPoena(60.0)
                .build();
        predispitnaObavezaRepository.save(mat1Kolokvijum);

        System.out.println("Kreirano " + predispitnaObavezaRepository.count() + " predispitnih obaveza");

        // Beležimo ostvarene predispitne poene
        StudentPredispitnaObaveza prog1KolokvijumPoeni = StudentPredispitnaObaveza.builder()
                .studentPredmet(student1Prog1)
                .predispitnaObaveza(prog1Kolokvijum)
                .osvojeniPoeni(42.0)
                .build();
        studentPredispitnaObavezaRepository.save(prog1KolokvijumPoeni);

        StudentPredispitnaObaveza webProjekatPoeni = StudentPredispitnaObaveza.builder()
                .studentPredmet(student2Web)
                .predispitnaObaveza(webProjekat)
                .osvojeniPoeni(35.0)
                .build();
        studentPredispitnaObavezaRepository.save(webProjekatPoeni);

        StudentPredispitnaObaveza mat1KolokvijumPoeni = StudentPredispitnaObaveza.builder()
                .studentPredmet(student3Mat1)
                .predispitnaObaveza(mat1Kolokvijum)
                .osvojeniPoeni(48.0)
                .build();
        studentPredispitnaObavezaRepository.save(mat1KolokvijumPoeni);

        System.out.println("Kreirano " + studentPredispitnaObavezaRepository.count() + " zapisa o predispitnim obavezama studenata");

        // Upis u novu školsku godinu
        // UpisGodine upisStudent1 = UpisGodine.builder()
        //         .studentskiIndeks(indeks1)
        //         .skolskaGodina(skolskaGodina2324)
        //         .godinaStudija(2)
        //         .datumUpisa(LocalDate.of(2023, 10, 2))
        //         .napomena("Upis u drugu godinu studija")
        //         .build();
        // upisStudent1.getPredmeti().add(prog2);
        // upisStudent1.getPredmeti().add(bp);
        // upisGodineRepository.save(upisStudent1);

        UpisGodine upisStudent2 = UpisGodine.builder()
                .studentskiIndeks(indeks2)
                .skolskaGodina(skolskaGodina2324)
                .godinaStudija(4)
                .datumUpisa(LocalDate.of(2023, 10, 3))
                .napomena("Upis u četvrtu godinu studija")
                .build();
        upisStudent2.getPredmeti().add(web);
        upisStudent2.getPredmeti().add(mobile);
        upisGodineRepository.save(upisStudent2);

        UpisGodine upisStudent3 = UpisGodine.builder()
                .studentskiIndeks(indeks3b)
                .skolskaGodina(skolskaGodina2223)
                .godinaStudija(2)
                .datumUpisa(LocalDate.of(2022, 10, 1))
                .napomena("Upis nakon prelaska na RI program")
                .build();
        upisStudent3.getPredmeti().add(prog1);
        upisStudent3.getPredmeti().add(mat1);
        upisGodineRepository.save(upisStudent3);

        System.out.println("Kreirano " + upisGodineRepository.count() + " upisa godine");

        // Evidentiramo obnovu godine
        ObnovaGodine obnovaStudent2 = ObnovaGodine.builder()
                .studentskiIndeks(indeks2)
                .skolskaGodina(skolskaGodina2324)
                .godinaStudija(3)
                .datumObnove(LocalDate.of(2023, 10, 5))
                .napomena("Obnova treće godine zbog neispunjenih uslova")
                .build();
        obnovaStudent2.getPredmeti().add(prog2);
        obnovaStudent2.getPredmeti().add(bp);
        obnovaGodineRepository.save(obnovaStudent2);

        ObnovaGodine obnovaStudent1 = ObnovaGodine.builder()
                .studentskiIndeks(indeks1)
                .skolskaGodina(skolskaGodina2223)
                .godinaStudija(1)
                .datumObnove(LocalDate.of(2022, 9, 29))
                .napomena("Obnova prve godine zbog nedovoljnih bodova")
                .build();
        obnovaStudent1.getPredmeti().add(prog1);
        obnovaStudent1.getPredmeti().add(mat1);
        obnovaGodineRepository.save(obnovaStudent1);

        ObnovaGodine obnovaStudent3 = ObnovaGodine.builder()
                .studentskiIndeks(indeks3b)
                .skolskaGodina(skolskaGodina2324)
                .godinaStudija(3)
                .datumObnove(LocalDate.of(2023, 10, 6))
                .napomena("Obnova treće godine radi usklađivanja predmeta")
                .build();
        obnovaStudent3.getPredmeti().add(bp);
        obnovaStudent3.getPredmeti().add(web);
        obnovaGodineRepository.save(obnovaStudent3);

        System.out.println("Kreirano " + obnovaGodineRepository.count() + " obnova godine");

        // Kreiramo ispitne rokove i ispite
        IspitniRok januarRok = IspitniRok.builder()
                .datumPocetka(LocalDate.of(2024, 1, 20))
                .datumZavrsetka(LocalDate.of(2024, 2, 10))
                .skolskaGodina(skolskaGodina2324)
                .build();
        ispitniRokRepository.save(januarRok);

        IspitniRok februarRok = IspitniRok.builder()
                .datumPocetka(LocalDate.of(2024, 2, 15))
                .datumZavrsetka(LocalDate.of(2024, 3, 5))
                .skolskaGodina(skolskaGodina2324)
                .build();
        ispitniRokRepository.save(februarRok);

        IspitniRok junRok = IspitniRok.builder()
                .datumPocetka(LocalDate.of(2023, 6, 10))
                .datumZavrsetka(LocalDate.of(2023, 7, 1))
                .skolskaGodina(skolskaGodina2223)
                .build();
        ispitniRokRepository.save(junRok);

        System.out.println("Kreirano " + ispitniRokRepository.count() + " ispitnih rokova");

        Ispit prog1Januar = Ispit.builder()
                .datum(LocalDate.of(2024, 1, 25))
                .predmet(prog1)
                .nastavnik(nastavnikMilan)
                .vremePocetka(LocalTime.of(9, 0))
                .ispitniRok(januarRok)
                .build();
        ispitRepository.save(prog1Januar);

        Ispit mat1Februar = Ispit.builder()
                .datum(LocalDate.of(2024, 2, 22))
                .predmet(mat1)
                .nastavnik(nastavnikPetar)
                .vremePocetka(LocalTime.of(10, 0))
                .ispitniRok(februarRok)
                .build();
        ispitRepository.save(mat1Februar);

        Ispit webJun = Ispit.builder()
                .datum(LocalDate.of(2023, 6, 21))
                .predmet(web)
                .nastavnik(nastavnicaJelena)
                .vremePocetka(LocalTime.of(12, 0))
                .ispitniRok(junRok)
                .build();
        ispitRepository.save(webJun);

        System.out.println("Kreirano " + ispitRepository.count() + " ispita");

        // Prijave ispita
        PrijavaIspita prijavaIspita1 = PrijavaIspita.builder()
                .studentskiIndeks(indeks1)
                .ispit(prog1Januar)
                .datumPrijave(LocalDateTime.of(2024, 1, 15, 12, 30))
                .build();
        prijavaIspitaRepository.save(prijavaIspita1);

        PrijavaIspita prijavaIspita2 = PrijavaIspita.builder()
                .studentskiIndeks(indeks2)
                .ispit(mat1Februar)
                .datumPrijave(LocalDateTime.of(2024, 2, 10, 9, 45))
                .build();
        prijavaIspitaRepository.save(prijavaIspita2);

        PrijavaIspita prijavaIspita3 = PrijavaIspita.builder()
                .studentskiIndeks(indeks3b)
                .ispit(webJun)
                .datumPrijave(LocalDateTime.of(2023, 6, 5, 14, 15))
                .build();
        prijavaIspitaRepository.save(prijavaIspita3);

        System.out.println("Kreirano " + prijavaIspitaRepository.count() + " prijava ispita");

        // Izlasci na ispit
        IzlazakIspit izlazakProg1 = IzlazakIspit.builder()
                .prijavaIspita(prijavaIspita1)
                .poeniSaIspita(45.0)
                .ukupnoPoena(87.0)
                .napomena("Odličan rezultat")
                .datumIzlaska(LocalDateTime.of(2024, 1, 25, 11, 30))
                .build();
        izlazakIspitRepository.save(izlazakProg1);

        IzlazakIspit izlazakMat1 = IzlazakIspit.builder()
                .prijavaIspita(prijavaIspita2)
                .poeniSaIspita(38.0)
                .ukupnoPoena(78.0)
                .napomena("Stabilan uspeh")
                .datumIzlaska(LocalDateTime.of(2024, 2, 22, 11, 0))
                .build();
        izlazakIspitRepository.save(izlazakMat1);

        IzlazakIspit izlazakWeb = IzlazakIspit.builder()
                .prijavaIspita(prijavaIspita3)
                .poeniSaIspita(42.0)
                .ukupnoPoena(88.0)
                .napomena("Istaknut projekat")
                .datumIzlaska(LocalDateTime.of(2023, 6, 21, 13, 45))
                .build();
        izlazakIspitRepository.save(izlazakWeb);

        System.out.println("Kreirano " + izlazakIspitRepository.count() + " izlazaka na ispit");

        // Položeni predmeti
        PolozenPredmet prog1Polozen = PolozenPredmet.builder()
                .studentskiIndeks(indeks1)
                .predmet(prog1)
                .ocena(10)
                .nacinPolaganja(NacinPolaganja.ISPIT)
                .izlazakNaIspit(izlazakProg1)
                .build();
        polozenPredmetRepository.save(prog1Polozen);

        PolozenPredmet mat1Polozen = PolozenPredmet.builder()
                .studentskiIndeks(indeks2)
                .predmet(mat1)
                .ocena(8)
                .nacinPolaganja(NacinPolaganja.ISPIT)
                .izlazakNaIspit(izlazakMat1)
                .build();
        polozenPredmetRepository.save(mat1Polozen);

        PolozenPredmet webPriznat = PolozenPredmet.builder()
                .studentskiIndeks(indeks3b)
                .predmet(web)
                .ocena(9)
                .nacinPolaganja(NacinPolaganja.PRIZNAVANJE)
                .visokoskolskaUstanova(ftn)
                .nazivPredmetaSaDrugeVSU("Advanced Web Development")
                .build();
        polozenPredmetRepository.save(webPriznat);

        System.out.println("Kreirano " + polozenPredmetRepository.count() + " položenih predmeta");


    }
}
