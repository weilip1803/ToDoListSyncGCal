# wen hao
###### command\Command.java
``` java
 *
 */
public abstract class Command {
	
	protected String returnMsg = "";
	
	public static Logger logger = Logger.getLogger("Command");
	
	public Command() {
		
	}
	
	protected Item getTask(long taskId) {
		ArrayList<Item> taskList = getTaskList(); 
		for (int i  = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getId() == taskId) {
				return taskList.get(i);
			}
		}
		return null;
	}
	
	protected ArrayList<Item> getTaskList() {
		return POMPOM.getStorage().getTaskList();		
	}
	
	protected boolean checkExists(long taskId) {
		boolean exists;
		try {
			Item toDelete = getTask(taskId);
			exists = true;
		} catch (IndexOutOfBoundsException e) {
			exists =  false;
		}
		return exists;
	}
	
	protected void showCorrectTab(Item item) {
		
		if (item.getType().toLowerCase().equals(POMPOM.LABEL_EVENT.toLowerCase())) {
			if (item.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
				POMPOM.setCurrentTab(POMPOM.LABEL_COMPLETED_EVENT);
			} else {
				POMPOM.setCurrentTab(POMPOM.LABEL_EVENT);
			}
		} else {
			if (item.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
				POMPOM.setCurrentTab(POMPOM.LABEL_COMPLETED_TASK);
			} else {
				POMPOM.setCurrentTab(POMPOM.LABEL_TASK);
			}
		}
		
	}
	
	public abstract String execute();
	
}
```
###### command\DelCommand.java
``` java
 *
 */
public class DelCommand extends Command {

	private static final String MESSAGE_TASK_DELETED_ID = "%1s has been deleted from %2s";
	private static final String MESSAGE_TASK_ERROR = "Unable to delete";

	private long taskId;
	private String taskTitle;
	private boolean isById;
	private boolean isUndo;
	private boolean canDelete;
	private Item toDelete;
	ArrayList<Item> taskList = getTaskList();

	public DelCommand(long taskId) {
		this.taskId = taskId;
		this.isById = true;
		this.isUndo = false;
		this.toDelete = getTask(taskId);

		logger.log(Level.INFO, "DelCommand with id initialized");
	}

	public DelCommand(String taskTitle) {
		this.taskTitle = taskTitle;
		this.isById = false;
		this.isUndo = false;
		this.toDelete = getTask(taskId);

		logger.log(Level.INFO, "DelCommand with title initialized");
	}

	public DelCommand(long taskId, boolean isUndo) {
		this.taskId = taskId;
		this.isUndo = isUndo;
		this.isById = true;

		logger.log(Level.INFO, "Counter action DelCommand initialized");
	}

	private void removeTask() {
		if (isById) {

			ArrayList<Item> taskList = getTaskList();
			for (int i = 0; i < taskList.size(); i++) {
				if (taskList.get(i).getId() == taskId) {
					taskList.remove(i);
				}
			}
			POMPOM.getStorage().setTaskList(taskList);

		} else {

			ArrayList<Item> taskList = getTaskList();
			for (int i = 0; i < taskList.size(); i++) {
				if (taskList.get(i).getTitle().toLowerCase().equals(taskTitle.toLowerCase())) {
					taskList.remove(i);
				}
			}
			POMPOM.getStorage().setTaskList(taskList);

		}
	}

	private Command createCounterAction() {
		AddCommand counterAction = new AddCommand(toDelete.getId(), toDelete.getType(), toDelete.getTitle(),
				toDelete.getDescription(), toDelete.getPriority(), toDelete.getStatus(), toDelete.getLabel(),
				toDelete.getStartDate(), toDelete.getEndDate(), toDelete.isRecurring(), toDelete.getPrevId(),
				toDelete.getNextId());
		return counterAction;
	}

	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}

	private void setProperPointers() {
		Item currentTask = getTask(toDelete.getId());

		if (!(currentTask.getPrevId() == null)) {
			Item prevTask = getTask(currentTask.getPrevId());
			prevTask.setNextId(currentTask.getNextId());
		}

		if (!(currentTask.getNextId() < currentTask.getId())) {
			Item nextTask = getTask(currentTask.getNextId());
			nextTask.setPrevId(currentTask.getPrevId());
		}

	}

	private void setProperReturnMessage() {

		if (canDelete) {

			if (toDelete.getType().toLowerCase().equals(POMPOM.LABEL_EVENT.toLowerCase())) {
				if (toDelete.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
					returnMsg = String.format(MESSAGE_TASK_DELETED_ID, taskId, POMPOM.LABEL_COMPLETED_EVENT);
				} else {
					returnMsg = String.format(MESSAGE_TASK_DELETED_ID, taskId, POMPOM.LABEL_EVENT);
				}
			} else {
				if (toDelete.getStatus().equals(POMPOM.STATUS_COMPLETED)) {
					returnMsg = String.format(MESSAGE_TASK_DELETED_ID, taskId, POMPOM.LABEL_COMPLETED_TASK);
				} else {
					returnMsg = String.format(MESSAGE_TASK_DELETED_ID, taskId, POMPOM.LABEL_TASK);
				}
			}

		} else {
			returnMsg = MESSAGE_TASK_ERROR;
		}

	}

	public String execute() {

		canDelete = checkExists(taskId);

		toDelete = getTask(taskId);

		if (toDelete.isRecurring()) {
			setProperPointers();
		}

		if (canDelete) {

			if (!isUndo) {
				updateUndoStack();
			}

			removeTask();
			logger.log(Level.INFO, "DelCommand by Id has be executed");

		}

		POMPOM.refreshStatus();
		showCorrectTab(toDelete);
		setProperReturnMessage();
		return returnMsg;
	}

}
```
###### command\EditCommand.java
``` java
 *
 */
public class EditCommand extends Command {
	
	private static final String MESSAGE_TASK_EDITED = "%s. was successfully edited";	
	private static final String MESSAGE_TASK_ERROR = "Unable to edit task %s";
	
	private static final String FIELD_TYPE = "type";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_DESCRIPTION = "description";
	private static final String FIELD_PRIORITY = "priority";
	private static final String FIELD_STATUS = "status";
	private static final String FIELD_LABEL = "label";
	private static final String FIELD_START_DATE = "start date";
	private static final String FIELD_END_DATE = "end date";
	
	public long taskId;
	private String field;
	private String newData;
	private Date newDate;
	private Item task;
	private boolean canEdit;
	private boolean isUndo;
	
	public EditCommand(long taskId, String field, String newData) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newData = newData;
		isUndo = false;
		
		logger.log(Level.INFO, "EditCommand initialized");
	}
	
	public EditCommand(long taskId, String field, Date newDate) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newDate = newDate;
		isUndo = false;
		
		logger.log(Level.INFO, "EditCommand initialized");
	}
	
	public EditCommand(long taskId, String field, String newData, boolean isUndo) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newData = newData;
		this.isUndo = isUndo;
		
		logger.log(Level.INFO, "Counter action EditCommand initialized");
	}
	
	public EditCommand(long taskId, String field, Date newDate, boolean isUndo) {
		this.taskId = taskId;
		this.task = getTask(taskId);
		this.field = field;
		this.newDate = newDate;
		this.isUndo = isUndo;
		
		logger.log(Level.INFO, "Counter action EditCommand initialized");
	}
	
	private void updateChanges() {		
		
		switch (field.toLowerCase()) {
		case FIELD_TITLE :
			task.setTitle(newData);			
			break;
		case FIELD_TYPE :
			task.setType(newData);
			break;
		case FIELD_DESCRIPTION :
			task.setDescription(newData);
			break;
		case FIELD_PRIORITY :
			task.setPriority(newData);
			break;
		case FIELD_STATUS :
			task.setStatus(newData);
			break;
		case FIELD_LABEL :
			task.setLabel(newData);
			break;
		case FIELD_START_DATE :
			task.setStartDate(newDate);
			break;
		case FIELD_END_DATE :
			task.setEndDate(newDate);
			break;
		}
		
	}
	
	private Command createCounterAction() {
		EditCommand counterAction;
		
		switch (field.toLowerCase()) {
		case FIELD_TITLE:
			counterAction = new EditCommand(taskId, field, task.getTitle(), true);			
			break;
		case FIELD_DESCRIPTION:
			counterAction = new EditCommand(taskId, field, task.getDescription(), true);
			break;
		case FIELD_PRIORITY:
			counterAction = new EditCommand(taskId, field, task.getPriority(), true);
			break;
		case FIELD_STATUS:
			counterAction = new EditCommand(taskId, field, task.getStatus(), true);
			break;
		case FIELD_LABEL:
			counterAction = new EditCommand(taskId, field, task.getLabel(), true);
			break;
		case FIELD_START_DATE:
			counterAction = new EditCommand(taskId, field, task.getStartDate(), true);
			break;
		case FIELD_END_DATE:
			counterAction = new EditCommand(taskId, field, task.getEndDate(), true);
			break;
		default:
			counterAction = null;
			break;
		}
		
		return counterAction;
		
	}
	
	private void updateUndoStack() {
		Command counterAction = createCounterAction();
		POMPOM.getUndoStack().push(counterAction);
	}
	
	public String execute() {
		canEdit = checkExists(taskId);
		
		if (canEdit) {
			if (!isUndo) updateUndoStack();
			updateChanges();
			returnMsg = String.format(MESSAGE_TASK_EDITED, taskId);
			ArrayList<Item> taskList = getTaskList();
			POMPOM.getStorage().setTaskList(taskList);
		} else {
			returnMsg = MESSAGE_TASK_ERROR;
		}
		
		POMPOM.refreshStatus();
		showCorrectTab(task);
		
		logger.log(Level.INFO, "EditCommand has be executed");
		return returnMsg;
	}
	
}
```
###### command\ExitCommand.java
``` java
 *
 */
public class ExitCommand {
	
	public ExitCommand() {
		try {
			POMPOM.getStorage().saveStorage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
```
###### command\InvalidCommand.java
``` java
 *
 */
public class InvalidCommand extends Command{

	private String MESSAGE_ERROR = "%s is not a valid command";
	private String error;
	
	public InvalidCommand(String error) {
		this.error = error;
	}
	
	public String execute() {
		returnMsg = String.format(MESSAGE_ERROR, error);
		return returnMsg;
	}

	
	
}
```
###### command\MultiDelCommand.java
``` java
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
```
###### command\MultiEditCommand.java
``` java
 *
 */
import java.util.ArrayList;

import main.POMPOM;

public class MultiEditCommand extends Command {

	private static final String MESSAGE_MULTIEDIT = "Multiple fields has been edited";
	
	ArrayList<EditCommand> editList;
	
	public MultiEditCommand(ArrayList<EditCommand> editList) {
		this.editList = editList;
	}
	
	public String execute() {
		for (int i = 0; i < editList.size(); i++) {
			editList.get(i).execute();
		}
		
		POMPOM.refreshStatus();
		showCorrectTab(getTask(editList.get(0).taskId));
		returnMsg = MESSAGE_MULTIEDIT;
		return returnMsg;
	}
	
}
```
###### command\PathCommand.java
``` java
 *
 */
import java.io.IOException;
import java.util.logging.Level;

import main.POMPOM;

public class PathCommand extends Command {
	
	private static final String MESSAGE_SET_PATH = "Storage path set to: %s";
	
	private String storageFilePath;
	
	public PathCommand(String storageFilePath) {
		this.storageFilePath = storageFilePath;
	}
	
	@Override
	public String execute() {
		
		POMPOM.getStorage().setStorageFilePath(storageFilePath);
		try {
			POMPOM.getStorage().saveSettings();
			POMPOM.getStorage().init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.log(Level.INFO, "PathCommand has be executed");
		returnMsg = String.format(MESSAGE_SET_PATH, storageFilePath);
		return returnMsg;
	}
	
}
```
###### command\SearchCommand.java
``` java
 *
 */
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import main.POMPOM;
import utils.Item;

public class SearchCommand extends Command {

	private static final String MESSAGE_SEARCH = "Search resulted in %s result(s).";
	private static final double PERCENT_TO_ACCEPT = 60.0;

	public ArrayList<Item> searchResults;
	private String keyword;
	private ArrayList<String> keywordTokens;

	public SearchCommand(String keyword) {
		this.searchResults = new ArrayList<Item>();
		this.keyword = keyword;
		this.keywordTokens = tokenize(keyword);
	}

	private ArrayList<Item> search() {

		ArrayList<Item> taskList = getTaskList();
		boolean toAdd = false;
		for (int i = 0; i < taskList.size(); i++) {

			Item currentTask = taskList.get(i);
			ArrayList<String> taskTitleTokens = tokenize(currentTask.getTitle());
			for (int j = 0; j < taskTitleTokens.size(); j++) {
				for (int k = 0; k < keywordTokens.size(); k++) {
					
					String titleToken = taskTitleTokens.get(j);
					String keyToken = keywordTokens.get(k);

					double percentSimilarity = computeStrSimilarity(titleToken, keyToken);
					if (titleToken.contains(keyToken) || percentSimilarity >= PERCENT_TO_ACCEPT) {
						toAdd = true;
						break;
					}

				}

				if (toAdd) {
					break;
				}
						
			}

			if (toAdd) {
				searchResults.add(currentTask);
				toAdd = false;
			}

		}

		return searchResults;

	}

	private static ArrayList<String> tokenize(String keyword) {

		StringTokenizer tokenizer = new StringTokenizer(keyword);
		ArrayList<String> strTokens = new ArrayList<String>();

		// Tokenizes the search String taken in
		while (tokenizer.hasMoreTokens()) {
			strTokens.add(tokenizer.nextToken());
		}
		return strTokens;
	}

	public static int computeEditDistance(String s1, String s2) {

		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		int[] costToChange = new int[s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0) {
					costToChange[j] = j;
				} else {
					if (j > 0) {
						int newValue = costToChange[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
							newValue = Math.min(Math.min(newValue, lastValue), costToChange[j]) + 1;
						}
						costToChange[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0) {
				costToChange[s2.length()] = lastValue;
			}
		}
		return costToChange[s2.length()];
	}

	public static double computeStrSimilarity(String s1, String s2) {
		// s1 should always be bigger, for easy check thus the swapping.
		if (s2.length() > s1.length()) {
			String tempStr = s1;
			s1 = s2;
			s2 = tempStr;
		}

		int MAX_PERCENT = 100;
		int MAX_LENGTH = s1.length();

		if (MAX_LENGTH == 0) {
			return MAX_PERCENT;
		}
		return ((MAX_LENGTH - computeEditDistance(s1, s2)) / (double) MAX_LENGTH) * MAX_PERCENT;
	}

	public String execute() {

		POMPOM.setSearchList(search());
		POMPOM.setCurrentTab(POMPOM.LABEL_SEARCH);
		logger.log(Level.INFO, "SearchCommand has be executed");
		returnMsg = String.format(MESSAGE_SEARCH, searchResults.size());
		return returnMsg;

	}

}
```
###### command\UndoCommand.java
``` java
 *
 */
import main.POMPOM;

public class UndoCommand extends Command {

	private static final String MESSAGE_UNDO = "Previous action has been successfully undone";
	private static final String MESSAGE_ERROR = "There is nothing to undo";

	public String execute() {
		
		Stack<Command> undoStack = POMPOM.getUndoStack();
		
		// checks if stack is empty as popping an empty stack will cause exceptions
		if (undoStack.isEmpty()) {
			returnMsg = MESSAGE_ERROR;
			return returnMsg;
		} else {
			Command undo = undoStack.pop();
			undo.execute();
			logger.log(Level.INFO, "UndoCommand has be executed");
			returnMsg = MESSAGE_UNDO;
			return returnMsg;
		}
	}
	
}
```
###### main\POMPOM.java
``` java
 *
 */
public class POMPOM {

	public static final String STATUS_PENDING = "Pending";
	public static final String STATUS_ONGOING = "Ongoing";
	public static final String STATUS_COMPLETED = "Completed";
	public static final String STATUS_OVERDUE = "Overdue";
	public static final String STATUS_FLOATING = "Floating";

	public static final String LABEL_TASK = "Task";
	public static final String LABEL_COMPLETED_TASK = "CompletedTask";
	public static final String LABEL_EVENT = "Event";
	public static final String LABEL_COMPLETED_EVENT = "CompletedEvent";
	public static final String LABEL_SEARCH = "Search";
	
	public static final String PRIORITY_HIGH = "high";
	public static final String PRIORITY_MED = "medium";
	public static final String PRIORITY_LOW = "low";

	private static Storage storage;
	private static Stack<Command> undoStack;
	public static PrettyTimeParser timeParser;
	public static ArrayList<Item> searchList;
	public static String currentTab;
	public static boolean showHelp = false;
	public POMPOM() {
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() throws IOException {
		try {
			storage = new Storage();
			storage.init();
			undoStack = new Stack<Command>();
			refreshStatus();
			timeParser = new PrettyTimeParser();
			timeParser.parseSyntax("next year");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void refreshStatus() {
		ArrayList<Item> taskList = storage.getTaskList();
		Date currentDate = new Date();

		for (int i = 0; i < taskList.size(); i++) {
			Item currentTask = taskList.get(i);
			Date taskStartDate = currentTask.getStartDate();
			Date taskEndDate = currentTask.getEndDate();

			if (currentTask.getType().equals(LABEL_TASK)) {
				
				if (isNotCompleted(currentTask)) {
					if (taskStartDate == null && taskEndDate == null) {
						currentTask.setStatus(STATUS_FLOATING);
					} else if (taskEndDate == null) {
						if (currentDate.before(taskStartDate)) {
							currentTask.setStatus(STATUS_PENDING);
						} else {
							currentTask.setStatus(STATUS_ONGOING);
						}
					} else if (taskStartDate == null) {
						if (currentDate.before(taskEndDate)) {
							currentTask.setStatus(STATUS_ONGOING);
						} else {
							currentTask.setStatus(STATUS_OVERDUE);
						}
					} else if (currentDate.before(taskStartDate)) {
						currentTask.setStatus(STATUS_PENDING);
					} else if (currentDate.after(taskStartDate) && currentDate.before(taskEndDate)) {
						currentTask.setStatus(STATUS_ONGOING);
					} else if (currentDate.after(taskStartDate) && currentDate.after(taskEndDate)) {
						currentTask.setStatus(STATUS_OVERDUE);
					}
				}

			} else {
				
				if (isNotCompleted(currentTask)) {
					if (currentDate.before(taskStartDate)) {
						currentTask.setStatus(STATUS_PENDING);
					} else if (currentDate.after(taskStartDate) && currentDate.before(taskEndDate)) {
						currentTask.setStatus(STATUS_ONGOING);
					} else if (currentDate.after(taskStartDate) && currentDate.after(taskEndDate)) {
						currentTask.setStatus(STATUS_COMPLETED);
					}
				}
				
			}
		}
	}

	public String execute(String input) {
		Parser parser = Parser.getInstance();
		Command command = parser.executeCommand(input);
		String returnMsg = command.execute();
		refreshStatus();
		return returnMsg;
	}

	public static String executeCommand(Command executable) {
		String returnMsg = executable.execute();
		refreshStatus();
		return returnMsg;
	}

	private static boolean isNotCompleted(Item item) {

		return !item.getStatus().equalsIgnoreCase(STATUS_COMPLETED);

	}

	public static PrettyTimeParser getTimeParser() {
		return timeParser;
	}

	public static void setTimeParser(PrettyTimeParser timeParser) {
		POMPOM.timeParser = timeParser;
	}

	public static Storage getStorage() {
		return storage;
	}

	public static Stack<Command> getUndoStack() {
		return undoStack;
	}

	public static void saveSettings(String storageFilePath) throws IOException {
		storage.setStorageFilePath(storageFilePath);
		storage.saveSettings();
	}

	public static String getCurrentTab() {
		return POMPOM.currentTab;
	}

	public static void setCurrentTab(String setTab) {
		POMPOM.currentTab = setTab;
	}

	public static ArrayList<Item> getSearchList() {
		return searchList;
	}

	public static void setSearchList(ArrayList<Item> searchList) {
		POMPOM.searchList = searchList;
	}

}
```
###### Test\TestCommand.java
``` java
 *
 */
public class TestCommand {

	POMPOM pompom = new POMPOM();
	Date currentDate = new Date();
	DateTimeParser startParser = new DateTimeParser("start", "april 1");
	Date startDate = startParser.getDate();

	DateTimeParser endParser = new DateTimeParser("end", "4 june");
	Date endDate = endParser.getDate();

	@Test
	public void testAdd() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

	}

	@Test
	public void testDeleteById() {
		AddCommand add = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		add.execute();

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		DelCommand delete = new DelCommand(addedTask.getId());

		// check if the delete command returns the right status message
		assertEquals(addedTask.getId() + " has been deleted from " + addedTask.getType(), delete.execute());

		// check if the item was really deleted
		assertEquals(0, taskList.size());
	}
	
	@Test
	public void testMultiDelete() {
		AddCommand add_0 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "2d drawing", "low", "ongoing", "lab 1",
				currentDate, currentDate);
		AddCommand add_1 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "solar system", "medium", "ongoing", "lab 2",
				currentDate, currentDate);
		AddCommand add_2 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "3d drawing", "high", "ongoing", "lab 3",
				currentDate, currentDate);
		AddCommand add_3 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "high", "ongoing", "lab 4",
				currentDate, currentDate);
		AddCommand add_4 = new AddCommand(POMPOM.LABEL_TASK, "do cs2103t", "V0.2", "high", "ongoing", "deadline",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add commands returns the right status message
		assertEquals("Task added", add_0.execute());
		assertEquals("Task added", add_1.execute());
		assertEquals("Task added", add_2.execute());
		assertEquals("Task added", add_3.execute());
		assertEquals("Task added", add_4.execute());

		Item addedTask_0 = taskList.get(0);
		Item addedTask_1 = taskList.get(1);
		Item addedTask_2 = taskList.get(2);
		Item addedTask_3 = taskList.get(3);
		Item addedTask_4 = taskList.get(4);

		DelCommand delete_0 = new DelCommand(addedTask_0.getId());
		DelCommand delete_1 = new DelCommand(addedTask_1.getId());
		DelCommand delete_2 = new DelCommand(addedTask_2.getId());
		DelCommand delete_3 = new DelCommand(addedTask_3.getId());
		DelCommand delete_4 = new DelCommand(addedTask_4.getId());
		ArrayList<DelCommand> deleteList = new ArrayList<DelCommand>();
		deleteList.add(delete_0);
		deleteList.add(delete_1);
		deleteList.add(delete_2);
		deleteList.add(delete_3);
		deleteList.add(delete_4);
		MultiDelCommand multiDelete = new MultiDelCommand(deleteList);

		// check if the delete command returns the right status message
		assertEquals("5 tasks has been deleted.", multiDelete.execute());

		// check if the item was really deleted
		assertEquals(0, taskList.size());
	}

	@Test
	public void testEditTitle() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		EditCommand edit = new EditCommand(addedTask.getId(), "title", "do cs2103t");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + ". was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("do cs2103t", addedTask.getTitle());

	}

	@Test
	public void testEditDescription() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		EditCommand edit = new EditCommand(addedTask.getId(), "description", "V0.2");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + ". was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("V0.2", addedTask.getDescription());

	}

	@Test
	public void testEditPriority() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		EditCommand edit = new EditCommand(addedTask.getId(), "priority", "high");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + ". was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("High", addedTask.getPriority());

	}

	@Test
	public void testEditStatus() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, endDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Ongoing", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		EditCommand edit = new EditCommand(addedTask.getId(), "status", POMPOM.STATUS_COMPLETED);

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + ". was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("Completed", addedTask.getStatus());

	}

	@Test
	public void testEditLabel() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		EditCommand edit = new EditCommand(addedTask.getId(), "label", "deadline");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + ". was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("deadline", addedTask.getLabel());

	}

	@Test
	public void testEditStartDate() throws ParseException {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		//Create date object
		Date editDate = sdf.parse("01/04/2016 00:00");
		
		
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		EditCommand edit = new EditCommand(addedTask.getId(), "start date", editDate);

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + ". was successfully edited", edit.execute());
		
		
		String date = sdf.format(addedTask.getStartDate());
		
		assertEquals("01/04/2016 00:00", date);

	}

	@Test
	public void testMultiEdit() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		EditCommand edit_0 = new EditCommand(addedTask.getId(), "title", "do cs3241 lab 5");
		EditCommand edit_1 = new EditCommand(addedTask.getId(), "description", "build your town");
		EditCommand edit_2 = new EditCommand(addedTask.getId(), "priority", "high");

		ArrayList<EditCommand> editList = new ArrayList<EditCommand>();
		editList.add(edit_0);
		editList.add(edit_1);
		editList.add(edit_2);

		MultiEditCommand multiEdit = new MultiEditCommand(editList);

		// check if the edit command returns the right status message
		assertEquals("Multiple fields has been edited", multiEdit.execute());

		// check if the edit command did edit the actual item
		assertEquals("do cs3241 lab 5", addedTask.getTitle());
		assertEquals("build your town", addedTask.getDescription());
		assertEquals("High", addedTask.getPriority());

	}

	@Test
	public void testUndoAdd() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		UndoCommand undo = new UndoCommand();

		// check if the undo command returns the right status message
		assertEquals("Previous action has been successfully undone", undo.execute());

		// check if the taskList is empty because add was undid
		assertEquals(0, taskList.size());

	}

	@Test
	public void testUndoDelete() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		DelCommand delete = new DelCommand(addedTask.getId());

		// check if the delete command returns the right status message
		assertEquals(addedTask.getId() + " has been deleted from " + addedTask.getType(), delete.execute());

		// check if the item was really deleted
		assertEquals(0, taskList.size());

		UndoCommand undo = new UndoCommand();

		// check if the undo command returns the right status message
		assertEquals("Previous action has been successfully undone", undo.execute());

		// check if the taskList contain the recovered task
		Item recoveredTask = taskList.get(0);
		assertEquals("do cs3241", recoveredTask.getTitle());
		assertEquals("bezier curve", recoveredTask.getDescription());
		assertEquals("Medium", recoveredTask.getPriority());
		assertEquals("Overdue", recoveredTask.getStatus());
		assertEquals("lab", recoveredTask.getLabel());

	}

	@Test
	public void testUndoEditTitle() {
		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Overdue", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

		EditCommand edit = new EditCommand(addedTask.getId(), "title", "do cs2103t");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + ". was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("do cs2103t", addedTask.getTitle());

		UndoCommand undo = new UndoCommand();

		// check if the undo command returns the right status message
		assertEquals("Previous action has been successfully undone", undo.execute());

		// check if the title changed back to previous title
		assertEquals("do cs3241", addedTask.getTitle());

	}
	
	@Test
	public void testUndoEmpty() {
		
		pompom = new POMPOM();
		UndoCommand undo = new UndoCommand();
		assertEquals("There is nothing to undo", undo.execute());
		
	}

	@Test
	public void testSearch() {
		AddCommand command_0 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "2d drawing", "low", "ongoing", "lab 1",
				currentDate, currentDate);
		AddCommand command_1 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "solar system", "medium", "ongoing",
				"lab 2", currentDate, currentDate);
		AddCommand command_2 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "3d drawing", "high", "ongoing", "lab 3",
				currentDate, currentDate);
		AddCommand command_3 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "high", "ongoing",
				"lab 4", currentDate, currentDate);
		AddCommand command_4 = new AddCommand(POMPOM.LABEL_TASK, "do cs2103t", "V0.2", "high", "ongoing", "deadline",
				currentDate, currentDate);

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		// check if the add commands returns the right status message
		assertEquals("Task added", command_0.execute());
		assertEquals("Task added", command_1.execute());
		assertEquals("Task added", command_2.execute());
		assertEquals("Task added", command_3.execute());
		assertEquals("Task added", command_4.execute());

		SearchCommand search = new SearchCommand("cs3241");

		// check if the search command returns the right status message
		assertEquals("Search resulted in 4 result(s).", search.execute());

		// check if the all search results contains the keyword
		assertEquals(4, search.searchResults.size());
		for (int i = 0; i < 4; i++) {
			Item currentTask = search.searchResults.get(i);
			assertEquals(true, currentTask.getTitle().contains("cs3241"));
		}

	}

	@Test
	public void testView() {

		ViewCommand view = new ViewCommand("completedevent");
		assertEquals("CompletedEvent tab has been selected for viewing.", view.execute());
		assertEquals(POMPOM.LABEL_COMPLETED_EVENT, POMPOM.getCurrentTab());

		view = new ViewCommand("completedtask");
		assertEquals("CompletedTask tab has been selected for viewing.", view.execute());
		assertEquals(POMPOM.LABEL_COMPLETED_TASK, POMPOM.getCurrentTab());

		view = new ViewCommand("event");
		assertEquals("Event tab has been selected for viewing.", view.execute());
		assertEquals(POMPOM.LABEL_EVENT, POMPOM.getCurrentTab());

		view = new ViewCommand("task");
		assertEquals("Task tab has been selected for viewing.", view.execute());
		assertEquals(POMPOM.LABEL_TASK, POMPOM.getCurrentTab());

		view = new ViewCommand("search");
		assertEquals("Search tab has been selected for viewing.", view.execute());
		assertEquals(POMPOM.LABEL_SEARCH, POMPOM.getCurrentTab());

	}

	@Test
	public void testSetPath() {

		String currentPath = POMPOM.getStorage().getStorageFilePath();
		PathCommand path = new PathCommand(currentPath);
		assertEquals(String.format("Storage path set to: %s", currentPath), path.execute());

	}

	@Test
	public void testInvalid() {

		InvalidCommand invalid = new InvalidCommand("asd");
		assertEquals("asd is not a valid command", invalid.execute());

	}

	@Test
	public void testAddRecurring() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

	}

	@Test
	public void testDelRecurringFirstTask() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);

		DelRecurringCommand delRecurringCommand = new DelRecurringCommand(addedTask_1.getId());
		assertEquals("A series of recurring tasks has been deleted", delRecurringCommand.execute());
		
		assertEquals(0, taskList.size());
		
	}
	
	@Test
	public void testDelRecurringMiddleTask() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_2 = taskList.get(1);

		DelRecurringCommand delRecurringCommand = new DelRecurringCommand(addedTask_2.getId());
		assertEquals("A series of recurring tasks has been deleted", delRecurringCommand.execute());
		
		assertEquals(0, taskList.size());
		
	}
	
	@Test
	public void testDelRecurringLastTask() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_3 = taskList.get(2);

		DelRecurringCommand delRecurringCommand = new DelRecurringCommand(addedTask_3.getId());
		assertEquals("A series of recurring tasks has been deleted", delRecurringCommand.execute());
		
		assertEquals(0, taskList.size());
		
	}
	
	@Test
	public void testEditRecurringTitle() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "title", "it works perfectly");
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());
		
		assertEquals("it works perfectly", addedTask_1.getTitle());
		assertEquals("it works perfectly", addedTask_2.getTitle());
		assertEquals("it works perfectly", addedTask_3.getTitle());
		
	}
	
	@Test
	public void testEditRecurringDescription() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "description", "it works perfectly");
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());
		
		assertEquals("it works perfectly", addedTask_1.getDescription());
		assertEquals("it works perfectly", addedTask_2.getDescription());
		assertEquals("it works perfectly", addedTask_3.getDescription());
		
	}
	
	@Test
	public void testEditRecurringLabel() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "label", "work work work");
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());
		
		assertEquals("work work work", addedTask_1.getLabel());
		assertEquals("work work work", addedTask_2.getLabel());
		assertEquals("work work work", addedTask_3.getLabel());
		
	}
	
	@Test
	public void testEditRecurringPriority() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "priority", "high");
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());
		
		assertEquals("High", addedTask_1.getPriority());
		assertEquals("High", addedTask_2.getPriority());
		assertEquals("High", addedTask_3.getPriority());
		
	}
	
	@Test
	public void testEditRecurringFirstTask() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "title", "it works perfectly");
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());
		
		assertEquals("it works perfectly", addedTask_1.getTitle());
		assertEquals("it works perfectly", addedTask_2.getTitle());
		assertEquals("it works perfectly", addedTask_3.getTitle());
		
	}
	
	@Test
	public void testEditRecurringMiddleTask() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_2.getId(), "title", "it works perfectly");
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());
		
		assertEquals("it works perfectly", addedTask_1.getTitle());
		assertEquals("it works perfectly", addedTask_2.getTitle());
		assertEquals("it works perfectly", addedTask_3.getTitle());
		
	}
	
	@Test
	public void testEditRecurringLastTask() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_3.getId(), "title", "it works perfectly");
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());
		
		assertEquals("it works perfectly", addedTask_1.getTitle());
		assertEquals("it works perfectly", addedTask_2.getTitle());
		assertEquals("it works perfectly", addedTask_3.getTitle());
		
	}

	@Test
	public void testUndoAddRecurring() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());
		
		UndoCommand undo = new UndoCommand();
		assertEquals("Previous action has been successfully undone", undo.execute());

	}
	
	@Test
	public void testRecurringLinkage() {

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		String returnMsg = pompom.execute("add do project every monday f:4 apr e:2 may");

		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);

		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());
		
		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);
		
		UndoCommand undo = new UndoCommand();
		DelCommand delete = new DelCommand(addedTask_2.getId());
		
		// check if the correct status message is returned
		assertEquals(addedTask_2.getId() + " has been deleted from Event", delete.execute());
		assertEquals(2, taskList.size());
		
		DelRecurringCommand delRecurring_1 = new DelRecurringCommand(addedTask_1.getId());
		assertEquals("A series of recurring tasks has been deleted", delRecurring_1.execute());
		assertEquals(0, taskList.size());

		assertEquals("Previous action has been successfully undone", undo.execute());
		assertEquals("Previous action has been successfully undone", undo.execute());
		
		// check if linkage is restored if deleted middle task is added back by undo
		assertEquals(3, taskList.size());
		DelRecurringCommand delRecurring_2 = new DelRecurringCommand(addedTask_2.getId());
		assertEquals("A series of recurring tasks has been deleted", delRecurring_2.execute());
		assertEquals(0, taskList.size());
	}
}
```
###### Test\TestPOMPOM.java
``` java
 *
 */
public class TestPOMPOM {

	@Test
	public void testStatusPending() {
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241 f:tomorrow e:next friday");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs3241 f:next monday");
		Item secondTask = taskList.get(1);

		pompom.refreshStatus();
		/**
		 * check if the statuses are pending as current date is before start
		 * date
		 */
		assertEquals(POMPOM.STATUS_PENDING, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_PENDING, secondTask.getStatus());

	}

	@Test
	public void testStatusOngoing() {
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241 f:now e:next friday");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs3241 f:now");
		Item secondTask = taskList.get(1);

		pompom.execute("add do cs3241 next tuesday");
		Item thirdTask = taskList.get(2);

		pompom.refreshStatus();
		/**
		 * check if the statuses are ongoing as current date is within the start
		 * and end date specified
		 */
		assertEquals(POMPOM.STATUS_ONGOING, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_ONGOING, secondTask.getStatus());
		assertEquals(POMPOM.STATUS_ONGOING, thirdTask.getStatus());

	}

	@Test
	public void testStatusOverdue() {
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241 f:yesterday e:yesterday");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs3241 last monday");
		Item secondTask = taskList.get(1);

		pompom.refreshStatus();
		/**
		 * check if the statuses are overdue as current date after end date
		 */
		assertEquals(POMPOM.STATUS_OVERDUE, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_OVERDUE, secondTask.getStatus());

	}

	@Test
	public void testStatusFloating() {
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs2103t");
		Item secondTask = taskList.get(1);

		pompom.refreshStatus();
		/**
		 * check if the statuses are floating as start and end date are not
		 * specified
		 */
		assertEquals(POMPOM.STATUS_FLOATING, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_FLOATING, secondTask.getStatus());

	}

	@Test
	public void testStatusCompleted() {
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241 f:now e:next friday");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs3241 f:now");
		Item secondTask = taskList.get(1);

		pompom.execute("add do cs3241 next tuesday");
		Item thirdTask = taskList.get(2);

		ArrayList<EditCommand> editList = new ArrayList<EditCommand>();
		for (int i = 0; i < 3; i++) {
			Item currentTask = taskList.get(i);
			EditCommand command = new EditCommand(currentTask.getId(), "status", POMPOM.STATUS_COMPLETED);
			editList.add(command);
		}

		MultiEditCommand multiEdit = new MultiEditCommand(editList);
		multiEdit.execute();

		pompom.refreshStatus();
		/**
		 * check if the statuses remain completed as edit command was used
		 */
		assertEquals(POMPOM.STATUS_COMPLETED, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_COMPLETED, secondTask.getStatus());
//		assertEquals(POMPOM.STATUS_COMPLETED, thirdTask.getStatus());

	}

}
```
###### Test\TestSystem.java
``` java
 *
 */
public class TestSystem {

	POMPOM main = new POMPOM();
	/*SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	String sDateTime = dateFormat.format("01-04-2016") + " " + 8 + ":" + 00;
	Date sDate = dateFormat.parse(sDateTime);*/
	Date currentDate = new Date();
	ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
	
	@Test
	public void testInit() {
		

	}
	
	@Test
	public void testAddTitleOnly() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand = "add do project";
		String returnMsg = main.execute(userCommand);
		
		// check if the add command returns the right status message
		assertEquals("Task added", returnMsg);

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do project", addedTask.getTitle());

	}
	
	@Test
	public void testAddEventTitleOnly() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand = "event clubbing f:april 3";
		String returnMsg = main.execute(userCommand);
		
		// check if the add command returns the right status message
		assertEquals("Event added", returnMsg);

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("clubbing", addedTask.getTitle());

	}
	
	@Test
	public void testAddTitleAndEndDate() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand = "add do project april 3";
		String returnMsg = main.execute(userCommand);
		
		// check if the add command returns the right status message
		assertEquals("Task added", returnMsg);

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do project", addedTask.getTitle());
		//assertEquals(___, addedTask.getEndDate);

	}
	
	@Test
	public void testAddTitleStartEndDate() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand = "add do project april 3 f:today";
		String returnMsg = main.execute(userCommand);
		
		// check if the add command returns the right status message
		assertEquals("Task added", returnMsg);

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do project  f", addedTask.getTitle());
		//assertEquals(___, addedTask.getEndDate());
		//assertEquals(___, addedTask.getStartDate());

	}
	
	@Test
	public void testAddTitleStartEndDateLabel() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand = "add do project e:april 3 f:today l:must do";
		String returnMsg = main.execute(userCommand);
		
		// check if the add command returns the right status message
		assertEquals("Task added", returnMsg);

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do project", addedTask.getTitle());
		//assertEquals(___, addedTask.getEndDate());
		//assertEquals(___, addedTask.getStartDate());
		assertEquals("must do", addedTask.getLabel());

	}
	
	@Test
	public void testAddTitleStartEndDateLabelPriority() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand = "add do project e:april 3 f:today l:must do p:high";
		String returnMsg = main.execute(userCommand);
		
		// check if the add command returns the right status message
		assertEquals("Task added", returnMsg);

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do project", addedTask.getTitle());
		//assertEquals(___, addedTask.getEndDate());
		//assertEquals(___, addedTask.getStartDate());
		assertEquals("must do", addedTask.getLabel());
		assertEquals("high", addedTask.getPriority());

	}
	
	
	@Test
	public void testDeleteById() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand_1 = "add do project1 april 1";
		String userCommand_2 = "add do project2 april 2";
		String userCommand_3 = "add do project3 april 3";
		
		String returnMsg_1 = main.execute(userCommand_1);
		String returnMsg_2 = main.execute(userCommand_2);
		String returnMsg_3 = main.execute(userCommand_3);
		
		// check if the add commands returns the right status message
		assertEquals("Task added", returnMsg_1);
		assertEquals("Task added", returnMsg_2);
		assertEquals("Task added", returnMsg_3);
		
		long id = taskList.get(1).getId();
		String delCommand = "delete " + id;
		String returnMsg = main.execute(delCommand);
		
		// check if the delete command returns the right status message
		
		
		// check if the correct task got deleted
		assertEquals(2, taskList.size());
		assertEquals("do project1", taskList.get(0).getTitle());
		assertEquals("do project3", taskList.get(1).getTitle());
		
	}
	
	@Test
	public void testDeleteByTitle() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand_1 = "add do project april 1";
		String userCommand_2 = "add do project april 2";
		String userCommand_3 = "add do project april 3";
		
		String returnMsg_1 = main.execute(userCommand_1);
		String returnMsg_2 = main.execute(userCommand_2);
		String returnMsg_3 = main.execute(userCommand_3);
		
		// check if the add commands returns the right status message
		assertEquals("Task added", returnMsg_1);
		assertEquals("Task added", returnMsg_2);
		assertEquals("Task added", returnMsg_3);
		
		String delCommand = "delete do project";
		String returnMsg = main.execute(delCommand);
		
		// check if the delete command returns the right status message
		//assertEquals("All tasks with title \"do project\" have been deleted", returnMsg);
		
		// check if the correct tasks got deleted
		assertEquals(3, taskList.size());
		
	}
	
	@Test
	public void testEditTitle() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand_1 = "add do project1 april 1";
		String userCommand_2 = "add do project2 april 2";
		String userCommand_3 = "add do project3 april 3";
		
		String returnMsg_1 = main.execute(userCommand_1);
		String returnMsg_2 = main.execute(userCommand_2);
		String returnMsg_3 = main.execute(userCommand_3);
		
		// check if the add commands returns the right status message
		assertEquals("Task added", returnMsg_1);
		assertEquals("Task added", returnMsg_2);
		assertEquals("Task added", returnMsg_3);
		
		long id_1 = taskList.get(0).getId();
		long id_2 = taskList.get(1).getId();
		long id_3 = taskList.get(2).getId();

		String editCommand_1 = "edit " + id_1 + " title do project4";
		String editCommand_2 = "edit " + id_2 + " title do project5";
		String editCommand_3 = "edit " + id_3 + " title do project6";
		
		String returnMsg_4 = main.execute(editCommand_1);
		String returnMsg_5 = main.execute(editCommand_2);
		String returnMsg_6 = main.execute(editCommand_3);
		
		// check if the edit command returns the right status message
		assertEquals(id_1 + ". was successfully edited", returnMsg_4);
		assertEquals(id_2 + ". was successfully edited", returnMsg_5);
		assertEquals(id_3 + ". was successfully edited", returnMsg_6);
		
		// check if the correct task got deleted
		assertEquals(3, taskList.size());
		assertEquals("do project4", taskList.get(0).getTitle());
		assertEquals("do project5", taskList.get(1).getTitle());
		assertEquals("do project6", taskList.get(2).getTitle());
		
	}
	
	@Test
	public void testEditDate() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand_1 = "add do project1 april 1";
		String userCommand_2 = "add do project2 april 2";
		String userCommand_3 = "add do project3 april 3";
		
		String returnMsg_1 = main.execute(userCommand_1);
		String returnMsg_2 = main.execute(userCommand_2);
		String returnMsg_3 = main.execute(userCommand_3);
		
		// check if the add commands returns the right status message
		assertEquals("Task added", returnMsg_1);
		assertEquals("Task added", returnMsg_2);
		assertEquals("Task added", returnMsg_3);
		
		long id_1 = taskList.get(0).getId();
		long id_2 = taskList.get(1).getId();
		long id_3 = taskList.get(2).getId();

		/*String editCommand_1 = "edit " + id_1 + " start date do project4";
		String editCommand_2 = "edit " + id_2 + " title do project5";
		String editCommand_3 = "edit " + id_3 + " title do project6";
		
		String returnMsg_4 = main.execute(editCommand_1);
		String returnMsg_5 = main.execute(editCommand_2);
		String returnMsg_6 = main.execute(editCommand_3);
		
		// check if the edit command returns the right status message
		assertEquals(id_1 + ". was successfully edited", returnMsg_4);
		assertEquals(id_2 + ". was successfully edited", returnMsg_5);
		assertEquals(id_3 + ". was successfully edited", returnMsg_6);
		
		// check if the correct task got deleted
		assertEquals(3, taskList.size());
		assertEquals("do project4", taskList.get(0).getTitle());
		assertEquals("do project5", taskList.get(1).getTitle());
		assertEquals("do project6", taskList.get(2).getTitle());*/
		
	}
	
	@Test
	public void testEditLabel() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand_1 = "add do project1 april 1 l:work";
		String userCommand_2 = "add do project2 april 2 l:work";
		String userCommand_3 = "add do project3 april 3 l:work";
		
		String returnMsg_1 = main.execute(userCommand_1);
		String returnMsg_2 = main.execute(userCommand_2);
		String returnMsg_3 = main.execute(userCommand_3);
		
		// check if the add commands returns the right status message
		assertEquals("Task added", returnMsg_1);
		assertEquals("Task added", returnMsg_2);
		assertEquals("Task added", returnMsg_3);
		
		long id_1 = taskList.get(0).getId();
		long id_2 = taskList.get(1).getId();
		long id_3 = taskList.get(2).getId();

		String editCommand_1 = "edit " + id_1 + " label do work";
		String editCommand_2 = "edit " + id_2 + " label more work";
		String editCommand_3 = "edit " + id_3 + " label even more work";
		
		String returnMsg_4 = main.execute(editCommand_1);
		String returnMsg_5 = main.execute(editCommand_2);
		String returnMsg_6 = main.execute(editCommand_3);
		
		// check if the edit command returns the right status message
		assertEquals(id_1 + ". was successfully edited", returnMsg_4);
		assertEquals(id_2 + ". was successfully edited", returnMsg_5);
		assertEquals(id_3 + ". was successfully edited", returnMsg_6);
		
		// check if the correct task got deleted
		assertEquals(3, taskList.size());
		assertEquals("do work", taskList.get(0).getLabel());
		assertEquals("more work", taskList.get(1).getLabel());
		assertEquals("even more work", taskList.get(2).getLabel());
		
	}
	
	@Test
	public void testEditPriority() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand_1 = "add do project1 april 1 l:work";
		String userCommand_2 = "add do project2 april 2 l:work";
		String userCommand_3 = "add do project3 april 3 l:work";
		
		String returnMsg_1 = main.execute(userCommand_1);
		String returnMsg_2 = main.execute(userCommand_2);
		String returnMsg_3 = main.execute(userCommand_3);
		
		// check if the add commands returns the right status message
		assertEquals("Task added", returnMsg_1);
		assertEquals("Task added", returnMsg_2);
		assertEquals("Task added", returnMsg_3);
		
		long id_1 = taskList.get(0).getId();
		long id_2 = taskList.get(1).getId();
		long id_3 = taskList.get(2).getId();

		String editCommand_1 = "edit " + id_1 + " priority low";
		String editCommand_2 = "edit " + id_2 + " priority medium";
		String editCommand_3 = "edit " + id_3 + " priority high";
		
		String returnMsg_4 = main.execute(editCommand_1);
		String returnMsg_5 = main.execute(editCommand_2);
		String returnMsg_6 = main.execute(editCommand_3);
		
		// check if the edit command returns the right status message
		assertEquals(id_1 + ". was successfully edited", returnMsg_4);
		assertEquals(id_2 + ". was successfully edited", returnMsg_5);
		assertEquals(id_3 + ". was successfully edited", returnMsg_6);
		
		// check if the correct task got deleted
		assertEquals(3, taskList.size());
		assertEquals("low", taskList.get(0).getPriority());
		assertEquals("medium", taskList.get(1).getPriority());
		assertEquals("high", taskList.get(2).getPriority());
		
	}
	
	@Test
	public void testEditDescription() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand_1 = "add do project1 april 1 l:work";
		String userCommand_2 = "add do project2 april 2 l:work";
		String userCommand_3 = "add do project3 april 3 l:work";
		
		String returnMsg_1 = main.execute(userCommand_1);
		String returnMsg_2 = main.execute(userCommand_2);
		String returnMsg_3 = main.execute(userCommand_3);
		
		// check if the add commands returns the right status message
		assertEquals("Task added", returnMsg_1);
		assertEquals("Task added", returnMsg_2);
		assertEquals("Task added", returnMsg_3);
		
		long id_1 = taskList.get(0).getId();
		long id_2 = taskList.get(1).getId();
		long id_3 = taskList.get(2).getId();

		String editCommand_1 = "edit " + id_1 + " description this is a cs1101s project";
		String editCommand_2 = "edit " + id_2 + " description this is a cs2020 project";
		String editCommand_3 = "edit " + id_3 + " description this is a cs3230 project";
		
		String returnMsg_4 = main.execute(editCommand_1);
		String returnMsg_5 = main.execute(editCommand_2);
		String returnMsg_6 = main.execute(editCommand_3);
		
		// check if the edit command returns the right status message
		assertEquals(id_1 + ". was successfully edited", returnMsg_4);
		assertEquals(id_2 + ". was successfully edited", returnMsg_5);
		assertEquals(id_3 + ". was successfully edited", returnMsg_6);
		
		// check if the correct task got deleted
		assertEquals(3, taskList.size());
		assertEquals("this is a cs1101s project", taskList.get(0).getDescription());
		assertEquals("this is a cs2020 project", taskList.get(1).getDescription());
		assertEquals("this is a cs3230 project", taskList.get(2).getDescription());
		
	}
	
	@Test
	public void testSearch() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand_1 = "add do project1 april 1 l:work";
		String userCommand_2 = "add do project2 april 2 l:work";
		String userCommand_3 = "add do project3 april 3 l:work";
		
		String returnMsg_1 = main.execute(userCommand_1);
		String returnMsg_2 = main.execute(userCommand_2);
		String returnMsg_3 = main.execute(userCommand_3);
		
		// check if the add commands returns the right status message
		assertEquals("Task added", returnMsg_1);
		assertEquals("Task added", returnMsg_2);
		assertEquals("Task added", returnMsg_3);
		
		String searchCommand_1 = "search do";
		String searchCommand_2 = "search project";
		String searchCommand_3 = "search project1";
		
		String returnMsg_4 = main.execute(searchCommand_1);
		assertEquals("Search resulted in 3 result(s).", returnMsg_4);
		assertEquals(3, POMPOM.getSearchList().size());
		
		String returnMsg_5 = main.execute(searchCommand_2);
		assertEquals("Search resulted in 3 result(s).", returnMsg_5);
		assertEquals(3, POMPOM.getSearchList().size());
		
		String returnMsg_6 = main.execute(searchCommand_3);
		assertEquals("Search resulted in 1 result(s).", returnMsg_6);
		assertEquals(1, POMPOM.getSearchList().size());

		
	}
	
	@Test
	public void testUndoAdd() {
		
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		
		String userCommand = "add do project";
		String returnMsg = main.execute(userCommand);
		
		// check if the add command returns the right status message
		assertEquals("Task added", returnMsg);

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do project", addedTask.getTitle());
		
		String undoCommand = "undo";
		returnMsg = main.execute(undoCommand);
		
		// check if the add command returns the right status message
		assertEquals(0, taskList.size());
		

	}
	
	
}
```
