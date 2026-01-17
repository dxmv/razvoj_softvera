package org.raflab.studsluzbadesktopclient.utils;

import javafx.scene.control.Alert;

public class StringUtils {

	public static boolean allContains(String filter, Object ...objects ) {
		for(Object obj: objects) {
			if(obj!=null && !obj.toString().contains(filter))
				return false;
		}
		return true;
	}
	
	public static boolean atLeastOneContains(String filter, Object ...objects ) {
		for(Object obj: objects) {
			if(obj!=null && obj.toString().toLowerCase().contains(filter.toLowerCase()))
				return true;
		}
		return false;
	}
	public void prikaziPoruku(String title, String content, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.show();
	}
}
