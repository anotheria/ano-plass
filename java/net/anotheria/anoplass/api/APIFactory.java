package net.anotheria.anoplass.api;

/**
 * Interface to be implemented by APIFactory for a parametrized API. 
 * The API modell decouples api implementation from configuration or user, so the only class tied to the implementation
 * is the factory which is supplied along with the interface to the API Finder.
 * @author lrosenberg
 *
 * @param <T> the API class.
 */
public interface APIFactory<T extends API> {
	/**
	 * Creates a new API instance. APIFinder manages the instances, so usually this method will be called only once in a lifetime.
	 * Note: Your client shouldn't rely on the proper implementation, since the implementation object will be proxied by mocks/performance monitoring proxies and
	 * only methods exposed in the T-interface are accessable.
	 * @return an api instance.
	 */
	 T createAPI();
}
