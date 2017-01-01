package parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.HelpCommand;
/**
 *  @@author A0121760R
 *
 */
public class HelpParser{
	
	protected static Logger logger = Logger.getLogger("Parser");
	private static final String LOG_CREATE_HELP_PARSER = "HelpParser Created";
	
	public HelpParser(){
		logger.log(Level.INFO, LOG_CREATE_HELP_PARSER);
	}
	
	public Command parse(){
		return new HelpCommand(); 
	}
}
