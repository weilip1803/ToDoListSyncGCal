package parser;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import Test.TestSystem;
import command.DelCommand;
import main.POMPOM;
import utils.Item;
/**
 *  @@author A0121760R
 *
 */
public class DoneParserTest {

	POMPOM pompom = new POMPOM();
	TestSystem test = new TestSystem();
	PrettyTimeParser timeParser = pompom.timeParser;
	
	@Test
	public void testDelete() {
		//Initialize and fill up tasklist
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		AddParser addParser = new AddParser("do cs2103",false);
		addParser.parse().execute();
	
		//Run deleteParser and make sure the tasklist becomes empty.
		Long itemId = taskList.get(0).getId();
		DoneParser doneParser = new DoneParser(itemId.toString());
		assertEquals(taskList.get(0).getStatus(), pompom.STATUS_FLOATING);
	}
	
	@Test
	public void testNonExistentId() {
		//Initialize and fill up tasklist
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		AddParser addParser = new AddParser("do cs2103",false);
		addParser.parse().execute();
	
		//Run deleteParser and make sure nothing is deleted and the
		//correct error message is returned.
		Long invalidItemId = taskList.get(0).getId()+1;
		DoneParser doneParser = new DoneParser(invalidItemId.toString());
		String errorMsg = doneParser.parse().execute();
		assertEquals(taskList.get(0).getStatus(), pompom.STATUS_FLOATING);
		assertEquals(errorMsg, String.format(DelCommand.MESSAGE_ID_INVALID,invalidItemId));
	}
	
	@Test
	public void testNullId() {
		//Initialize
		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
		AddParser addParser = new AddParser("do cs2103",false);
		addParser.parse().execute();
	
		//Run deleteParser and make sure nothing is deleted and the
		//correct error message is returned.		
		DoneParser doneParser = new DoneParser(null);
		String errorMsg = doneParser.parse().execute();
		assertEquals(taskList.get(0).getStatus(), pompom.STATUS_FLOATING);
		assertEquals(errorMsg, String.format(
						DoneParser.INVALID_DONE_ARGUMENT_RETURN_MESSAGE,""));
	}

}
