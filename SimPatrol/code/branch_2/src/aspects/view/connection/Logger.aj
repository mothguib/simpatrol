package view.connection;

import util.net.ServerSideTCPSocket;

;

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
		logger.Logger.getInstance().log(
				"[SimPatrol.TCPConnection(" + connection.getName()
						+ ")]: Started listening to messages.");
	}

	/**
	 * ServerSideTCPConnection.stopWorking()
	 */
	pointcut stopServerSideTCPConnection(ServerSideTCPConnection connection) : 
		call(* ServerSideTCPSocket.disconnect(..)) &&
		this(connection) &&
		withincode(* ServerSideTCPConnection.stopWorking(..));

	after(ServerSideTCPConnection connection) : stopServerSideTCPConnection(connection) {
		logger.Logger.getInstance().log(
				"[SimPatrol.TCPConnection(" + connection.getName()
						+ ")]: Server disconnected client.");
		logger.Logger.getInstance().log(
				"[SimPatrol.TCPConnection(" + connection.getName()
						+ ")]: Stopped listening to messages. ");
	}

	/**
	 * ServerSideTCPConnection.run()
	 */
	pointcut runServerSideTCPConnection(ServerSideTCPConnection connection) : 
		call(* ServerSideTCPSocket.connect(..)) && 
		this(connection) &&
		withincode(* ServerSideTCPConnection.run(..));

	after(ServerSideTCPConnection connection) : runServerSideTCPConnection(connection) {
		logger.Logger.getInstance().log(
				"[SimPatrol.TCPConnection(" + connection.getName()
						+ ")]: Client connected.");
	}

	/**
	 * Logs ServerSideTCPConnection.run()
	 */
	pointcut runServerSideTCPConnection1(ServerSideTCPConnection connection) :
		call(* ServerSideTCPSocket.disconnect(..)) &&
		this(connection) &&
		withincode(* ServerSideTCPConnection.run(..));

	after(ServerSideTCPConnection connection) : runServerSideTCPConnection1(connection) {
		logger.Logger.getInstance().log(
				"[SimPatrol.TCPConnection(" + connection.getName()
						+ ")]: Client disconnected.");
		logger.Logger.getInstance().log(
				"[SimPatrol.TCPConnection(" + connection.getName()
						+ ")]: Waiting for new connections. ");
	}

	/**
	 * UDPConnection.start()
	 */
	pointcut startUDPConnection(UDPConnection connection) : 
		execution(* UDPConnection.start(..)) && this(connection);

	after(UDPConnection connection) : startUDPConnection(connection) {
		logger.Logger.getInstance().log(
				"[SimPatrol.UDPConnection(" + connection.getName()
						+ ")]: Started listening to messages.");
	}

	/**
	 * UDPConnection.stop()
	 */
	pointcut stopUDPConnection(UDPConnection connection) : 
		execution(* UDPConnection.stopWorking(..)) && this(connection);

	after(UDPConnection connection) : stopUDPConnection(connection) {
		logger.Logger.getInstance().log(
				"[SimPatrol.UDPConnection(" + connection.getName()
						+ ")]: Stopped listening to messages.");
	}
}
