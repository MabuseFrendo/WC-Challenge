package gui;

public class Ergebnis {
 private String sucht;
 private String fuer;
 private int runde;
 
 
public Ergebnis(String sucht, String fuer, int runde) {
	this.sucht = sucht;
	this.fuer = fuer;
	this.runde = runde;
}


public String getSucht() {
	return sucht;
}


public void setSucht(String sucht) {
	this.sucht = sucht;
}


public String getFuer() {
	return fuer;
}


public void setFuer(String fuer) {
	this.fuer = fuer;
}


public int getRunde() {
	return runde;
}


public void setRunde(int runde) {
	this.runde = runde;
}


}
