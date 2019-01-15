package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Database {

	private static final String URL = "jdbc:derby:database";
	
	public static final Database INSTANCE = new Database();			//singleton
	
	private final List<DatabaseTable> tables = new ArrayList<>();
	
	private Connection connection;
	
	{ 
		//TODO: rather than initializing the list here statically, add a table for each StorageSite when the layout.xml is being processed
		addTable(new TransactionsTable());
	}
	
	//TODO: only for current testing, remove this method later
	public List<DatabaseTable> getTables() {
		return tables;
	}
	
	/** add all tables before initialize() is called */
	public synchronized void addTable(DatabaseTable table) {
		tables.add(table);
	}
	
	/**
	 * Initializes the connection to the database. Call this after all tables have been added.
	 */
	public void initialize() {
		try {
			Connection c = null;
			try {
				//get connection to existing database
				c = DriverManager.getConnection(URL);
			} catch (SQLException databaseNotFound) {
				//create new database
				c = DriverManager.getConnection(URL + ";create=true");
				Statement creationStatement = c.createStatement();
				for (DatabaseTable table : tables) {
					System.out.println(table.getCreationString());
					creationStatement.addBatch(table.getCreationString());
				}
				creationStatement.executeBatch();
			}
			connection = c;
			
			if (connection.isReadOnly()) {
				JOptionPane.showMessageDialog(null, 
						"Establishing Database Connection failed.\nPlease restart the program to try again."
						, "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			
			for (DatabaseTable table : tables) {
				table.prepareStatements(connection);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Establishing Database Connection failed.");
		}
	}
	
	/**
	 * Utility method for testing purposes
	 */
	public void printToConsole(){
		for (DatabaseTable table : tables) {
			table.printToConsole();
		}
	}
}




