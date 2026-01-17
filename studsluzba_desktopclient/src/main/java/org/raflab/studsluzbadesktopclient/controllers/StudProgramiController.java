package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.StudProgramDto;
import org.raflab.studsluzbadesktopclient.services.StudProgramService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class StudProgramiController {

    @FXML private TableView<StudProgramDto> tabelaProgrami;
    @FXML private TableColumn<StudProgramDto, String> oznakaCol;
    @FXML private TableColumn<StudProgramDto, String> nazivCol;
    @FXML private TableColumn<StudProgramDto, String> zvanjeCol;
    @FXML private TableColumn<StudProgramDto, Integer> espbCol;

    private final StudProgramService studProgramService;

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

        CompletableFuture.supplyAsync(() -> studProgramService.getSudijskiProgramiSorted())
                .thenAccept(list -> {
                    if (list != null) {
                        Platform.runLater(() ->
                                tabelaProgrami.setItems(FXCollections.observableArrayList(list))
                        );
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
}