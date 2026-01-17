package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.IspitniRokDto;
import org.raflab.studsluzba.model.dto.SkolskaGodinaDto;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.services.SkolskaGodinaService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IspitniRokUnosController {

    @FXML private ComboBox<SkolskaGodinaDto> skGodinaCb;
    @FXML private DatePicker pocetakPicker;
    @FXML private DatePicker krajPicker;

    private final IspitniRokService ispitniRokService;
    private final SkolskaGodinaService skolskaGodinaService;

    @FXML
    public void initialize() {
        initConverters();
        loadData();
    }

    private void initConverters() {
        skGodinaCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(SkolskaGodinaDto object) {
                return object == null ? "" : object.getOznaka();
            }
            @Override
            public SkolskaGodinaDto fromString(String string) { return null; }
        });
    }

    private void loadData() {
        skolskaGodinaService.getAll()
                .collectList()
                .subscribe(list -> Platform.runLater(() ->
                        skGodinaCb.setItems(FXCollections.observableArrayList(list))
                ));
    }

    @FXML
    private void handleSave() {
        if (isInputInvalid()) {
            prikaziPoruku("Upozorenje", "Sva polja moraju biti popunjena!", Alert.AlertType.WARNING);
            return;
        }

        // Provera logike datuma
        if (pocetakPicker.getValue().isAfter(krajPicker.getValue())) {
            prikaziPoruku("Greška", "Datum završetka ne može biti pre datuma početka!", Alert.AlertType.ERROR);
            return;
        }

        IspitniRokDto dto = IspitniRokDto.builder()
                .datumPocetka(pocetakPicker.getValue())
                .datumZavrsetka(krajPicker.getValue())
                .skolskaGodinaId(skGodinaCb.getValue().getId())
                .build();

        ispitniRokService.saveRok(dto).subscribe(
                res -> Platform.runLater(() -> {
                    prikaziPoruku("Uspeh", "Ispitni rok je uspešno sačuvan.", Alert.AlertType.INFORMATION);
                    clearForm();
                }),
                err -> Platform.runLater(() ->{
                    err.printStackTrace();
                        prikaziPoruku("Greška na serveru", err.getMessage(), Alert.AlertType.ERROR);
                })
        );
    }

    private void clearForm() {
        skGodinaCb.getSelectionModel().clearSelection();
        skGodinaCb.setValue(null);
        pocetakPicker.setValue(null);
        krajPicker.setValue(null);
    }

    private boolean isInputInvalid() {
        return skGodinaCb.getValue() == null ||
                pocetakPicker.getValue() == null ||
                krajPicker.getValue() == null;
    }

    private void prikaziPoruku(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}