package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.raflab.studsluzba.model.dto.ObnovaGodineDto;
import org.raflab.studsluzba.model.dto.PolozenPredmetDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.RemainingTuitionDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzba.model.dto.UpisGodineDto;
import org.raflab.studsluzba.model.dto.UplataDto;
import org.raflab.studsluzbadesktopclient.services.PaymentService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.state.SelectedStudentStore;
import org.raflab.studsluzbadesktopclient.state.SelectedStudentStore.Selection;
import org.springframework.stereotype.Component;

@Component
public class StudentProfileController {

    private final SelectedStudentStore selectedStudentStore;
    private final StudentService studentService;
    private final PaymentService paymentService;

    private final ObservableList<PolozenPredmetDto> passedExams = FXCollections.observableArrayList();
    private final ObservableList<PredmetDto> failedExams = FXCollections.observableArrayList();
    private final ObservableList<UplataDto> payments = FXCollections.observableArrayList();
    private final ObservableList<UpisGodineDto> enrollments = FXCollections.observableArrayList();
    private final ObservableList<ObnovaGodineDto> repeatedYears = FXCollections.observableArrayList();

    private String activeIndex;
    private Long activeStudentId;

    @FXML
    private Label statusLabel;
    @FXML
    private Label indeksValue;
    @FXML
    private Label imeValue;
    @FXML
    private Label prezimeValue;
    @FXML
    private Label jmbgValue;
    @FXML
    private Label datumRodjenjaValue;
    @FXML
    private Label mestoRodjenjaValue;
    @FXML
    private Label drzavaRodjenjaValue;
    @FXML
    private Label polValue;
    @FXML
    private Label drzavljanstvoValue;
    @FXML
    private Label nacionalnostValue;
    @FXML
    private Label mestoPrebivalistaValue;
    @FXML
    private Label adresaValue;
    @FXML
    private Label brojTelefonaValue;
    @FXML
    private Label fakultetskiEmailValue;
    @FXML
    private Label privatniEmailValue;

    @FXML
    private TableView<PolozenPredmetDto> passedExamsTable;
    @FXML
    private Label passedExamsMessageLabel;
    @FXML
    private TableView<PredmetDto> failedExamsTable;
    @FXML
    private Label failedExamsMessageLabel;
    @FXML
    private TableView<UplataDto> paymentsTable;
    @FXML
    private Label paymentsMessageLabel;
    @FXML
    private Label remainingEurLabel;
    @FXML
    private Label remainingRsdLabel;
    @FXML
    private TableView<UpisGodineDto> enrollmentsTable;
    @FXML
    private Label enrollmentsMessageLabel;
    @FXML
    private TableView<ObnovaGodineDto> repeatedYearsTable;
    @FXML
    private Label repeatedYearsMessageLabel;

    public StudentProfileController(SelectedStudentStore selectedStudentStore,
                                    StudentService studentService,
                                    PaymentService paymentService) {
        this.selectedStudentStore = selectedStudentStore;
        this.studentService = studentService;
        this.paymentService = paymentService;
    }

    @FXML
    public void initialize() {
        if (passedExamsTable != null) {
            passedExamsTable.setItems(passedExams);
        }
        if (failedExamsTable != null) {
            failedExamsTable.setItems(failedExams);
        }
        if (paymentsTable != null) {
            paymentsTable.setItems(payments);
        }
        if (enrollmentsTable != null) {
            enrollmentsTable.setItems(enrollments);
        }
        if (repeatedYearsTable != null) {
            repeatedYearsTable.setItems(repeatedYears);
        }
        selectedStudentStore.selectionProperty().addListener((obs, oldSelection, newSelection) ->
                applySelection(newSelection));
        applySelection(selectedStudentStore.getSelection());
    }

    private void applySelection(Selection selection) {
        if (selection == null || selection.getStudent() == null) {
            statusLabel.setText("Nijedan student nije izabran. Pronađite studenta unosom broja indeksa.");
            activeIndex = null;
            activeStudentId = null;
            clearBasicInfo();
            clearDataViews();
            return;
        }
        StudentDto student = selection.getStudent();
        this.activeIndex = selection.getIndex();
        this.activeStudentId = student.getId();
        updateBasicInfo(selection);
        loadPayments(activeStudentId);
        if (hasIndex(activeIndex)) {
            loadIndexBoundData(activeIndex);
        } else {
            requestIndexForStudent(student);
        }
    }

    private void loadPassedExams(String index) {
        showMessage(passedExamsMessageLabel, "Učitavanje...");
        studentService.findPassedExams(index)
                .collectList()
                .subscribe(list -> runOnFx(() -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }
                    passedExams.setAll(list);
                    showMessage(passedExamsMessageLabel, list.isEmpty() ? "Nema evidentiranih položenih ispita." : "");
                }), error -> runOnFx(() -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }
                    passedExams.clear();
                    showMessage(passedExamsMessageLabel, "Greška: " + error.getMessage());
                }));
    }

    private void loadFailedExams(String index) {
        showMessage(failedExamsMessageLabel, "Učitavanje...");
        studentService.findFailedExams(index)
                .collectList()
                .subscribe(list -> runOnFx(() -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }
                    failedExams.setAll(list);
                    showMessage(failedExamsMessageLabel, list.isEmpty() ? "Nema nepoloženih ispita." : "");
                }), error -> runOnFx(() -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }
                    failedExams.clear();
                    showMessage(failedExamsMessageLabel, "Greška: " + error.getMessage());
                }));
    }

    private void loadEnrollments(String index) {
        showMessage(enrollmentsMessageLabel, "Učitavanje...");
        studentService.findEnrolledYears(index)
                .collectList()
                .subscribe(list -> runOnFx(() -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }
                    enrollments.setAll(list);
                    showMessage(enrollmentsMessageLabel, list.isEmpty() ? "Nema upisanih godina." : "");
                }), error -> runOnFx(() -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }
                    enrollments.clear();
                    showMessage(enrollmentsMessageLabel, "Greška: " + error.getMessage());
                }));
    }

    private void loadRepeatedYears(String index) {
        showMessage(repeatedYearsMessageLabel, "Učitavanje...");
        studentService.findRepeatedYears(index)
                .collectList()
                .subscribe(list -> runOnFx(() -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }
                    repeatedYears.setAll(list);
                    showMessage(repeatedYearsMessageLabel, list.isEmpty() ? "Nema obnovljenih godina." : "");
                }), error -> runOnFx(() -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }
                    repeatedYears.clear();
                    showMessage(repeatedYearsMessageLabel, "Greška: " + error.getMessage());
                }));
    }

    private void loadPayments(Long studentId) {
        if (studentId == null) {
            payments.clear();
            showMessage(paymentsMessageLabel, "ID studenta nije dostupan za prikaz uplata.");
            updateRemainingTuitionLabels(null);
            return;
        }
        showMessage(paymentsMessageLabel, "Učitavanje...");
        paymentService.findPaymentsForStudent(studentId)
                .collectList()
                .subscribe(list -> runOnFx(() -> {
                    if (!studentId.equals(activeStudentId)) {
                        return;
                    }
                    payments.setAll(list);
                    showMessage(paymentsMessageLabel, list.isEmpty() ? "Nema evidentiranih uplata." : "");
                }), error -> runOnFx(() -> {
                    if (!studentId.equals(activeStudentId)) {
                        return;
                    }
                    payments.clear();
                    showMessage(paymentsMessageLabel, "Greška: " + error.getMessage());
                }));
        paymentService.getRemainingTuition(studentId)
                .subscribe(dto -> runOnFx(() -> {
                    if (!studentId.equals(activeStudentId)) {
                        return;
                    }
                    updateRemainingTuitionLabels(dto);
                }), error -> runOnFx(() -> {
                    if (!studentId.equals(activeStudentId)) {
                        return;
                    }
                    updateRemainingTuitionLabels(null);
                    showMessage(paymentsMessageLabel, "Greška: " + error.getMessage());
                }));
    }

    private void updateBasicInfo(Selection selection) {
        StudentDto student = selection.getStudent();
        String indeks = selection.getIndex();
        updateStatusForIndex(indeks);
        indeksValue.setText(valueOrPlaceholder(indeks));
        imeValue.setText(valueOrPlaceholder(student.getIme()));
        prezimeValue.setText(valueOrPlaceholder(student.getPrezime()));
        jmbgValue.setText(valueOrPlaceholder(student.getJmbg()));
        datumRodjenjaValue.setText(valueOrPlaceholder(student.getDatumRodjenja()));
        mestoRodjenjaValue.setText(valueOrPlaceholder(student.getMestoRodjenja()));
        drzavaRodjenjaValue.setText(valueOrPlaceholder(student.getDrzavaRodjenja()));
        polValue.setText(student.getPol() != null ? student.getPol().name() : "--");
        drzavljanstvoValue.setText(valueOrPlaceholder(student.getDrzavljanstvo()));
        nacionalnostValue.setText(valueOrPlaceholder(student.getNacionalnost()));
        mestoPrebivalistaValue.setText(valueOrPlaceholder(student.getMestoPrebivalista()));
        adresaValue.setText(formatAddress(student.getUlicaPrebivalista(), student.getBrojPrebivalista()));
        brojTelefonaValue.setText(valueOrPlaceholder(student.getBrojTelefona()));
        fakultetskiEmailValue.setText(valueOrPlaceholder(student.getFakultetskiEmail()));
        privatniEmailValue.setText(valueOrPlaceholder(student.getPrivatniEmail()));
    }

    private void clearBasicInfo() {
        indeksValue.setText("--");
        imeValue.setText("--");
        prezimeValue.setText("--");
        jmbgValue.setText("--");
        datumRodjenjaValue.setText("--");
        mestoRodjenjaValue.setText("--");
        drzavaRodjenjaValue.setText("--");
        polValue.setText("--");
        drzavljanstvoValue.setText("--");
        nacionalnostValue.setText("--");
        mestoPrebivalistaValue.setText("--");
        adresaValue.setText("--");
        brojTelefonaValue.setText("--");
        fakultetskiEmailValue.setText("--");
        privatniEmailValue.setText("--");
    }

    private void clearDataViews() {
        passedExams.clear();
        failedExams.clear();
        payments.clear();
        enrollments.clear();
        repeatedYears.clear();
        showMessage(passedExamsMessageLabel, "");
        showMessage(failedExamsMessageLabel, "");
        showMessage(paymentsMessageLabel, "");
        showMessage(enrollmentsMessageLabel, "");
        showMessage(repeatedYearsMessageLabel, "");
        updateRemainingTuitionLabels(null);
    }

    private void loadIndexBoundData(String index) {
        if (!hasIndex(index)) {
            showMissingIndexMessages(null);
            return;
        }
        loadPassedExams(index);
        loadFailedExams(index);
        loadEnrollments(index);
        loadRepeatedYears(index);
    }

    private void requestIndexForStudent(StudentDto student) {
        if (student == null || student.getId() == null) {
            showMissingIndexMessages("Broj indeksa nije poznat. Otvorite profil unosom broja indeksa.");
            return;
        }
        showIndexLoadingMessage();
        studentService.findActiveIndexValue(student.getId())
                .subscribe(index -> runOnFx(() -> handleIndexResolved(student.getId(), index)),
                        error -> runOnFx(() -> handleIndexLookupError(student.getId(), error)));
    }

    private void handleIndexResolved(Long studentId, String index) {
        if (!studentId.equals(activeStudentId)) {
            return;
        }
        if (!hasIndex(index)) {
            showMissingIndexMessages("Aktivan indeks nije pronađen. Otvorite profil unosom broja indeksa.");
            return;
        }
        this.activeIndex = index;
        indeksValue.setText(valueOrPlaceholder(index));
        updateStatusForIndex(index);
        loadIndexBoundData(index);
    }

    private void handleIndexLookupError(Long studentId, Throwable error) {
        if (!studentId.equals(activeStudentId)) {
            return;
        }
        String message = error == null || error.getMessage() == null
                ? "Greška pri pronalaženju broja indeksa."
                : "Greška pri pronalaženju broja indeksa: " + error.getMessage();
        showMissingIndexMessages(message);
    }

    private void showIndexLoadingMessage() {
        String message = "Učitavanje broja indeksa...";
        showMessage(passedExamsMessageLabel, message);
        showMessage(failedExamsMessageLabel, message);
        showMessage(enrollmentsMessageLabel, message);
        showMessage(repeatedYearsMessageLabel, message);
        passedExams.clear();
        failedExams.clear();
        enrollments.clear();
        repeatedYears.clear();
    }

    private void showMissingIndexMessages(String customMessage) {
        String message = customMessage != null ? customMessage
                : "Broj indeksa nije poznat. Otvorite profil unosom broja indeksa.";
        showMessage(passedExamsMessageLabel, message);
        showMessage(failedExamsMessageLabel, message);
        showMessage(enrollmentsMessageLabel, message);
        showMessage(repeatedYearsMessageLabel, message);
        passedExams.clear();
        failedExams.clear();
        enrollments.clear();
        repeatedYears.clear();
    }

    private boolean hasIndex(String index) {
        return index != null && !index.isBlank();
    }

    private void updateStatusForIndex(String indeks) {
        if (statusLabel != null) {
            statusLabel.setText(hasIndex(indeks)
                    ? "Profil studenta za indeks " + indeks
                    : "Profil studenta");
        }
    }

    private void updateRemainingTuitionLabels(RemainingTuitionDto dto) {
        if (dto == null) {
            remainingEurLabel.setText("--");
            remainingRsdLabel.setText("--");
            return;
        }
        remainingEurLabel.setText(dto.getPreostaloEur() != null ? dto.getPreostaloEur().toPlainString() : "--");
        remainingRsdLabel.setText(dto.getPreostaloRsd() != null ? dto.getPreostaloRsd().toPlainString() : "--");
    }

    private void showMessage(Label label, String message) {
        if (label != null) {
            label.setText(message == null ? "" : message);
        }
    }

    private String valueOrPlaceholder(Object value) {
        if (value == null) {
            return "--";
        }
        String str = value.toString().trim();
        return str.isEmpty() ? "--" : str;
    }

    private String formatAddress(String ulica, String broj) {
        String ulicaValue = valueOrPlaceholder(ulica);
        String brojValue = valueOrPlaceholder(broj);
        if ("--".equals(ulicaValue) && "--".equals(brojValue)) {
            return "--";
        }
        if ("--".equals(ulicaValue)) {
            return brojValue;
        }
        if ("--".equals(brojValue)) {
            return ulicaValue;
        }
        return ulicaValue + " " + brojValue;
    }

    private void runOnFx(Runnable action) {
        if (action == null) {
            return;
        }
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
