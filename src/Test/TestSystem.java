package Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import parser.AddParser;
import parser.Parser;
import command.AddCommand;
import command.AddRecurringCommand;
import command.DelCommand;
import command.DelRecurringCommand;
import command.EditCommand;
import command.EditRecurringCommand;
import command.UndoCommand;
import main.POMPOM;
import storage.Storage;
import utils.Item;

/**
 * Integrated system testing
 * 
 * @@author A0121628L
 *
 */
public class TestSystem {

	POMPOM pompom;
	Date currentDate;
	ArrayList<Item> taskList;
	PrettyTimeParser timeParser;

	/**
	 * Method to initialize variables
	 * 
	 * @throws IOException
	 * 
	 * @@author A0121628L
	 */
	@Before
	public void init() throws IOException {
		pompom = new POMPOM();
		currentDate = new Date();
		timeParser = new PrettyTimeParser();
		pompom.saveSettings("SystemTest.txt");
		pompom.getStorage().init();
		taskList = POMPOM.getStorage().getTaskList();
		POMPOM.getStorage().saveStorage();
	}

	/********************************* Helper Methods ***************************************/
	/**
	 * Using the library we use in the pompom program to parse the date string.
	 * returns a date *
	 * 
	 * @param date
	 * @return Date
	 */
	private Date parseFromDate(String date) {
		List<Date> startDate;
		if (date != null) {
			startDate = timeParser.parse(date);

			if (startDate.size() == 0) {
				return null;
			} else {
				return startDate.get(0);
			}

		} else {
			return null;
		}

	}

	public boolean isSameItemList(ArrayList<Item> listA, ArrayList<Item> listB) {
		int lengthA = listA.size();
		int lengthB = listB.size();
		boolean sameList = true;
		if (lengthA != lengthB) {
			return false;
		} else {
			/** Test every aspect of the item value are equals to each other */
			for (int i = 0; i < lengthA; i++) {
				// If the previous item was not the same return false.
				if (!sameList) {
					return false;
				}
				Item currentItemA = listA.get(0);
				Item currentItemB = listB.get(0);

				sameList = testIsSameItem(currentItemA, currentItemB);
			}
			return sameList;
		}

	}

	public boolean testIsSameItem(Item itemA, Item itemB) {
		if (itemA == null && itemB == null) {
			return true;
		} else if (itemA == null && itemB != null) {
			return false;
		} else if (itemA != null && itemB == null) {
			return false;
		} else {
			if (!testStringEquivalence(itemA.getTitle(), itemB.getTitle())) {
				return false;
			}
			if (!testStringEquivalence(itemA.getLabel(), itemB.getLabel())) {
				return false;
			}
			if (!testStringEquivalence(itemA.getPriority(), itemB.getPriority())) {
				return false;
			}
			if (!testStringEquivalence(itemA.getSd(), itemB.getSd())) {
				return false;
			}
			if (!testStringEquivalence(itemA.getEd(), itemB.getEd())) {
				return false;
			}
			return true;
		}

	}

	public boolean testStringEquivalence(String stringA, String StringB) {
		if (stringA == null && StringB == null) {
			return true;
		} else if (stringA == null && StringB != null) {
			return false;
		} else if (stringA != null && StringB == null) {
			return false;
		} else {
			return stringA.equals(StringB);
		}

	}

	/********************************* End of Helper Methods ***************************************/

	/**
	 * Try adding a basic task and test whether it returns the correct title a
	 * not
	 * 
	 * @@author A0121628L
	 */
	@Test
	public void testAddTitleOnly() {
		// Make sure subsequent tests start from clean slate
		taskList.clear();

		String userCommand = "add do project";
		String returnMsg = pompom.execute(userCommand);

		// check if the add command returns the right status message
		assertEquals("Task added", returnMsg);

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do project", addedTask.getTitle());

	}

	/**
	 * Simple test to check whether add
	 */
	@Test
	public void testUndoAdd() {
		// Make sure subsequent tests start from clean slate
		taskList.clear();

		String userCommand = "add do project";
		String returnMsg = pompom.execute(userCommand);

		// check if the add command returns the right status message
		assertEquals("Task added", returnMsg);

		// check if the taskList contain the added task
		Item addedTask = taskList.get(0);
		assertEquals("do project", addedTask.getTitle());

		String undoCommand = "undo";
		returnMsg = pompom.execute(undoCommand);

		// check if the add command returns the right status message
		assertEquals(UndoCommand.MESSAGE_UNDO, returnMsg);
		assertEquals(0, taskList.size());

	}

	/**
	 *
	 * Full test for adding events and check storage whether desired events are
	 * added properly
	 * 
	 * @@author A0121628L
	 */
	@Test
	public void testAddAndDeleteEvent() {
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		// With all required fields except priority and label(Optional)
		String userCommand_1 = "event clubbing f:3 apr 2018 e:3 may 2019";
		String returnMsg_1 = pompom.execute(userCommand_1);
		// Test With all fields
		String userCommand_2 = "event Afternoon Nap f:today l:Sleep e:tomorrow p:h";
		String returnMsg_2 = pompom.execute(userCommand_2);
		// Test with all required fields except label and additional space
		// Jumble up delimiters
		String userCommand_3 = "event Trying for 3 syllabus title f:next monday p:high e:next year          ";
		String returnMsg_3 = pompom.execute(userCommand_3);
		// Test with all required fields except Priority Jumble up delimiters
		// again
		String userCommand_4 = "event Test 4 f:12/28/2015 l:test e:01/28/2016";
		String returnMsg_4 = pompom.execute(userCommand_4);

		// check if the add command returns the right status message
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Event"),
				returnMsg_1);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Event"),
				returnMsg_2);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Event"),
				returnMsg_3);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Event"),
				returnMsg_4);

		// Expected dates from library
		Date addedStartDate_1 = parseFromDate("3 apr 2018");
		Date addedEndDate_1 = parseFromDate("3 may 2019");

		Date addedStartDate_2 = parseFromDate("today");
		Date addedEndDate_2 = parseFromDate("tomorrow");

		Date addedStartDate_3 = parseFromDate("next monday");
		Date addedEndDate_3 = parseFromDate("next year");

		Date addedStartDate_4 = parseFromDate("12/28/2015");
		Date addedEndDate_4 = parseFromDate("01/28/2016");

		// Check if the task values are added correctly to the storage a not
		Item expectedAddedTask_1 = new Item(null, POMPOM.LABEL_EVENT,
				"clubbing", null, null, POMPOM.STATUS_PENDING, null,
				addedStartDate_1, addedEndDate_1);
		Item addedTask_1 = taskList.get(0);
		assertTrue(testIsSameItem(expectedAddedTask_1, addedTask_1));

		Item expectedAddedTask_2 = new Item(1L, POMPOM.LABEL_EVENT,
				"Afternoon Nap", "High", null, POMPOM.STATUS_ONGOING, "Sleep",
				addedStartDate_2, addedEndDate_2);
		Item addedTask_2 = taskList.get(1);
		assertTrue(testIsSameItem(expectedAddedTask_2, addedTask_2));

		Item expectedAddedTask_3 = new Item(null, POMPOM.LABEL_EVENT,
				"Trying for 3 syllabus title", "High", null,
				POMPOM.STATUS_ONGOING, null, addedStartDate_3, addedEndDate_3);
		Item addedTask_3 = taskList.get(2);
		assertTrue(testIsSameItem(addedTask_3, expectedAddedTask_3));
		Item expectedAddedTask_4 = new Item(null, POMPOM.LABEL_EVENT, "Test 4",
				null, null, POMPOM.STATUS_COMPLETED, "test", addedStartDate_4,
				addedEndDate_4);
		Item addedTask_4 = taskList.get(3);
		assertTrue(testIsSameItem(expectedAddedTask_4, addedTask_4));

		// Test a simple delete on a event
		String id = taskList.get(0).getId().toString();
		String userCommand_5 = "delete " + id;

		
		String returnMsg_5 = pompom.execute(userCommand_5);

		assertEquals(
				String.format(String.format(DelCommand.MESSAGE_TASK_DELETED,
						id, POMPOM.LABEL_EVENT), id), returnMsg_5);
		assertEquals(3, taskList.size());
	}

	/**
	 * Test recurring add the correct number of task a not. We are just going
	 * check the number of task added here because add recurring uses the Add
	 * command to add events one by one.
	 */
	@Test
	public void testAddRecurringEvent() {
		// Reset task list and add 21 events daily from 04/11 to 01/05
		taskList.clear();
		String userCommand_1 = "event asd f:04/11/2016 e:tomorrow p:h r:daily until 01 may 2016";
		String returnMsg_1 = pompom.execute(userCommand_1);

		assertEquals(String.format(AddRecurringCommand.MESSAGE_RECURRING, ""),
				returnMsg_1);
		assertEquals(21, taskList.size());

		// Reset task list and add 8 events. Weekly from 04/11 to 01/05
		taskList.clear();
		String userCommand_2 = "event asd f:04/11/2016 e:tomorrow p:h r:weekly until 30 may 2016";
		String returnMsg_2 = pompom.execute(userCommand_2);

		assertEquals(String.format(AddRecurringCommand.MESSAGE_RECURRING, ""),
				returnMsg_2);
		assertEquals(8, taskList.size());

		// Reset task list and add 4 events. biweekly from 04/11 to 01/05
		taskList.clear();
		String userCommand_3 = "event asd f:04/11/2016 e:tomorrow p:h r:biweekly until 30 may 2016";
		String returnMsg_3 = pompom.execute(userCommand_3);

		assertEquals(String.format(AddRecurringCommand.MESSAGE_RECURRING, ""),
				returnMsg_3);
		assertEquals(4, taskList.size());

		// Reset task list and add 2 events. r:monthly from 04/11 to 05/06
		taskList.clear();
		String userCommand_4 = "event asd f:04/11/2016 e:tomorrow p:h r:monthly until 05 june 2016";
		String returnMsg_4 = pompom.execute(userCommand_4);

		assertEquals(String.format(AddRecurringCommand.MESSAGE_RECURRING, ""),
				returnMsg_4);
		assertEquals(2, taskList.size());

		// Reset task list and add 2 events. r:monthly from 04/11 to 05/06
		taskList.clear();
		String userCommand_5 = "event asd f:04/11/2016 e:tomorrow p:h r:annually until 05 mar 2017";
		String returnMsg_5 = pompom.execute(userCommand_5);

		assertEquals(String.format(AddRecurringCommand.MESSAGE_RECURRING, ""),
				returnMsg_5);
		assertEquals(1, taskList.size());

		// Reset task list and add 2 events. r:daily from 11 apr to 30 apr 20
		// days exclude 6 DAYS
		// 13 APR TO 18 apr. which totals up too 14 events
		taskList.clear();
		String userCommand_6 = "event asd f:04/11/2016 e:tomorrow p:h r:daily until 30 apr 2016 x:13 apr to 18 apr";
		String returnMsg_6 = pompom.execute(userCommand_6);

		assertEquals(String.format(AddRecurringCommand.MESSAGE_RECURRING, ""),
				returnMsg_6);
		assertEquals(14, taskList.size());
	}

	/**
	 * This method conducts test on the possible bad commands at least on test
	 * heuristics methods are self explanatory
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAddEventBadCommands() throws IOException {
		int initialSize = taskList.size();
		String badUserCommand_1 = "event clubbing f: e:3 may 2019";
		String badReturnMsg_1 = pompom.execute(badUserCommand_1);

		// check if the add command returns the right invalid message
		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, ""),
				badReturnMsg_1);
		// Make sure nothing is added
		assertEquals(initialSize, POMPOM.getStorage().getTaskList().size());

		String badUserCommand_2 = "event clubbing  e:3 may 2019";
		String badReturnMsg_2 = pompom.execute(badUserCommand_2);

		assertEquals(String.format(AddParser.MESSAGE_DATES_SPECIFIED, ""),
				badReturnMsg_2);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_4 = "eveana e:3 may 2019";
		String badReturnMsg_4 = pompom.execute(badUserCommand_4);

		assertEquals(String.format(Parser.INVALID_CMD_MESSAGE, "eveana"),
				badReturnMsg_4);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_5 = "event asd e:asd";
		String badReturnMsg_5 = pompom.execute(badUserCommand_5);

		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, "asd"),
				badReturnMsg_5);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_6 = "event asd f:3 may 2015 e:2 may 2015";
		String badReturnMsg_6 = pompom.execute(badUserCommand_6);

		assertEquals(AddParser.MESSAGE_END_BEFORE_FROM, badReturnMsg_6);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_7 = "event asd f:3 may 2015 e:2 may 2015";
		String badReturnMsg_7 = pompom.execute(badUserCommand_7);

		assertEquals(AddParser.MESSAGE_END_BEFORE_FROM, badReturnMsg_7);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_8 = "event asd f:today e:tomorrow r:daily asdatil 01 may 2017 x:asdf";
		String badReturnMsg_8 = pompom.execute(badUserCommand_8);

		assertEquals(String.format(AddParser.MESSAGE_RECURRING_ERROR,
				"daily asdatil 01 may 2017"), badReturnMsg_8);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_9 = "event asd f:today e:tomorrow r:daily until 01 may 2017 x:asdf";
		String badReturnMsg_9 = pompom.execute(badUserCommand_9);

		assertEquals(String.format(AddParser.MESSAGE_EXCLUSION_ERROR, "asdf"),
				badReturnMsg_9);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_10 = "event asd f:today e:tomorrow p:ragge r:daily until 01 may 2017 today to 01 may 2017";
		String badReturnMsg_10 = pompom.execute(badUserCommand_10);

		assertEquals(String.format(AddParser.MESSAGE_PRIORITY_ERROR, "ragge"),
				badReturnMsg_10);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_11 = "add asd e:tomorrow p:h r:daily until 01 may 2017 today to 01 may 2017";
		String badReturnMsg_11 = pompom.execute(badUserCommand_11);

		assertEquals(AddParser.MESSAGE_START_DATE_SPECIFIED, badReturnMsg_11);
		assertEquals(initialSize, taskList.size());

	}

	/**
	 * Full test for adding task. Again using the at least once heuristic to
	 * make sure that the fields are working
	 */
	@Test
	public void testAddTask() {
		// Make sure subsequent tests start from clean slate
		taskList.clear();
		// Test with only end date
		String userCommand_1 = "add clubbing e:3 may 2019";
		String returnMsg_1 = pompom.execute(userCommand_1);

		// Test With all fields
		String userCommand_2 = "add Afternoon Nap f:today l:Sleep  p:h";
		String returnMsg_2 = pompom.execute(userCommand_2);
		// Test with all required fields except label and additional space
		// Jumble up delimiters
		String userCommand_3 = "add Trying for 3 syllabus title f:next monday p:high e:next year          ";
		String returnMsg_3 = pompom.execute(userCommand_3);
		// Test Add task with no date
		String userCommand_4 = "add Test 4  l:test ";
		String returnMsg_4 = pompom.execute(userCommand_4);
		// check if the add command returns the right status message
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_1);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_2);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_3);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_4);

		// Expected dates from library
		Date addedStartDate_1 = null;
		Date addedEndDate_1 = parseFromDate("3 may 2019");

		Date addedStartDate_2 = parseFromDate("today");
		Date addedEndDate_2 = null;

		Date addedStartDate_3 = parseFromDate("next monday");
		Date addedEndDate_3 = parseFromDate("next year");

		Date addedStartDate_4 = null;
		Date addedEndDate_4 = null;

		// Check if the task values are added correctly to the storage a not
		Item expectedAddedTask_1 = new Item(null, POMPOM.LABEL_EVENT,
				"clubbing", null, null, POMPOM.STATUS_PENDING, null,
				addedStartDate_1, addedEndDate_1);
		Item addedTask_1 = taskList.get(0);
		assertTrue(testIsSameItem(expectedAddedTask_1, addedTask_1));

		Item expectedAddedTask_2 = new Item(1L, POMPOM.LABEL_EVENT,
				"Afternoon Nap", "High", null, POMPOM.STATUS_ONGOING, "Sleep",
				addedStartDate_2, addedEndDate_2);
		Item addedTask_2 = taskList.get(1);
		assertTrue(testIsSameItem(expectedAddedTask_2, addedTask_2));

		Item expectedAddedTask_3 = new Item(null, POMPOM.LABEL_EVENT,
				"Trying for 3 syllabus title", "High", null,
				POMPOM.STATUS_ONGOING, null, addedStartDate_3, addedEndDate_3);
		Item addedTask_3 = taskList.get(2);
		assertTrue(testIsSameItem(addedTask_3, expectedAddedTask_3));
		Item expectedAddedTask_4 = new Item(null, POMPOM.LABEL_EVENT, "Test 4",
				null, null, POMPOM.STATUS_COMPLETED, "test", addedStartDate_4,
				addedEndDate_4);
		Item addedTask_4 = taskList.get(3);
		assertTrue(testIsSameItem(expectedAddedTask_4, addedTask_4));

	}

	/**
	 * Test all the bad commands of task and whether it returns the correct
	 * error message or not. This is the same type of test with event bad
	 * commands but to be complete we got to do with the task method as well
	 */
	@Test
	public void testAddTaskBadCommands() {
		int initialSize = taskList.size();
		String badUserCommand_1 = "add";  
		String badReturnMsg_1 = pompom.execute(badUserCommand_1);

		assertEquals(String.format(AddParser.MESSAGE_EMPTY_ERROR, ""),
				badReturnMsg_1);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_2 = "add do this f:asdf";
		String badReturnMsg_2 = pompom.execute(badUserCommand_2);

		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, "asdf"),
				badReturnMsg_2);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_4 = "add do this f:today e:tomorrow r:asdf";
		String badReturnMsg_4 = pompom.execute(badUserCommand_4);

		assertEquals(String.format(AddParser.MESSAGE_RECURRING_ERROR, "asdf"),
				badReturnMsg_4);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_5 = "add do this f:today e:tomorrow r:daily until 01 may 2017 x:asdf";
		String badReturnMsg_5 = pompom.execute(badUserCommand_5);

		assertEquals(String.format(AddParser.MESSAGE_EXCLUSION_ERROR, "asdf"),
				badReturnMsg_5);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_6 = "add do this p:asdf";
		String badReturnMsg_6 = pompom.execute(badUserCommand_6);

		assertEquals(String.format(AddParser.MESSAGE_PRIORITY_ERROR, "asdf"),
				badReturnMsg_6);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_7 = "add do this e:today f:tomorrow";
		String badReturnMsg_7 = pompom.execute(badUserCommand_7);

		assertEquals(String.format(AddParser.MESSAGE_END_BEFORE_FROM, "asdf"),
				badReturnMsg_7);
		assertEquals(initialSize, taskList.size());

		String badUserCommand_8 = "add do this r:daily until 01 may 2017";
		String badReturnMsg_8 = pompom.execute(badUserCommand_8);

		assertEquals(String.format(AddParser.MESSAGE_START_DATE_SPECIFIED, ""),
				badReturnMsg_8);
		assertEquals(initialSize, taskList.size());

	}

	/**
	 * Test deleting a normal task and whether it handles invalid delete id
	 * correctly or not
	 */
	@Test
	public void testDeleteById() {

		// Make sure subsequent tests start from clean slate
		taskList.clear();

		String userCommand_1 = "add do project1 f:april 1";
		String userCommand_2 = "add do project2 f:april 2";
		String userCommand_3 = "add do project3 f:april 3";

		String returnMsg_1 = pompom.execute(userCommand_1);
		String returnMsg_2 = pompom.execute(userCommand_2);
		String returnMsg_3 = pompom.execute(userCommand_3);

		// check if the add commands returns the right status message
		assertEquals("Task added", returnMsg_1);
		assertEquals("Task added", returnMsg_2);
		assertEquals("Task added", returnMsg_3);

		long id = taskList.get(1).getId();
		String delCommand = "delete " + id;
		pompom.execute(delCommand);

		// check if the correct task got deleted
		assertEquals(2, taskList.size());
		assertEquals("do project1", taskList.get(0).getTitle());
		assertEquals("do project3", taskList.get(1).getTitle());

		// Test invalid delete
		String delFail = "delete " + "99999";
		String delFailReturnMsg = pompom.execute(delFail);

		assertEquals(String.format(DelCommand.MESSAGE_ID_INVALID, "99999"),
				delFailReturnMsg);
	}

	/**
	 * Test undo a recurring add and deleterecur we test this by checking the
	 * tasklist size as we assume task is added correctly.
	 */
	@Test
	public void testDeleteOnRecurringAndUndo() {

		// Make sure subsequent tests start from clean slate
		taskList.clear();
		// 20 days
		String userCommand_1 = "add test f:04/11/2016 e:tomorrow p:h r:daily until 30 apr 2016";
		String returnMsg_1 = pompom.execute(userCommand_1);

		// check if the add commands returns the right status message
		assertEquals(AddRecurringCommand.MESSAGE_RECURRING, returnMsg_1);
		assertEquals(20, taskList.size());
		String undoCommnad_1 = "undo";
		String undoReturnMsg_1 = pompom.execute(undoCommnad_1);

		assertEquals(UndoCommand.MESSAGE_UNDO, undoReturnMsg_1);
		assertEquals(0, taskList.size());

		// Then add back the same things
		returnMsg_1 = pompom.execute(userCommand_1);
		assertEquals(AddRecurringCommand.MESSAGE_RECURRING, returnMsg_1);

		// Delete a middle task first
		long id_1 = taskList.get(1).getId();
		String stringId_1 = Long.toString(id_1);
		String delCommand = "delete " + id_1;
		String deleteReturnMsg = pompom.execute(delCommand);
		assertEquals(String.format(DelCommand.MESSAGE_TASK_DELETED, stringId_1,
				"Task"), deleteReturnMsg);
		assertEquals(19, taskList.size());

		// Delete recurring
		long id = taskList.get(2).getId();
		String delRecurCommand = "deleterecur " + id;
		String deleteRecurReturnMsg = pompom.execute(delRecurCommand);

		assertEquals(DelRecurringCommand.MESSAGE_DELETE_RECURRING,
				deleteRecurReturnMsg);
		// check if the delete command returns the right status message

		// check if the correct task got deleted
		assertEquals(0, taskList.size());

		// Try undo
		String undoCommand_2 = "undo";
		String undoReturnMsg_2 = pompom.execute(undoCommand_2);

		// check if the correct task got deleted
		assertEquals(UndoCommand.MESSAGE_UNDO, undoReturnMsg_2);
		assertEquals(19, taskList.size());
	}

	/**
	 * Simple test for editing title
	 */
	@Test
	public void testEditTitle() {

		// Make sure subsequent tests start from clean slate
		taskList.clear();

		String userCommand_1 = "add do project1 april 1";
		String userCommand_2 = "add do project2 april 2";
		String userCommand_3 = "add do project3 april 3";

		String returnMsg_1 = pompom.execute(userCommand_1);
		String returnMsg_2 = pompom.execute(userCommand_2);
		String returnMsg_3 = pompom.execute(userCommand_3);

		// check if the add commands returns the right status message
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_1);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_2);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_3);

		long id_1 = taskList.get(0).getId();
		long id_2 = taskList.get(1).getId();
		long id_3 = taskList.get(2).getId();

		String editCommand_1 = "edit " + id_1 + " title do project4";
		String editCommand_2 = "edit " + id_2 + " title do project5";
		String editCommand_3 = "edit " + id_3 + " title do project6";

		String returnMsg_4 = pompom.execute(editCommand_1);
		String returnMsg_5 = pompom.execute(editCommand_2);
		String returnMsg_6 = pompom.execute(editCommand_3);

		// check if the edit command returns the right status message
		assertEquals(id_1 + " was successfully edited", returnMsg_4);
		assertEquals(id_2 + " was successfully edited", returnMsg_5);
		assertEquals(id_3 + " was successfully edited", returnMsg_6);

		// check if the correct task got deleted
		assertEquals(3, taskList.size());
		assertEquals("do project4", taskList.get(0).getTitle());
		assertEquals("do project5", taskList.get(1).getTitle());
		assertEquals("do project6", taskList.get(2).getTitle());

	}

	/**
	 * Simple test for editing date
	 */
	@Test
	public void testEditDate() {

		// Make sure subsequent tests start from clean slate
		taskList.clear();

		String userCommand_1 = "add do project1 f:april 1";
		String userCommand_2 = "add do project2 f:april 2";
		String userCommand_3 = "add do project3 f:april 3";

		String returnMsg_1 = pompom.execute(userCommand_1);
		String returnMsg_2 = pompom.execute(userCommand_2);
		String returnMsg_3 = pompom.execute(userCommand_3);

		// check if the add commands returns the right status message
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_1);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_2);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_3);

		long id_1 = taskList.get(0).getId();
		long id_2 = taskList.get(1).getId();
		long id_3 = taskList.get(2).getId();

		String stringId_1 = Long.toString(id_1);
		String stringId_3 = Long.toString(id_3);

		String editCommand_1 = "edit " + id_1 + " start date today";
		// This command should fail invalid end date
		String editCommand_2 = "edit " + id_2 + " end date 01/11/2013";
		String editCommand_3 = "edit " + id_3 + " end date next year";

		String returnMsg_4 = pompom.execute(editCommand_1);
		String returnMsg_5 = pompom.execute(editCommand_2);
		String returnMsg_6 = pompom.execute(editCommand_3);

		// check if the edit command returns the right status message
		assertEquals(
				String.format(EditCommand.MESSAGE_TASK_EDITED, stringId_1),
				returnMsg_4);
		assertEquals(EditCommand.MESSAGE_DATE_CHANGE_ERROR, returnMsg_5);
		assertEquals(
				String.format(EditCommand.MESSAGE_TASK_EDITED, stringId_3),
				returnMsg_6);

		// Check if list still contains 3 times
		assertEquals(3, taskList.size());
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
		assertEquals(sdf.format(parseFromDate("today")), taskList.get(0)
				.getSd());
		assertEquals(sdf.format(parseFromDate("april 2")), taskList.get(1)
				.getSd());
		assertEquals(sdf.format(parseFromDate("next year")), taskList.get(2)
				.getEd());

	}

	/**
	 * Simple test for editing label
	 */
	@Test
	public void testEditLabel() {

		// Make sure subsequent tests start from clean slate
		taskList.clear();

		String userCommand_1 = "add do project1 april 1 l:work";
		String userCommand_2 = "add do project2 april 2 l:work";
		String userCommand_3 = "add do project3 april 3 l:work";

		String returnMsg_1 = pompom.execute(userCommand_1);
		String returnMsg_2 = pompom.execute(userCommand_2);
		String returnMsg_3 = pompom.execute(userCommand_3);

		// check if the add commands returns the right status message
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_1);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_2);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_3);

		long id_1 = taskList.get(0).getId();
		long id_2 = taskList.get(1).getId();
		long id_3 = taskList.get(2).getId();

		String editCommand_1 = "edit " + id_1 + " label do work";
		String editCommand_2 = "edit " + id_2 + " label more work";
		String editCommand_3 = "edit " + id_3 + " label even more work";

		String returnMsg_4 = pompom.execute(editCommand_1);
		String returnMsg_5 = pompom.execute(editCommand_2);
		String returnMsg_6 = pompom.execute(editCommand_3);

		// check if the edit command returns the right status message
		assertEquals(id_1 + " was successfully edited", returnMsg_4);
		assertEquals(id_2 + " was successfully edited", returnMsg_5);
		assertEquals(id_3 + " was successfully edited", returnMsg_6);

		// check if the correct task got deleted
		assertEquals(3, taskList.size());
		assertEquals("do work", taskList.get(0).getLabel());
		assertEquals("more work", taskList.get(1).getLabel());
		assertEquals("even more work", taskList.get(2).getLabel());

	}

	/**
	 * Simple test on editing priority
	 */
	@Test
	public void testEditPriority() {

		// Make sure subsequent tests start from clean slate
		taskList.clear();

		String userCommand_1 = "add do project1 april 1 l:work";
		String userCommand_2 = "add do project2 april 2 l:work";
		String userCommand_3 = "add do project3 april 3 l:work";

		String returnMsg_1 = pompom.execute(userCommand_1);
		String returnMsg_2 = pompom.execute(userCommand_2);
		String returnMsg_3 = pompom.execute(userCommand_3);

		// check if the add commands returns the right status message
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_1);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_2);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_3);

		long id_1 = taskList.get(0).getId();
		long id_2 = taskList.get(1).getId();
		long id_3 = taskList.get(2).getId();

		String editCommand_1 = "edit " + id_1 + " priority low";
		String editCommand_2 = "edit " + id_2 + " priority medium";
		String editCommand_3 = "edit " + id_3 + " priority high";

		String returnMsg_4 = pompom.execute(editCommand_1);
		String returnMsg_5 = pompom.execute(editCommand_2);
		String returnMsg_6 = pompom.execute(editCommand_3);

		// check if the edit command returns the right status message
		assertEquals(id_1 + " was successfully edited", returnMsg_4);
		assertEquals(id_2 + " was successfully edited", returnMsg_5);
		assertEquals(id_3 + " was successfully edited", returnMsg_6);

		// check if the correct task got deleted
		assertEquals(3, taskList.size());
		assertEquals("Low", taskList.get(0).getPriority());
		assertEquals("Medium", taskList.get(1).getPriority());
		assertEquals("High", taskList.get(2).getPriority());

	}

	/**
	 * Simple test on edit recurring
	 */
	@Test
	public void testEditRecurring() {
		taskList.clear();
		String userCommand_1 = "add test f:04/11/2016 e:tomorrow l:Sweet p:h r:daily until 30 apr 2016";
		String returnMsg_1 = pompom.execute(userCommand_1);

		// check if the add commands returns the right status message
		assertEquals(AddRecurringCommand.MESSAGE_RECURRING, returnMsg_1);
		long id = taskList.get(0).getId();
		String userCommand_2 = "editrecur " + id + " title new testing";
		String returnMsg_2 = pompom.execute(userCommand_2);
		assertEquals(EditRecurringCommand.MESSAGE_EDIT_RECURRING, returnMsg_2);

		for (Item item : taskList) {
			assertEquals(item.getTitle(), "new testing");
		}

		String userCommand_3 = "editrecur " + id + " priority high";
		String returnMsg_3 = pompom.execute(userCommand_3);
		assertEquals(EditRecurringCommand.MESSAGE_EDIT_RECURRING, returnMsg_3);

		for (Item item : taskList) {
			assertEquals(item.getPriority(), "High");
		}

		String userCommand_4 = "editrecur " + id + " label Test Label";
		String returnMsg_4 = pompom.execute(userCommand_4);
		assertEquals(EditRecurringCommand.MESSAGE_EDIT_RECURRING, returnMsg_4);

		for (Item item : taskList) { 
			assertEquals(item.getLabel(), "Test Label");
		}
		// Test does undo returns original Label a not
		String undoCommand = "undo";
		String undoReturnMsg = pompom.execute(undoCommand);

		assertEquals(UndoCommand.MESSAGE_UNDO, undoReturnMsg);
		for (Item item : taskList) {
			assertEquals(item.getLabel(), "Sweet");
		}
	}

	/**
	 * Simple test on search. Search allows for error. For more information on
	 * the search algorithm Please view search command class.
	 */
	@Test
	public void testSearch() {

		// Make sure subsequent tests start from clean slate
		taskList.clear();

		String userCommand_1 = "add do project1 april 1 l:work";
		String userCommand_2 = "add do project2 april 2 l:work";
		String userCommand_3 = "add asdas april 3 l:work";

		String returnMsg_1 = pompom.execute(userCommand_1);
		String returnMsg_2 = pompom.execute(userCommand_2);
		String returnMsg_3 = pompom.execute(userCommand_3);

		// check if the add commands returns the right status message
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_1);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_2);
		assertEquals(String.format(AddCommand.MESSAGE_TASK_ADDED, "Task"),
				returnMsg_3);

		String searchCommand_1 = "search do";
		String searchCommand_2 = "search project";
		String searchCommand_3 = "search asdas";

		String returnMsg_4 = pompom.execute(searchCommand_1);
		assertEquals("Search resulted in 2 result(s).", returnMsg_4);
		assertEquals(2, POMPOM.getSearchList().size());

		String returnMsg_5 = pompom.execute(searchCommand_2);
		assertEquals("Search resulted in 2 result(s).", returnMsg_5);
		assertEquals(2, POMPOM.getSearchList().size());

		String returnMsg_6 = pompom.execute(searchCommand_3);
		assertEquals("Search resulted in 1 result(s).", returnMsg_6);
		assertEquals(1, POMPOM.getSearchList().size());

	}

	/**
	 * Reset to the state before test was conducted
	 * 
	 * @throws IOException
	 */
	@After
	public void resetToOriginal() throws IOException {
		POMPOM.saveSettings(Storage.DEFAULT_STORAGE_FILE_PATH);
	}

}
