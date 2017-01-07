//package parser;
//
//import static org.junit.Assert.*;
//
//import org.junit.Test;
//
//import command.Command;
//import main.POMPOM;
//import command.AddCommand;
///**
// *  @@author Josh
// *
// */
//public class ParserTest {
//	POMPOM pompom = new POMPOM();
//	Parser parser = Parser.getInstance();
//	
//	/*
//	 * This is the boundary case for the valid user commands partition
//	 */
//	@Test
//	public void testAddCommand() {
//		Command outputCommand = parser.executeCommand("add do cs2013");
//		assertTrue(outputCommand instanceof command.AddCommand);
//	}
//	
//	/*
//	 * This is the boundary case for the invalid user commands partition
//	 */
//	@Test
//	public void testFailCommand() {
//		Command outputCommand = parser.executeCommand("ad do cs2013");
//		assertTrue(outputCommand instanceof command.InvalidCommand);
//	}
//	
//	/*
//	 * This is the boundary case for the invalid user commands partition
//	 */
//	@Test
//	public void testRecurringCommands() {
//		Command outputCommand = parser.executeCommand("delere recur do cs2013");
//		assertTrue(outputCommand instanceof command.InvalidCommand);
//	}
//	
//
//
//}
