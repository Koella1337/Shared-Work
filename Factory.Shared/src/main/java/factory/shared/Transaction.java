package factory.shared;

import factory.shared.enums.Material;

/**
 * A transaction of materials bought from another company for a specific StorageSite.
 */
public class Transaction {
	
	public final String company;
	public final Material material;
	public final int containerAmount;
	public final int cost;
	public final String date;
	public final int storageSiteID;
	
	public Transaction(String company, Material material, int containerAmount, 
			int cost, String date, int storageSiteID) {
		this.company = company;
		this.material = material;
		this.containerAmount = containerAmount;
		this.cost = cost;
		this.date = date;
		this.storageSiteID = storageSiteID;
	}

	@Override
	public String toString() {
		return "(Transaction company=" + company + ", material=" + material + ", containerAmount=" + containerAmount
				+ ", cost=" + cost + ", date=" + date + ", storageSiteID=" + storageSiteID + ")";
	}
	
}
