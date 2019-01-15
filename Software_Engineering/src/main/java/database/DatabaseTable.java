package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DatabaseTable {

	private final Map<String, String> columnNameToDatatypeMap = new LinkedHashMap<>();	//LinkedHashMap keeps insertion order
	protected PreparedStatement selectEverything;
	
	public abstract String getTableName();
	
	/** Fills the table initially (called when database was newly created) */
	public abstract void initialTableFill();
	
	/** Utility method for testing purposes */
	public abstract void printToConsole();
	
	protected abstract void initializeMap(Map<String, String> columnNameToDatatypeMap);
	
	public DatabaseTable() {
		this.initializeMap(columnNameToDatatypeMap);
	}
	
	public void prepareStatements(Connection databaseConnection) {
		try {
			selectEverything = databaseConnection.prepareStatement("SELECT * FROM " + getTableName());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getCreationString() {
		return String.format("CREATE TABLE %s (%s)", this.getTableName(), this.getColumnCreationString());
	}
	
	private String getColumnCreationString() {
		StringBuilder colCreationSb = new StringBuilder();
		
		for (String colName : columnNameToDatatypeMap.keySet()) {
			colCreationSb.append(colName);
			colCreationSb.append(" ");
			colCreationSb.append(columnNameToDatatypeMap.get(colName));
			colCreationSb.append(", ");
		}
		
		colCreationSb.delete(colCreationSb.length() - 2, colCreationSb.length());	//delete last ", "
		return colCreationSb.toString();
	}
	
}
