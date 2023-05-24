package gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.Datenbank;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


//TODO name exsitiert bereits alert
//TODO liste aller paarungen in anderem modal
public class WC_Main_Page extends Application {
	
	private static final Logger log = LogManager.getLogger(WC_Main_Page.class);

	
	public static void main(String[] args) {
		launch(args);
	}
	
	private final static boolean DELETEMODE = false;
	private final static boolean TESTMODE = false;

	static {
		try {
			Datenbank.setDBLocation(TESTMODE);
			Datenbank.createTablePersons();
			Datenbank.createTableErgebnisse();
			if (DELETEMODE) {
//				Datenbank.deleteWholeTableEntriesPersons();
//				Datenbank.deleteWholeTableEntriesLosung();
				log.warn("Alte Einträge gelöscht");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private final double xDimension = 500d;
	private final double yDimension = 350d;
	
	protected ArrayList<Person> plAuswahl= new ArrayList<>();
	
	private ComboBox<String> cbPersonen = new ComboBox<>();
	private ObservableList<String> ol = FXCollections.observableArrayList();
	private Button btnNeueRunde = new Button("Neue Runde");
	private Button btnDabei = new Button("ist dabei!");
	private Button btnBereit = new Button("Bereit!!");
	private Button btnNameEntfernen = new Button("Name entfernen");
	private Label lbLetzteRunde = new Label("Ergebnisse letzer Runde");
	private ListView<String> ltLetzteRunde = new ListView<>();
    private ObservableList<String> olList = FXCollections.observableArrayList();
    private List<Ergebnis> letzeRundeList = new ArrayList<>();
    private Button btnUebernehmen = new Button("Übernehme Alle");
    private int letzteRunde;
            

	@Override
	public void start(Stage primaryStage) throws Exception {

		cbPersonen.setItems(loadObservableList());
		cbPersonen.setEditable(true);
		cbPersonen.setPrefWidth(250);
		btnDabei.setOnAction(a -> {
			plAuswahl.add(new Person( cbPersonen.getSelectionModel().getSelectedItem(), null));
			ol.remove(cbPersonen.getSelectionModel().getSelectedItem());
			cbPersonen.getSelectionModel().clearSelection();
			cbPersonen.getEditor().clear();
			if (plAuswahl.size()>=3)
				btnBereit.setDisable(false);
		});
	
		btnNameEntfernen.setOnAction(a->{
			try {
			String name = cbPersonen.getSelectionModel().getSelectedItem();
			plAuswahl.remove(new Person(name, null));
			Datenbank.deleteEntryByNamePerson(name);
			cbPersonen.getEditor().clear();
			} catch (Exception e) {
			 e.printStackTrace();
			}
		});
		btnNeueRunde.setOnAction(a -> {
			plAuswahl= new ArrayList<>();
			loadObservableList();
			btnBereit.setDisable(true);
			
		});
		btnBereit.setDisable(true);
			
		HBox hb1 = new HBox(10,btnNeueRunde,btnDabei,btnBereit);
		hb1.setPadding(new Insets(2));
		btnNameEntfernen.setAlignment(Pos.TOP_LEFT);
		
		ltLetzteRunde.setItems(loadObservableListList());
		ltLetzteRunde.setPrefHeight(360); //160
		btnUebernehmen.setOnAction(a -> {
			plAuswahl.clear();
			letzeRundeList.forEach(a2-> {
				plAuswahl.add(new Person(a2.getSucht(),null));
				ol.remove(a2.getSucht());
				});
			btnBereit.setDisable(false);
		});
		
		
		
		VBox vb1 = new VBox(10, cbPersonen, hb1,btnNameEntfernen, lbLetzteRunde,ltLetzteRunde,btnUebernehmen);
		vb1.setPadding(new Insets(2));
		vb1.setMinSize(yDimension, yDimension);
		
		
		
		Scene scene1 = new Scene(vb1);
		btnBereit.setOnAction(a -> {
//			if (plAuswahl.size()>=3)
//				btnBereit.setDisable(false);
			if (plAuswahl.isEmpty())
				log.info("Keine Auswahl Getroffen");
			else {
			insertPersonsInDB();
			VBox vb2 = new WC_AulosungPage(plAuswahl).getSecondWindow(primaryStage,scene1);
			vb2.setMinSize(xDimension, yDimension);
			primaryStage.setScene(new Scene(vb2));		
			}
		});
		setScene(primaryStage, scene1);
	}
	

	
	public void setScene(Stage primaryStage,Scene scene) {
		primaryStage.setScene(scene);
		primaryStage.setTitle("WC-Challenge");
		primaryStage.show();
	}
	
	private void insertPersonsInDB() {
		ArrayList<Person> insertList = new ArrayList<>();
		plAuswahl.forEach(p -> {
			try {
				String name = p.getName();
				if (Datenbank.lesePersonID(name) == null)
					insertList.add(new Person(name, null));
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		if (!insertList.isEmpty()) {
			try {
				Datenbank.insertPersonenListe(insertList);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	private ObservableList<String> loadObservableList() {
		cbPersonen.getSelectionModel().clearSelection();
		try {
			ol.clear();
			for (String p : Datenbank.leseNamen())
				ol.add(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ol;
	}
	
	private ObservableList<String> loadObservableListList() {
		try {
			olList.clear();
			letzteRunde = Datenbank.leseLetzteRunde();
			if (letzteRunde == 0) 
				olList.add("Keine Letzte Runde abrufbar");
			else {
				log.info("Letzte Runde -> " + letzteRunde);
			letzeRundeList = Datenbank.leseErgebnisseNachRundeList(letzteRunde);
			olList.add("letzte Runde: " + letzteRunde);
			letzeRundeList.forEach(l -> olList.add(l.getSucht() + " suchte aus für " + l.getFuer()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return olList;
	}



	public int getLetzteRunde() {
		return letzteRunde;
	}
	
	

}
