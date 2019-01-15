package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import factory.shared.Constants;

public class Database {

	private static final String URL = "jdbc:derby:database";
	
	public static final Database INSTANCE = new Database();			//singleton
	
	private final List<DatabaseTable> tables = new ArrayList<>();
	
	private Connection connection;
	
	/** add all tables before initialize() is called */
	public synchronized void addTable(DatabaseTable table) {
		tables.add(table);
	}
	
	/**
	 * Initializes the connection to the database. Call this after all tables have been added.
	 * @return true if the database was newly created. false if a database already existed.
	 */
	public void initialize() {
		try {
			Connection c = null;
			boolean wasNewlyCreated;
			try {
				//get connection to existing database
				c = DriverManager.getConnection(URL);
				wasNewlyCreated = false;
			} catch (SQLException databaseNotFound) {
				//create new database
				c = DriverManager.getConnection(URL + ";create=true");
				Statement creationStatement = c.createStatement();
				for (DatabaseTable table : tables) {
					if (Constants.DEBUG)
						System.out.println(table.getCreationString());
					creationStatement.addBatch(table.getCreationString());
				}
				creationStatement.executeBatch();
				wasNewlyCreated = true;
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
				if (wasNewlyCreated)
					table.initialTableFill();
			}
			
			if (Constants.DEBUG)
				this.printToConsole();
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




