package gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class PersonFX {
private Person personInDB; 
	
	private LongProperty id;
	private StringProperty name; 
	private StringProperty note;
	
	
	public PersonFX( Person person) {
		personInDB=person;
		
		id = new SimpleLongProperty(person.getId());
		name = new SimpleStringProperty(person.getName());
		note = new SimpleStringProperty(person.getNote());
	}
	
	
	public Person getPersonInDB() {
		return personInDB;
	}
	
	public long getId() {
		return id.get();
	}
	public LongProperty idProperty() {
		return id;
	}

	public String getName() {
		return name.get();
	}
	public StringProperty nameProperty () {
		return name;
	}
	
	public String getNote() {
		return note.get();
	}
	public StringProperty noteProperty () {
		return note;
	}
}
