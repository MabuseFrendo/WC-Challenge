package mix;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import db.Datenbank;
import gui.Person;
import gui.WC_Main_Page;

public class Losung {
	
	private static final Logger log = LogManager.getLogger(WC_Main_Page.class);

	private ArrayList<Person> plAuswahl = new ArrayList<>();
	private HashMap<String, String> sonderregeln = new HashMap<>();
	private boolean needforRepeat;
	private HashMap<String, String> finalerMix;
	private int runde;
	private HashMap<String, String> letzteRundeHM = new HashMap<>();

	
	
	public Losung(ArrayList<Person> plAuswahl, HashMap<String,String> sonderregeln,int runde) {
		this.plAuswahl=plAuswahl;
		this.sonderregeln=sonderregeln;
		this.runde=runde;
		try {
			letzteRundeHM=Datenbank.leseErgebnisseNachRunde(runde-1); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void mix() {
		do {
			needforRepeat = false;
			ArrayList<Person> personen1 = (ArrayList<Person>) plAuswahl.clone();
			ArrayList<Person> personen2 = (ArrayList<Person>) plAuswahl.clone();
			finalerMix = new HashMap<>();
			
			log.info("Start First Shuffel");
			for (Person p1 : personen1) {
				int zufallszahl = new Random().nextInt(personen2.size());
				Person p2 = personen2.get(zufallszahl);
				if (p1.getName().contentEquals(p2.getName())|| gleichLetzteRunde(p1.getName(),p2.getName())) {
					log.info(p1.getName() +" = "+p2.getName()+ " => Mix Again");
					needforRepeat = true;
					break;

				}
				finalerMix.put(p1.getName(), p2.getName());
				log.info(p1.getName() + " and " + p2.getName() + " Added!");
				log.info(p2.getName() + " removed!");
				personen2.remove(zufallszahl);
			}
		} while (needforRepeat);
		log.info("End first Shuffel");

		finalerMix.forEach((k, v) -> log.info(k + " sucht aus für " + v));
		considerSonderregeln();
	}

	private void considerSonderregeln() {
		log.info("Start second Shuffel");
		sonderregeln.forEach((keySonderregeln, valueSonderrelgen) -> {
			String valueFromKeySonderregeln = finalerMix.entrySet().stream()
					.filter(entry -> Objects.equals(entry.getKey(), keySonderregeln)).findFirst().get().getValue();
			String keyFromMix = finalerMix.entrySet().stream()
					.filter(entry -> Objects.equals(entry.getValue(), valueSonderrelgen)).findFirst().get().getKey();
			finalerMix.replace(keySonderregeln, valueSonderrelgen);
			finalerMix.replace(keyFromMix, valueFromKeySonderregeln);
			log.info("Sonderregel '" + keySonderregeln + " sucht aus für '" + valueSonderrelgen + "' berücksichitgt");
			log.info(keyFromMix + " sucht jetzt aus für " + valueFromKeySonderregeln);
			finalerMix.forEach((k, v) -> {
				if (k.contentEquals(v) || gleichLetzteRunde(k,v)) {
					log.info(k +" = "+v+ " => Mix Again");
					log.info("### Mix again rekursiv ###");	
					needforRepeat=true;
				}
				});
				
		});
		if (needforRepeat) {
			log.info("End second Shuffel");
			mix();
	}
	}
	
	
	public void logFinalEntries() {
		System.out.println();
		log.info("Berücksichtigte Sonderregeln => ");
		sonderregeln.forEach((k, v) -> log.info(k + " sucht aus für " + v));
		System.out.println();
		log.info("Berücksichtigte Paarungen von Runde #" + (runde-1)+" => ");
		letzteRundeHM.forEach((k,v) -> log.info(k + " suchte aus für " + v));
		System.out.println();
		log.info("Finales Ergebnis #"+runde+" => ");
		finalerMix.forEach((k, v) -> log.info(k + " sucht aus für " + v));
	}
	
	private boolean gleichLetzteRunde(String name1, String name2) {
		List<Entry<String, String>> duplicateEntriesList =letzteRundeHM.entrySet().stream().filter(f-> f.getKey().contentEquals(name1) && f.getValue().contentEquals(name2)).collect(Collectors.toList());
		return !duplicateEntriesList.isEmpty();
	}
	
	public HashMap<String, String> getFinalerMix() {
		return finalerMix;
	}

}
