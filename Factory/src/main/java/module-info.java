module factory {
	requires transitive factory.shared;
	requires transitive factory.subsystem.monitoring;
	requires transitive factory.subsystem.assemblylines;
	requires transitive factory.subsystem.agv;
	requires transitive factory.subsystem.warehouse;
	
	requires java.xml;
}
