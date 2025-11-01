package org.raflab.studsluzba.mapper;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.model.dto.*;

public final class EntityMapper {

    private EntityMapper() {
    }

    public static SrednjaSkolaDto toDto(SrednjaSkola entity) {
        if (entity == null) {
            return null;
        }
        return SrednjaSkolaDto.builder()
                .id(entity.getId())
                .naziv(entity.getNaziv())
                .mesto(entity.getMesto())
                .vrsta(entity.getVrsta())
                .build();
    }

    public static VrstaStudijaDto toDto(VrstaStudija entity) {
        if (entity == null) {
            return null;
        }
        return VrstaStudijaDto.builder()
                .id(entity.getId())
                .skraceniNaziv(entity.getSkraceniNaziv())
                .punNaziv(entity.getPunNaziv())
                .build();
    }

    public static StudProgramDto toDto(StudProgram entity) {
        if (entity == null) {
            return null;
        }
        return StudProgramDto.builder()
                .id(entity.getId())
                .oznaka(entity.getOznaka())
                .naziv(entity.getNaziv())
                .godinaAkreditacije(entity.getGodinaAkreditacije())
                .zvanje(entity.getZvanje())
                .trajanjeSemestara(entity.getTrajanjeSemestara())
                .espbBodovi(entity.getEspbBodovi())
                .vrstaStudijaId(entity.getVrstaStudija() != null ? entity.getVrstaStudija().getId() : null)
                .build();
    }

    public static PredmetDto toDto(Predmet entity) {
        if (entity == null) {
            return null;
        }
        return PredmetDto.builder()
                .id(entity.getId())
                .sifra(entity.getSifra())
                .naziv(entity.getNaziv())
                .opis(entity.getOpis())
                .espbBodovi(entity.getEspbBodovi())
                .semestar(entity.getSemestar())
                .brPredavanja(entity.getBrPredavanja())
                .brVezbi(entity.getBrVezbi())
                .studijskiProgramId(entity.getStudijskiProgram() != null ? entity.getStudijskiProgram().getId() : null)
                .build();
    }

    public static StudentDto toDto(Student entity) {
        if (entity == null) {
            return null;
        }
        return StudentDto.builder()
                .id(entity.getId())
                .ime(entity.getIme())
                .prezime(entity.getPrezime())
                .srednjeIme(entity.getSrednjeIme())
                .jmbg(entity.getJmbg())
                .datumRodjenja(entity.getDatumRodjenja())
                .mestoRodjenja(entity.getMestoRodjenja())
                .drzavaRodjenja(entity.getDrzavaRodjenja())
                .drzavljanstvo(entity.getDrzavljanstvo())
                .nacionalnost(entity.getNacionalnost())
                .pol(entity.getPol())
                .mestoPrebivalista(entity.getMestoPrebivalista())
                .ulicaPrebivalista(entity.getUlicaPrebivalista())
                .brojPrebivalista(entity.getBrojPrebivalista())
                .brojTelefona(entity.getBrojTelefona())
                .fakultetskiEmail(entity.getFakultetskiEmail())
                .privatniEmail(entity.getPrivatniEmail())
                .brojLicneKarte(entity.getBrojLicneKarte())
                .izdavalacLicneKarte(entity.getIzdavalacLicneKarte())
                .zavrsenaSkolaId(entity.getZavrsenaSkola() != null ? entity.getZavrsenaSkola().getId() : null)
                .uspehSrednjaSkola(entity.getUspehSrednjaSkola())
                .uspehPrijemni(entity.getUspehPrijemni())
                .build();
    }

    public static IndeksDto toDto(Indeks entity) {
        if (entity == null) {
            return null;
        }
        return IndeksDto.builder()
                .id(entity.getId())
                .studentId(entity.getStudent() != null ? entity.getStudent().getId() : null)
                .godinaUpisa(entity.getGodinaUpisa())
                .brojIndeksa(entity.getBrojIndeksa())
                .studijskiProgramId(entity.getStudijskiProgram() != null ? entity.getStudijskiProgram().getId() : null)
                .status(entity.getStatus())
                .datumAktivacije(entity.getDatumAktivacije())
                .datumDeaktivacije(entity.getDatumDeaktivacije())
                .build();
    }

    public static NastavnikDto toDto(Nastavnik entity) {
        if (entity == null) {
            return null;
        }
        return NastavnikDto.builder()
                .id(entity.getId())
                .ime(entity.getIme())
                .prezime(entity.getPrezime())
                .srednjeIme(entity.getSrednjeIme())
                .email(entity.getEmail())
                .build();
    }

    public static NastavnikZvanjeDto toDto(NastavnikZvanje entity) {
        if (entity == null) {
            return null;
        }
        return NastavnikZvanjeDto.builder()
                .id(entity.getId())
                .nastavnikId(entity.getNastavnik() != null ? entity.getNastavnik().getId() : null)
                .nazivZvanja(entity.getNazivZvanja())
                .datumIzbora(entity.getDatumIzbora())
                .uzaNaucnaOblast(entity.getUzaNaucnaOblast())
                .datumPrestanka(entity.getDatumPrestanka())
                .build();
    }

    public static NastavnikPredmetDto toDto(NastavnikPredmet entity) {
        if (entity == null) {
            return null;
        }
        return NastavnikPredmetDto.builder()
                .id(entity.getId())
                .skolskaGodinaId(entity.getSkolskaGodina() != null ? entity.getSkolskaGodina().getId() : null)
                .nastavnikId(entity.getNastavnik() != null ? entity.getNastavnik().getId() : null)
                .predmetId(entity.getPredmet() != null ? entity.getPredmet().getId() : null)
                .build();
    }

    public static StudentPredmetDto toDto(StudentPredmet entity) {
        if (entity == null) {
            return null;
        }
        return StudentPredmetDto.builder()
                .id(entity.getId())
                .skolskaGodinaId(entity.getSkolskaGodina() != null ? entity.getSkolskaGodina().getId() : null)
                .indeksId(entity.getIndeks() != null ? entity.getIndeks().getId() : null)
                .nastavnikPredmetId(entity.getNastavnikPredmet() != null ? entity.getNastavnikPredmet().getId() : null)
                .build();
    }

    public static PredispitnaObavezaDto toDto(PredispitnaObaveza entity) {
        if (entity == null) {
            return null;
        }
        return PredispitnaObavezaDto.builder()
                .id(entity.getId())
                .nastavnikPredmetId(entity.getNastavnikPredmet() != null ? entity.getNastavnikPredmet().getId() : null)
                .vrsta(entity.getVrsta())
                .maksimalanBrojPoena(entity.getMaksimalanBrojPoena())
                .build();
    }

    public static StudentPredispitnaObavezaDto toDto(StudentPredispitnaObaveza entity) {
        if (entity == null) {
            return null;
        }
        return StudentPredispitnaObavezaDto.builder()
                .id(entity.getId())
                .studentPredmetId(entity.getStudentPredmet() != null ? entity.getStudentPredmet().getId() : null)
                .predispitnaObavezaId(entity.getPredispitnaObaveza() != null ? entity.getPredispitnaObaveza().getId() : null)
                .osvojeniPoeni(entity.getOsvojeniPoeni())
                .build();
    }

    public static IspitniRokDto toDto(IspitniRok entity) {
        if (entity == null) {
            return null;
        }
        return IspitniRokDto.builder()
                .id(entity.getId())
                .datumPocetka(entity.getDatumPocetka())
                .datumZavrsetka(entity.getDatumZavrsetka())
                .skolskaGodinaId(entity.getSkolskaGodina() != null ? entity.getSkolskaGodina().getId() : null)
                .build();
    }

    public static IspitDto toDto(Ispit entity) {
        if (entity == null) {
            return null;
        }
        return IspitDto.builder()
                .id(entity.getId())
                .datum(entity.getDatum())
                .predmetId(entity.getPredmet() != null ? entity.getPredmet().getId() : null)
                .nastavnikId(entity.getNastavnik() != null ? entity.getNastavnik().getId() : null)
                .vremePocetka(entity.getVremePocetka())
                .zakljucen(entity.getZakljucen())
                .ispitniRokId(entity.getIspitniRok() != null ? entity.getIspitniRok().getId() : null)
                .build();
    }

    public static PrijavaIspitaDto toDto(PrijavaIspita entity) {
        if (entity == null) {
            return null;
        }
        return PrijavaIspitaDto.builder()
                .id(entity.getId())
                .studentskiIndeksId(entity.getStudentskiIndeks() != null ? entity.getStudentskiIndeks().getId() : null)
                .ispitId(entity.getIspit() != null ? entity.getIspit().getId() : null)
                .datumPrijave(entity.getDatumPrijave())
                .build();
    }

    public static IzlazakIspitDto toDto(IzlazakIspit entity) {
        if (entity == null) {
            return null;
        }
        return IzlazakIspitDto.builder()
                .id(entity.getId())
                .prijavaIspitaId(entity.getPrijavaIspita() != null ? entity.getPrijavaIspita().getId() : null)
                .poeniSaIspita(entity.getPoeniSaIspita())
                .ukupnoPoena(entity.getUkupnoPoena())
                .napomena(entity.getNapomena())
                .ponistavanIspit(entity.getPonistavanIspit())
                .datumIzlaska(entity.getDatumIzlaska())
                .build();
    }

    public static ObnovaGodineDto toDto(ObnovaGodine entity) {
        if (entity == null) {
            return null;
        }
        return ObnovaGodineDto.builder()
                .id(entity.getId())
                .studentskiIndeksId(entity.getStudentskiIndeks() != null ? entity.getStudentskiIndeks().getId() : null)
                .skolskaGodinaId(entity.getSkolskaGodina() != null ? entity.getSkolskaGodina().getId() : null)
                .godinaStudija(entity.getGodinaStudija())
                .datumObnove(entity.getDatumObnove())
                .napomena(entity.getNapomena())
                .predmetiIds(mapPredmeti(entity.getPredmeti()))
                .build();
    }

    public static UpisGodineDto toDto(UpisGodine entity) {
        if (entity == null) {
            return null;
        }
        return UpisGodineDto.builder()
                .id(entity.getId())
                .studentskiIndeksId(entity.getStudentskiIndeks() != null ? entity.getStudentskiIndeks().getId() : null)
                .skolskaGodinaId(entity.getSkolskaGodina() != null ? entity.getSkolskaGodina().getId() : null)
                .godinaStudija(entity.getGodinaStudija())
                .datumUpisa(entity.getDatumUpisa())
                .napomena(entity.getNapomena())
                .predmetiIds(mapPredmeti(entity.getPredmeti()))
                .build();
    }

    public static PolozenPredmetDto toDto(PolozenPredmet entity) {
        if (entity == null) {
            return null;
        }
        return PolozenPredmetDto.builder()
                .id(entity.getId())
                .studentskiIndeksId(entity.getStudentskiIndeks() != null ? entity.getStudentskiIndeks().getId() : null)
                .predmetId(entity.getPredmet() != null ? entity.getPredmet().getId() : null)
                .ocena(entity.getOcena())
                .nacinPolaganja(entity.getNacinPolaganja())
                .izlazakNaIspitId(entity.getIzlazakNaIspit() != null ? entity.getIzlazakNaIspit().getId() : null)
                .visokoskolskaUstanovaId(entity.getVisokoskolskaUstanova() != null ? entity.getVisokoskolskaUstanova().getId() : null)
                .nazivPredmetaSaDrugeVSU(entity.getNazivPredmetaSaDrugeVSU())
                .build();
    }

    public static VSUDto toDto(VSU entity) {
        if (entity == null) {
            return null;
        }
        return VSUDto.builder()
                .id(entity.getId())
                .naziv(entity.getNaziv())
                .mesto(entity.getMesto())
                .drzava(entity.getDrzava())
                .build();
    }

    public static SkolskaGodinaDto toDto(SkolskaGodina entity) {
        if (entity == null) {
            return null;
        }
        return SkolskaGodinaDto.builder()
                .id(entity.getId())
                .aktivna(entity.getAktivna())
                .build();
    }

    private static Set<Long> mapPredmeti(Set<Predmet> predmeti) {
        if (predmeti == null || predmeti.isEmpty()) {
            return Collections.emptySet();
        }
        return predmeti.stream()
                .map(Predmet::getId)
                .collect(Collectors.toSet());
    }
}
