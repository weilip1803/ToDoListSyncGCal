package Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import command.AddCommand;
import command.DelCommand;
import command.DelRecurringCommand;
import command.EditCommand;
import command.EditRecurringCommand;
import command.InvalidCommand;
import command.PathCommand;
import command.SearchCommand;
import command.UndoCommand;
import command.ViewCommand;
import main.POMPOM;
import utils.Item;

import static org.junit.Assert.assertEquals;

/**
 * @@author A0121528M
 *
 */
public class TestCommand {

	/** Parameters **/ 
	POMPOM pompom;
	ArrayList<Item> taskList;
	Date currentDate;
	Date startDate;
	Date endDate;
	SimpleDateFormat formatter;

	/**
	 * This method initializes POMPOM class, the dates to be used and clears the
	 * storage for testing. Also initializes the SimpleDateFormat for later use.
	 */
	@Before
	public void initialize() {
		pompom = new POMPOM();
		taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		currentDate = new Date();
		startDate = parseDate("1 apr");
		endDate = parseDate("5 may 2020");
		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	}

	/******************* HELPER METHODS *******************/

	private Date parseDate(String dateString) {
		List<Date> date;
		PrettyTimeParser timeParser = new PrettyTimeParser();

		if (dateString != null) {
			date = timeParser.parse(dateString);

			if (date.isEmpty()) {
				return null;
			} else {
				return date.get(0);
			}

		} else {
			return null;
		}

	}

	/******************* END OF HELPER METHODS ************/

	/******************* UNIT TEST CASES ******************/

	@Test
	public void testAdd() {

		AddCommand command = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "medium", "ongoing", "lab",
				startDate, endDate);

		// check if the add command returns the right status message
		assertEquals("Task added", command.execute());

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do cs3241", addedTask.getTitle());
		assertEquals("bezier curve", addedTask.getDescription());
		assertEquals("Medium", addedTask.getPriority());
		assertEquals("Ongoing", addedTask.getStatus());
		assertEquals("lab", addedTask.getLabel());

	}

	@Test
	public void testDelete() {

		testAdd();

		Item addedTask = taskList.get(0);

		DelCommand delete = new DelCommand(addedTask.getId());

		// check if the delete command returns the right status message
		assertEquals(addedTask.getId() + " has been deleted from " + addedTask.getType(), delete.execute());

		// check if the item was really deleted
		assertEquals(0, taskList.size());
	}

	@Test
	public void testEditTitle() {

		testAdd();

		Item addedTask = taskList.get(0);

		EditCommand edit = new EditCommand(addedTask.getId(), "title", "do cs2103t");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + " was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("do cs2103t", addedTask.getTitle());

	}

	@Test
	public void testEditDescription() {

		testAdd();

		Item addedTask = taskList.get(0);

		EditCommand edit = new EditCommand(addedTask.getId(), "description", "V0.2");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + " was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("V0.2", addedTask.getDescription());

	}

	@Test
	public void testEditPriority() {

		testAdd();

		Item addedTask = taskList.get(0);

		EditCommand edit = new EditCommand(addedTask.getId(), "priority", "high");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + " was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("High", addedTask.getPriority());

	}

	@Test
	public void testEditStatus() {

		testAdd();

		Item addedTask = taskList.get(0);

		EditCommand edit = new EditCommand(addedTask.getId(), "status", POMPOM.STATUS_COMPLETED);

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + " was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("Completed", addedTask.getStatus());

	}

	@Test
	public void testEditLabel() {

		testAdd();

		Item addedTask = taskList.get(0);

		EditCommand edit = new EditCommand(addedTask.getId(), "label", "deadline");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + " was successfully edited", edit.execute());

		// check if the edit command did edit the actual item
		assertEquals("deadline", addedTask.getLabel());

	}

	@Test
	public void testEditStartDate() throws ParseException {

		testAdd();

		Item addedTask = taskList.get(0);

		Date editDate = parseDate("2 apr 1pm");

		EditCommand edit = new EditCommand(addedTask.getId(), "start date", editDate);

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + " was successfully edited", edit.execute());

		String date = formatter.format(addedTask.getStartDate());

		// check if the edit command did edit the actual item
		assertEquals("02/04/2016 13:00", date);

	}

	@Test
	public void testUndoAdd() {

		testAdd();

		UndoCommand undo = new UndoCommand();

		// check if the undo command returns the right status message
		assertEquals("Previous action has been successfully undone", undo.execute());

		// check if the taskList is empty because add was undone
		assertEquals(0, taskList.size());

	}

	@Test
	public void testUndoDelete() {

		testAdd();

		Item addedTask = taskList.get(0);

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
		assertEquals("Ongoing", recoveredTask.getStatus());
		assertEquals("lab", recoveredTask.getLabel());

	}

	@Test
	public void testUndoEditTitle() {

		testAdd();

		Item addedTask = taskList.get(0);

		EditCommand edit = new EditCommand(addedTask.getId(), "title", "do cs2103t");

		// check if the edit command returns the right status message
		assertEquals(addedTask.getId() + " was successfully edited", edit.execute());

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

		// Populates task list for searching
		AddCommand command_0 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "2d drawing", "low", "ongoing", "lab 1",
				startDate, endDate);
		AddCommand command_1 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "solar system", "medium", "ongoing",
				"lab 2", startDate, endDate);
		AddCommand command_2 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "3d drawing", "high", "ongoing", "lab 3",
				startDate, endDate);
		AddCommand command_3 = new AddCommand(POMPOM.LABEL_TASK, "do cs3241", "bezier curve", "high", "ongoing",
				"lab 4", startDate, endDate);
		AddCommand command_4 = new AddCommand(POMPOM.LABEL_TASK, "do cs2103t", "V0.2", "high", "ongoing", "deadline",
				startDate, endDate);

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
		assertEquals("asd", invalid.execute());

	}

	@Test
	public void testAddRecurring() {

		// add recurring tasks using POMPOM class to populating the tasks
		String returnMsg = pompom.execute("add do project f:4 apr e:5 apr r:daily until 6 apr");
 
		// check if the correct status message is returned
		assertEquals("Recurring tasks has been added", returnMsg);
 
		// check if the correct number of tasks has been added
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		// check if all recurring tasks have the same title
		assertEquals("do project", addedTask_1.getTitle());
		assertEquals("do project", addedTask_2.getTitle());
		assertEquals("do project", addedTask_3.getTitle());

	}

	@Test
	public void testDelRecurringFirstTask() {

		testAddRecurring();

		Item addedTask_1 = taskList.get(0);

		DelRecurringCommand delRecurringCommand = new DelRecurringCommand(addedTask_1.getId());

		// check if the DelRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been deleted", delRecurringCommand.execute());

		// check if all recurring tasks have been deleted
		assertEquals(0, taskList.size());

	}

	@Test
	public void testDelRecurringMiddleTask() {

		testAddRecurring();

		Item addedTask_2 = taskList.get(1);

		DelRecurringCommand delRecurringCommand = new DelRecurringCommand(addedTask_2.getId());

		// check if the DelRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been deleted", delRecurringCommand.execute());

		// check if all recurring tasks have been deleted
		assertEquals(0, taskList.size());

	}

	@Test
	public void testDelRecurringLastTask() {

		testAddRecurring();

		Item addedTask_3 = taskList.get(2);

		DelRecurringCommand delRecurringCommand = new DelRecurringCommand(addedTask_3.getId());

		// check if the DelRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been deleted", delRecurringCommand.execute());

		// check if all recurring tasks have been deleted
		assertEquals(0, taskList.size());

	}

	@Test
	public void testEditRecurringTitle() {

		testAddRecurring();

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "title",
				"it works perfectly");
		
		// check if the EditRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());

		// check if all recurring tasks have been edited
		assertEquals("it works perfectly", addedTask_1.getTitle());
		assertEquals("it works perfectly", addedTask_2.getTitle());
		assertEquals("it works perfectly", addedTask_3.getTitle());

	}

	@Test
	public void testEditRecurringDescription() {

		testAddRecurring();

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "description",
				"it works perfectly");
		
		// check if the EditRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());

		// check if all recurring tasks have been edited
		assertEquals("it works perfectly", addedTask_1.getDescription());
		assertEquals("it works perfectly", addedTask_2.getDescription());
		assertEquals("it works perfectly", addedTask_3.getDescription());

	}

	@Test
	public void testEditRecurringLabel() {

		testAddRecurring();

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "label",
				"work work work");
		
		// check if the EditRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());

		// check if all recurring tasks have been edited
		assertEquals("work work work", addedTask_1.getLabel());
		assertEquals("work work work", addedTask_2.getLabel());
		assertEquals("work work work", addedTask_3.getLabel());

	}

	@Test
	public void testEditRecurringPriority() {

		testAddRecurring();

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "priority", "high");
		
		// check if the EditRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());

		// check if all recurring tasks have been edited
		assertEquals("High", addedTask_1.getPriority());
		assertEquals("High", addedTask_2.getPriority());
		assertEquals("High", addedTask_3.getPriority());

	}

	@Test
	public void testEditRecurringFirstTask() {

		testAddRecurring();

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_1.getId(), "title",
				"it works perfectly");
		
		// check if the EditRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());

		// check if all recurring tasks have been edited
		assertEquals("it works perfectly", addedTask_1.getTitle());
		assertEquals("it works perfectly", addedTask_2.getTitle());
		assertEquals("it works perfectly", addedTask_3.getTitle());

	}

	@Test
	public void testEditRecurringMiddleTask() {

		testAddRecurring();

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_2.getId(), "title",
				"it works perfectly");
		
		// check if the EditRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());

		// check if all recurring tasks have been edited
		assertEquals("it works perfectly", addedTask_1.getTitle());
		assertEquals("it works perfectly", addedTask_2.getTitle());
		assertEquals("it works perfectly", addedTask_3.getTitle());

	}

	@Test
	public void testEditRecurringLastTask() {

		testAddRecurring();

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		EditRecurringCommand editRecurringCommand = new EditRecurringCommand(addedTask_3.getId(), "title",
				"it works perfectly");
		
		// check if the EditRecurringCommand returns the right status message
		assertEquals("A series of recurring tasks has been edited", editRecurringCommand.execute());

		// check if all recurring tasks have been edited
		assertEquals("it works perfectly", addedTask_1.getTitle());
		assertEquals("it works perfectly", addedTask_2.getTitle());
		assertEquals("it works perfectly", addedTask_3.getTitle());

	}

	@Test
	public void testUndoAddRecurring() {

		testAddRecurring();

		UndoCommand undo = new UndoCommand();
		
		// check if the UndoCommand returns the right status message
		assertEquals("Previous action has been successfully undone", undo.execute());

		// check if all recurring tasks have been removed by undo
		assertEquals(0, taskList.size());
     
	}

	@Test
	public void testUndoDelRecurring() {

		testAddRecurring();

		Item firstTask = taskList.get(0);

		DelRecurringCommand deleteRecur = new DelRecurringCommand(firstTask.getId());
		assertEquals("A series of recurring tasks has been deleted", deleteRecur.execute());

		UndoCommand undo = new UndoCommand();
		
		// check if the UndoCommand returns the right status message
		assertEquals("Previous action has been successfully undone", undo.execute());

		// check if all recurring tasks have been added back by undo
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		assertEquals("do project", addedTask_1.getTitle());
		assertEquals("do project", addedTask_2.getTitle());
		assertEquals("do project", addedTask_3.getTitle());
	}

	@Test
	public void testUndoEditRecurring() {

		testEditRecurringTitle();

		UndoCommand undo = new UndoCommand();
		
		// check if the UndoCommand returns the right status message
		assertEquals("Previous action has been successfully undone", undo.execute());

		// check if all recurring tasks have been changed back by undo
		assertEquals(3, taskList.size());

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		assertEquals("do project", addedTask_1.getTitle());
		assertEquals("do project", addedTask_2.getTitle());
		assertEquals("do project", addedTask_3.getTitle());
	}

	/**
	 * Recurring tasks are linked by the circular doubly linked list concept.
	 * Thus, pointers must be updated if the middle node is deleted. This test
	 * adds 3 tasks and deletes the middle one to test the pointers are being
	 * updated.
	 */
	@Test
	public void testRecurringLinkage() {

		testAddRecurring();

		Item addedTask_1 = taskList.get(0);
		Item addedTask_2 = taskList.get(1);
		Item addedTask_3 = taskList.get(2);

		UndoCommand undo = new UndoCommand();
		DelCommand delete = new DelCommand(addedTask_2.getId());

		// check if the correct status message is returned
		assertEquals(addedTask_2.getId() + " has been deleted from Task", delete.execute());
		assertEquals(2, taskList.size());

		DelRecurringCommand delRecurring_1 = new DelRecurringCommand(addedTask_1.getId());
		assertEquals("A series of recurring tasks has been deleted", delRecurring_1.execute());
		assertEquals(0, taskList.size());

		assertEquals("Previous action has been successfully undone", undo.execute());
		assertEquals("Previous action has been successfully undone", undo.execute());

		// check if linkage is restored if deleted middle task is added back by
		// undo
		assertEquals(3, taskList.size());
		DelRecurringCommand delRecurring_2 = new DelRecurringCommand(addedTask_2.getId());
		assertEquals("A series of recurring tasks has been deleted", delRecurring_2.execute());
		assertEquals(0, taskList.size());
	}
}
