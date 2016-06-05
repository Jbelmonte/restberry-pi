package org.company.techtest;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Application entry point.
 */
public class Main {
	public static final Log LOGGER = LogFactory.getLog(Main.class);

	/**
	 * Launch an {@link AppStarter} instance.
	 * 
	 * @param args
	 *            CLI arguments
	 * 
	 * @see {@link AppStarter#start()}
	 */
	public static void main(String[] args) {
		try {
			// Launch an app starter instance.
			AppStarter starter = new AppStarter();

			// Configure with CLI parameters
			configureWithArgs(starter, args);

			// Start
			starter.start();
		} catch (IOException e) {
			LOGGER.error("Error starting application", e);
			System.exit(-1);
		}
	}

	protected static void configureWithArgs(AppStarter starter, String[] args) {
		int port = AppStarter.DEFAULT_PORT;
		starter.setPort(port);
	}
}
