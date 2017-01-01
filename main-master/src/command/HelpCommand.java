package command;

import main.POMPOM;

/**
 * @@author A0121528M
 */
public class HelpCommand extends Command {
	
	/** Messaging **/
	private String MESSAGE_HELP = "Help dialog requested";
	
	/**
	 * Constructor for HelpCommand
	 */
	public HelpCommand() {
		
	}
	
	/**
	 * Carries out the action of showing the help dialog
	 */
	public String execute() {
		POMPOM.showHelp = true;
		String returnMsg = String.format(MESSAGE_HELP);
		return returnMsg;
	}
}
