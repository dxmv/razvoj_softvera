package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.raflab.studsluzba.model.dto.CreateUplataRequest;
import org.raflab.studsluzba.model.dto.UplataDto;
import org.raflab.studsluzbadesktopclient.services.PaymentService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Consumer;

@Component
public class PaymentModalController {

    private final PaymentService paymentService;

    @FXML
    private Label studentLabel;
    @FXML
    private TextField iznosTf;
    @FXML
    private DatePicker datumUplateDp;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;

    private Long currentStudentId;
    private Consumer<UplataDto> onSuccessCallback;

    public PaymentModalController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @FXML
    public void initialize() {
        datumUplateDp.setValue(LocalDate.now());
        clearMessages();
        
        // Allow only numeric input with decimal
        iznosTf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                iznosTf.setText(oldVal);
            }
        });
    }

    public void setStudentInfo(Long studentId, String studentName) {
        this.currentStudentId = studentId;
        if (studentLabel != null) {
            studentLabel.setText(studentName != null ? studentName : "ID: " + studentId);
        }
    }

    public void setOnSuccessCallback(Consumer<UplataDto> callback) {
        this.onSuccessCallback = callback;
    }

    @FXML
    public void handlePayment() {
        clearMessages();

        if (currentStudentId == null) {
            showError("ID studenta nije postavljen");
            return;
        }

        String iznosText = iznosTf.getText();
        if (iznosText == null || iznosText.isBlank()) {
            showError("Molimo unesite iznos uplate");
            return;
        }

        BigDecimal iznos;
        try {
            iznos = new BigDecimal(iznosText.trim());
            if (iznos.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Iznos mora biti pozitivan broj");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Neispravan format iznosa");
            return;
        }

        LocalDate datumUplate = datumUplateDp.getValue();
        if (datumUplate == null) {
            showError("Molimo izaberite datum uplate");
            return;
        }

        CreateUplataRequest request = CreateUplataRequest.builder()
                .iznosUDinarima(iznos)
                .datumUplate(datumUplate)
                .build();

        paymentService.createPayment(currentStudentId, request)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            showSuccess("Uplata uspešno evidentirana!");
                            if (onSuccessCallback != null) {
                                onSuccessCallback.accept(result);
                            }
                            closeAfterDelay();
                        }),
                        error -> Platform.runLater(() -> {
                            String message = error.getMessage();
                            if (message != null && message.contains("404")) {
                                showError("Greška: Student nije pronađen.");
                            } else {
                                showError("Greška pri evidentiranju uplate: " + message);
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
        Stage stage = (Stage) studentLabel.getScene().getWindow();
        stage.close();
    }
}
