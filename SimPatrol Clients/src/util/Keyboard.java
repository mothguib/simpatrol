/* Keyboard.java */

/* The package of this class. */
package util;

/* Imported classes and/or interfaces. */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** Implements a keyboard, with its functions of reading data. */
public abstract class Keyboard {
	/* Attributes. */
	private static final BufferedReader KEYBOARD = new BufferedReader(
			new InputStreamReader(System.in));

	/* Methods. */
	/**
	 * Reads a string line from the keyboard.
	 * 
	 * @throws IOException
	 */
	public static String readLine() throws IOException {
		return KEYBOARD.readLine();
	}

}
