package org.raflab.studsluzbadesktopclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Component;

@Component
public class MainWindowController {

    @FXML
    private BorderPane mainPane;

    public void setContent(Node node) {
        if (mainPane != null) {
            mainPane.setCenter(node);
        }
    }
}
