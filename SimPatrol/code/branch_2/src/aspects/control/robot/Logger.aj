package control.robot;

/**
 * This aspect is responsible for logging data of all robot classes
 */
public aspect Logger {

	/**
	 * Starts the controller robot
	 */
	pointcut startRobot(Robot robot) :  
		execution (* Robot.start()) &&
		this(robot);

	after(Robot robot) : startRobot(robot) {
		String message = "";
		if (robot instanceof DynamicityControllerRobot) {
			DynamicityControllerRobot dynamicityControllerRobot = (DynamicityControllerRobot) robot;
			message += "[SimPatrol.DynamicityRobot("
					+ dynamicityControllerRobot.getOBJECT();
		} else if (robot instanceof MortalityControllerRobot) {
			MortalityControllerRobot mortalityControllerRobot = (MortalityControllerRobot) robot;
			message += "[SimPatrol.MortalityRobot("
					+ mortalityControllerRobot.getOBJECT();
		} else if (robot instanceof StaminaControllerRobot) {
			StaminaControllerRobot staminaControllerRobot = (StaminaControllerRobot) robot;
			message += "[SimPatrol.StaminaRobot("
					+ staminaControllerRobot.getAGENT().getObjectId();
		}
		message += ")]: Started working.";
		logger.Logger.getInstance().log(message);
	}

	/**
	 * Stops the controller robot
	 */
	pointcut stopRobot(Robot robot) :  
		execution (* Robot.stopWorking()) &&
		this(robot);

	after(Robot robot) : stopRobot(robot) {
		String message = "";
		if (robot instanceof DynamicityControllerRobot) {
			DynamicityControllerRobot dynamicityControllerRobot = (DynamicityControllerRobot) robot;
			message += "[SimPatrol.DynamicityRobot("
					+ dynamicityControllerRobot.getOBJECT();
		} else if (robot instanceof MortalityControllerRobot) {
			MortalityControllerRobot mortalityControllerRobot = (MortalityControllerRobot) robot;
			message += "[SimPatrol.MortalityRobot("
					+ mortalityControllerRobot.getOBJECT();
		} else if (robot instanceof StaminaControllerRobot) {
			StaminaControllerRobot staminaControllerRobot = (StaminaControllerRobot) robot;
			message += "[SimPatrol.StaminaRobot("
					+ staminaControllerRobot.getAGENT().getObjectId();
		}
		message += ")]: Stopped working.";
		logger.Logger.getInstance().log(message);
	}
}
