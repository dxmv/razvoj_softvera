package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.state.SelectedStudentStore;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

@Component
public class SearchStudentController {

    private final StudentService studentService;
    private final MainView mainView;
    private final SelectedStudentStore selectedStudentStore;

    @FXML
    private TextField imeStudentaTf;

    @FXML
    private TextField brojIndeksaTf;

    @FXML
    private TableView<StudentDto> tabelaStudenti;

    @FXML
    private Button openProfileButton;

    public SearchStudentController(StudentService studentService,
                                   MainView mainView,
                                   SelectedStudentStore selectedStudentStore) {
        this.studentService = studentService;
        this.mainView = mainView;
        this.selectedStudentStore = selectedStudentStore;
    }

    @FXML
    public void initialize() {
        tabelaStudenti.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                openSelectedStudentProfile();
            }
        });
        if (openProfileButton != null) {
            openProfileButton.disableProperty().bind(tabelaStudenti.getSelectionModel().selectedItemProperty().isNull());
        }
    }

    public void handleSearchStudent(ActionEvent actionEvent) {
        String imeFilter = imeStudentaTf.getText() == null ? "" : imeStudentaTf.getText().trim();
        if (imeFilter.isEmpty()) {
            tabelaStudenti.setItems(FXCollections.observableArrayList(studentService.sviStudenti()));
        } else {
            tabelaStudenti.setItems(FXCollections.observableArrayList(studentService.searchStudentsPaged(imeFilter)));
        }
    }

    public void handleFindStudentByIndex(ActionEvent actionEvent) {
        String indeks = brojIndeksaTf.getText() != null ? brojIndeksaTf.getText().trim() : "";
        if (indeks.isEmpty()) {
            showInfo("Unesite broj indeksa za pretragu.");
            return;
        }
        try {
            StudentDto student = studentService.findStudentByIndex(indeks);
            if (student == null) {
                showInfo("Student sa zadatim brojem indeksa nije pronađen.");
                return;
            }
            openStudentProfile(student, indeks);
        } catch (Exception ex) {
            showInfo("Došlo je do greške pri pretrazi: " + ex.getMessage());
        }
    }

    private void openSelectedStudentProfile() {
        StudentDto selected = tabelaStudenti.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        openStudentProfile(selected, null);
    }

    public void handleOpenSelectedStudentProfile(ActionEvent actionEvent) {
        openSelectedStudentProfile();
    }

    private void openStudentProfile(StudentDto student, String indeks) {
        if (student == null) {
            return;
        }
        if (indeks == null || indeks.isBlank()) {
            selectedStudentStore.select(student);
        } else {
            selectedStudentStore.select(student, indeks);
        }
        mainView.navigateTo("studentProfile");
    }

    private void showInfo(String poruka) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pretraga studenta");
        alert.setHeaderText(null);
        alert.setContentText(poruka);
        alert.showAndWait();
    }
}
