package org.raflab.studsluzbadesktopclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzbadesktopclient.state.SelectedStudentStore;
import org.raflab.studsluzbadesktopclient.state.SelectedStudentStore.Selection;
import org.springframework.stereotype.Component;

@Component
public class StudentProfileController {

    private final SelectedStudentStore selectedStudentStore;

    @FXML
    private Label statusLabel;
    @FXML
    private Label indeksValue;
    @FXML
    private Label imeValue;
    @FXML
    private Label prezimeValue;
    @FXML
    private Label srednjeImeValue;
    @FXML
    private Label jmbgValue;
    @FXML
    private Label datumRodjenjaValue;
    @FXML
    private Label mestoRodjenjaValue;
    @FXML
    private Label drzavaRodjenjaValue;
    @FXML
    private Label polValue;
    @FXML
    private Label drzavljanstvoValue;
    @FXML
    private Label nacionalnostValue;
    @FXML
    private Label mestoPrebivalistaValue;
    @FXML
    private Label adresaValue;
    @FXML
    private Label brojTelefonaValue;
    @FXML
    private Label fakultetskiEmailValue;
    @FXML
    private Label privatniEmailValue;

    public StudentProfileController(SelectedStudentStore selectedStudentStore) {
        this.selectedStudentStore = selectedStudentStore;
    }

    @FXML
    public void initialize() {
        selectedStudentStore.selectionProperty()
                .addListener((obs, oldSelection, newSelection) -> updateProfile(newSelection));
        updateProfile(selectedStudentStore.getSelection());
    }

    private void updateProfile(Selection selection) {
        if (selection == null || selection.getStudent() == null) {
            statusLabel.setText("Nijedan student nije izabran. Pronađite studenta unosom broja indeksa.");
            clearLabels();
            return;
        }
        StudentDto student = selection.getStudent();
        String indeks = selection.getIndex();
        statusLabel.setText(indeks != null ?
                "Profil studenta za indeks " + indeks :
                "Profil studenta");
        indeksValue.setText(valueOrPlaceholder(indeks));
        imeValue.setText(valueOrPlaceholder(student.getIme()));
        prezimeValue.setText(valueOrPlaceholder(student.getPrezime()));
        srednjeImeValue.setText(valueOrPlaceholder(student.getSrednjeIme()));
        jmbgValue.setText(valueOrPlaceholder(student.getJmbg()));
        datumRodjenjaValue.setText(valueOrPlaceholder(student.getDatumRodjenja()));
        mestoRodjenjaValue.setText(valueOrPlaceholder(student.getMestoRodjenja()));
        drzavaRodjenjaValue.setText(valueOrPlaceholder(student.getDrzavaRodjenja()));
        polValue.setText(student.getPol() != null ? student.getPol().name() : "--");
        drzavljanstvoValue.setText(valueOrPlaceholder(student.getDrzavljanstvo()));
        nacionalnostValue.setText(valueOrPlaceholder(student.getNacionalnost()));
        mestoPrebivalistaValue.setText(valueOrPlaceholder(student.getMestoPrebivalista()));
        String broj = valueOrPlaceholder(student.getBrojPrebivalista());
        String ulica = valueOrPlaceholder(student.getUlicaPrebivalista());
        adresaValue.setText(formatAddress(ulica, broj));
        brojTelefonaValue.setText(valueOrPlaceholder(student.getBrojTelefona()));
        fakultetskiEmailValue.setText(valueOrPlaceholder(student.getFakultetskiEmail()));
        privatniEmailValue.setText(valueOrPlaceholder(student.getPrivatniEmail()));
    }

    private void clearLabels() {
        indeksValue.setText("--");
        imeValue.setText("--");
        prezimeValue.setText("--");
        srednjeImeValue.setText("--");
        jmbgValue.setText("--");
        datumRodjenjaValue.setText("--");
        mestoRodjenjaValue.setText("--");
        drzavaRodjenjaValue.setText("--");
        polValue.setText("--");
        drzavljanstvoValue.setText("--");
        nacionalnostValue.setText("--");
        mestoPrebivalistaValue.setText("--");
        adresaValue.setText("--");
        brojTelefonaValue.setText("--");
        fakultetskiEmailValue.setText("--");
        privatniEmailValue.setText("--");
    }

    private String valueOrPlaceholder(Object value) {
        if (value == null) {
            return "--";
        }
        String str = value.toString().trim();
        return str.isEmpty() ? "--" : str;
    }

    private String formatAddress(String ulica, String broj) {
        if ((ulica == null || ulica.equals("--")) && (broj == null || broj.equals("--"))) {
            return "--";
        }
        if (broj == null || broj.equals("--")) {
            return ulica;
        }
        if (ulica == null || ulica.equals("--")) {
            return broj;
        }
        return ulica + " " + broj;
    }
}
