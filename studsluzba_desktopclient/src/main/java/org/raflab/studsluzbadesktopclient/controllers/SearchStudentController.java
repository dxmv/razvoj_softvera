package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class SearchStudentController {

    private final StudentService studentService;
    private final MainView mainView;

    @FXML
    private TextField imeStudentaTf;

    @FXML
    private TableView<StudentDto> tabelaStudenti;

    public SearchStudentController(StudentService studentService, MainView mainView) {
        this.studentService = studentService;
        this.mainView = mainView;
    }

    @FXML
    public void initialize() {
        tabelaStudenti.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                openSelectedStudentProfile();
            }
        });
    }

    public void handleSearchStudent(ActionEvent actionEvent) {
        if(imeStudentaTf.getText().isEmpty())
            tabelaStudenti.setItems(FXCollections.observableArrayList(studentService.sviStudenti()));
        else{
            Flux<StudentDto> flux = studentService.searchStudentsAsync(imeStudentaTf.getText());
            //Mono predstavlja 0 ili 1 element.
            flux.collectList() // pretvara Flux u Mono<List<StudentDto>>
                    .subscribe(
                            list -> {
                                // Ovo se izvršava kada stigne rezultat
                                tabelaStudenti.setItems(FXCollections.observableArrayList(list));
                                System.out.println("Rezultat je stigao.");
                            },
                            error -> {
                                System.out.println(error.getMessage());
                            }
                    );
            System.out.println("Nakon search operacije.");
        }
    }

    private void openSelectedStudentProfile() {
        StudentDto selected = tabelaStudenti.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        // TODO: When a dedicated student profile view is implemented, pass the student id via ViewState attributes.
        mainView.navigateTo("newStudent");
    }
}
