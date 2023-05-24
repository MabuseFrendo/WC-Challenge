package gui;

public class Person {
	private long id;
	private String name;
	private String note;
	
	public Person(long id, String name, String note) {
		this.id=id;
		this.name = name;
		this.note = note;
	}
	
	public Person(String name, String note) {
		this.name = name;
		this.note = note;
	}
	
	public Person(PersonFX fx) {
		this.id=fx.getId();
		this.name = fx.getName();
		this.note = fx.getNote();
	}

	public void setId(long id) {
		this.id=id;
	}
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
