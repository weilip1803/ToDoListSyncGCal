package parser;

import static org.junit.Assert.*;

import org.junit.Test;

import command.Command;
import main.POMPOM;
import command.AddCommand;
/**
 *  @@author A0121760R
 *
 */
public class ParserTest {
	POMPOM pompom = new POMPOM();
	Parser parser = Parser.getInstance();
	
	/*
	 * This is the boundary case for the valid user commands partition
	 */
	@Test
	public void testAddCommand() {
		Command outputCommand = parser.parse("add do cs2013");
		assertTrue(outputCommand instanceof command.AddCommand);
	}
	
	/*
	 * This is the boundary case for the invalid user commands partition
	 */
	@Test
	public void testFailCommand() {
		Command outputCommand = parser.parse("ad do cs2013");
		assertTrue(outputCommand instanceof command.InvalidCommand);
	}
	
	/*
	 * This is the boundary case for the invalid user commands partition
	 */
	@Test
	public void testRecurringCommands() {
		Command outputCommand = parser.parse("delere recur do cs2013");
		assertTrue(outputCommand instanceof command.InvalidCommand);
	}
	


}
