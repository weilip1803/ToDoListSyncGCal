package command;
/**
 * @@author wen hao
 *
 */
import java.util.ArrayList;

public class MultiDelCommand extends Command {

	private static final String MESSAGE_MULTIDEL = "%s tasks has been deleted.";
	
	ArrayList<DelCommand> deleteList;
	
	public MultiDelCommand(ArrayList<DelCommand> deleteList) {
		this.deleteList = deleteList;
	}
	
	public String execute() {
		
		for (int i = 0; i < deleteList.size(); i++) {
			deleteList.get(i).execute();
		}
		
		returnMsg = String.format(MESSAGE_MULTIDEL, deleteList.size());
		return returnMsg;
	}
	
}