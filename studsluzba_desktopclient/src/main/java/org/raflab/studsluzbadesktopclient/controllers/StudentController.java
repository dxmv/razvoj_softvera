package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.coder.CoderFactory;
import org.raflab.studsluzbadesktopclient.coder.CoderType;
import org.raflab.studsluzbadesktopclient.coder.SimpleCode;
import org.raflab.studsluzba.model.Pol;
import org.raflab.studsluzba.model.dto.SrednjaSkolaDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzbadesktopclient.services.SkoleService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentController {

    private final StudentService studentService;
    private final CoderFactory coderFactory;
    private final MainView mainView;
    private final SkoleService skoleService;
    @FXML
    private TextField imeTf;
    @FXML
    private TextField prezimeTf;
    @FXML
    private TextField srednjeImeTf;
    @FXML
    private RadioButton muski;
    @FXML
    private RadioButton zenski;
    @FXML
    private TextField jmbgTf;
    @FXML
    private DatePicker datumRodjenjaDp;
    @FXML
    private DatePicker datumAktivacijeDp;
    @FXML
    ComboBox<SimpleCode> mestoRodjenjaCb;
    @FXML
    private TextField emailPrivatniTf;
    @FXML
    private TextField emailFakultetTf;
    @FXML
    TextField brojTelefonaTf;
    @FXML
    TextField adresaTf;
    @FXML
    ComboBox<SimpleCode> mestoStanovanjaCb;
    @FXML
    ComboBox<SimpleCode> drzavaRodjenjaCb;
    @FXML
    ComboBox<SimpleCode> drzavljanstvoCb;
    @FXML
    TextField nacionalnostTf;
    @FXML
    TextField brojLicneKarteTf;
    @FXML
    TextField godinaUpisaTf;
    @FXML
    TextField brojIndeksaTf;
    @FXML
    TextField godinaIndeksaTf;
    @FXML
    TextField uspehSrednjaSkolaTf;
    @FXML
    TextField uspehPrijemniTf;

    @FXML
    ComboBox<SrednjaSkolaDto> srednjaSkolaCb;

    @FXML Label labelError;

//    @FXML
//    ComboBox<StudProgram> studProgramCb;

//    @FXML
//    ComboBox<VisokoskolskaUstanova> visokoskolskaUstanovaCb;

    public StudentController(StudentService studentService, CoderFactory coderFactory, MainView mainView, SkoleService skoleService) {
        this.studentService = studentService;
        this.coderFactory = coderFactory;
        this.mainView = mainView;
        this.skoleService = skoleService;
    }

    @FXML
    public void initialize(){
        drzavaRodjenjaCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.DRZAVA).getCodes()));
        drzavaRodjenjaCb.setValue(new SimpleCode("Serbia"));

        drzavljanstvoCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.DRZAVA).getCodes()));
        drzavljanstvoCb.setValue(new SimpleCode("Serbia"));

        mestoRodjenjaCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.MESTO).getCodes()));
        mestoRodjenjaCb.setValue(new SimpleCode("Beograd"));

        mestoStanovanjaCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.MESTO).getCodes()));
        mestoStanovanjaCb.setValue(new SimpleCode("Beograd"));
        try {
            List<SrednjaSkolaDto> srednjeSkole = skoleService.getSrednjeSkole();
            srednjaSkolaCb.setItems(FXCollections.observableArrayList(srednjeSkole));
        }catch (Exception e){
            labelError.setText(e.getMessage());
        }
    }

    public void handleOpenModalSrednjeSkole(ActionEvent ae) {
        mainView.openModal("addSrednjaSkola");
    }

    public void updateSrednjeSkole() {
        try{
            List<SrednjaSkolaDto> srednjeSkole = skoleService.getSrednjeSkole();
            srednjaSkolaCb.setItems(FXCollections.observableArrayList(srednjeSkole));
        }catch (Exception e){
            labelError.setText(e.getMessage());
        }
    }

    public void handleOpenModalVisokoskolskeUstanove(ActionEvent ae) {
        //mainViewManager.openModal("addVisaUstanovaForStudent");
    }

    public void handleSaveStudent(ActionEvent event) {
        StudentDto studentDTO = new StudentDto();

        studentDTO.setIme(imeTf.getText());
        studentDTO.setPrezime(prezimeTf.getText());
        studentDTO.setSrednjeIme(srednjeImeTf.getText());
        studentDTO.setPol(muski.isSelected() ? Pol.MUSKI : Pol.ZENSKI);
        studentDTO.setJmbg(jmbgTf.getText());
        if (datumRodjenjaDp.getValue() != null) {
            studentDTO.setDatumRodjenja(datumRodjenjaDp.getValue());
        }
        if (mestoRodjenjaCb.getValue() != null) {
            studentDTO.setMestoRodjenja(mestoRodjenjaCb.getValue().getCode());
        }
        studentDTO.setFakultetskiEmail(emailFakultetTf.getText());
        studentDTO.setPrivatniEmail(emailPrivatniTf.getText());
        studentDTO.setBrojTelefona(brojTelefonaTf.getText());
        if (mestoStanovanjaCb.getValue() != null) {
            studentDTO.setMestoPrebivalista(mestoStanovanjaCb.getValue().getCode());
        }
        studentDTO.setUlicaPrebivalista(adresaTf.getText());
        studentDTO.setDrzavaRodjenja(drzavaRodjenjaCb.getValue() != null ? drzavaRodjenjaCb.getValue().getCode() : null);
        studentDTO.setDrzavljanstvo(drzavljanstvoCb.getValue() != null ? drzavljanstvoCb.getValue().getCode() : null);
        studentDTO.setNacionalnost(nacionalnostTf.getText());
        studentDTO.setBrojLicneKarte(brojLicneKarteTf.getText());
        SrednjaSkolaDto selectedSkola = srednjaSkolaCb.getValue();
        studentDTO.setZavrsenaSkolaId(selectedSkola != null ? selectedSkola.getId() : null);
        studentDTO.setUspehSrednjaSkola(parseDoubleOrNull(uspehSrednjaSkolaTf.getText()));
        studentDTO.setUspehPrijemni(parseDoubleOrNull(uspehPrijemniTf.getText()));

        studentService.saveStudent(studentDTO);
        resetForm();
    }

    private Double parseDoubleOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            labelError.setText("Neispravan broj: " + value);
            return null;
        }
    }




    private void resetForm() {
        imeTf.clear();
        prezimeTf.clear();
        srednjeImeTf.clear();

        muski.setSelected(false);
        zenski.setSelected(false);

        jmbgTf.clear();
        datumRodjenjaDp.setValue(null);
        datumAktivacijeDp.setValue(null);

        mestoRodjenjaCb.setValue(null);
        emailPrivatniTf.clear();
        emailFakultetTf.clear();
        brojTelefonaTf.clear();
        adresaTf.clear();

        mestoStanovanjaCb.setValue(null);
        drzavaRodjenjaCb.setValue(null);
        drzavljanstvoCb.setValue(null);

        nacionalnostTf.clear();
        brojLicneKarteTf.clear();
        godinaUpisaTf.clear();
        brojIndeksaTf.clear();
        godinaIndeksaTf.clear();
        uspehSrednjaSkolaTf.clear();
        uspehPrijemniTf.clear();
    }
}
