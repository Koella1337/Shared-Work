package app.model;

import java.util.concurrent.CancellationException;

@SuppressWarnings("serial")
public class DeathException extends CancellationException {
	
	public DeathException(Entity source) {
		super("The entity " + source + " died.");
	}
	
}
