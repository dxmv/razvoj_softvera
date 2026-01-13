package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import org.raflab.studsluzbadesktopclient.dtos.StudentDTO;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class SearchStudentController {

    private final StudentService studentService;

    @FXML
    private TextField imeStudentaTf;

    @FXML
    private TableView<StudentDTO> tabelaStudenti;

    public SearchStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    public void handleSearchStudent(ActionEvent actionEvent) {
        if(imeStudentaTf.getText().isEmpty())
            tabelaStudenti.setItems(FXCollections.observableArrayList(studentService.sviStudenti()));
        else{
            Flux<StudentDTO> flux = studentService.searchStudentsAsync(imeStudentaTf.getText());
            //Mono predstavlja 0 ili 1 element.
            flux.collectList() // pretvara Flux u Mono<List<StudentDTO>>
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
}