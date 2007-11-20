/* MetricClient.java */

/* The package of this class. */
package metric_clients;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.file.FileWriter;
import util.net.UDPClientConnection;

/**
 * Implements a client object that collects an specific metric and saves it in a
 * determined file.
 */
public class MetricFileClient extends Thread {
	/* Attributes. */
	/** Registers if the metric client shall stop working. */
	private boolean stop_working;

	/** The UDP connection of the metric. */
	private UDPClientConnection connection;

	/**
	 * The object that writes on the output file the obtained values for the
	 * metric.
	 */
	private FileWriter file_writer;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param remote_socket_address
	 *            The IP address of the SimPatrol server.
	 * @param remote_socket_number
	 *            The number of the socket that the server listens to, related
	 *            to this metric.
	 * @param file_path
	 *            The path of the file where the metric values will be saved.
	 * @param metric_name
	 *            The name of the collected metric.
	 * @throws IOException
	 */
	public MetricFileClient(String remote_socket_address,
			int remote_socket_number, String file_path, String metric_name)
			throws IOException {
		this.stop_working = false;
		this.connection = new UDPClientConnection(remote_socket_address,
				remote_socket_number);
		this.file_writer = new FileWriter(file_path);
		this.file_writer.println(metric_name);
	}

	/** Indicates that the agent must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}

	public void run() {
		// starts its connection
		this.connection.start();

		while (!this.stop_working) {
			String[] metrics = this.connection.getBufferAndFlush();

			for (int i = 0; i < metrics.length; i++) {
				int value_index = metrics[i].indexOf("value=\"");
				metrics[i] = metrics[i].substring(value_index + 7);
				double metric_value = Double.parseDouble(metrics[i].substring(
						0, metrics[i].indexOf("\"")));
				this.file_writer.println(metric_value, 5);
			}
		}

		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.file_writer.close();
	}
}