package gui;

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

public class TestKlasse {

	static ArrayList<Person> plAuswahl = new ArrayList<>();
	static HashMap<String, String> sonderregeln = new HashMap<>();
//	static HashMap<String,String> finaleListe = new HashMap<>();
	static boolean needRekursiv;
	static int runde=2;
	static HashMap<String, String> mix;
	static HashMap<String, String> letzteRundeHM = new HashMap<>();

	public static void main(String[] args) {
		
		try {
			letzteRundeHM=Datenbank.leseErgebnisseNachRunde(runde-1); //gehört nachher in den Konstruktor
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		plAuswahl.add(new Person("Kevin", null));
		plAuswahl.add(new Person("Brösel", null));
		plAuswahl.add(new Person("Tobi", null));
		plAuswahl.add(new Person("Max", null));
		plAuswahl.add(new Person("Lu", null));
		plAuswahl.add(new Person("Klara", null));
		plAuswahl.add(new Person("Klaus", null));
		plAuswahl.add(new Person("Anna", null));
		plAuswahl.add(new Person("Mari", null));


		sonderregeln.put("Tobi", "Brösel");
		sonderregeln.put("Klara", "Klaus");
		sonderregeln.put("Anna", "Kevin");

//		mix();
//		considerSonderregeln();
		
		mix2();

	}

	public static void mix() {
		do {
			needRekursiv = false;
			ArrayList<Person> personen1 = (ArrayList<Person>) plAuswahl.clone();
			ArrayList<Person> personen2 = (ArrayList<Person>) plAuswahl.clone();
			mix = new HashMap<>();
			
			System.out.println("Start First Shuffel");
			for (Person p1 : personen1) {
				int zufallszahl = new Random().nextInt(personen2.size());
				Person p2 = personen2.get(zufallszahl);
				if (p1.getName().contentEquals(p2.getName())) {
					System.out.println("Mix Again");
					needRekursiv = true;
					break;

				}
				mix.put(p1.getName(), p2.getName());
				System.out.println(p1.getName() + " and " + p2.getName() + " Added!");
				System.out.println(p2.getName() + " removed!");
				personen2.remove(zufallszahl);
			}
		} while (needRekursiv);
		System.out.println("End first Shuffel");

		mix.forEach((k, v) -> System.out.println(k + " sucht aus für " + v));
	}

	private static void considerSonderregeln() {
		System.out.println("Start second Shuffel");

		sonderregeln.forEach((keySonderregeln, valueSonderrelgen) -> {
			String valueFromKeySonderregeln = mix.entrySet().stream()
					.filter(entry -> Objects.equals(entry.getKey(), keySonderregeln)).findFirst().get().getValue();
			String keyFromMix = mix.entrySet().stream()
					.filter(entry -> Objects.equals(entry.getValue(), valueSonderrelgen)).findFirst().get().getKey();
			mix.replace(keySonderregeln, valueSonderrelgen);
			mix.replace(keyFromMix, valueFromKeySonderregeln);
			System.out.println("Sonderregel '" + keySonderregeln + " sucht aus für " + valueSonderrelgen + "' berücksichitgt");
			System.out.println(keyFromMix + " sucht jetzt aus für " + valueFromKeySonderregeln + "' berücksichitgt");

		});
//		sonderregeln.put("Tobi", "Brösel");
//		sonderregeln.put("Klara", "Klaus");
//		sonderregeln.put("Anna", "Kevin");

		System.out.println("End second Shuffel");
		System.out.println("Finales Ergebnis => ");
		mix.forEach((k, v) -> System.out.println(k + " sucht aus für " + v));

	}
	
	public static void mix2() {
		do {
			needRekursiv = false;
			ArrayList<Person> personen1 = (ArrayList<Person>) plAuswahl.clone();
			ArrayList<Person> personen2 = (ArrayList<Person>) plAuswahl.clone();
			mix = new HashMap<>();
			
			System.out.println("Start First Shuffel");
			for (Person p1 : personen1) {
				int zufallszahl = new Random().nextInt(personen2.size());
				Person p2 = personen2.get(zufallszahl);
				if (p1.getName().contentEquals(p2.getName()) || gleichLetzteRunde(p1.getName(),p2.getName())) {
					System.out.println(p1.getName() +" = "+p2.getName()+ " => Mix Again");
					needRekursiv = true;
					break;

				}
				mix.put(p1.getName(), p2.getName());
				System.out.println(p1.getName() + " and " + p2.getName() + " Added!");
				System.out.println(p2.getName() + " removed!");
				personen2.remove(zufallszahl);
			}
		} while (needRekursiv);
		System.out.println("End first Shuffel");

		mix.forEach((k, v) -> System.out.println(k + " sucht aus für " + v));
		considerSonderregeln2();
	}
	
	private static void considerSonderregeln2() {
		System.out.println("Start second Shuffel");

		sonderregeln.forEach((keySonderregeln, valueSonderrelgen) -> {
			String valueFromKeySonderregeln = mix.entrySet().stream()
					.filter(entry -> Objects.equals(entry.getKey(), keySonderregeln)).findFirst().get().getValue();
			String keyFromMix = mix.entrySet().stream()
					.filter(entry -> Objects.equals(entry.getValue(), valueSonderrelgen)).findFirst().get().getKey();
			mix.replace(keySonderregeln, valueSonderrelgen);
			mix.replace(keyFromMix, valueFromKeySonderregeln);
			System.out.println("Sonderregel '" + keySonderregeln + " sucht aus für " + valueSonderrelgen + "' berücksichitgt");
			System.out.println(keyFromMix + " sucht jetzt aus für " + valueFromKeySonderregeln + "' berücksichitgt");

		});
//		sonderregeln.put("Tobi", "Brösel");
//		sonderregeln.put("Klara", "Klaus");
//		sonderregeln.put("Anna", "Kevin");

		System.out.println("End second Shuffel");
		System.out.println("Finales Ergebnis => ");
		mix.forEach((k, v) -> System.out.println(k + " sucht aus für " + v));
		mix.forEach((k, v) -> {
		if (k.contentEquals(v) || gleichLetzteRunde(k,v)) {
			System.out.println(k +" = "+v+ " => Mix Again");
			System.out.println("### Mix again rekursiv ###");	
			needRekursiv=true;
		}
		});
		if (needRekursiv)
			mix2();
	}

	private static boolean gleichLetzteRunde(String name1, String name2) {
		List<Entry<String, String>> duplicateEntriesList =letzteRundeHM.entrySet().stream().filter(f-> f.getKey().contentEquals(name1) && f.getValue().contentEquals(name2)).collect(Collectors.toList());
		return !duplicateEntriesList.isEmpty();
	}


}
