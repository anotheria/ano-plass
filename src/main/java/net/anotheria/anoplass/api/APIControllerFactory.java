package net.anotheria.anoplass.api;

/**
 * Creates and returns an instance of the api controller which in turn configures the APIFinder.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class APIControllerFactory {
	/**
	 * The instance of the controller.
	 */
	private static APIController controller = new APIController();
	/**
	 * Returns the singleton instance of the controller.
	 *
	 * @return APIController
	 */
	public static APIController getAPIController(){
		return controller;
	}
}
