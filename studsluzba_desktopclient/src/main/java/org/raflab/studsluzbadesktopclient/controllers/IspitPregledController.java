package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.IspitPrikazDto;
import org.raflab.studsluzba.model.dto.IspitniRokDto;
import org.raflab.studsluzba.model.dto.PrijavaIspitaPrikazDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzbadesktopclient.services.IspitService; // Dodato
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class IspitPregledController {

    @FXML private ComboBox<IspitniRokDto> rokCb;
    @FXML private ComboBox<IspitPrikazDto> ispitCb;
    @FXML private Button ucitajIspiteBtn;

    @FXML private TableView<PrijavaIspitaPrikazDto> studentiTable;
    @FXML private Label countLabel;

    private final IspitniRokService ispitniRokService;
    private final IspitService ispitService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final StringUtils stringUtils = new StringUtils();

    @FXML
    public void initialize() {
        initConverters();
        setupListeners();
        loadRokovi();

        ispitCb.setDisable(true);
    }

    private void initConverters() {
        rokCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(IspitniRokDto rok) {
                if (rok == null) return "";
                return String.format("%s - %s",
                        rok.getDatumPocetka().format(formatter),
                        rok.getDatumZavrsetka().format(formatter));
            }
            @Override
            public IspitniRokDto fromString(String string) { return null; }
        });

        ispitCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(IspitPrikazDto ispit) {
                return ispit == null ? "" : ispit.getNazivPredmeta() + " (" + ispit.getDatum().format(formatter) + ")";
            }
            @Override
            public IspitPrikazDto fromString(String s) { return null; }
        });
    }

    private void setupListeners() {
        ispitCb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ucitajStudenteZaIspit(newVal.getId());
            } else {
                studentiTable.getItems().clear();
                if (countLabel != null) countLabel.setText("Prijavljenih studenata: 0");
            }
        });

        rokCb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            ispitCb.getItems().clear();
            ispitCb.setDisable(true);
            studentiTable.getItems().clear();
        });
    }

    private void loadRokovi() {
        ispitniRokService.getAllRokovi()
                .collectList()
                .subscribe(
                        list -> Platform.runLater(() -> rokCb.setItems(FXCollections.observableArrayList(list))),
                        error -> Platform.runLater(() ->
                                stringUtils.prikaziPoruku("Greška", "Server nije dostupan", Alert.AlertType.ERROR))
                );
    }

    @FXML
    private void handleUcitajIspite() {
        IspitniRokDto selektovaniRok = rokCb.getValue();
        if (selektovaniRok == null) {
            stringUtils.prikaziPoruku("Greška", "Prvo izaberite ispitni rok!", Alert.AlertType.WARNING);
            return;
        }

        ispitniRokService.getIspitiZaRok(selektovaniRok.getId())
                .collectList()
                .subscribe(
                        lista -> Platform.runLater(() -> {
                            ispitCb.setItems(FXCollections.observableArrayList(lista));
                            ispitCb.setDisable(false);
                        }),
                        err -> Platform.runLater(() ->
                                stringUtils.prikaziPoruku("Greška", "Neuspešno učitavanje ispita.", Alert.AlertType.ERROR))
                );
    }

    private void ucitajStudenteZaIspit(Long ispitId) {
        ispitService.getPrijavljeniStudenti(ispitId)
                .collectList()
                .subscribe(
                        studenti -> Platform.runLater(() -> {
                            studentiTable.setItems(FXCollections.observableArrayList(studenti));
                            if (countLabel != null) {
                                countLabel.setText("Prijavljenih studenata: " + studenti.size());
                            }
                        }),
                        err -> Platform.runLater(() ->
                                stringUtils.prikaziPoruku("Greška", "Greška pri učitavanju studenata.", Alert.AlertType.ERROR))
                );
    }
}