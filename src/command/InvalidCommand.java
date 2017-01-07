package command;
/**
 * @@author wen hao
 *
 */
public class InvalidCommand extends Command{

	
	private String error;
	
	public InvalidCommand(String error) {
		this.error = error;
	}
	
	public String execute() {
		return error;
	}

	
	
}
