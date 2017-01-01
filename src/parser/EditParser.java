package parser;

import java.util.Date;
import java.util.logging.Level;

import command.Command;
import command.EditCommand;
import command.InvalidCommand;
import main.POMPOM;
import static org.junit.Assert.assertNotNull;

/**
 *  @@author A0121760R
 *
 */
public class EditParser extends ArgsParser{
	
	private static final int INDEX_REST_OF_THE_STRING = 1;
	private static final int INDEX_TASK_ID = 0;
	private static final String MESSAGE_NO_ARGUMENTS = "No Arguments";
	private static final String MESSAGE_PROPER_PRIORITY = "Priority only can be set to high/hi/h, medium/med/m or low/lo/l!";
	private static final String MESSAGE_INVALID_DATE_ERROR = "Please enter a vaild date";
	private static final String MESSAGE_INVALID_ARGUMENT_ERROR = "Please enter a vaild argument";
	public static final String MESSAGE_ID_ERROR = "Id must be a number";
	public static final String MESSAGE_TITLE_EMPTY_ERROR = "Title cannot be empty!";
	public static final String MESSAGE_FIELD_ERROR = "There is no such field!";
	private static final String STRING_EMPTY = "";
	private static final int VALID_NUMBER_OF_ARGUMENTS = 2;
	private static final String STRING_SPACE = " ";
	private static final String FIELD_PRIORITY = "priority";
	private static final String FIELD_TITLE = "title";	
	private static final String FIELD_LABEL = "label";
	private static final String FIELD_START_DATE = "start date";
	private static final String FIELD_END_DATE = "end date";
	
	private static final String LOG_CREATE_EDIT_PARSER = "EditParser Created for \"%s\"";

	private static final String[] FIELD_ARRAY = {FIELD_PRIORITY, FIELD_TITLE, 
												 FIELD_LABEL, 
												 FIELD_START_DATE, FIELD_END_DATE};
	
	private long taskID;
	private String field;
	private String newData;
	private String dataErrorMsg;
	private boolean correctId;
	private boolean correctField;
	private boolean correctData;
	private Date newDate = null;
	
	public EditParser(String userCommand){
		super(userCommand);
		assertNotNull(commandArgumentsString);
		correctId = extractTaskID();
		correctField = extractFields();
		correctData = extractNewData();
		
		logger.log(Level.INFO, String.format(LOG_CREATE_EDIT_PARSER ,
												commandArgumentsString));
	}
	
	public Command parse(){
		
		Command invalidCommand = validateFields();
		if (hasInvalidCommand(invalidCommand)){
			return invalidCommand;
		}
		
		//editing date field
		if (isNotEmptyDate()){
			return new EditCommand(taskID, field, newDate); 
		} else{
			return new EditCommand(taskID, field, newData);
		}
	}
 
	/**
	 * This method gets the ask ID from the command the user has entered.
	 * 
	 * @return
	 * 		true if task ID was sucessfully set. False if not.
	 */			
	public boolean extractTaskID(){
		
		//no arguments was given!
		if(hasNoArguments){
			return false;
		}
		
		//Splits the argument into 2. One part has the task ID. The other has the rest.
		String[] arguments = commandArgumentsString.split(STRING_SPACE,VALID_NUMBER_OF_ARGUMENTS);
		if (isInvalidArgumentsLength(arguments)){
			return false;
		}
		String taskIDString = arguments[INDEX_TASK_ID];				
		commandArgumentsString = arguments[INDEX_REST_OF_THE_STRING];
		
		return canSetTaskID(taskIDString);
	}
	
	/**
	 * This method extracts all the fields inside the command passed by the user.
	 * 
	 * @return
	 */
	public boolean extractFields(){
		if(isInvalidFields()){
			return false;
		}
		for (String currField: FIELD_ARRAY){
			//match currFied (the one inside the field array with the first available field.
			if (isFirstField(currField)){
				field=currField;
				commandArgumentsString = commandArgumentsString.substring(field.length()).trim();
				return true;
			}
		}
		//no fields are matched! means all the fields not valid.
		return false;
	}
	
	/**
	 * This method extracts the data related to the field. Also does validation.
	 * 
	 * @return
	 * 		the appropriate data object for the field
	 */
	public boolean extractNewData(){
		newData = commandArgumentsString;
		if (isNullField()){
			dataErrorMsg = MESSAGE_INVALID_ARGUMENT_ERROR;
			return false;
		}
		
		if (isEmptyTitleField()){
			dataErrorMsg = MESSAGE_TITLE_EMPTY_ERROR;
			return false;
		}
		
		//Process Date
		if (isValidDateField() ){
			return processDateField();
		}
		
		//Process Priority
		if(isProcessingPriorityField()){
			if (field != null) {
				return processPriorityField();
			} 
			else {
				dataErrorMsg = MESSAGE_PROPER_PRIORITY;
				return false;
			}
		}
		if(hasNoArguments){
			dataErrorMsg = MESSAGE_NO_ARGUMENTS;
			return false;
		}
		return true;
	}

	private boolean isProcessingPriorityField() {
		return field == FIELD_PRIORITY;
	}

	private boolean isEmptyTitleField() {
		return field.equals("title") && newData.equals("");
	}

	private boolean isNullField() {
		return field == null;
	}
	/**
	 * This method checks if the data is a valid priority data, then returns
	 * the appropriate priority data than can be used by the command.
	 * 
	 * @return 
	 * 		true if priority is sucessfully set. false if it is not.
	 */
	private boolean processPriorityField() {
		if (isValidHighPriorityCommand(newData)) {
			newData = POMPOM.PRIORITY_HIGH;
			return true;
		} else if (isValidMediumPriorityCommand(newData)){
			newData = POMPOM.PRIORITY_MED;
			return true;
		} else if (isValidLowPriorityCommand(newData)){
			newData = POMPOM.PRIORITY_LOW;
			return true;
		} else {
			dataErrorMsg = MESSAGE_PROPER_PRIORITY;
			return false;
		}
	}
	
	/**
	 * This method checks if the data is a valid date data, then returns
	 * the appropriate date data than can be used by the command.
	 * 
	 * @return 
	 * 		true if priority is sucessfully set. false if it is not.
	 */
	private boolean processDateField() {
		newDate = getDate(newData); 
		if(isNotEmptyDate()){
			return true;
		}else{
			dataErrorMsg = MESSAGE_INVALID_DATE_ERROR;
			return false;
		}
	}
	
	/**
	 * This method attempts to see if the ID is a valid long or not. If it is,
	 * the TaskID field is set. Else, the invalidCommand field will be set.
	 * 
	 * @return 
	 * 		true if can successfully set Task ID. False if cannot.
	 */
	private boolean canSetTaskID(String taskIDString) {
		try{
			taskID = Long.parseLong(taskIDString);
		} catch (Exception e){
			return false;
		}
		return true;
	}
	
	/**
	 * This method checks for all error flags. If any of them is raised, 
	 * an invalidCommand is returned. 
	 * @return
	 * 		invalidCommand with error msg if got raised error flags. false if dont have.
	 */
	private Command validateFields() {
		if(!correctId){
			return new InvalidCommand(MESSAGE_ID_ERROR);
		} else if(!correctField){
			return new InvalidCommand(String.format(MESSAGE_FIELD_ERROR));
		} else if(!correctData){
			return new InvalidCommand(dataErrorMsg);
		} else{
			return null;
		}
	}

	private Date getDate(String data) {
		return POMPOM.timeParser.parseSyntax(data).get(0).getDates().get(0);
	}
	
	public String getField(){
		return field;
	}
	
	public String getNewData(){
		return newData;
	}
	
	private boolean isInvalidArgumentsLength(String[] arguments) {
		return arguments.length < VALID_NUMBER_OF_ARGUMENTS;
	}
	
	private boolean isFirstField(String currField) {
		return commandArgumentsString.indexOf(currField) == 0;
	}

	private boolean isInvalidFields() {
		return commandArgumentsString == null || commandArgumentsString.equals(STRING_EMPTY);
	}
	
	private boolean isValidHighPriorityCommand(String priority){
		//Appropriate commands are: "high", "hi", "h"
		return priority.equalsIgnoreCase(AddParser.PRIORITY_HIGH_CMD1) 
				|| priority.equalsIgnoreCase(AddParser.PRIORITY_HIGH_CMD2) 
				|| priority.equalsIgnoreCase(AddParser.PRIORITY_HIGH_CMD3);
	}
	
	private boolean isValidMediumPriorityCommand(String priority){
		//Appropriate commands are: "medium", "med", "m"
		return priority.equalsIgnoreCase(AddParser.PRIORITY_MEDIUM_CMD1) 
				|| priority.equalsIgnoreCase(AddParser.PRIORITY_MEDIUM_CMD2) 
				|| priority.equalsIgnoreCase(AddParser.PRIORITY_MEDIUM_CMD3);
	}
	
	private boolean isValidLowPriorityCommand(String priority){
		//Appropriate commands are: "low", "lo", "l"
		return priority.equalsIgnoreCase(AddParser.PRIORITY_LOW_CMD1) 
				|| priority.equalsIgnoreCase(AddParser.PRIORITY_LOW_CMD2) 
				|| priority.equalsIgnoreCase(AddParser.PRIORITY_LOW_CMD3);
	}
	
	private boolean hasInvalidCommand(Command invalidCommand) {
		return invalidCommand!=null;
	}
	
	private boolean isNotEmptyDate() {
		return newDate != null;
	}

	private boolean isValidDateField() {
		return field.equals(FIELD_START_DATE) || field.equals(FIELD_END_DATE);
	}
	
}
