package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.StudProgramDto;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PredmetController {

    @FXML private ComboBox<StudProgramDto> studProgramCb;
    @FXML private TableView<PredmetDto> predmetiTable;
    @FXML private TableColumn<PredmetDto, String> colSifra;
    @FXML private TableColumn<PredmetDto, String> colNaziv;
    @FXML private TableColumn<PredmetDto, Integer> colEspb;
    @FXML private TableColumn<PredmetDto, Integer> colSemestar;
    @FXML private TableColumn<PredmetDto, Double> colProsek;

    @FXML private TextField nazivField;
    @FXML private TextField sifraField;
    @FXML private TextField espbField;
    @FXML private TextField semestarField;
    @FXML private ComboBox<StudProgramDto> studProgramUnosCb;

    @FXML private TextField godinaOdField;
    @FXML private TextField godinaDoField;

    @FXML private Label messageLabel;

    @Autowired
    private PredmetService predmetService;

    @FXML
    public void initialize() {
        initTableColumns();
        setupComboBoxConverters();
        ucitajSveStudijskePrograme();
    }

    private void initTableColumns() {
        colSifra.setCellValueFactory(new PropertyValueFactory<>("sifra"));
        colNaziv.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        colEspb.setCellValueFactory(new PropertyValueFactory<>("espbBodovi"));
        colSemestar.setCellValueFactory(new PropertyValueFactory<>("semestar"));
        colProsek.setCellValueFactory(new PropertyValueFactory<>("prosecnaOcena"));
    }

    private void setupComboBoxConverters() {
        StringConverter<StudProgramDto> programConverter = new StringConverter<>() {
            @Override
            public String toString(StudProgramDto object) {
                return object == null ? "" : object.getNaziv();
            }
            @Override
            public StudProgramDto fromString(String string) { return null; }
        };
        studProgramCb.setConverter(programConverter);
        studProgramUnosCb.setConverter(programConverter);
    }

    private void ucitajSveStudijskePrograme() {
        predmetService.getAllStudijskiProgrami()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> {
                    studProgramCb.setItems(FXCollections.observableArrayList(list));
                    studProgramUnosCb.setItems(FXCollections.observableArrayList(list));
                }));
    }

    @FXML
    private void handleUcitajPredmete() {
        StudProgramDto program = studProgramCb.getValue();
        if (program == null) {
            prikaziObavestenje("Greška", "Izaberite studijski program.");
            return;
        }

        String godinaOd = godinaOdField.getText().trim();
        String godinaDo = godinaDoField.getText().trim();

        predmetService.getPredmetiByProgram(program.getId()).subscribe(predmeti -> {
            ObservableList<PredmetDto> tableData = FXCollections.observableArrayList(predmeti);
            Platform.runLater(() -> predmetiTable.setItems(tableData));

            // Fetch average grades for each predmet
            for (PredmetDto p : predmeti) {
                predmetService.getAverageOcena(p.getId(), godinaOd, godinaDo)
                        .subscribe(prosek -> {
                            p.setProsecnaOcena(prosek);
                            Platform.runLater(() -> predmetiTable.refresh());
                        }, err -> {
                            // If error, leave prosecnaOcena as null
                        });
            }
        });
    }

    @FXML
    private void handleGenerisiIzvestaj() {
        StudProgramDto program = studProgramCb.getValue();
        if (program == null) {
            prikaziObavestenje("Greška", "Izaberite studijski program.");
            return;
        }

        ObservableList<PredmetDto> items = predmetiTable.getItems();
        if (items == null || items.isEmpty()) {
            prikaziObavestenje("Greška", "Nema podataka za generisanje izveštaja. Prvo učitajte predmete.");
            return;
        }

        // Filter out predmeti without average grades
        List<PredmetDto> predmetiSaProsekom = items.stream()
                .filter(p -> p.getProsecnaOcena() != null && p.getProsecnaOcena() > 0)
                .collect(Collectors.toList());

        if (predmetiSaProsekom.isEmpty()) {
            prikaziObavestenje("Greška", "Nema predmeta sa prosečnom ocenom za izabrani period.");
            return;
        }

        String godinaOd = godinaOdField.getText().trim();
        String godinaDo = godinaDoField.getText().trim();

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("studijskiProgram", program.getNaziv());
            parameters.put("godinaOd", godinaOd);
            parameters.put("godinaDo", godinaDo);

            JasperReport report = JasperCompileManager.compileReport(
                    MainView.class.getResourceAsStream("/reports/predmetiStatistika.jrxml"));
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(predmetiSaProsekom);
            JasperPrint jp = JasperFillManager.fillReport(report, parameters, dataSource);

            String fileName = "predmeti_statistika_" + program.getNaziv().replaceAll("\\s+", "_") + ".pdf";
            JasperExportManager.exportReportToPdfFile(jp, fileName);

            prikaziObavestenje("Uspeh", "Izveštaj je uspešno generisan: " + fileName);
        } catch (JRException e) {
            prikaziObavestenje("Greška", "Greška pri generisanju izveštaja: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @FXML
    private void handleSave() {
        clearMessage();

        // Validation
        if (nazivField.getText() == null || nazivField.getText().trim().isEmpty()) {
            showErrorMessage("Naziv predmeta je obavezan.");
            return;
        }
        if (sifraField.getText() == null || sifraField.getText().trim().isEmpty()) {
            showErrorMessage("Šifra predmeta je obavezna.");
            return;
        }
        if (studProgramUnosCb.getValue() == null) {
            showErrorMessage("Morate izabrati studijski program.");
            return;
        }

        int espb;
        int semestar;
        try {
            espb = Integer.parseInt(espbField.getText().trim());
            if (espb <= 0) {
                showErrorMessage("ESPB bodovi moraju biti pozitivan broj.");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorMessage("ESPB bodovi moraju biti validan broj.");
            return;
        }

        try {
            semestar = Integer.parseInt(semestarField.getText().trim());
            if (semestar <= 0 || semestar > 8) {
                showErrorMessage("Semestar mora biti između 1 i 8.");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Semestar mora biti validan broj.");
            return;
        }

        PredmetDto dto = PredmetDto.builder()
                .naziv(nazivField.getText().trim())
                .sifra(sifraField.getText().trim())
                .studijskiProgramId(studProgramUnosCb.getValue().getId())
                .espbBodovi(espb)
                .semestar(semestar)
                .build();

        predmetService.savePredmet(dto).subscribe(
                v -> Platform.runLater(() -> {
                    showSuccessMessage("Predmet \"" + dto.getNaziv() + "\" je uspešno dodat.");
                    resetForm();
                }),
                error -> Platform.runLater(() -> {
                    String errorMsg = error.getMessage();
                    if (errorMsg != null && errorMsg.contains("409")) {
                        showErrorMessage("Predmet sa ovom šifrom već postoji.");
                    } else if (errorMsg != null && errorMsg.contains("400")) {
                        showErrorMessage("Neispravan zahtev. Proverite unete podatke.");
                    } else {
                        showErrorMessage("Greška pri čuvanju predmeta: " + (errorMsg != null ? errorMsg : "Nepoznata greška"));
                    }
                })
        );
    }

    private void resetForm() {
        nazivField.clear();
        sifraField.clear();
        espbField.clear();
        semestarField.clear();
        studProgramUnosCb.getSelectionModel().clearSelection();
    }

    private void showSuccessMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-text-fill: #2e7d32;");
        }
    }

    private void showErrorMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-text-fill: #c62828;");
        }
    }

    private void clearMessage() {
        if (messageLabel != null) {
            messageLabel.setText("");
        }
    }

    private void prikaziObavestenje(String title, String content) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.show();
        });
    }
}