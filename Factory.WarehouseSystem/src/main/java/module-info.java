module factory.subsystem.warehouse {
	exports factory.subsystems.warehouse.interfaces;
	exports factory.subsystems.warehouse;

	requires transitive factory.shared;
	
	requires transitive org.apache.derby.engine;
	
	requires java.desktop;
	requires java.xml;
	requires java.sql;
}
