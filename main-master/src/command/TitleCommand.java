package command;

import utils.Item;

/**
 * Returns the title of the task given id
 * @author A0121628L
 *
 */
public class TitleCommand extends Command {
	Long id;
	public TitleCommand(Long id){
		this.id = id;
	}
	@Override 
	public String execute() {
		Item item = getTask(id);
		if(item == null){
			return String.format(DelCommand.MESSAGE_ID_INVALID, id.toString());
		}
		return item.getTitle();
	}

}
