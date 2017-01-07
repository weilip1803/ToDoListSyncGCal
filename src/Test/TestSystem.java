package Test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;

import org.junit.Test;

import main.POMPOM;
import utils.Item;
/**
 * @@author wen hao
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
