package parser;

import command.Command;
import command.EditCommand;
import main.POMPOM;
import java.util.logging.Level;
/**
 *  @@author Josh
 *
 */

public class DoneParser extends ArgsParser{
	
	private int itemID;
	private Command outputCommand = null;
	
	public DoneParser(String userCommand){
		super(userCommand);
		getItemId();
	}
	
	public Command executeCommand(){
		if (outputCommand == null){
			return new EditCommand(itemID, "status", POMPOM.STATUS_COMPLETED);
		} else{
			return outputCommand;
		}
		
	}
	
	public void getItemId(){
		try{
			itemID = Integer.parseInt(commandArgumentsString);
		} catch (Exception e){
			InvalidParser InvalidArgumentParser = new InvalidParser(commandArgumentsString);
			outputCommand = InvalidArgumentParser.executeCommand();
			logger.log(Level.INFO,"tried to parse '"+commandArgumentsString+"' into integer but failed.");
		}
	}	
}
