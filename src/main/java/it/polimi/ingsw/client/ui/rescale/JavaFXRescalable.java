package it.polimi.ingsw.client.ui.rescale;

/**
 * Interface for the rescale feature
 */
public interface JavaFXRescalable {
	/**
	 * Rescales the object with a given scale
	 * @param scale The scale for the object
	 */
	void rescale(double scale);
	
	/**
	 * FInd the current scale value
	 * @return The current scale value
	 */
	double getCurrentScaleValue();
}
