package org.raflab.studsluzbadesktopclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.springframework.stereotype.Component;

@Component
public class EventsController {

    @FXML
    private void mouseKlik(MouseEvent event) {
        System.out.println("Kliknuto dugme");
    }
    @FXML
    private void mouseUsao(MouseEvent event) {
        System.out.println("Miš je ušao u dugme");
    }
    @FXML
    private void mouseIzasao(MouseEvent event) {
        System.out.println("Miš je izašao iz dugmeta");
    }

    @FXML
    private void klikNaPane(MouseEvent event) {
        if (event.getClickCount() == 2) {
            System.out.println("Dvostruki klik");
        } else if (event.getButton() == MouseButton.PRIMARY) {
            System.out.println("Levi klik");
        } else if (event.getButton() == MouseButton.SECONDARY) {
            System.out.println("Desni klik");
        }
    }

    @FXML
    private void mousePomeren(MouseEvent event) {
        //System.out.println("X=" + event.getX() + " Y=" + event.getY());
    }

    @FXML
    private Circle krug;
    private double offsetX, offsetY;
    @FXML
    private void mousePressed(MouseEvent event) {
        offsetX = event.getSceneX() - krug.getCenterX();
        offsetY = event.getSceneY() - krug.getCenterY();
    }
    @FXML
    private void mouseDragged(MouseEvent event) {
        krug.setCenterX(event.getSceneX() - offsetX);
        krug.setCenterY(event.getSceneY() - offsetY);
    }


    @FXML
    private void hoverOn(MouseEvent event) {
        ((Button) event.getSource()).setStyle("-fx-background-color: lightblue");
    }
    @FXML
    private void hoverOff(MouseEvent event) {
        ((Button) event.getSource()).setStyle("");
    }

    @FXML
    private Pane platno;
    private double startX, startY;
    private Line trenutnaLinija;
    @FXML
    private void pocetakLinije(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
        trenutnaLinija = new Line(startX, startY, startX, startY);
        platno.getChildren().add(trenutnaLinija);
    }
    @FXML
    private void nastaviLiniju(MouseEvent event) {
        trenutnaLinija.setEndX(event.getX());
        trenutnaLinija.setEndY(event.getY());
    }

    @FXML
    private Pane pane;
    @FXML
    private void prikaziMenu(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            ContextMenu menu = new ContextMenu();
            MenuItem item1 = new MenuItem("Opcija 1");
            MenuItem item2 = new MenuItem("Opcija 2");
            menu.getItems().addAll(item1, item2);
            menu.show(pane, event.getScreenX(), event.getScreenY());
        }
    }

    @FXML
    private void kursorNaRect(MouseEvent event) {
        ((Rectangle) event.getSource()).setCursor(Cursor.HAND);
    }
    @FXML
    private void kursorVanRect(MouseEvent event) {
        ((Rectangle) event.getSource()).setCursor(Cursor.DEFAULT);
    }

}
