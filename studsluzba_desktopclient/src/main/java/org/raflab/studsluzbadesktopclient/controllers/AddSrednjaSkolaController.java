package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.raflab.studsluzbadesktopclient.coder.CoderFactory;
import org.raflab.studsluzbadesktopclient.coder.CoderType;
import org.raflab.studsluzbadesktopclient.coder.SimpleCode;
import org.raflab.studsluzba.model.VrstaSkole;
import org.raflab.studsluzba.model.dto.SrednjaSkolaDto;
import org.raflab.studsluzbadesktopclient.services.SkoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AddSrednjaSkolaController {
	
	@Autowired
	SkoleService skoleService;
	
	@Autowired StudentController  studentController;
	
	@FXML TextField nazivNoveSrednjeSkoleTf;
	@FXML ComboBox<SimpleCode> mestoNoveSrednjeSkoleCb;
	@FXML ComboBox<SimpleCode> tipNoveSrednjeSkoleCb;
	@FXML Label labelErrorModal;
	
	@Autowired
	CoderFactory coderFactory;
	
	
	@FXML public void addSrednjaSkola(ActionEvent event) {
		SrednjaSkolaDto ss = new SrednjaSkolaDto();

		if(mestoNoveSrednjeSkoleCb.getValue()!=null) ss.setMesto(mestoNoveSrednjeSkoleCb.getValue().toString());
		ss.setNaziv(nazivNoveSrednjeSkoleTf.getText());
		ss.setVrsta(resolveVrsta(tipNoveSrednjeSkoleCb.getValue()));

		try{
			skoleService.saveSrednjaSkola(ss);
			studentController.updateSrednjeSkole();
			closeStage(event);
		}catch (Exception e){
			labelErrorModal.setText(e.getMessage());
		}
	}
	
	@FXML
    	public void initialize() {		
		tipNoveSrednjeSkoleCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.TIP_SREDNJE_SKOLE).getCodes()));
		mestoNoveSrednjeSkoleCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.MESTO).getCodes()));
	}

	private VrstaSkole resolveVrsta(SimpleCode code) {
		if (code == null || code.getCode() == null) {
			return null;
		}
		String normalized = code.getCode().trim().toUpperCase();
		if (normalized.contains("GIM")) {
			return VrstaSkole.GIMNAZIJA;
		}
		if (normalized.contains("STRU")) {
			return VrstaSkole.STRUCNA;
		}
		if (normalized.contains("UMET")) {
			return VrstaSkole.UMETNICKA;
		}
		return VrstaSkole.OSTALO;
	}
	
	private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
