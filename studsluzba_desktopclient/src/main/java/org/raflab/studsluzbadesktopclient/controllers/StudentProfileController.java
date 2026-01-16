package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import net.sf.jasperreports.engine.JRException;
import org.raflab.studsluzba.model.dto.ObnovaGodineDto;
import org.raflab.studsluzba.model.dto.PolozenPredmetDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.RemainingTuitionDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzba.model.dto.UpisGodineDto;
import org.raflab.studsluzba.model.dto.UplataDto;
import org.raflab.studsluzbadesktopclient.model.reports.EnrollmentCertificateData;
import org.raflab.studsluzbadesktopclient.model.reports.ExamByYearGroup;
import org.raflab.studsluzbadesktopclient.model.reports.ExamDetails;
import org.raflab.studsluzbadesktopclient.model.reports.PassedExamCertificateData;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.CertificateService;
import org.raflab.studsluzbadesktopclient.services.PaymentService;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.state.SelectedStudentStore;
import org.raflab.studsluzbadesktopclient.state.SelectedStudentStore.Selection;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class StudentProfileController {

    private final SelectedStudentStore selectedStudentStore;
    private final StudentService studentService;
    private final PaymentService paymentService;
    private final PredmetService predmetService;
    private final CertificateService certificateService;
    private final MainView mainView;

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

    // Statistics tab
    @FXML
    private Label statsMessageLabel;
    @FXML
    private Label totalEspbLabel;
    @FXML
    private Label prosecnaOcenaLabel;
    @FXML
    private Label passedCountLabel;

    public StudentProfileController(SelectedStudentStore selectedStudentStore,
                                    StudentService studentService,
                                    PaymentService paymentService,
                                    PredmetService predmetService,
                                    CertificateService certificateService,
                                    MainView mainView) {
        this.selectedStudentStore = selectedStudentStore;
        this.studentService = studentService;
        this.paymentService = paymentService;
        this.predmetService = predmetService;
        this.certificateService = certificateService;
        this.mainView = mainView;
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
        showMessage(statsMessageLabel, "");
        updateRemainingTuitionLabels(null);
        clearStatisticsLabels();
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
        loadStatistics(index);
    }

    private void loadStatistics(String index) {
        showMessage(statsMessageLabel, "Učitavanje statistike...");
        clearStatisticsLabels();

        studentService.findPassedExams(index)
                .collectList()
                .subscribe(passedExamsList -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }

                    if (passedExamsList.isEmpty()) {
                        runOnFx(() -> {
                            showMessage(statsMessageLabel, "Nema položenih ispita za izračunavanje statistike.");
                            updateStatisticsLabels(0, 0.0, 0);
                        });
                        return;
                    }

                    // Calculate GPA from grades
                    double gpa = passedExamsList.stream()
                            .map(PolozenPredmetDto::getOcena)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(0.0);

                    int passedCount = passedExamsList.size();

                    // Collect predmet IDs to fetch ESPB
                    List<Long> predmetIds = passedExamsList.stream()
                            .map(PolozenPredmetDto::getPredmetId)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());

                    if (predmetIds.isEmpty()) {
                        runOnFx(() -> {
                            showMessage(statsMessageLabel, "");
                            updateStatisticsLabels(0, gpa, passedCount);
                        });
                        return;
                    }

                    // Fetch predmet details to get ESPB
                    predmetService.findPredmetsByIds(predmetIds)
                            .collectList()
                            .subscribe(predmeti -> {
                                if (!index.equals(activeIndex)) {
                                    return;
                                }

                                Map<Long, Integer> predmetEspbMap = predmeti.stream()
                                        .filter(p -> p.getEspbBodovi() != null)
                                        .collect(Collectors.toMap(PredmetDto::getId, PredmetDto::getEspbBodovi));

                                // Sum ESPB for all passed exams
                                int totalEspb = passedExamsList.stream()
                                        .map(PolozenPredmetDto::getPredmetId)
                                        .filter(Objects::nonNull)
                                        .map(predmetEspbMap::get)
                                        .filter(Objects::nonNull)
                                        .mapToInt(Integer::intValue)
                                        .sum();

                                runOnFx(() -> {
                                    showMessage(statsMessageLabel, "");
                                    updateStatisticsLabels(totalEspb, gpa, passedCount);
                                });
                            }, error -> runOnFx(() -> {
                                if (!index.equals(activeIndex)) {
                                    return;
                                }
                                showMessage(statsMessageLabel, "Greška pri učitavanju ESPB podataka: " + error.getMessage());
                                // Still show GPA and count even if ESPB fetch failed
                                updateStatisticsLabels(0, gpa, passedCount);
                            }));
                }, error -> runOnFx(() -> {
                    if (!index.equals(activeIndex)) {
                        return;
                    }
                    showMessage(statsMessageLabel, "Greška pri učitavanju statistike: " + error.getMessage());
                    clearStatisticsLabels();
                }));
    }

    private void updateStatisticsLabels(int totalEspb, double gpa, int passedCount) {
        if (totalEspbLabel != null) {
            totalEspbLabel.setText(String.valueOf(totalEspb));
        }
        if (prosecnaOcenaLabel != null) {
            prosecnaOcenaLabel.setText(gpa > 0 ? String.format("%.2f", gpa) : "--");
        }
        if (passedCountLabel != null) {
            passedCountLabel.setText(String.valueOf(passedCount));
        }
    }

    private void clearStatisticsLabels() {
        if (totalEspbLabel != null) {
            totalEspbLabel.setText("--");
        }
        if (prosecnaOcenaLabel != null) {
            prosecnaOcenaLabel.setText("--");
        }
        if (passedCountLabel != null) {
            passedCountLabel.setText("--");
        }
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
        showMessage(statsMessageLabel, message);
        passedExams.clear();
        failedExams.clear();
        enrollments.clear();
        repeatedYears.clear();
        clearStatisticsLabels();
    }

    private void showMissingIndexMessages(String customMessage) {
        String message = customMessage != null ? customMessage
                : "Broj indeksa nije poznat. Otvorite profil unosom broja indeksa.";
        showMessage(passedExamsMessageLabel, message);
        showMessage(failedExamsMessageLabel, message);
        showMessage(enrollmentsMessageLabel, message);
        showMessage(repeatedYearsMessageLabel, message);
        showMessage(statsMessageLabel, message);
        passedExams.clear();
        failedExams.clear();
        enrollments.clear();
        repeatedYears.clear();
        clearStatisticsLabels();
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

    @FXML
    public void handleGenerateEnrollmentCertificate() {
        if (activeIndex == null || activeStudentId == null) {
            showMessage(statusLabel, "Nema izabranog studenta. Molimo izaberite studenta sa aktivnim indeksom.");
            return;
        }

        Selection selection = selectedStudentStore.getSelection();
        if (selection == null || selection.getStudent() == null) {
            showMessage(statusLabel, "Greška: Student nije dostupan.");
            return;
        }

        StudentDto student = selection.getStudent();
        showMessage(statusLabel, "Generisanje uverenja o studiranju...");

        // Fetch enrollment and renewal data
        studentService.findEnrolledYears(activeIndex)
                .collectList()
                .zipWith(studentService.findRepeatedYears(activeIndex).collectList())
                .subscribe(tuple -> {
                    List<UpisGodineDto> enrollments = tuple.getT1();
                    List<ObnovaGodineDto> renewals = tuple.getT2();

                    EnrollmentCertificateData data = EnrollmentCertificateData.builder()
                            .ime(student.getIme())
                            .prezime(student.getPrezime())
                            .srednjeIme(student.getSrednjeIme())
                            .indeks(activeIndex)
                            .jmbg(student.getJmbg())
                            .datumRodjenja(student.getDatumRodjenja())
                            .mestoRodjenja(student.getMestoRodjenja())
                            .drzavaRodjenja(student.getDrzavaRodjenja())
                            .studijskiProgram("N/A") // Could be enriched from backend
                            .fakultet("Računarski fakultet")
                            .upisaneGodine(enrollments)
                            .obnovljeneGodine(renewals)
                            .trenutniStatus("Aktivan")
                            .build();

                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String filename = "uverenje_studiranje_" + activeIndex.replace("/", "_") + "_" + timestamp + ".pdf";

                    try {
                        certificateService.generateEnrollmentCertificate(data, filename);
                        runOnFx(() -> showMessage(statusLabel, "Uverenje o studiranju uspešno generisano: " + filename));
                    } catch (JRException e) {
                        runOnFx(() -> showMessage(statusLabel, "Greška pri generisanju uverenja: " + e.getMessage()));
                        e.printStackTrace();
                    }
                }, error -> runOnFx(() -> showMessage(statusLabel, "Greška pri preuzimanju podataka: " + error.getMessage())));
    }

    @FXML
    public void handleGeneratePassedExamsCertificate() {
        if (activeIndex == null || activeStudentId == null) {
            showMessage(statusLabel, "Nema izabranog studenta. Molimo izaberite studenta sa aktivnim indeksom.");
            return;
        }

        Selection selection = selectedStudentStore.getSelection();
        if (selection == null || selection.getStudent() == null) {
            showMessage(statusLabel, "Greška: Student nije dostupan.");
            return;
        }

        StudentDto student = selection.getStudent();
        showMessage(statusLabel, "Generisanje uverenja o položenim ispitima...");

        // Fetch passed exams
        studentService.findPassedExams(activeIndex)
                .collectList()
                .subscribe(passedExams -> {
                    if (passedExams.isEmpty()) {
                        runOnFx(() -> showMessage(statusLabel, "Student nema evidentirane položene ispite."));
                        return;
                    }

                    // Collect unique predmet IDs
                    List<Long> predmetIds = passedExams.stream()
                            .map(PolozenPredmetDto::getPredmetId)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());

                    // Fetch predmet details
                    predmetService.findPredmetsByIds(predmetIds)
                            .collectList()
                            .subscribe(predmeti -> {
                                Map<Long, PredmetDto> predmetMap = predmeti.stream()
                                        .collect(Collectors.toMap(PredmetDto::getId, p -> p));

                                // Group exams by study year based on semester
                                Map<Integer, List<ExamDetails>> examsByYear = new TreeMap<>();

                                for (PolozenPredmetDto passed : passedExams) {
                                    PredmetDto predmet = predmetMap.get(passed.getPredmetId());
                                    if (predmet == null) continue;

                                    Integer semestar = predmet.getSemestar();
                                    Integer year = semestar != null ? (semestar + 1) / 2 : 0; // 1-2 -> 1, 3-4 -> 2, etc.

                                    ExamDetails examDetail = ExamDetails.builder()
                                            .sifraPredmeta(predmet.getSifra())
                                            .nazivPredmeta(predmet.getNaziv())
                                            .ocena(passed.getOcena())
                                            .espbBodovi(predmet.getEspbBodovi())
                                            .datumPolaganja("") // Could be enriched if available
                                            .build();

                                    examsByYear.computeIfAbsent(year, k -> new ArrayList<>()).add(examDetail);
                                }

                                // Convert to ExamByYearGroup list
                                List<ExamByYearGroup> examGroups = examsByYear.entrySet().stream()
                                        .map(entry -> ExamByYearGroup.builder()
                                                .godinaStudija(entry.getKey())
                                                .ispiti(entry.getValue())
                                                .build())
                                        .collect(Collectors.toList());

                                PassedExamCertificateData data = PassedExamCertificateData.builder()
                                        .ime(student.getIme())
                                        .prezime(student.getPrezime())
                                        .srednjeIme(student.getSrednjeIme())
                                        .indeks(activeIndex)
                                        .examsByYear(examGroups)
                                        .build();

                                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                                String filename = "uverenje_polozeni_" + activeIndex.replace("/", "_") + "_" + timestamp + ".pdf";

                                try {
                                    certificateService.generatePassedExamsCertificate(data, filename);
                                    runOnFx(() -> showMessage(statusLabel, "Uverenje o položenim ispitima uspešno generisano: " + filename));
                                } catch (JRException e) {
                                    runOnFx(() -> showMessage(statusLabel, "Greška pri generisanju uverenja: " + e.getMessage()));
                                    e.printStackTrace();
                                }
                            }, error -> runOnFx(() -> showMessage(statusLabel, "Greška pri preuzimanju predmeta: " + error.getMessage())));
                }, error -> runOnFx(() -> showMessage(statusLabel, "Greška pri preuzimanju položenih ispita: " + error.getMessage())));
    }

    @FXML
    public void handleOpenEnrollmentModal() {
        if (!hasIndex(activeIndex)) {
            showMessage(statusLabel, "Nema aktivnog indeksa. Otvorite profil unosom broja indeksa.");
            return;
        }

        mainView.<EnrollYearModalController>openModalWithConfigurator(
                "enrollYearModal",
                "Upis godine",
                420, 380,
                controller -> {
                    controller.setIndex(activeIndex);
                    controller.setOnSuccessCallback(result -> {
                        // Refresh enrollments data
                        loadEnrollments(activeIndex);
                        loadFailedExams(activeIndex);
                    });
                }
        );
    }

    @FXML
    public void handleOpenRenewalModal() {
        if (!hasIndex(activeIndex)) {
            showMessage(statusLabel, "Nema aktivnog indeksa. Otvorite profil unosom broja indeksa.");
            return;
        }

        mainView.<RepeatYearModalController>openModalWithConfigurator(
                "repeatYearModal",
                "Obnova godine",
                520, 550,
                controller -> {
                    controller.setIndex(activeIndex);
                    controller.setOnSuccessCallback(result -> {
                        // Refresh data after renewal
                        loadRepeatedYears(activeIndex);
                        loadFailedExams(activeIndex);
                    });
                }
        );
    }

    @FXML
    public void handleOpenPaymentModal() {
        if (activeStudentId == null) {
            showMessage(statusLabel, "Nema izabranog studenta za unos uplate.");
            return;
        }

        Selection selection = selectedStudentStore.getSelection();
        String studentName = selection != null && selection.getStudent() != null
                ? selection.getStudent().getIme() + " " + selection.getStudent().getPrezime()
                : "ID: " + activeStudentId;

        mainView.<PaymentModalController>openModalWithConfigurator(
                "paymentModal",
                "Unos uplate",
                420, 350,
                controller -> {
                    controller.setStudentInfo(activeStudentId, studentName);
                    controller.setOnSuccessCallback(result -> {
                        // Refresh payments data
                        loadPayments(activeStudentId);
                    });
                }
        );
    }
}
