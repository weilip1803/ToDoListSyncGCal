package parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import command.AddCommand;
import command.AddRecurringCommand;
import command.Command;
import command.InvalidCommand;
import main.POMPOM;
import utils.Item;
import Test.TestSystem;

import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;


/**
 * @@author A0121760R
 *
 */

public class AddParserTest {

	POMPOM pompom = new POMPOM();
	TestSystem test = new TestSystem();
	PrettyTimeParser timeParser = pompom.timeParser;
	
	/*
	 * For Reference:
	 * 
	 * Non-Recurring:
	 * 
	 * AddCommand(Task/Event, title, description,
	 * 				priority, status(pending/overdue),label
	 * 				startDate, endDate, isRecurring)
	 * 
	 * Recurring:
	 * 
	 * AddCommand(Task/Event, title, description, 
	 * 			priority, status(pending/overdue), label, 
	 * 			recurringStartDates.get(i), recurringEndDates.get(i), 
	 * 			isRecurring);
	 *
	 */
	
	//Good Cases
	@Test
	public void testTitleOnly() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103",false);
		addParser.parse().execute();
	
		//Expected Case
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "do cs2103", null, null, 
										POMPOM.STATUS_PENDING, null, null, 
										null, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testTitleAndStartDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 f:tomorrow",false);
		addParser.parse().execute();
	
		//Expected Case
		Date startDate = getDate("tomorrow");
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "do cs2103", null, null, 
										POMPOM.STATUS_PENDING, null, startDate, 
										null, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testTitleAndEndDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 e:20 april",false);
		addParser.parse().execute();
	
		//Expected Case
		Date endDate = getDate("20 april");
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "do cs2103", null, null, 
										POMPOM.STATUS_PENDING, null, null, 
										endDate, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testTitleAndLabel() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 l:homework",false);
		addParser.parse().execute();
	
		//Expected Case
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "do cs2103", null, null, 
										POMPOM.STATUS_PENDING, "homework", null, 
										null, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testTitleAndPriority() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 p:h",false);
		addParser.parse().execute();
	
		//Expected Case
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "do cs2103", null, "high", 
										POMPOM.STATUS_PENDING, null, null, 
										null, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testTitleAndStartAndEndDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 f:tomorrow e:20 april",false);
		addParser.parse().execute();
	
		//Expected Case
		Date startDate = getDate("tomorrow");
		Date endDate = getDate("20 april");
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "do cs2103", null, null, 
										POMPOM.STATUS_PENDING, null, startDate, 
										endDate, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testTitleAndStartAndEndDateWiithLabel() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 f:tomorrow e:20 april l:homework",false);
		addParser.parse().execute();
	
		//Expected Case
		Date startDate = getDate("tomorrow");
		Date endDate = getDate("20 april");
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "do cs2103", null, null, 
										POMPOM.STATUS_PENDING, "homework", startDate, 
										endDate, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testTitleAndStartAndEndDateWiithPriority() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 f:tomorrow e:20 april p:hi",false);
		addParser.parse().execute();
	
		//Expected Case
		Date startDate = getDate("tomorrow");
		Date endDate = getDate("20 april");
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "do cs2103", null, "high", 
										POMPOM.STATUS_PENDING, null, startDate, 
										endDate, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testFullCommand() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 f:tomorrow e:20 april l:homework p:h",false);
		addParser.parse().execute();
	
		//Expected Case
		Date startDate = getDate("tomorrow");
		Date endDate = getDate("20 april");
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "do cs2103", null, "high", 
										POMPOM.STATUS_PENDING, "homework", startDate, 
										endDate, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testBasicRecurring() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 f:12 april e:13 april r:daily until 16 april",false);
		addParser.parse().execute();
	
		//Expected Case
		ArrayList<AddCommand> recurringAddCommands = new ArrayList<AddCommand>();
		ArrayList<Date> startDateList = new ArrayList<Date>();
		startDateList.add(getDate("12 april"));
		startDateList.add(getDate("13 april"));
		startDateList.add(getDate("14 april"));
		startDateList.add(getDate("15 april"));
		startDateList.add(getDate("16 april"));
		
		ArrayList<Date> endDateList = new ArrayList<Date>();
		endDateList.add(getDate("13 april"));
		endDateList.add(getDate("14 april"));
		endDateList.add(getDate("15 april"));
		endDateList.add(getDate("16 april"));
		endDateList.add(getDate("17 april"));
		
		for (int i=0; i<startDateList.size(); i++){
			recurringAddCommands.add(new AddCommand(POMPOM.LABEL_TASK, "do cs2103", 
										null, null, POMPOM.STATUS_PENDING, null, 
										startDateList.get(i), endDateList.get(i), true));
		}
		
		Command callFromCommand = new AddRecurringCommand(recurringAddCommands);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality;
		Item expected;
		for (int i=0; i<startDateList.size(); i++){
			reality = taskList.get(i);
			expected = taskList.get(i+startDateList.size());
			assertTrue(test.testIsSameItem(expected, reality));
		}
		
	}
	
	@Test
	public void testBasicRecurringWithExclude() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("do cs2103 f:12 april e:13 april r:daily until 16 april x:12 april to 13 april",false);
		addParser.parse().execute();
	
		//Expected Case
		ArrayList<AddCommand> recurringAddCommands = new ArrayList<AddCommand>();
		ArrayList<Date> startDateList = new ArrayList<Date>();
		startDateList.add(getDate("14 april"));
		startDateList.add(getDate("15 april"));
		startDateList.add(getDate("16 april"));
		
		ArrayList<Date> endDateList = new ArrayList<Date>();
		endDateList.add(getDate("15 april"));
		endDateList.add(getDate("16 april"));
		endDateList.add(getDate("17 april"));
		
		for (int i=0; i<startDateList.size(); i++){
			recurringAddCommands.add(new AddCommand(POMPOM.LABEL_TASK, "do cs2103", 
										null, null, POMPOM.STATUS_PENDING, null, 
										startDateList.get(i), endDateList.get(i), true));
		}
		
		Command callFromCommand = new AddRecurringCommand(recurringAddCommands);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality;
		Item expected;;
		for (int i=0; i<startDateList.size(); i++){
			reality = taskList.get(i);
			expected = taskList.get(i+startDateList.size());
			assertTrue(test.testIsSameItem(expected, reality));
		}
		
	}
	
	//Corner cases
	@Test
	public void testInvalidDelimiters() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("k:try this",false);
		addParser.parse().execute();
	
		//Expected Case
		Command callFromCommand = new AddCommand(POMPOM.LABEL_TASK, "k:try this", null, null, 
										POMPOM.STATUS_PENDING, null, null, 
										null, false);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality = taskList.get(0);
		Item expected = taskList.get(1);
		assertTrue(test.testIsSameItem(expected, reality));
	}
	
	@Test
	public void testRemoveSpaces() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("asd f:today e:tomorrow l:Work p:hr:daily until 17 aprx:14 apr to 15 apr",false);
		addParser.parse().execute();
	
		//Expected Case
		ArrayList<AddCommand> recurringAddCommands = new ArrayList<AddCommand>();
		ArrayList<Date> startDateList = new ArrayList<Date>();
		startDateList.add(getDate("11 april"));
		startDateList.add(getDate("12 april"));
		startDateList.add(getDate("13 april"));
		startDateList.add(getDate("16 april"));
		startDateList.add(getDate("17 april"));
		
		
		ArrayList<Date> endDateList = new ArrayList<Date>();
		endDateList.add(getDate("12 april"));
		endDateList.add(getDate("13 april"));
		endDateList.add(getDate("14 april"));
		endDateList.add(getDate("17 april"));
		endDateList.add(getDate("18 april"));

		
		for (int i=0; i<startDateList.size(); i++){
			recurringAddCommands.add(new AddCommand(POMPOM.LABEL_TASK, "asd", 
										null, "high", POMPOM.STATUS_PENDING, "Work", 
										startDateList.get(i), endDateList.get(i), true));
		}
		
		Command callFromCommand = new AddRecurringCommand(recurringAddCommands);
		callFromCommand.execute();
		
		//Get the tasks and compare
		Item reality;
		Item expected;
		for (int i=0; i<startDateList.size(); i++){
			reality = taskList.get(i);
			expected = taskList.get(i+startDateList.size());
			assertTrue(test.testIsSameItem(expected, reality));
		}
		
	}
	
	//Bad Cases
	@Test
	public void testEmptyArguments() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser(null,false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(AddParser.MESSAGE_EMPTY_ERROR);
		assertEquals(AddParser.MESSAGE_EMPTY_ERROR,invalidCommand.execute());
		
	}
	
	@Test
	public void testSpaceArgument() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser(" ",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(AddParser.MESSAGE_EMPTY_ERROR);
		assertEquals(AddParser.MESSAGE_EMPTY_ERROR,invalidCommand.execute());
		
	}
	
	@Test
	public void testInvalidEndDateFull() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("title lol f:tomorrow e:the day after r:weekly"
											+ "until 30 april x:13 april to 24 april",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(String.format(AddParser.MESSAGE_DATE_ERROR, "the day after"));
		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, "the day after"), invalidCommand.execute());
		
	}
	
	@Test
	public void testInvalidFromDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("add test f:lol",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"));
		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"), invalidCommand.execute());
	}
	
	@Test
	public void testInvalidEndDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("add test e:lol",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"));
		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"), invalidCommand.execute());
	}
	
	@Test
	public void testInvalidFromAndEndDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("add test f:lol e:lol",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"));
		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"), invalidCommand.execute());
	}
	
	@Test
	public void testInvalidEndDateValidStartDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("add test f:tomorrow e:lol",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"));
		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"), invalidCommand.execute());
	}
	
	@Test
	public void testInvalidStartDateValidEndDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("add test f:lol e:tomorrow",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"));
		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, "lol"), invalidCommand.execute());
	}
	
	@Test
	public void testEmptyStartDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("add test f: e:tomorrow",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(String.format(AddParser.MESSAGE_DATE_ERROR, ""));
		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, ""), invalidCommand.execute());
	}
	
	@Test
	public void testEmptyEndDate() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("add test f:tomorrow e:",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(String.format(AddParser.MESSAGE_DATE_ERROR, ""));
		assertEquals(String.format(AddParser.MESSAGE_DATE_ERROR, ""), invalidCommand.execute());
	}
	
	@Test
	public void testAddEmptyLabelNoTitle() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("add l:",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(AddParser.MESSAGE_EMPTY_ERROR);
		assertEquals(String.format(AddParser.MESSAGE_EMPTY_ERROR), invalidCommand.execute());
	}
	
	
	@Test
	public void testAddEmptyPriorityNoTitle() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		
		//Reality Case
		AddParser addParser = new AddParser("add p:",false);
		addParser.parse().execute();
	
		//Expected Case
		Command invalidCommand = new InvalidCommand(AddParser.MESSAGE_EMPTY_ERROR);
		assertEquals(String.format(AddParser.MESSAGE_EMPTY_ERROR), invalidCommand.execute());
	}
	
	
	//Helper Methods
	
	private Date getDate(String dateString) {
		return timeParser.parseSyntax(dateString).get(0).getDates().get(0);
	}


}
