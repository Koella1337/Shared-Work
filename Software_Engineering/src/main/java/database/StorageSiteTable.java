package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import factory.shared.Constants;
import factory.shared.Container;
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
	public void initialTableFill() {
		Random rng = new Random();
		
		for (Material mat : Material.values()) {
			if (mat == Material.CAR)
				continue;
			
			try {
				this.addMaterial(mat, rng.nextInt(Constants.RESOURCE_BOX_MAX_CONTAINERS / 2) + 5);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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
	
	public synchronized void addMaterial(Material material, int containerAmount) throws SQLException {
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
	
	public synchronized void removeMaterial(Material material, int containerAmount) throws SQLException {
		int containersStored = getContainerAmount(material);
		
		if (containersStored > 0) {
			updateMaterial.setInt(1, Math.max(0, containersStored - containerAmount));
			updateMaterial.setString(2, material.toString());
			updateMaterial.execute();
		}
	}
	
	/** Retrieves the amount of containers in this StorageSite of a specific material. */
	public synchronized int getContainerAmount(Material material) throws SQLException {
		selectMaterial.setString(1, material.toString());
		ResultSet result = selectMaterial.executeQuery();
		
		if (result.next())
			return result.getInt(2);
		else
			return 0;
	}
	
	public synchronized Container[] getAllContainers() throws SQLException {
		List<Container> containers = new ArrayList<>();
		ResultSet result = selectEverything.executeQuery();
		
		while (result.next()) {
			Material material = Material.valueOf(result.getString(1));
			int containerAmount = result.getInt(2);
			
			for (int i = 0; i < containerAmount; i++)
				containers.add(new Container(material));
		}
		
		return containers.toArray(new Container[0]);
	}

	/**
	 * Utility method for testing purposes
	 */
	@Override
	public synchronized void printToConsole() {
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
