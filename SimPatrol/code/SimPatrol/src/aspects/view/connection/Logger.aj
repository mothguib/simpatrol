package view.connection;

import util.net.ServerSideTCPSocket;
import view.connection.ServerSideTCPConnection;

/**
 * Logs view.connection
 */
public aspect Logger {

	/**
	 * ServerSideTCPConnection.start()
	 */
	pointcut startServerSideTCPConnection(ServerSideTCPConnection connection) : 
		execution(* ServerSideTCPConnection.start(..)) && this(connection);

	after(ServerSideTCPConnection connection) : startServerSideTCPConnection(connection) {
		control.event.Logger.println("[SimPatrol.TCPConnection("
				+ connection.getName() + ")]: Started listening to messages.");
		control.event.Logger.println("[SimPatrol.TCPConnection("
				+ connection.getName() + ")]: Waiting for new connection. ");
	}

	/**
	 * ServerSideTCPConnection.stopWorking()
	 */
	pointcut stopServerSideTCPConnection(ServerSideTCPConnection connection) :
		execution(* ServerSideTCPConnection.stopActing(..)) && this(connection);

	before(ServerSideTCPConnection connection) : stopServerSideTCPConnection(connection) {
		control.event.Logger.println("[SimPatrol.TCPConnection("
				+ connection.getName() + ")]: Stopped listening to messages. ");
	}

	/**
	 * ServerSideTCPConnection.run()
	 */
	pointcut runServerSideTCPConnection(ServerSideTCPConnection connection) : 
		call(* ServerSideTCPSocket.connect(..)) &&
		this(connection) &&
		withincode(* ServerSideTCPConnection.run(..));

	after(ServerSideTCPConnection connection) : runServerSideTCPConnection(connection) {
		if (connection.isConnected())
			control.event.Logger.println("[SimPatrol.TCPConnection("
					+ connection.getName() + ")]: Client connected.");
	}

	/**
	 * Logs ServerSideTCPConnection.run()
	 */
	pointcut runServerSideTCPConnection1(ServerSideTCPConnection connection) :
		call(* ServerSideTCPConnection.reset(..)) &&
		this(connection) &&
		withincode(* ServerSideTCPConnection.run(..));

	after(ServerSideTCPConnection connection) : runServerSideTCPConnection1(connection) {
		control.event.Logger.println("[SimPatrol.TCPConnection("
				+ connection.getName() + ")]: Client disconnected.");
		control.event.Logger.println("[SimPatrol.TCPConnection("
				+ connection.getName() + ")]: Waiting for new connection. ");
	}

	/**
	 * Logs ServerSideTCPConnection.run()
	 */
	pointcut runServerSideTCPConnection2(ServerSideTCPConnection connection) :
		call(* ServerSideTCPSocket.disconnect(..)) &&
		this(connection) &&
		withincode(* ServerSideTCPConnection.run(..));

	before(ServerSideTCPConnection connection) : runServerSideTCPConnection2(connection) {
		if (connection.isConnected())
			control.event.Logger.println("[SimPatrol.TCPConnection("
					+ connection.getName() + ")]: Server disconnected client.");
	}

	/**
	 * UDPConnection.start()
	 */
	pointcut startUDPConnection(UDPConnection connection) : 
		execution(* UDPConnection.start(..)) && this(connection);

	after(UDPConnection connection) : startUDPConnection(connection) {
		control.event.Logger.println("[SimPatrol.UDPConnection("
				+ connection.getName() + ")]: Started listening to messages.");
	}

	/**
	 * UDPConnection.stop()
	 */
	pointcut stopUDPConnection(UDPConnection connection) : 
		execution(* UDPConnection.stopActing(..)) && this(connection);

	after(UDPConnection connection) : stopUDPConnection(connection) {
		control.event.Logger.println("[SimPatrol.UDPConnection("
				+ connection.getName() + ")]: Stopped listening to messages.");
	}
}
