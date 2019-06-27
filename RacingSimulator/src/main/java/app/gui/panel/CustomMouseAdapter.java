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
	public void mouseClicked(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();

		Optional<? extends Car> clickedCar = findCarForMouseClick(x, y);

		if (clickedCar.isPresent()) {
			guiHandler.getMenuPanel().showCarStats(clickedCar.get());
		}

	}

	public Optional<? extends Car> findCarForMouseClick(double xPos, double yPos) {
		return guiHandler.getCars().parallelStream().filter(isCollidingWithPoint(xPos, yPos)).findAny();
	}

	public Predicate<Car> isCollidingWithPoint(double pointX, double pointY) {
		return car -> {
			Transform transform = car.getTransform();
			double carX = transform.getXPos();
			double carY = transform.getYPos();
			double carWidth = transform.getXSize();
			double carHeight = transform.getYSize();

			boolean isCollidingY = pointY >= carY && pointY <= carY + carHeight;
			return  isCollidingY;
		};
	}

}
