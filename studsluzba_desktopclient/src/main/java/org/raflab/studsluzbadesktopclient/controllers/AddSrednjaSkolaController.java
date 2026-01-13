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
import org.raflab.studsluzbadesktopclient.dtos.SrednjaSkolaDTO;
import org.raflab.studsluzbadesktopclient.services.SifarniciService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AddSrednjaSkolaController {
	
	@Autowired
	SifarniciService sifarniciService;
	
	@Autowired StudentController  studentController;
	
	@FXML TextField nazivNoveSrednjeSkoleTf;
	@FXML ComboBox<SimpleCode> mestoNoveSrednjeSkoleCb;
	@FXML ComboBox<SimpleCode> tipNoveSrednjeSkoleCb;
	@FXML Label labelErrorModal;
	
	@Autowired
	CoderFactory coderFactory;
	
	
	@FXML public void addSrednjaSkola(ActionEvent event) {
		SrednjaSkolaDTO ss = new SrednjaSkolaDTO();

		if(mestoNoveSrednjeSkoleCb.getValue()!=null) ss.setMesto(mestoNoveSrednjeSkoleCb.getValue().toString());
		ss.setNaziv(nazivNoveSrednjeSkoleTf.getText());
		if(tipNoveSrednjeSkoleCb.getValue()!=null) ss.setVrstaSkole(tipNoveSrednjeSkoleCb.getValue().toString());

		try{
			sifarniciService.saveSrednjaSkola(ss);
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
	
	private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
