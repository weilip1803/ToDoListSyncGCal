package parser;

import command.Command;
import command.DelCommand;
import command.ExitCommand;
import command.UndoCommand;
/**
 *  @@author Josh
 *
 */

public class UndoParser {


	
	public Command parse(){
		return new UndoCommand();
		
	}
	

	
}
