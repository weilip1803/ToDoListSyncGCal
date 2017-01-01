package parser;

import command.Command;
import command.DelCommand;
import command.InvalidCommand;
import command.SearchCommand;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;

/**
 *  @@author A0121760R
 */
public class SearchParser extends ArgsParser{
	
	private static final String STRING_EMPTY = "";
	private static final String MESSAGE_NO_ARGUMENTS_ERROR = "Search must have arguments";
	private static final String LOG_CREATE_SEARCH_PARSER = "SearchParser Created for \"%s\"";;
	private String keyWord;
	
	public SearchParser(String commandArguments) {
		super(commandArguments);
		assertNotNull(commandArgumentsString);
		keyWord = commandArguments;
		logger.log(Level.INFO, String.format(LOG_CREATE_SEARCH_PARSER ,
				commandArgumentsString));
		
	}
	
	public Command parse(){
			if(isInvalidKeyword()){
				return new InvalidCommand(MESSAGE_NO_ARGUMENTS_ERROR);
			}
			return new SearchCommand(keyWord);
	}
	
	private boolean isInvalidKeyword() {
		return keyWord == null || keyWord.equals(STRING_EMPTY);
	}

}
