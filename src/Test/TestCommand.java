package Test;

import java.io.InvalidClassException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import command.AddCommand;
import command.DelCommand;
import command.DelRecurringCommand;
import command.EditCommand;
import command.EditRecurringCommand;
import command.InvalidCommand;
import command.MultiDelCommand;
import command.MultiEditCommand;
import command.PathCommand;
import command.SearchCommand;
import command.UndoCommand;
import command.ViewCommand;
import main.POMPOM;
import parser.DateTimeParser;
import utils.Item;

import static org.junit.Assert.assertEquals;

/**
 * @@author wen hao
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
