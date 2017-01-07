package command;

import main.POMPOM;

public class ViewCommand extends Command {

	private static final String MESSAGE_VIEW = "%s tab has been selected for viewing.";
	private static final String MESSAGE_ERROR = "%s is not a valid tab.";
	private String tab;

	public ViewCommand(String tab) {
		this.tab = tab;
	} 
	
	public String execute() {
		
		if (tab.equalsIgnoreCase(POMPOM.LABEL_COMPLETED_EVENT)) {
			tab = POMPOM.LABEL_COMPLETED_EVENT;
			POMPOM.setCurrentTab(tab);
			returnMsg = String.format(MESSAGE_VIEW, tab);
			return returnMsg;
		} else if (tab.equalsIgnoreCase(POMPOM.LABEL_COMPLETED_TASK)) {
			tab = POMPOM.LABEL_COMPLETED_TASK;
			POMPOM.setCurrentTab(tab);
			returnMsg = String.format(MESSAGE_VIEW, tab);
			return returnMsg;
		} else if (tab.equalsIgnoreCase(POMPOM.LABEL_EVENT)) {
			tab = POMPOM.LABEL_EVENT;
			POMPOM.setCurrentTab(tab);
			returnMsg = String.format(MESSAGE_VIEW, tab);
			return returnMsg;
		} else if (tab.equalsIgnoreCase(POMPOM.LABEL_TASK)) {
			tab = POMPOM.LABEL_TASK;
			POMPOM.setCurrentTab(tab);
			returnMsg = String.format(MESSAGE_VIEW, tab);
			return returnMsg;
		} else if (tab.equalsIgnoreCase(POMPOM.LABEL_SEARCH)) {
			tab = POMPOM.LABEL_SEARCH;
			POMPOM.setCurrentTab(tab);
			returnMsg = String.format(MESSAGE_VIEW, tab);
			return returnMsg;
		} else {
			returnMsg = String.format(MESSAGE_ERROR, tab);
			return returnMsg;
		}

	}

}
