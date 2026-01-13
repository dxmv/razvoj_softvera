package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.raflab.studsluzba.model.dto.StudProgramDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StudProgramiController {

    @FXML private TableView<StudProgramDto> tabelaProgrami;
    @FXML private TableColumn<StudProgramDto, String> oznakaCol;
    @FXML private TableColumn<StudProgramDto, String> nazivCol;
    @FXML private TableColumn<StudProgramDto, String> zvanjeCol;
    @FXML private TableColumn<StudProgramDto, Integer> espbCol;

    private final WebClient webClient;

    public StudProgramiController(WebClient webClient) {
        this.webClient = webClient;
    }

    @FXML
    public void initialize() {
        oznakaCol.setCellValueFactory(new PropertyValueFactory<>("oznaka"));
        nazivCol.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        zvanjeCol.setCellValueFactory(new PropertyValueFactory<>("zvanje"));
        espbCol.setCellValueFactory(new PropertyValueFactory<>("espbBodovi"));

        loadData();
    }

    @FXML
    private void loadData() {
        webClient.get()
                .uri("/api/stud-programi")
                .retrieve()
                .bodyToFlux(StudProgramDto.class)
                .collectList()
                .subscribe(list -> Platform.runLater(() -> tabelaProgrami.getItems().setAll(list)));
    }
}