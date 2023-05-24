package gui;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import db.Datenbank;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import mix.Losung;


public class WC_ErgebnisDialog extends Dialog<ButtonType>  {
		
	
   public WC_ErgebnisDialog(ArrayList<Person> plAuswahl, HashMap<String,String> sonderregeln,int runde) {
	   VBox vb = new VBox(10);
	   
	   
	    
	   Losung lo = new Losung(plAuswahl, sonderregeln,runde);
	   lo.mix();
	   lo.logFinalEntries();
	   lo.getFinalerMix().forEach((k,v) -> {
		   TextField sucht = new TextField(k);
		   sucht.setEditable(false);
		   Label lbl = new Label(" sucht aus f�r ");
		   TextField fuer = new TextField(v);
		   fuer.setEditable(false);
		   HBox hb = new HBox(10,sucht,lbl,fuer);
		   hb.setPadding(new Insets(2));
		   vb.getChildren().add(hb);
			   
	   });
	   
	   
	   
	   
	   
	   
	   
	   ButtonType speichern = new ButtonType("Speichern", ButtonData.OK_DONE);
		ButtonType zurueck = new ButtonType("Zur�ck", ButtonData.BACK_PREVIOUS);
		
		this.getDialogPane().setContent(vb);
		this.setTitle("Ergebnisse");
		this.getDialogPane().getButtonTypes().addAll(speichern, zurueck);


		
	   
	   Button btnSpeichern = (Button) this.getDialogPane().lookupButton(speichern);
		Stage s = (Stage) this.getDialogPane().getScene().getWindow();
//		s.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
		this.getDialogPane().setStyle("-fx-background-color: ghostwhite");

		btnSpeichern.setOnAction(a -> {
			try {
				Datenbank.insertErgebnisListe(lo.getFinalerMix(), runde);
				System.out.println("Runde "+runde+" gespeichert");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		
		/*
		 *  EventFilter //was unterschied zu set on action
		 */
//		btnSpeichern.addEventFilter(ActionEvent.ACTION, e -> {
//
//		});
		
		/*
		 *  ResultConverter
		 */
		this.setResultConverter(new Callback<ButtonType, ButtonType>() {

			@Override
			public ButtonType call(ButtonType arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}
//			@Override
//			public ButtonType call(ButtonType arg0) {
//				if (arg0 == speichern) {
//					Benutzer benutzer = null; // brauch ich f�r PBKFD Methode (try/catch)
//					try {
//						benutzer = new Benutzer(1, txtVorname.getText(), txtNachname.getText(), txtAdresse.getText(),
//								txtEmail.getText(), txtTelNummer.getText(),
//								PBKDF.generateStrongPasswordHash(txtPasswort.getText()));
//					} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
//						e1.printStackTrace();
//					}
//					ServiceFunctionsReturn sfrp = ServiceFunctions.post("benutzer", benutzer.toXML());
//					if (!sfrp.isRc()) {
//						new Alert(AlertType.ERROR, new Meldung(sfrp.getLine()).toString()).showAndWait(); 
//					}
//				}
//				return arg0;
//
//   }

   });
	
}
}
