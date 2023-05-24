package gui;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import db.Datenbank;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//TODO RUNDE angeben
public class WC_AulosungPage extends WC_Main_Page  {
	private HashMap<String,String> sonderregelList;
	
	private Label lbAnzahl = new Label("Wie viele Sonderregeln?");
	private ComboBox<Long> cbAnzahl = new ComboBox<>();
	private ObservableList<Long> ol1 = FXCollections.observableArrayList();
	private Button btnErzeugen = new Button("Erzeugen");
	private Label lbRunde = new Label("Runde");
	private int letzeRunde;
	
	
	private ObservableList<String> olSucht = FXCollections.observableArrayList();
	private ObservableList<String> olFuer = FXCollections.observableArrayList();
	
	
	
   public WC_AulosungPage(ArrayList<Person> plAuswahl) {
	   this.plAuswahl=plAuswahl;
	   for (Long i = 0L; i <=plAuswahl.size();i++)
		   ol1.add(i);
	   this.cbAnzahl.setItems(ol1);
	   this.cbAnzahl.getSelectionModel().selectFirst();
	   this.plAuswahl.forEach(p -> olSucht.add(p.getName()));
	   this.olFuer=olSucht;
	   this.sonderregelList= new HashMap<>();
	   try {
		this.letzeRunde= Datenbank.leseLetzteRunde();
	} catch (SQLException e) {
		e.printStackTrace();
	}
   }

public VBox getSecondWindow(Stage primaryStage,Scene scene1) {
	
	TextField txtRunde = new TextField();
	txtRunde.setText(String.valueOf(this.letzeRunde+1));
	txtRunde.setEditable(true);
	
	HBox hb =new HBox(10,lbAnzahl,cbAnzahl,lbRunde,txtRunde,btnErzeugen);
	hb.setPadding(new Insets(3));
	
	
	VBox finalVB = new VBox(hb);
	
	txtRunde.textProperty().addListener((observable, oldValue, newValue) -> {
		btnErzeugen.setDisable(newValue.isEmpty());
     });
	
	btnErzeugen.setOnAction(a -> {
		HBox hb2 = new HBox();
		long anzahl = cbAnzahl.getSelectionModel().getSelectedItem();
		for (int x = 0; x<anzahl;x++) {
			
			ComboBox<String> cbSucht = new ComboBox<>();
			cbSucht.setItems(olSucht);
			Label lbSuchtFuer = new Label("sucht aus für ");
			ComboBox<String> cbFuer = new ComboBox<>();
			cbFuer.setItems(olFuer);
			Button btnOK = new Button("OK");
			
			hb2 = new HBox(5, cbSucht, lbSuchtFuer, cbFuer,btnOK);
			
			
			btnOK.setOnAction(a2 -> {
				sonderregelList.put(cbSucht.getSelectionModel().getSelectedItem(), cbFuer.getSelectionModel().getSelectedItem());
//				olSucht.remove(cbSucht.getSelectionModel().getSelectedItem());  Funkt nicht!
//				olFuer.remove(cbFuer.getSelectionModel().getSelectedItem());
				});
			
			finalVB.getChildren().add(hb2);
			
		}
		
		Button btnOK = new Button("Runde Starten");
		btnOK.addEventFilter(ActionEvent.ACTION, e -> {
			new WC_ErgebnisDialog(super.plAuswahl,sonderregelList,Integer.valueOf(txtRunde.getText())).showAndWait();
			e.consume();
		});
		Button btnBack= new Button("Zurück");
		  btnBack.setOnAction(a2 -> {
			  primaryStage.setScene(scene1);
			  primaryStage.show();
		  });
		finalVB.getChildren().add(new HBox(10,btnOK,btnBack));
//		btnErzeugen.setDisable(true);
		txtRunde.setEditable(false);
	});
	
		
 return finalVB;
}
	
	
}
