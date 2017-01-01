package parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.DelCommand;
import command.ExitCommand;
import command.UndoCommand;
/**
 *  @@author A0121760R
 *
 */

public class UndoParser {	
	
	protected static Logger logger = Logger.getLogger("Parser");
	private static final String LOG_CREATE_UNDO_PARSER = "UndoParser Created";
	
	public UndoParser(){
		logger.log(Level.INFO, LOG_CREATE_UNDO_PARSER);
	}
	
	public Command parse(){
		return new UndoCommand();
	}
}
