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

import db.Datenbank;
import gui.Person;

public class Losung {

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
			
			System.out.println("Start First Shuffel");
			for (Person p1 : personen1) {
				int zufallszahl = new Random().nextInt(personen2.size());
				Person p2 = personen2.get(zufallszahl);
				if (p1.getName().contentEquals(p2.getName())|| gleichLetzteRunde(p1.getName(),p2.getName())) {
					System.out.println(p1.getName() +" = "+p2.getName()+ " => Mix Again");
					needforRepeat = true;
					break;

				}
				finalerMix.put(p1.getName(), p2.getName());
				System.out.println(p1.getName() + " and " + p2.getName() + " Added!");
				System.out.println(p2.getName() + " removed!");
				personen2.remove(zufallszahl);
			}
		} while (needforRepeat);
		System.out.println("End first Shuffel");

		finalerMix.forEach((k, v) -> System.out.println(k + " sucht aus für " + v));
		considerSonderregeln();
	}

	private void considerSonderregeln() {
		System.out.println("Start second Shuffel");
		sonderregeln.forEach((keySonderregeln, valueSonderrelgen) -> {
			String valueFromKeySonderregeln = finalerMix.entrySet().stream()
					.filter(entry -> Objects.equals(entry.getKey(), keySonderregeln)).findFirst().get().getValue();
			String keyFromMix = finalerMix.entrySet().stream()
					.filter(entry -> Objects.equals(entry.getValue(), valueSonderrelgen)).findFirst().get().getKey();
			finalerMix.replace(keySonderregeln, valueSonderrelgen);
			finalerMix.replace(keyFromMix, valueFromKeySonderregeln);
			System.out.println("Sonderregel '" + keySonderregeln + " sucht aus für '" + valueSonderrelgen + "' berücksichitgt");
			System.out.println(keyFromMix + " sucht jetzt aus für " + valueFromKeySonderregeln);
			finalerMix.forEach((k, v) -> {
				if (k.contentEquals(v) || gleichLetzteRunde(k,v)) {
					System.out.println(k +" = "+v+ " => Mix Again");
					System.out.println("### Mix again rekursiv ###");	
					needforRepeat=true;
				}
				});
				
		});
		if (needforRepeat) {
			System.out.println("End second Shuffel");
			mix();
	}
	}
	
	
	public void logFinalEntries() {
		System.out.println("\nBerücksichtigte Sonderregeln => ");
		sonderregeln.forEach((k, v) -> System.out.println(k + " sucht aus für " + v));
		System.out.println("\nBerücksichtigte Paarungen von Runde #" + (runde-1)+" => ");
		letzteRundeHM.forEach((k,v) -> System.out.println(k + " suchte aus für " + v));
		System.out.println("\nFinales Ergebnis #"+runde+" => ");
		finalerMix.forEach((k, v) -> System.out.println(k + " sucht aus für " + v));
	}
	
	private boolean gleichLetzteRunde(String name1, String name2) {
		List<Entry<String, String>> duplicateEntriesList =letzteRundeHM.entrySet().stream().filter(f-> f.getKey().contentEquals(name1) && f.getValue().contentEquals(name2)).collect(Collectors.toList());
		return !duplicateEntriesList.isEmpty();
	}
	
	public HashMap<String, String> getFinalerMix() {
		return finalerMix;
	}

}
