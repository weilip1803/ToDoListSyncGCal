package parser;

import command.Command;
import command.UndoCommand;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;
/**
 *  @@author A0121760R
 *
 */
public class PathParser extends ArgsParser{
	
	private String path;
	private static final String LOG_CREATE_PATH_PARSER = "PathParser Created for \"%s\"";;
	
	public PathParser(String userCommand){
		super(userCommand);
		assertNotNull(commandArgumentsString);
		this.path = commandArgumentsString;
		logger.log(Level.INFO, String.format(LOG_CREATE_PATH_PARSER ,
					commandArgumentsString));
	}
	
	public Command parse(){
		return new UndoCommand();	
	}
	
}
