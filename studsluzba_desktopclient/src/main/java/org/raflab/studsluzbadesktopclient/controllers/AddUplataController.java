package org.raflab.studsluzbadesktopclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.PaymentService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class AddUplataController {

    private final PaymentService paymentService;
    private final MainView mainView;
    
    private Long studentId;
    private StudentProfileController profileController;

    @FXML
    private TextField iznosTf;
    
    @FXML
    private DatePicker datumDp;
    
    @FXML
    private Label errorLabel;

    public AddUplataController(PaymentService paymentService, MainView mainView) {
        this.paymentService = paymentService;
        this.mainView = mainView;
    }

    @FXML
    public void initialize() {
        // Set default date to today
        datumDp.setValue(LocalDate.now());
        errorLabel.setText("");
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setProfileController(StudentProfileController profileController) {
        this.profileController = profileController;
    }

    @FXML
    public void handleSaveUplata() {
        errorLabel.setText("");
        
        if (studentId == null) {
            errorLabel.setText("Greška: Student ID nije dostupan");
            return;
        }

        // Validate amount
        String iznosText = iznosTf.getText();
        if (iznosText == null || iznosText.trim().isEmpty()) {
            errorLabel.setText("Molimo unesite iznos");
            return;
        }

        BigDecimal iznos;
        try {
            iznos = new BigDecimal(iznosText.trim());
            if (iznos.compareTo(BigDecimal.ZERO) <= 0) {
                errorLabel.setText("Iznos mora biti veći od nule");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Neispravan format iznosa");
            return;
        }

        LocalDate datum = datumDp.getValue();
        
        // Call service to create payment
        paymentService.createPaymentWithCurrentRate(studentId, iznos, datum)
                .subscribe(
                        uplataDto -> {
                            // Success - close modal and refresh parent
                            javafx.application.Platform.runLater(() -> {
                                if (profileController != null) {
                                    profileController.refreshPaymentsData();
                                }
                                closeModal();
                            });
                        },
                        error -> {
                            // Error - show message
                            javafx.application.Platform.runLater(() -> {
                                String errorMessage = error.getMessage();
                                if (errorMessage != null && errorMessage.contains("Nema aktivne skolske fodine")) {
                                    errorLabel.setText("Greška: Nema aktivne školske godine");
                                } else if (errorMessage != null) {
                                    errorLabel.setText("Greška: " + errorMessage);
                                } else {
                                    errorLabel.setText("Greška pri kreiranju uplate");
                                }
                            });
                        }
                );
    }

    @FXML
    public void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) iznosTf.getScene().getWindow();
        stage.close();
    }
}
