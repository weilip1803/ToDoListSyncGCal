package command;

/**
 * @@author A0121528M
 */
public class InvalidCommand extends Command {

	/** Command Parameter **/
	private String error;

	/**
	 * Constructor for InvalidCommand object
	 * 
	 * @param error
	 */
	public InvalidCommand(String error) {
		this.error = error;
	}

	/**
	 * Does nothing and only return the error. 
	 * Error message should already be formatted before reaching this point.
	 */
	public String execute() {
		return error;
	}

}
