module factory.subsystem.monitoring {
	exports factory.subsystems.monitoring;
	exports factory.subsystems.monitoring.interfaces;

	requires transitive factory.shared;
	requires transitive factory.subsystem.assemblylines;
	requires transitive factory.subsystem.agv;
	requires transitive factory.subsystem.warehouse;
	
	requires java.logging;
}
