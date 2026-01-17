package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import org.raflab.studsluzba.model.dto.ObnovaGodineDto;
import org.raflab.studsluzba.model.dto.ObnovaGodineRequest;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class RepeatYearModalController {

    private final StudentService studentService;

    @FXML
    private Label indeksLabel;
    @FXML
    private ComboBox<Integer> godinaStudijaCb;
    @FXML
    private DatePicker datumObnoveDp;
    @FXML
    private TextArea napomenaTa;
    @FXML
    private ListView<PredmetItem> predmetiListView;
    @FXML
    private Label loadingLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;

    private String currentIndex;
    private Consumer<ObnovaGodineDto> onSuccessCallback;
    private final ObservableList<PredmetItem> predmetItems = FXCollections.observableArrayList();
    private final Map<Long, BooleanProperty> selectionMap = new HashMap<>();

    public RepeatYearModalController(StudentService studentService) {
        this.studentService = studentService;
    }

    @FXML
    public void initialize() {
        godinaStudijaCb.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
        godinaStudijaCb.setValue(1);
        datumObnoveDp.setValue(LocalDate.now());
        
        predmetiListView.setItems(predmetItems);
        predmetiListView.setCellFactory(CheckBoxListCell.forListView(item -> item.selectedProperty()));
        
        clearMessages();
    }

    public void setIndex(String index) {
        this.currentIndex = index;
        if (indeksLabel != null) {
            indeksLabel.setText(index != null ? index : "--");
        }
        if (index != null && !index.isBlank()) {
            loadFailedSubjects(index);
        }
    }

    public void setOnSuccessCallback(Consumer<ObnovaGodineDto> callback) {
        this.onSuccessCallback = callback;
    }

    private void loadFailedSubjects(String index) {
        loadingLabel.setText("Učitavanje nepoloženih predmeta...");
        loadingLabel.setVisible(true);
        predmetItems.clear();
        selectionMap.clear();

        studentService.findFailedExams(index)
                .collectList()
                .subscribe(
                        subjects -> Platform.runLater(() -> {
                            loadingLabel.setVisible(false);
                            if (subjects.isEmpty()) {
                                loadingLabel.setText("Nema nepoloženih predmeta.");
                                loadingLabel.setVisible(true);
                            } else {
                                for (PredmetDto predmet : subjects) {
                                    PredmetItem item = new PredmetItem(predmet);
                                    predmetItems.add(item);
                                    selectionMap.put(predmet.getId(), item.selectedProperty());
                                }
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            loadingLabel.setText("Greška pri učitavanju predmeta: " + error.getMessage());
                        })
                );
    }

    @FXML
    public void handleRepeat() {
        clearMessages();

        if (currentIndex == null || currentIndex.isBlank()) {
            showError("Indeks nije postavljen");
            return;
        }

        Integer godinaStudija = godinaStudijaCb.getValue();
        if (godinaStudija == null) {
            showError("Molimo izaberite godinu studija");
            return;
        }

        Set<Long> selectedPredmetIds = predmetItems.stream()
                .filter(item -> item.isSelected())
                .map(item -> item.getPredmet().getId())
                .collect(Collectors.toSet());

        if (selectedPredmetIds.isEmpty()) {
            showError("Molimo izaberite bar jedan predmet za obnovu");
            return;
        }

        ObnovaGodineRequest request = ObnovaGodineRequest.builder()
                .godinaStudija(godinaStudija)
                .datumObnove(datumObnoveDp.getValue())
                .napomena(napomenaTa.getText())
                .predmetIds(selectedPredmetIds)
                .build();

        studentService.repeatYear(currentIndex, request)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            showSuccess("Godina uspešno obnovljena!");
                            if (onSuccessCallback != null) {
                                onSuccessCallback.accept(result);
                            }
                            closeAfterDelay();
                        }),
                        error -> Platform.runLater(() -> {
                            String message = error.getMessage();
                            if (message != null && message.contains("400")) {
                                showError("Greška: Godina je već obnovljena, predmet je već položen, ili ESPB prekoračuje limit.");
                            } else if (message != null && message.contains("404")) {
                                showError("Greška: Nema aktivne školske godine.");
                            } else {
                                showError("Greška pri obnovi: " + message);
                            }
                        })
                );
    }

    @FXML
    public void handleCancel() {
        closeModal();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        successLabel.setText("");
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        errorLabel.setText("");
    }

    private void clearMessages() {
        errorLabel.setText("");
        successLabel.setText("");
    }

    private void closeAfterDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Platform.runLater(this::closeModal);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void closeModal() {
        Stage stage = (Stage) indeksLabel.getScene().getWindow();
        stage.close();
    }

    // Inner class for ListView items with checkbox support
    public static class PredmetItem {
        private final PredmetDto predmet;
        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        public PredmetItem(PredmetDto predmet) {
            this.predmet = predmet;
        }

        public PredmetDto getPredmet() {
            return predmet;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        @Override
        public String toString() {
            return predmet.getSifra() + " - " + predmet.getNaziv() + " (" + predmet.getEspbBodovi() + " ESPB)";
        }
    }
}
