package command;

import main.POMPOM;

public class HelpCommand extends Command {
	private String MESSAGE_HELP = "Help initialize";
	
	public HelpCommand() {
		
	}
	
	public String execute() {
		POMPOM.showHelp = true;
		String returnMsg = String.format(MESSAGE_HELP);
		return returnMsg;
	}
}
