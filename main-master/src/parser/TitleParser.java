package parser;

import org.ocpsoft.prettytime.shade.com.joestelmach.natty.generated.DateParser.parse_return;

import command.Command;
import command.DelCommand;
import command.InvalidCommand;
import command.TitleCommand;

/**
 * @@author A0121760R
 *
 */
public class TitleParser extends ArgsParser{
	Long id;
	public TitleParser(String commandArguments){
		super(commandArguments);
		
		
	}
	public Command parse(){
		try{
			id = Long.parseLong(this.commandArgumentsString);
		}catch (Exception e){
			return new InvalidCommand(DelCommand.MESSAGE_ID_INVALID);
		}
		return new TitleCommand(id);
	}
}
