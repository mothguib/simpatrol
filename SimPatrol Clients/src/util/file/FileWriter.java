/* FileWriter.java */

/* The package of this class. */
package util.file;

/* The package of this class. */
import java.io.IOException;
import java.io.PrintWriter;

/** Implements objects able to write text files. */
public class FileWriter {
	/* Attributes */
	/** A java 2 native file writer. */
	private PrintWriter out;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The path of the file to be written.
	 * @throws IOException
	 */
	public FileWriter(String path) throws IOException {
		this.out = new PrintWriter(new java.io.FileWriter(path), true);
	}

	/** Closes the file. */
	public void close() {
		if (this.out != null) {
			this.out.close();
			this.out = null;
		}
	}

	/**
	 * Writes the given character into the file.
	 * 
	 * @param c
	 *            The character to be written into the file.
	 */
	public void print(char c) {
		this.out.print(String.valueOf(c));
	}

	/**
	 * Writes the given string into the file.
	 * 
	 * @param string
	 *            The string to be written into the file.
	 */
	public void print(String string) {
		this.out.print(string);
	}

	/**
	 * Writes the given integer value into the file.
	 * 
	 * @param i
	 *            The integer value to be written into the file.
	 */
	public void print(int i) {
		this.out.print(i);
	}

	/**
	 * Writes the given double value into the file.
	 * 
	 * @param d
	 *            The double value to be written into the file.
	 */
	public void print(double d) {
		this.out.print(d);
	}

	/**
	 * Writes the given double into the file, respecting the given number of
	 * decimal places.
	 * 
	 * If a value smaller than 1 is passed as the number of decimal places, only
	 * the rounded integer part will be printed.
	 * 
	 * @param d
	 *            The double value to be written into the file.
	 * @param dec
	 *            The number of digital places to be considered.
	 */
	public void print(double d, int dec) {
		this.out.print(this.formatDouble(d, dec));
	}

	/** Writes a new line into the file. */
	public void println() {
		this.out.println();
	}

	/**
	 * Writes the given character and a new line into the file.
	 * 
	 * @param c
	 *            The character to be written into the file.
	 */
	public void println(char c) {
		this.out.println(String.valueOf(c));
	}

	/**
	 * Writes the given string and a new line into the file.
	 * 
	 * @param string
	 *            The string to be written into the file.
	 */
	public void println(String string) {
		this.out.println(string);
	}

	/**
	 * Writes the given integer value and a new line into the file.
	 * 
	 * @param i
	 *            The integer value to be written into the file.
	 */
	public void println(int i) {
		this.out.println(i);
	}

	/**
	 * Writes the given double value and a new line into the file.
	 * 
	 * @param d
	 *            The double value to be written into the file.
	 */
	public void println(double d) {
		this.out.println(d);
	}

	/**
	 * Writes the given double, respecting the given number of decimal places,
	 * and a new line into the file.
	 * 
	 * If a value smaller than 1 is passed as the number of decimal places, only
	 * the rounded integer part will be printed.
	 * 
	 * @param d
	 *            The double value to be written into the file.
	 * @param dec
	 *            The number of digital places to be considered.
	 */
	public void println(double d, int dec) {
		this.out.println(this.formatDouble(d, dec));
	}

	/** Saves the data from the buffer of the file to the disk. */
	public void flush() {
		this.out.flush();
	}

	/**
	 * Returns the string that represents the given double rounded by the given
	 * number of decimal places.
	 * 
	 * @param d
	 *            The double value to be written into the file.
	 * @param dec
	 *            The number of digital places to be considered.
	 * @return The string that representes the given double value.
	 */
	private String formatDouble(double d, int dec) {
		if (dec <= 0)
			return String.valueOf(Math.round(d));

		StringBuffer res = new StringBuffer();
		long aprox = (int) Math.round(d * Math.pow(10, dec));
		if (d < 0) {
			aprox = -aprox;
			res.append('-');
		}

		String num = String.valueOf(aprox);
		int n = num.length() - dec;
		if (n <= 0) {
			res.append("0.");

			for (int i = 0; i < -n; i++)
				res.append('0');

			res.append(num);
		} else {
			char[] array = num.toCharArray();
			res.append(array, 0, n).append('.').append(array, n, dec);
		}

		return res.toString();
	}

	/**
	 * Forces the file closing, when the garbage collector is called.
	 * 
	 * @throws IOException
	 */
	protected void finalize() throws IOException {
		this.close();
	}
}