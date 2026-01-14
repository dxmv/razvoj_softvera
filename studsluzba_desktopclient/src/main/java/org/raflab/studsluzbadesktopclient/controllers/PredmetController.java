package org.raflab.studsluzbadesktopclient.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.StatistikaDto;
import org.raflab.studsluzba.model.dto.StudProgramDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class PredmetController {

    @FXML private ComboBox<StudProgramDto> studProgramCb;
    @FXML private TableView<PredmetDto> predmetiTable;
    @FXML private TableColumn<PredmetDto, String> colSifra;
    @FXML private TableColumn<PredmetDto, String> colNaziv;
    @FXML private TableColumn<PredmetDto, Integer> colEspb;
    @FXML private TableColumn<PredmetDto, Integer> colSemestar;

    @FXML private TextField nazivField;
    @FXML private TextField sifraField;
    @FXML private TextField espbField;
    @FXML private TextField semestarField;
    @FXML private ComboBox<StudProgramDto> studProgramUnosCb;

    @FXML private ComboBox<StudProgramDto> statProgramCb;
    @FXML private TextField godinaOdField;
    @FXML private TextField godinaDoField;
    @FXML private TableView<StatistikaDto> statistikaTable;
    @FXML private TableColumn<StatistikaDto, String> colStatPredmet;
    @FXML private TableColumn<StatistikaDto, Double> colStatProsek;
    @FXML private TableColumn<StatistikaDto, String> colStatPeriod;

    @Autowired
    private WebClient webClient;

    @FXML
    public void initialize() {
        initTableColumns();
        setupComboBoxConverters();
        ucitajSveStudijskePrograme();

        studProgramCb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) ucitajPredmeteZaPregled(newVal.getId());
        });
    }

    private void initTableColumns() {
        colSifra.setCellValueFactory(new PropertyValueFactory<>("sifra"));
        colNaziv.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        colEspb.setCellValueFactory(new PropertyValueFactory<>("espbBodovi"));
        colSemestar.setCellValueFactory(new PropertyValueFactory<>("semestar"));

        colStatPredmet.setCellValueFactory(new PropertyValueFactory<>("nazivPredmeta"));
        colStatProsek.setCellValueFactory(new PropertyValueFactory<>("prosek"));
        colStatPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
    }

    private void setupComboBoxConverters() {
        StringConverter<StudProgramDto> programConverter = new StringConverter<>() {
            @Override
            public String toString(StudProgramDto object) {
                return object == null ? "" : object.getNaziv();
            }
            @Override
            public StudProgramDto fromString(String string) { return null; }
        };
        studProgramCb.setConverter(programConverter);
        studProgramUnosCb.setConverter(programConverter);
        statProgramCb.setConverter(programConverter);
    }

    private void ucitajSveStudijskePrograme() {
        webClient.get().uri("/api/stud-programi").retrieve()
                .bodyToFlux(StudProgramDto.class).collectList()
                .subscribe(list -> Platform.runLater(() -> {
                    studProgramCb.setItems(FXCollections.observableArrayList(list));
                    studProgramUnosCb.setItems(FXCollections.observableArrayList(list));
                    statProgramCb.setItems(FXCollections.observableArrayList(list));
                }));
    }

    private void ucitajPredmeteZaPregled(Long programId) {
        getPredmetiByProgram(programId).subscribe(list ->
                Platform.runLater(() -> predmetiTable.setItems(FXCollections.observableArrayList(list))));
    }

    @FXML
    private void handleIzracunajStatistiku() {
        StudProgramDto program = statProgramCb.getValue();
        if (program == null) {
            prikaziObavestenje("Greška", "Izaberite program u statistici.");
            return;
        }

        String gOd = godinaOdField.getText();
        String gDo = godinaDoField.getText();
        statistikaTable.getItems().clear();

        getPredmetiByProgram(program.getId()).subscribe(predmeti -> {
            for (PredmetDto p : predmeti) {
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/polozeni-predmeti/averageOcena")
                                .queryParam("predmetId", p.getId())
                                .queryParam("godinaOd", gOd)
                                .queryParam("godinaDo", gDo).build())
                        .retrieve()
                        .bodyToMono(Double.class)
                        .subscribe(prosek -> {
                            if (prosek != null && prosek > 0) {
                                Platform.runLater(() -> statistikaTable.getItems().add(
                                        new StatistikaDto(p.getNaziv(), prosek, gOd + "-" + gDo)));
                            }
                        }, err -> {});
            }
        });
    }

    private reactor.core.publisher.Mono<List<PredmetDto>> getPredmetiByProgram(Long programId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/predmeti/by-stud-program")
                        .queryParam("studProgramId", programId).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> {
                    List<PredmetDto> list = new ArrayList<>();
                    JsonNode content = node.get("content");
                    if (content != null && content.isArray()) {
                        content.forEach(p -> list.add(PredmetDto.builder()
                                .id(p.get("id").asLong()).sifra(p.get("sifra").asText())
                                .naziv(p.get("naziv").asText()).espbBodovi(p.get("espbBodovi").asInt())
                                .semestar(p.get("semestar").asInt()).build()));
                    }
                    return list;
                });
    }

    @FXML
    private void handleSave() {
        if (studProgramUnosCb.getValue() == null) return;
        PredmetDto dto = PredmetDto.builder()
                .naziv(nazivField.getText()).sifra(sifraField.getText())
                .studijskiProgramId(studProgramUnosCb.getValue().getId())
                .espbBodovi(Integer.parseInt(espbField.getText()))
                .semestar(Integer.parseInt(semestarField.getText())).build();

        webClient.post().uri("/api/predmeti/create").bodyValue(dto).retrieve().toBodilessEntity()
                .subscribe(r -> Platform.runLater(() -> {
                    prikaziObavestenje("Uspeh", "Dodato!");
                    nazivField.clear(); sifraField.clear(); espbField.clear(); semestarField.clear();
                }));
    }

    private void prikaziObavestenje(String title, String content) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.show();
        });
    }
}