package logger;

/**
 * 
 * This class represents a simple logger. It might be used by aspects and other
 * logging code.
 * 
 */
public class Logger {

	/**
	 * The single instance of Logger
	 */
	private static Logger instance = null;

	/**
	 * Default constructor
	 */
	private Logger() {
		;
	}

	/**
	 * @return The single instance of Logger
	 */
	public static Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}

	/**
	 * Logs <code>data</code> into the console.
	 */
	public void log(String data) {
		System.out.println(data);
	}
}
