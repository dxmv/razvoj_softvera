package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.raflab.studsluzba.model.dto.UpisGodineDto;
import org.raflab.studsluzba.model.dto.UpisGodineEnrollmentRequest;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Consumer;

@Component
public class EnrollYearModalController {

    private final StudentService studentService;

    @FXML
    private Label indeksLabel;
    @FXML
    private ComboBox<Integer> godinaStudijaCb;
    @FXML
    private DatePicker datumUpisaDp;
    @FXML
    private TextArea napomenaTa;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;

    private String currentIndex;
    private Consumer<UpisGodineDto> onSuccessCallback;

    public EnrollYearModalController(StudentService studentService) {
        this.studentService = studentService;
    }

    @FXML
    public void initialize() {
        godinaStudijaCb.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
        godinaStudijaCb.setValue(1);
        datumUpisaDp.setValue(LocalDate.now());
        clearMessages();
    }

    public void setIndex(String index) {
        this.currentIndex = index;
        if (indeksLabel != null) {
            indeksLabel.setText(index != null ? index : "--");
        }
    }

    public void setOnSuccessCallback(Consumer<UpisGodineDto> callback) {
        this.onSuccessCallback = callback;
    }

    @FXML
    public void handleEnroll() {
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

        UpisGodineEnrollmentRequest request = UpisGodineEnrollmentRequest.builder()
                .godinaStudija(godinaStudija)
                .datumUpisa(datumUpisaDp.getValue())
                .napomena(napomenaTa.getText())
                .build();

        studentService.enrollYear(currentIndex, request)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            showSuccess("Godina uspešno upisana!");
                            if (onSuccessCallback != null) {
                                onSuccessCallback.accept(result);
                            }
                            closeAfterDelay();
                        }),
                        error -> Platform.runLater(() -> {
                            String message = error.getMessage();
                            if (message != null && message.contains("400")) {
                                showError("Greška: Godina je već upisana ili nisu ispunjeni uslovi za upis.");
                            } else if (message != null && message.contains("404")) {
                                showError("Greška: Nema aktivne školske godine.");
                            } else {
                                showError("Greška pri upisu: " + message);
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
}
