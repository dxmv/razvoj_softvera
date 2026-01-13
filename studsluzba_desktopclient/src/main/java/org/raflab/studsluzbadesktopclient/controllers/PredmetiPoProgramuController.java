package org.raflab.studsluzbadesktopclient.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.StudProgramDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class PredmetiPoProgramuController {

    @FXML private ComboBox<StudProgramDto> programComboBox;
    @FXML private TableView<PredmetDto> tabelaPredmeti;
    @FXML private TableColumn<PredmetDto, String> sifraCol;
    @FXML private TableColumn<PredmetDto, String> nazivCol;
    @FXML private TableColumn<PredmetDto, Integer> espbCol;
    @FXML private TableColumn<PredmetDto, Integer> semestarCol;
    @FXML private Button btnDodaj;

    private final WebClient webClient;

    public PredmetiPoProgramuController(WebClient webClient) {
        this.webClient = webClient;
    }

    @FXML
    public void initialize() {
        // Odgovara "sifra" u JSON-u
        sifraCol.setCellValueFactory(new PropertyValueFactory<>("sifra"));

        // Odgovara "naziv" u JSON-u
        nazivCol.setCellValueFactory(new PropertyValueFactory<>("naziv"));

        // VAŽNO: Odgovara "espbBodovi" u JSON-u (ranije je bilo "espb")
        espbCol.setCellValueFactory(new PropertyValueFactory<>("espbBodovi"));

        // Odgovara "semestar" u JSON-u
        semestarCol.setCellValueFactory(new PropertyValueFactory<>("semestar"));

        // Kako će ComboBox prikazivati programe (naziv umesto memorijske adrese)
        programComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(StudProgramDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNaziv());
            }
        });
        programComboBox.setButtonCell(programComboBox.getCellFactory().call(null));

        // Reakcija na promenu u ComboBoxu
        programComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ucitajPredmeteZaProgram(newVal.getId());
                btnDodaj.setDisable(false);
            }
        });

        loadStudijskePrograme();
    }

    private void loadStudijskePrograme() {
        webClient.get().uri("/api/stud-programi").retrieve()
                .bodyToFlux(StudProgramDto.class).collectList()
                .subscribe(list -> Platform.runLater(() -> programComboBox.getItems().setAll(list)));
    }

    private void ucitajPredmeteZaProgram(Long programId) {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/predmeti/by-stud-program")
                        .queryParam("studProgramId", programId)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class) // Uzimamo kao JSON jer je Page objekat
                .subscribe(node -> {
                    JsonNode content = node.get("content");
                    List<PredmetDto> predmeti = new ArrayList<>();

                    if (content != null && content.isArray()) {
                        content.forEach(p -> {
                            predmeti.add(PredmetDto.builder()
                                    .id(p.get("id").asLong())
                                    .sifra(p.get("sifra").asText())
                                    .naziv(p.get("naziv").asText())
                                    .espbBodovi(p.get("espbBodovi").asInt())
                                    .semestar(p.get("semestar").asInt())
                                    .studijskiProgramId(programId)
                                    .build());
                        });
                    }

                    Platform.runLater(() -> tabelaPredmeti.getItems().setAll(predmeti));
                }, error -> {
                    System.err.println("Greška pri učitavanju predmeta: " + error.getMessage());
                });
    }

    @FXML
    private void handleDodajPredmet() {
        // Ovde ćemo pozvati formu za novi predmet
        System.out.println("Otvaram formu za program: " + programComboBox.getValue().getNaziv());
    }
}