package parser;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import main.POMPOM;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;

import org.junit.Test;
/**
 * @@author Josh
 *
 */
public class AddParserTest{

	Parser parser = Parser.getInstance();
	PrettyTimeParser timeParser = new PrettyTimeParser();
	
	/*
	 * Tests if can add floating tasks
	 */
	@Test
	public void testAddCommandTitleOnly(){
		AddParser add = new AddParser("do project",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertNull(add.getPriority());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can set priority normally
	 */
	@Test
	public void testAddCommandPriority(){
		AddParser add = new AddParser("do project p:high",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("high",add.getPriority());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can set priority normally (with a space in between)
	 */
	@Test
	public void testAddCommandPriority2(){
		AddParser add = new AddParser("do project p: high",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("high",add.getPriority());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can set priority with shortcut
	 */
	@Test
	public void testAddCommandPriorityShortcut(){
		AddParser add = new AddParser("do project p:h",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("high",add.getPriority());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can set label
	 */
	@Test
	public void testAddCommandLabel(){
		AddParser add = new AddParser("do project l:school work",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("school work",add.getLabel());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getPriority());	
		assertTrue(add.isValidArguments);
		
	}
	
	/*
	 * Tests if can set label and priority
	 */
	@Test
	public void testAddCommandLabelAndPriority(){
		AddParser add = new AddParser("do project l: school work p: high",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("school work",add.getLabel());
		assertEquals("high",add.getPriority());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can add tasks with end date
	 */
	@Test
	public void testAddCommandEndDate(){
		AddParser add = new AddParser("do project 28 march",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifferenceInSeconds = getEndDateDifference(add,"28 march");
		assertEquals(endDateDifferenceInSeconds,0);	
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can add tasks with end date prefix
	 */
	@Test
	public void testAddCommandEndDatePrefix(){
		AddParser add = new AddParser("do project e: 28 march",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifferenceInSeconds = getEndDateDifference(add, "28 march");
		assertEquals(endDateDifferenceInSeconds,0);	
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}

	
	/*
	 * Tests if can add tasks with start and end date.
	 */
	@Test
	public void testAddCommandStartEndDate(){
		AddParser add = new AddParser("do project e:28 march f:16 march",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"28 march");
		long startDateDifference= getStartDateDifference(add,"16 march");
		assertEquals(endDateDifference, 0);	
		assertEquals(startDateDifference, 0);	
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can add tasks with all fields filled
	 */
	@Test
	public void testAddCommandFullTask(){
		AddParser add = new AddParser("do project e:28 march f:16 march l:soc homework p:h",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"28 march");
		long startDateDifference= getStartDateDifference(add,"16 march");
		assertEquals("soc homework",add.getLabel());
		assertEquals(POMPOM.PRIORITY_HIGH, add.getPriority());
		assertEquals(endDateDifference, 0);	
		assertEquals(startDateDifference, 0);	
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	@Test
	public void testAddCommandFullEvent(){
		AddParser add = new AddParser("do project e:28 march f:16 march l:soc homework p:h",POMPOM.LABEL_EVENT);
		assertEquals("do project",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"28 march");
		long startDateDifference= getStartDateDifference(add,"16 march");
		assertEquals("soc homework",add.getLabel());
		assertEquals(POMPOM.PRIORITY_HIGH, add.getPriority());
		assertEquals(endDateDifference, 0);	
		assertEquals(startDateDifference, 0);	
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	
	/*
	 * Tests if can switch the order of the title and end date
	 */
	@Test
	public void testAddCommandReorder(){
		AddParser add = new AddParser("28 march do project",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifferenceInSeconds = getEndDateDifference(add,"28 march");
		assertEquals(endDateDifferenceInSeconds,0);		
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can switch the order of the start date, end date and title. 
	 */
	@Test
	public void testAddCommandReorderWithEndDate(){
		AddParser add = new AddParser("e:28 march f:16 march do project",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		Date endDate= timeParser.parseSyntax("28 march").get(0).getDates().get(0);
		Date startDate= timeParser.parseSyntax("16 march").get(0).getDates().get(0);
		assertEquals(endDate.compareTo(add.getEndDate()),1);	
		assertEquals(startDate.compareTo(add.getStartDate()),1);
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can switch the order of the start date, end date and title.
	 */
	@Test
	public void testAddCommandReorderWithEndDateFromFirst(){
		AddParser add = new AddParser("f:16 march e:28 march do project",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		Date endDate= timeParser.parseSyntax("28 march").get(0).getDates().get(0);
		Date startDate= timeParser.parseSyntax("16 march").get(0).getDates().get(0);
		assertEquals(endDate.compareTo(add.getEndDate()),1);	
		assertEquals(startDate.compareTo(add.getStartDate()),1);
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can add in weekly recurring tasks with end date
	 */
	@Test
	public void testAddCommandRecurringBasic(){
		AddParser add = new AddParser("do cs2103 every friday e:6 june",POMPOM.LABEL_TASK);
		assertEquals("do cs2103",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"6 june");
		assertEquals(endDateDifference,0);		
		DateGroup itemRecurringDateGroup = add.getItemRecurringDateGroup();
		Date addStartDate = itemRecurringDateGroup.getDates().get(0);
		long startDateDifference= getStartDateDifference(addStartDate,"this friday");
		assertEquals(startDateDifference,0);	
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());	
		assertTrue(add.getIsRecurring());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * tests if can switch the order of the fields for adding recurring tasks with
	 * end date.
	 */
	@Test
	public void testAddCommand8(){
		AddParser add = new AddParser("do cs2103 e:6 june every friday",POMPOM.LABEL_TASK);
		assertEquals("do cs2103",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"6 june");
		assertEquals(endDateDifference,0);		
		DateGroup itemRecurringDateGroup = add.getItemRecurringDateGroup();
		Date addStartDate = itemRecurringDateGroup.getDates().get(0);
		long startDateDifference= getStartDateDifference(addStartDate,"this friday");
		assertEquals(startDateDifference,0);
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());	
		assertTrue(add.getIsRecurring());
		assertTrue(add.isValidArguments);
	}	
	
	/*
	 * Tests if can add in a task with only a parsable title 
	 */
	@Test
	public void testAddCommand9(){
		AddParser add = new AddParser("2103",POMPOM.LABEL_TASK);
		assertEquals("2103",add.getTitle());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertNull(add.getPriority());
		assertTrue(add.isValidArguments);
	}	
	
	/*
	 * Tests if can add in a task with only a parsable title 
	 */
	@Test
	public void testAddCommandInvalidPriority(){
		AddParser add = new AddParser("2103 p:lol",POMPOM.LABEL_TASK);
		assertEquals("2103",add.getTitle());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertNull(add.getPriority());
		assertFalse(add.isValidArguments);
	}	
	
	//helper methods
	private long getEndDateDifference(AddParser add, String dateString) {
		long expectedEndDateInMillis= timeParser.parseSyntax(dateString).get(0).getDates().get(0).getTime();
		long parsedEndDateInMillis = add.getEndDate().getTime(); 
		long endDateDifferenceInSeconds = (expectedEndDateInMillis-parsedEndDateInMillis)/1000;
		return endDateDifferenceInSeconds;
	}
	
	private long getStartDateDifference(AddParser add, String dateString) {
		long expectedStartDateInMillis= timeParser.parseSyntax(dateString).get(0).getDates().get(0).getTime();
		long parsedStartDateInMillis = add.getStartDate().getTime(); 
		long startDateDifferenceInSeconds = (expectedStartDateInMillis-parsedStartDateInMillis)/1000;
		return startDateDifferenceInSeconds;
	}
	
	private long getEndDateDifference(Date add, String dateString) {
		long expectedEndDateInMillis= timeParser.parseSyntax(dateString).get(0).getDates().get(0).getTime();
		long parsedEndDateInMillis = add.getTime(); 
		long endDateDifferenceInSeconds = (expectedEndDateInMillis-parsedEndDateInMillis)/1000;
		return endDateDifferenceInSeconds;
	}
	
	private long getStartDateDifference(Date add, String dateString) {
		long expectedStartDateInMillis= timeParser.parseSyntax(dateString).get(0).getDates().get(0).getTime();
		long parsedStartDateInMillis = add.getTime(); 
		long startDateDifferenceInSeconds = (expectedStartDateInMillis-parsedStartDateInMillis)/1000;
		return startDateDifferenceInSeconds;
	}
}
	
	