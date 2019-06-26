package app.model;

import java.util.concurrent.TimeUnit;

public interface Entity {
	
	Position position();
	
	double size();
	
	void eat(double foodValue);
	
	
	//CompleteFutures: Hunt, complete: eat (enough based on foodvalue), fail on timeout
	
}
