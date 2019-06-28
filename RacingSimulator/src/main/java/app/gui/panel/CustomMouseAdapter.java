package app.gui.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.function.Predicate;

import app.gui.GuiHandler;
import app.model.Transform;
import app.model.car.Car;

public class CustomMouseAdapter extends MouseAdapter {

	private GuiHandler guiHandler;

	public CustomMouseAdapter(GuiHandler guiHandler) {
		super();
		this.guiHandler = guiHandler;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();

		Optional<? extends Car> clickedCar = findCarForMousePosition(x, y);

		if (clickedCar.isPresent()) {
			guiHandler.highlightCar(clickedCar.get());
			guiHandler.getMenuPanel().showCarStats(clickedCar.get());
		}
	}

	public Optional<? extends Car> findCarForMousePosition(double xPos, double yPos) {
		return guiHandler.getCars().parallelStream().filter(isCollidingWithPoint(xPos, yPos)).findAny();
	}

	public Predicate<Car> isCollidingWithPoint(double pointX, double pointY) {
		return car -> {
			Transform transform = car.getTransform();
			double carY = transform.getYPos();
			double carHeight = transform.getYSize();

			return pointY >= carY && pointY <= carY + carHeight;
		};
	}

}
