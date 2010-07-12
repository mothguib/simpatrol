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
					+ dynamicityControllerRobot.getObject();
		} else if (robot instanceof MortalityControllerRobot) {
			MortalityControllerRobot mortalityControllerRobot = (MortalityControllerRobot) robot;
			message += "[SimPatrol.MortalityRobot("
					+ mortalityControllerRobot.getObject();
		} else if (robot instanceof StaminaControllerRobot) {
			StaminaControllerRobot staminaControllerRobot = (StaminaControllerRobot) robot;
			message += "[SimPatrol.StaminaRobot("
					+ staminaControllerRobot.getAgent().getObjectId();
		}
		message += ")]: Started working.";
		control.event.Logger.println(message);
	}

	/**
	 * Stops the controller robot
	 */
	pointcut stopRobot(Robot robot) :  
		execution (* Robot.stopActing()) &&
		this(robot);

	after(Robot robot) : stopRobot(robot) {
		String message = "";
		if (robot instanceof DynamicityControllerRobot) {
			DynamicityControllerRobot dynamicityControllerRobot = (DynamicityControllerRobot) robot;
			message += "[SimPatrol.DynamicityRobot("
					+ dynamicityControllerRobot.getObject();
		} else if (robot instanceof MortalityControllerRobot) {
			MortalityControllerRobot mortalityControllerRobot = (MortalityControllerRobot) robot;
			message += "[SimPatrol.MortalityRobot("
					+ mortalityControllerRobot.getObject();
		} else if (robot instanceof StaminaControllerRobot) {
			StaminaControllerRobot staminaControllerRobot = (StaminaControllerRobot) robot;
			message += "[SimPatrol.StaminaRobot("
					+ staminaControllerRobot.getAgent().getObjectId();
		}
		message += ")]: Stopped working.";
		control.event.Logger.println(message);
	}
}
