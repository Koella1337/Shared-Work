package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import factory.shared.enums.Material;

public class StorageSiteTable extends DatabaseTable {
	
	private PreparedStatement selectMaterial;
	private PreparedStatement insertMaterial;
	private PreparedStatement updateMaterial;
	
	private final String tableName;
	
	public StorageSiteTable(int storageSiteId) {
		super();
		this.tableName = "StorageSite_" + storageSiteId;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	protected void initializeMap(Map<String, String> columnNameToDatatypeMap) {
		columnNameToDatatypeMap.put("Material", "VARCHAR(20)");
		columnNameToDatatypeMap.put("ContainersStored", "INT");
	}

	@Override
	public void prepareStatements(Connection databaseConnection) {
		super.prepareStatements(databaseConnection);
		try {
			selectMaterial = databaseConnection.prepareStatement("SELECT * FROM " + tableName + " WHERE Material = ?");
			
			insertMaterial = databaseConnection.prepareStatement("INSERT INTO " + tableName + " VALUES (?, ?)");
			
			updateMaterial = databaseConnection.prepareStatement("UPDATE " + tableName + " SET ContainersStored = ? WHERE Material = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addMaterial(Material material, int containerAmount) throws SQLException {
		int containersStored = getContainerAmount(material);
		
		if (containersStored > 0) {
			updateMaterial.setInt(1, containersStored + containerAmount);
			updateMaterial.setString(2, material.toString());
			updateMaterial.execute();
		} else {
			insertMaterial.setString(1, material.toString());
			insertMaterial.setInt(2, containerAmount);
			insertMaterial.execute();
		}
	}
	
	public void removeMaterial(Material material, int containerAmount) throws SQLException {
		int containersStored = getContainerAmount(material);
		
		if (containersStored > 0) {
			updateMaterial.setInt(1, Math.max(0, containersStored - containerAmount));
			updateMaterial.setString(2, material.toString());
			updateMaterial.execute();
		}
	}
	
	/** Retrieves the amount of containers in this StorageSite of a specific material. */
	public int getContainerAmount(Material material) throws SQLException {
		selectMaterial.setString(1, material.toString());
		ResultSet result = selectMaterial.executeQuery();
		
		if (result.next())
			return result.getInt(2);
		else
			return 0;
	}

	/**
	 * Utility method for testing purposes
	 */
	@Override
	public void printToConsole() {
		try {
			System.out.printf("------------ TABLE: %s ------------%n", tableName);
			System.out.printf("%-21s %s%n", "Material", "ContainersStored");
			
			ResultSet allData = selectEverything.executeQuery();
			while (allData.next()) {
				System.out.printf("%-21s %d%n", allData.getString(1), allData.getInt(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
