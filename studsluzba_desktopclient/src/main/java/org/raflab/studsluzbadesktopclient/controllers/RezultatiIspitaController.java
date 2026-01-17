package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.IspitniRokDto;
import org.raflab.studsluzba.model.dto.IspitPrikazDto; // Koristimo PrikazDto za listu
import org.raflab.studsluzba.model.dto.IspitniRezultatDto;
import org.raflab.studsluzbadesktopclient.services.IspitRezultatiService;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RezultatiIspitaController {

    private final IspitRezultatiService ispitRezultatService;
    private final IspitniRokService ispitniRokService;

    @FXML private ComboBox<IspitniRokDto> cbRokovi;
    @FXML private ComboBox<IspitPrikazDto> cbIspiti;

    @FXML private TableView<IspitniRezultatDto> tblRezultati;
    @FXML private TableColumn<IspitniRezultatDto, String> colIndeks;
    @FXML private TableColumn<IspitniRezultatDto, String> colStudent;
    @FXML private TableColumn<IspitniRezultatDto, String> colProgram;
    @FXML private TableColumn<IspitniRezultatDto, Double> colPredispitni;
    @FXML private TableColumn<IspitniRezultatDto, Double> colIspit;
    @FXML private TableColumn<IspitniRezultatDto, Double> colUkupno;
    @FXML private TableColumn<IspitniRezultatDto, Integer> colOcena;

    @FXML
    public void initialize() {
        colIndeks.setCellValueFactory(new PropertyValueFactory<>("indeks"));
        colStudent.setCellValueFactory(new PropertyValueFactory<>("student"));
        colProgram.setCellValueFactory(new PropertyValueFactory<>("studProgram"));
        colPredispitni.setCellValueFactory(new PropertyValueFactory<>("poeniPredispitni"));
        colIspit.setCellValueFactory(new PropertyValueFactory<>("poeniIspit"));
        colUkupno.setCellValueFactory(new PropertyValueFactory<>("ukupno"));
        colOcena.setCellValueFactory(new PropertyValueFactory<>("ocena"));

        ucitajIspitneRokove();

        cbRokovi.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ucitajIspiteZaRok(newVal.getId());
            }
        });
    }

    private void ucitajIspitneRokove() {
        ispitniRokService.getAllRokovi()
                .collectList()
                .subscribe(
                        lista -> Platform.runLater(() -> cbRokovi.setItems(FXCollections.observableArrayList(lista))),
                        this::hendlujGresku
                );
    }

    private void ucitajIspiteZaRok(Long rokId) {
        ispitniRokService.getIspitiZaRok(rokId)
                .collectList()
                .subscribe(
                        lista -> Platform.runLater(() -> cbIspiti.setItems(FXCollections.observableArrayList(lista))),
                        this::hendlujGresku
                );
    }

    @FXML
    private void handlePrikazi() {
        IspitPrikazDto selektovanIspit = cbIspiti.getSelectionModel().getSelectedItem();
        if (selektovanIspit != null) {
            ucitajPodatke(selektovanIspit.getId());
        } else {
            prikaziUpozorenje("Nije izabran ispit", "Molimo izaberite ispit kako biste videli rezultate.");
        }
    }

    public void ucitajPodatke(Long ispitId) {
        ispitRezultatService.getRezultati(ispitId)
                .collectList()
                .subscribe(
                        this::prikaziRezultate,
                        this::hendlujGresku
                );
    }

    private void prikaziRezultate(List<IspitniRezultatDto> rezultati) {
        Platform.runLater(() -> tblRezultati.setItems(FXCollections.observableArrayList(rezultati)));
    }

    private void prikaziUpozorenje(String naslov, String poruka) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(naslov);
            alert.setHeaderText(null);
            alert.setContentText(poruka);
            alert.showAndWait();
        });
    }

    private void hendlujGresku(Throwable err) {
        Platform.runLater(() -> {
            err.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška");
            alert.setContentText(err.getMessage());
            alert.showAndWait();
        });
    }
}