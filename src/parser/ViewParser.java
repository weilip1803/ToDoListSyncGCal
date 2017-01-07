package parser;

import command.Command;
import command.ViewCommand;
/**
 *  @@author Josh
 *
 */
public class ViewParser extends ArgsParser{
	
	private String view;
	
	public ViewParser(String commandArgument) {
		super(commandArgument);
		view = commandArgument;
	}
	public Command parse(){
		
			return new ViewCommand(view);
		
		
	}
	

}