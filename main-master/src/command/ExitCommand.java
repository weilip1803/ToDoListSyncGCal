package command;

import java.io.IOException;

import main.POMPOM;

/**
 * @@author A0121528M
 */
public class ExitCommand extends Command{
	
	/**
	 * Constructor for ExitCommand object.
	 * It saves the storage when initialized.
	 */
	public ExitCommand() {
		try {
			POMPOM.getStorage().saveStorage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Carries out the action of exiting
	 */
	public String execute() {
		System.exit(0);
		return returnMsg;
	}
}
