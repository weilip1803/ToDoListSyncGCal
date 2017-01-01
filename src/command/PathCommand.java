package command;

import java.io.IOException;
import java.util.logging.Level;

import main.POMPOM;

/**
 * @@author A0121528M
 */
public class PathCommand extends Command {
	
	/** Messaging **/
	private static final String MESSAGE_SET_PATH = "Storage path set to: %s";
	
	/** Command Parameter **/
	private String storageFilePath;
	
	/**
	 * Constructor for PathCommand object
	 * 
	 * @param storageFilePath
	 */
	public PathCommand(String storageFilePath) {
		this.storageFilePath = storageFilePath;
	}
	

	/**
	 * Carries out the action of changing storage path
	 */
	public String execute() {
		
		POMPOM.getStorage().setStorageFilePath(storageFilePath);
		
		// Saves settings and reinitializes storage
		try {
			POMPOM.getStorage().saveSettings();
			POMPOM.getStorage().init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.log(Level.INFO, "PathCommand has be executed");
		returnMsg = String.format(MESSAGE_SET_PATH, storageFilePath);
		return returnMsg;
	}
	
}
