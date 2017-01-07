package parser;

import java.util.ArrayList;
import java.util.Date;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Arrays;

import command.Command;
import command.EditCommand;
import main.POMPOM;
/**
 *  @@author Josh
 *
 */
public class EditParser extends ArgsParser{
	
	private final int INDEX_TASK_ID = 0;
	private final int INDEX_FIELDS = 1;
	private final int INDEX_NEW_DATAS = 2; 
	
	private static final String FIELD_PRIORITY = "priority";
	private static final String FIELD_TITLE = "title";	
	private static final String FIELD_STATUS = "status";
	private static final String FIELD_LABEL = "label";
	private static final String FIELD_START_DATE = "start date";
	private static final String FIELD_END_DATE = "end date";

	private static final String[] FIELD_ARRAY = {FIELD_PRIORITY, FIELD_TITLE, 
												 FIELD_STATUS, FIELD_LABEL, 
												 FIELD_START_DATE, FIELD_END_DATE};
	
	private int taskID;
	private String field;
	private String newData;
	private Date newDate = null;
	private String taskTitle=null;
	
	private ArrayList<String> argsArray;
	
	
	public EditParser(String userCommand){
		super(userCommand);
		
		extractTaskID();
		extractFields();
		extractNewData();
	}
	
	public Command parse(){
		System.out.println(newData);
		System.out.println(newDate);
		if (isEditingDateField()){
			return new EditCommand(taskID, field, newDate); 
		} else{
			return new EditCommand(taskID, field, newData);
		}
	}

	private boolean isEditingDateField() {
		return (newDate!=null);
	}
	
	public void extractTaskID(){
		String taskIDString = commandArgumentsString.split(" ",2)[0];
		commandArgumentsString = commandArgumentsString.split(" ",2)[1];
		try{
			taskID = Integer.parseInt(taskIDString);
		} catch (Exception e){
			taskTitle = taskIDString;
		}
	}
	
	public void extractFields(){
		
		for (String currField: FIELD_ARRAY){
			if (commandArgumentsString.indexOf(currField)==0){
				field=currField;
				commandArgumentsString = commandArgumentsString.replace(field, "").trim();
			}
		}
	}
	
	public void extractNewData(){
		newData = commandArgumentsString;
		if (field.equals("start date") || field.equals("end date") ){
			newDate = getDate(newData);
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
	
}
