package parser;

import static org.junit.Assert.*;

import org.junit.Test;
/**
 *  @@author Josh
 *
 */
public class EditParserTest {

	@Test
	public void testGetFields() {
		EditParser editParser = new EditParser("1 title new title");
		assertEquals(editParser.getField(),"title");
	}
	
	/*
	 * New data field has the name of the title inside.
	 */
	@Test
	public void testGetNewData() {
		EditParser editParser = new EditParser("1 title new title");
		
		assertEquals(editParser.getNewData(),"new title");
	}

}
