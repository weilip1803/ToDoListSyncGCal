package Test;

import static org.junit.Assert.*;

import org.junit.Test;
import main.POMPOM;

/**
 * @@author Jorel
 *
 */
public class TestSomething {
	/**
	 * This operation test return responds for correct input task add
	 */
	@Test
	public void test() {
		POMPOM pompom = new POMPOM();
		assertEquals("Task added", pompom.execute("add hello"));
	}
	
	/**
	 * This operation test return responds for invalid input
	 */
	@Test
	public void testTwo() {
		POMPOM pompom = new POMPOM();
		assertEquals("hello", pompom.execute("hello"));
	}

}
