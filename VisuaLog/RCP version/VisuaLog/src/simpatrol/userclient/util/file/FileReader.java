/* FileReader.java */

/* The package of this class. */
package simpatrol.userclient.util.file;

/* Imported classes and/or interfaces. */
import java.io.*;

/** Implements objects able to read text files. */
public class FileReader {
	/* Attributes */
	/** A Java native reader of buffers. */
	private BufferedReader file_reader;

	/** Buffer used to read the file content. */
	private String[] buffer;

	/** The line of the next token to be read from the file. */
	private int next_token_line;

	/** The column of the next token to be read from the file. */
	private int next_token_col;

	/** Registers the first line of read valid characters. */
	private int first_line;

	/** Counts how many lines were read from the file. */
	private int lines_count;

	/** Registers the column of the next char to be read, given a specific row. */
	private int next_char;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The path of the file to be read.
	 * @throws IOException
	 */
	public FileReader(String path) throws IOException {
		// sets the reader of buffers to manipulate the file to be read
		this.file_reader = new BufferedReader(new java.io.FileReader(path));

		// initiates the buffer used to read the file content
		this.initBuffer();
	}

	/**
	 * Closes the file.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (this.file_reader != null) {
			this.file_reader.close();
			this.file_reader = null;
		}
	}

	/**
	 * Returns if the end of the read file was reached.
	 * 
	 * @return TRUE if the end of the file was reached, FALSE if not.
	 */
	public boolean isEndOfFile() {
		return (this.next_token_line < 0);
	}

	/**
	 * Returns if the end of a line was reached, when reading the file.
	 * 
	 * @return TRUE if the end of the current line was reached, FALSE if not.
	 */
	public boolean isEndOfLine() {
		return (this.next_token_line != this.first_line);
	}

	/**
	 * Reads an entire line from the file, as a string.
	 * 
	 * @return The next line of the file.
	 * @throws IOException
	 */
	public String readLine() throws IOException {
		if (this.lines_count <= 0)
			return null;

		String line = this.buffer[this.first_line];
		if (this.next_char > 0)
			if (this.next_char >= line.length())
				line = "";
			else
				line = line.substring(this.next_char, line.length() - 1);

		this.buffer[this.first_line] = null;
		this.next_char = 0;
		this.first_line++;
		this.lines_count--;

		if (this.next_token_line >= 0 && this.next_token_line < this.first_line)
			this.findNext();

		return line;
	}

	/**
	 * Reads the next char of the file, including ' ' and \n.
	 * 
	 * @return The next char of the file, or '\0', if its end was reached.
	 * @throws IOException
	 */
	public char readChar() throws IOException {
		if (this.lines_count <= 0)
			return '\0';

		char new_char;
		String line = this.buffer[this.first_line];

		if (this.next_char >= line.length()) {
			new_char = '\n';
			this.readLine();
		} else {
			new_char = line.charAt(this.next_char++);

			if (new_char != ' ' && this.next_token_line >= 0)
				this.findNext();
		}

		return new_char;
	}

	/**
	 * Reads a string from the file.
	 * 
	 * @return The next string from the file.
	 * @throws IOException
	 */
	public String readString() throws IOException {
		String next_string = null;
		String line = this.buffer[this.next_token_line];

		for (int i = this.first_line; i < this.lines_count; i++)
			this.buffer[i] = null;

		this.buffer[0] = line;
		this.next_token_line = 0;
		this.first_line = 0;
		this.lines_count = 1;

		int i = 0;
		int size = line.length();
		for (i = this.next_token_col; i < size; i++)
			if (line.charAt(i) == ' ')
				break;

		next_string = line.substring(this.next_token_col, i);
		this.next_char = i;
		this.findNext();

		return next_string;
	}

	/**
	 * Reads an integer from the file.
	 * 
	 * @return The integer from the file.
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public int readInt() throws NumberFormatException, IOException {
		return Integer.valueOf(this.readString()).intValue();
	}

	/**
	 * Read a double value from the file.
	 * 
	 * @return The double value from the file.
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public double readDouble() throws NumberFormatException, IOException {
		return Double.valueOf(this.readString()).doubleValue();
	}

	/**
	 * Forces the file closing, when the garbage collector is called.
	 * 
	 * @throws IOException
	 */
	protected void finalize() throws IOException {
		this.close();
	}

	/**
	 * Initiates the buffer used to read the file content.
	 * 
	 * @throws IOException
	 */
	private void initBuffer() throws IOException {
		// the buffer reads 5 lines per time
		this.buffer = new String[5];

		// resets the controlling attributes of the reading task
		this.next_char = 0;
		this.next_token_line = 0;
		this.first_line = 0;
		this.lines_count = 0;

		// reads the first line, if possible, from the file to the buffer
		String line = this.file_reader.readLine();
		if (line == null)
			this.next_token_line = -1;
		else {
			this.buffer[0] = line;
			this.lines_count++;
			this.findNext();
		}
	}

	/**
	 * Finds the position of the next token to be read.
	 * 
	 * @throws IOException
	 */
	private void findNext() throws IOException {
		String line = this.buffer[this.first_line];

		if (line != null) {
			int size = line.length();
			for (int i = this.next_char; i < size; i++)
				if (line.charAt(i) != ' ') {
					this.next_token_col = i;
					return;
				}
		}

		this.next_token_line = -1;
		this.next_token_col = -1;

		while ((line = this.file_reader.readLine()) != null) {
			int size = line.length();
			for (int i = 0; i < size; i++)
				if (line.charAt(i) != ' ') {
					this.next_token_col = i;
					this.next_token_line = this.appendLine(line);
					return;
				}

			this.appendLine(line);
		}
	}

	/**
	 * Puts a line read from the file to the buffer.
	 * 
	 * @param string
	 *            The string obtained from the file to be read.
	 * @return The position of the read line.
	 */
	private int appendLine(String string) {
		if (this.lines_count == 0)
			this.first_line = 0;

		if (this.first_line + this.lines_count >= this.buffer.length) {
			String[] src = this.buffer;

			if (this.lines_count >= this.buffer.length)
				this.buffer = new String[2 * this.buffer.length];

			System.arraycopy(src, this.first_line, this.buffer, 0,
					this.lines_count);
			this.next_token_line -= this.first_line;
			this.first_line = 0;
		}

		buffer[this.first_line + this.lines_count] = string;
		this.lines_count++;

		return this.first_line + this.lines_count - 1;
	}
}