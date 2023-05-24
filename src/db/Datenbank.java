package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gui.Ergebnis;
import gui.Person;
import gui.WC_Main_Page;


public class Datenbank {
	private static final Logger log = LogManager.getLogger(WC_Main_Page.class);

	
//	private static String DBLocation;
	private static final String DBLocation = "C:\\Datenbanken\\WCDatenbank";
//	private static final String DBLocation = "C:\\Users\\manue\\Desktop\\JavaSchulung\\JavaKurs\\DatenbankWC";
	
	
	private static final String connString = "jdbc:derby:" + DBLocation + ";create=true";
	private static final String TABLE_PERSONS = "PERSONS"; 
	private static final String ID_PERSON = "ID";
	private static final String NAME = "NAME";
	private static final String NOTE = "NOTE";

	private static final String TABLE_ERGEBNISSE = "ERGEBNISSE"; 
	private static final String ID_ERGEBNIS = "ID";
	private static final String SUCHT = "SUCHT";
	private static final String FUER = "FUER";
	private static final String RUNDE = "RUNDE";
	

	public static void setDBLocation(boolean testmode) {
//		DBLocation = testmode ? "C:\\Datenbanken\\WCDatenbank" : "C:\\Datenbanken\\WCDatenbankProd"; funkt nicht
		
	}
	
	public static void createTableErgebnisse() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, TABLE_ERGEBNISSE.toUpperCase(), new String[] { "TABLE" });
			if (rs.next()) {
				log.info(TABLE_ERGEBNISSE + " exisitiert bereits");
				return;
			}
			String ct = "CREATE TABLE " + TABLE_ERGEBNISSE + " (" + ID_ERGEBNIS + " INTEGER GENERATED ALWAYS AS IDENTITY, " + SUCHT
					+ " VARCHAR(200), " + FUER + " VARCHAR(200), " +RUNDE + " INTEGER, " + "PRIMARY KEY(" + ID_PERSON + "))";
			;

			stmt.executeUpdate(ct);
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}

		
	public static void createTablePersons() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, TABLE_PERSONS.toUpperCase(), new String[] { "TABLE" });
			if (rs.next()) {
				log.info(TABLE_PERSONS + " exisitiert bereits");
				return;
			}
			String ct = "CREATE TABLE " + TABLE_PERSONS + " (" + ID_PERSON + " INTEGER GENERATED ALWAYS AS IDENTITY, " + NAME
					+ " VARCHAR(200), " + NOTE + " VARCHAR(200), " + "PRIMARY KEY(" + ID_PERSON + "))";
			;

			stmt.executeUpdate(ct);
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	public static void insertPersonenListe(ArrayList<Person> pl) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String insert = "INSERT INTO " + TABLE_PERSONS + "(" + NAME + "," + NOTE+ ")" + " VALUES (" 
		        + "?, " // name
				+ "?)"; // note
		
		try {
			conn = DriverManager.getConnection(connString);
			pstmt = conn.prepareStatement(insert);
			for (Person p : pl) {
				pstmt.setString(1, p.getName());
				pstmt.setString(2, p.getNote());
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static void insertErgebnisListe(HashMap<String,String> hm, int runde) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String insert = "INSERT INTO " + TABLE_ERGEBNISSE + "(" + SUCHT + "," + FUER+ ","+RUNDE+")" + " VALUES (" 
		        + "?, " // sucht
		        + "?, " // fuer
				+ "?)"; // runde
		
		try {
			conn = DriverManager.getConnection(connString);
			pstmt = conn.prepareStatement(insert);
			hm.entrySet();
			for (Entry<String, String> e :hm.entrySet()) {
					pstmt.setString(1, e.getKey());
					pstmt.setString(2, e.getValue());
					pstmt.setInt(3, runde);
					pstmt.executeUpdate();
			};
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}


	public static ArrayList<Person> lesePersonen() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<Person> al = new ArrayList<>();
		String select = "SELECT * FROM " + TABLE_PERSONS;
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();
			while (rs.next())
				al.add(new Person(rs.getLong(ID_PERSON), rs.getString(NAME), rs.getString(NOTE)));
			rs.close();
			return al;
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static HashMap<String,String> leseErgebnisseNachRunde(int runde) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		HashMap<String,String> hm = new HashMap<>();
		String select = "SELECT * FROM " + TABLE_ERGEBNISSE + " WHERE " + RUNDE + " = " + runde;
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();
			while (rs.next())
				hm.put(rs.getString(SUCHT), rs.getString(FUER));
			rs.close();
			return hm;
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static List<Ergebnis> leseErgebnisseNachRundeList(int runde) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Ergebnis> hm = new ArrayList<>();
		String select = "SELECT * FROM " + TABLE_ERGEBNISSE + " WHERE " + RUNDE + " = " + runde; 
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();
			while (rs.next())
				hm.add(new Ergebnis(rs.getString(SUCHT), rs.getString(FUER), rs.getInt(RUNDE)));
			rs.close();
			return hm;
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static int leseLetzteRunde() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int runde = 0;
		String select = "SELECT MAX("+RUNDE+")  as "+RUNDE+" FROM " + TABLE_ERGEBNISSE;
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();
			while (rs.next())
				runde= rs.getInt(RUNDE);
			rs.close();
			return runde;
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static ArrayList<String> leseNamen() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<String> al = new ArrayList<>();
		String select = "SELECT "+ NAME +" FROM " + TABLE_PERSONS;
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();
			while (rs.next())
				al.add(rs.getString(NAME));
			rs.close();
			return al;
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static Long lesePersonID(String name) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<Long> al = new ArrayList<>();
		String select = "SELECT "+ ID_PERSON +" FROM " + TABLE_PERSONS + " WHERE NAME = '" + name + "'";
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();
			while (rs.next())
				al.add(rs.getLong(ID_PERSON));
			rs.close();
			return !al.isEmpty() ? al.get(0) : null ;
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static void update(Person f2t) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String insert = "UPDATE " +  TABLE_PERSONS + " SET "+ NAME + " = ? , " +  NOTE + " = ? " + "WHERE " + ID_PERSON + " = ?";	 
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(insert);
			stmt.setString(1, f2t.getName());
			stmt.setString(2, f2t.getNote());
			stmt.setLong(3, f2t.getId()); //WHERE
			stmt.executeUpdate();		
		} 
		catch (SQLException e) {
			e.toString();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) {
				e.toString();
			}
		}
	}
	

	public static void deleteWholeTableEntriesPersons() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String delete = "DELETE FROM " + TABLE_PERSONS;
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(delete);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static void deleteWholeTableEntriesErgebnisse() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String delete = "DELETE FROM " + TABLE_ERGEBNISSE;
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(delete);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static void deleteEntryByIdPerson(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String delete = "DELETE FROM " + TABLE_PERSONS + " WHERE ID = " + id;
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(delete);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public static void deleteEntryByNamePerson(String name) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String delete = "DELETE FROM " + TABLE_PERSONS + " WHERE NAME = '" + name + "'";
		try {
			conn = DriverManager.getConnection(connString);
			stmt = conn.prepareStatement(delete);
			stmt.executeUpdate();
			log.info("Eintrag Gelöscht");
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
}
