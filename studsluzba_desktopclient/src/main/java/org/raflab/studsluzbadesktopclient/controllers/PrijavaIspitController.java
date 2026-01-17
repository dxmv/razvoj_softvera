package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.IspitPrikazDto;
import org.raflab.studsluzba.model.dto.IspitniRokDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.services.PrijavaIspitaService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class PrijavaIspitController {

    @FXML private ComboBox<StudentDto> studentCb;
    @FXML private ComboBox<IspitniRokDto> rokCb;
    @FXML private ComboBox<IspitPrikazDto> ispitCb;
    @FXML private Button prijaviBtn;

    private final PrijavaIspitaService prijavaIspitService;
    private final IspitniRokService ispitniRokService;
    private final StudentService studentService;

    private final StringUtils stringUtils = new StringUtils();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    public void initialize() {
        initConverters();
        loadRokovi();
        loadStudenti();
        setupListeners();

        prijaviBtn.setDisable(true);
        ispitCb.setDisable(true);
    }

    private void setupListeners() {
        rokCb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ucitajIspite(newVal.getId());
            }
        });

        studentCb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updatePrijaviButtonState();
        });

        ispitCb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updatePrijaviButtonState();
        });
    }

    private void updatePrijaviButtonState() {
        boolean studentSelected = studentCb.getValue() != null;
        boolean ispitSelected = ispitCb.getValue() != null;
        prijaviBtn.setDisable(!studentSelected || !ispitSelected);
    }

    private void loadStudenti() {
        studentService.fetchAllStudentsAsync()
                .collectList()
                .subscribe(
                        lista -> Platform.runLater(() -> studentCb.setItems(FXCollections.observableArrayList(lista))),
                        err -> Platform.runLater(() -> System.err.println("Greška pri učitavanju studenata: " + err.getMessage()))
                );
    }

    @FXML
    private void handlePrijaviIspit() {
        StudentDto selectedStudent = studentCb.getValue();
        IspitPrikazDto selektovaniIspit = ispitCb.getValue();

        if (selectedStudent == null || selektovaniIspit == null) {
            stringUtils.prikaziPoruku("Greška", "Niste izabrali studenta ili ispit!", Alert.AlertType.WARNING);
            return;
        }

        try {
            Long indeksId = prijavaIspitService.fetchIndeksId(selectedStudent.getId()).block();

            prijavaIspitService.prijaviIspit(selektovaniIspit.getId(), indeksId)
                    .subscribe(
                            res -> Platform.runLater(() -> {
                                stringUtils.prikaziPoruku("Uspeh", "Ispit uspešno prijavljen!", Alert.AlertType.INFORMATION);
                                resetForme();
                            }),
                            err -> Platform.runLater(() -> {
                                stringUtils.prikaziPoruku("Neuspešna prijava", "Uslovi nisu ispunjeni.", Alert.AlertType.ERROR);
                            })
                    );
        } catch (Exception e) {
            stringUtils.prikaziPoruku("Greška", "Sistem ne može da pronađe aktivan indeks za ovog studenta.", Alert.AlertType.ERROR);
        }
    }

    private void ucitajIspite(Long rokId) {
        ispitniRokService.getIspitiZaRok(rokId)
                .collectList()
                .subscribe(
                        lista -> Platform.runLater(() -> {
                            ispitCb.setItems(FXCollections.observableArrayList(lista));
                            ispitCb.setDisable(false);
                        }),
                        err -> Platform.runLater(() -> stringUtils.prikaziPoruku("Greška", "Neuspešno učitavanje ispita.", Alert.AlertType.ERROR))
                );
    }

    private void loadRokovi() {
        ispitniRokService.getAllRokovi()
                .collectList()
                .subscribe(
                        lista -> Platform.runLater(() -> rokCb.setItems(FXCollections.observableArrayList(lista))),
                        err -> Platform.runLater(() -> System.err.println("Greška pri učitavanju rokova"))
                );
    }

    private void initConverters() {
        studentCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(StudentDto student) {
                return student == null ? "" : student.getIme() + " " + student.getPrezime() + " (" + student.getId() + ")";
            }
            @Override
            public StudentDto fromString(String s) { return null; }
        });

        rokCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(IspitniRokDto rok) {
                return rok == null ? "" : rok.getDatumPocetka().format(formatter) + " - " + rok.getDatumZavrsetka().format(formatter);
            }
            @Override
            public IspitniRokDto fromString(String s) { return null; }
        });

        ispitCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(IspitPrikazDto ispit) {
                return ispit == null ? "" : ispit.getNazivPredmeta() + " (" + ispit.getDatum() + ")";
            }
            @Override
            public IspitPrikazDto fromString(String s) { return null; }
        });
    }

    private void resetForme() {
        studentCb.getSelectionModel().clearSelection();
        ispitCb.getSelectionModel().clearSelection();
        prijaviBtn.setDisable(true);
    }
}