package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.*;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class IspitUnosController {


    @FXML private ComboBox<PredmetDto> predmetCb;
    @FXML private DatePicker datumPicker;
    @FXML private ComboBox<Integer> satiCb;
    @FXML private ComboBox<Integer> minutiCb;
    @FXML private ComboBox<IspitniRokDto> ispitniRokCb;
    private final PredmetService predmetService;
    private final IspitService ispitService;
    private final IspitniRokService ispitniRokService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    public void initialize() {
        initTimeChoices();
        initConverters();
        loadData();
    }

    private void initTimeChoices() {
        satiCb.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(0, 23).boxed().toList()));
        minutiCb.setItems(FXCollections.observableArrayList(IntStream.of(0, 15, 30, 45).boxed().toList()));
    }

    private void initConverters() {
        predmetCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(PredmetDto object) {
                return object == null ? "" : object.getNaziv();
            }
            @Override
            public PredmetDto fromString(String string) { return null; }
        });
        ispitniRokCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(IspitniRokDto rok) {
                return rok == null ? "" : rok.getDatumPocetka().format(formatter) + " - " + rok.getDatumZavrsetka().format(formatter);
            }
            @Override
            public IspitniRokDto fromString(String s) { return null; }
        });
    }

    private void loadData() {
        predmetService.getAllPredmeti()
                .collectList()
                .subscribe(
                        list -> Platform.runLater(() -> predmetCb.setItems(FXCollections.observableArrayList(list))),
                        error -> Platform.runLater(() -> prikaziPoruku("Greška", "Neuspešno učitavanje predmeta.", Alert.AlertType.ERROR))
                );
        ispitniRokService.getAllRokovi()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> ispitniRokCb.setItems(FXCollections.observableArrayList(list))));

    }

    @FXML
    private void handleSave() {
        if (isInputInvalid()) {
            prikaziPoruku("Upozorenje", "Sva polja moraju biti popunjena!", Alert.AlertType.WARNING);
            return;
        }

        try {
            Long rokId = ispitniRokCb.getValue().getId();
            Long odabraniPredmetId = predmetCb.getValue().getId();

            ispitService.getNastavnikIdByPredmet(odabraniPredmetId).switchIfEmpty(Mono.error(new RuntimeException("Ovaj predmet nema dodeljenog nastavnika u bazi!")))
                    .flatMap(nastavnikId -> {
                        IspitDto noviIspit = IspitDto.builder()
                                .ispitniRokId(rokId)
                                .predmetId(odabraniPredmetId)
                                .nastavnikId(nastavnikId)
                                .datum(datumPicker.getValue())
                                .vremePocetka(LocalTime.of(satiCb.getValue(), minutiCb.getValue()))
                                .zakljucen(false)
                                .build();

                        return ispitService.saveIspit(noviIspit);
                    })
                    .subscribe(
                            res -> Platform.runLater(() -> {
                                prikaziPoruku("Uspeh", "Ispit je uspešno kreiran u bazi sa ID-jem: " + res.getId(), Alert.AlertType.INFORMATION);
                                clearForm();
                            }),
                            err -> Platform.runLater(() ->
                                    prikaziPoruku("Greška na serveru", "Ispit ne zadovoljava uslove.", Alert.AlertType.ERROR))
                    );

        } catch (NumberFormatException e) {
            prikaziPoruku("Greška u formatu", "ID roka mora biti broj!", Alert.AlertType.ERROR);
        }
    }
    private boolean isInputInvalid() {
        return
                ispitniRokCb.getValue()==null ||
                predmetCb.getValue() == null ||
                datumPicker.getValue() == null ||
                satiCb.getValue() == null ||
                minutiCb.getValue() == null;
    }

    private void clearForm() {
         ispitniRokCb.getSelectionModel().clearSelection();
        datumPicker.setValue(null);
        satiCb.getSelectionModel().clearSelection();
        minutiCb.getSelectionModel().clearSelection();
        predmetCb.getSelectionModel().clearSelection();
    }

    private void prikaziPoruku(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}