package parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.ExitCommand;
/**
 *  @@author A0121760R
 *
 */
public class ExitParser{
	
	protected static Logger logger = Logger.getLogger("Parser");
	private static final String LOG_CREATE_EXIT_PARSER = "ExitParser Created";
	
	public Command parse(){
		logger.log(Level.INFO, LOG_CREATE_EXIT_PARSER);
		return new ExitCommand();
	}
}
