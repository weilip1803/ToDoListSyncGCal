package Test;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import command.EditCommand;
import main.POMPOM;
import utils.Item;
import static org.junit.Assert.assertEquals;

/**
 * @@author A0121528M
 */
public class TestPOMPOM {

	/** Parameters **/
	POMPOM pompom;
	ArrayList<Item> taskList;

	/**
	 * This method initializes POMPOM class and clears the storage for testing.
	 */
	@Before
	public void initialize() {
		pompom = new POMPOM();
		taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();
	}

	@Test
	public void testStatusPending() {

		pompom.execute("add do cs3241 f:tomorrow e:next friday");
		pompom.execute("add do cs3241 f:next monday");

		Item firstTask = taskList.get(0);
		Item secondTask = taskList.get(1);

		POMPOM.refreshStatus();

		/**
		 * check if the statuses are pending as current date is before start
		 * date
		 */
		assertEquals(POMPOM.STATUS_PENDING, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_PENDING, secondTask.getStatus());

	}

	@Test
	public void testStatusOngoing() {

		pompom.execute("add do cs3241 f:now e:next friday");
		pompom.execute("add do cs3241 f:now");
		pompom.execute("add do cs3241 e:next tuesday");

		Item firstTask = taskList.get(0);
		Item secondTask = taskList.get(1);
		Item thirdTask = taskList.get(2);

		POMPOM.refreshStatus();

		/**
		 * check if the statuses are ongoing as current date is within the start
		 * and end date specified
		 */
		assertEquals(POMPOM.STATUS_ONGOING, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_ONGOING, secondTask.getStatus());
		assertEquals(POMPOM.STATUS_ONGOING, thirdTask.getStatus());

	}

	@Test
	public void testStatusOverdue() {

		pompom.execute("add do cs3241 f:yesterday e:yesterday");
		pompom.execute("add do cs3241 e:last monday");

		Item firstTask = taskList.get(0);
		Item secondTask = taskList.get(1);

		POMPOM.refreshStatus();

		/**
		 * check if the statuses are overdue as current date after end date
		 */
		assertEquals(POMPOM.STATUS_OVERDUE, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_OVERDUE, secondTask.getStatus());

	}

	@Test
	public void testStatusFloating() {

		pompom.execute("add do cs3241");
		pompom.execute("add do cs2103t");

		Item firstTask = taskList.get(0);
		Item secondTask = taskList.get(1);

		POMPOM.refreshStatus();

		/**
		 * check if the statuses are floating as start and end date are not
		 * specified
		 */
		assertEquals(POMPOM.STATUS_FLOATING, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_FLOATING, secondTask.getStatus());

	}

	@Test
	public void testStatusCompleted() {

		pompom.execute("add do cs3241 f:now e:next friday");
		pompom.execute("add do cs3241 f:now");
		pompom.execute("add do cs3241 e:next tuesday");

		Item firstTask = taskList.get(0);
		Item secondTask = taskList.get(1);
		Item thirdTask = taskList.get(2);

		for (int i = 0; i < 3; i++) {
			Item currentTask = taskList.get(i);
			EditCommand command = new EditCommand(currentTask.getId(), "status", POMPOM.STATUS_COMPLETED);
			command.execute();
		}

		POMPOM.refreshStatus();

		/**
		 * check if the statuses remain completed as edit command was used
		 */
		assertEquals(POMPOM.STATUS_COMPLETED, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_COMPLETED, secondTask.getStatus());
		assertEquals(POMPOM.STATUS_COMPLETED, thirdTask.getStatus());

	}

	@Test
	public void testEventStatusCompleted() {

		pompom.execute("event clubbing f:last wed 10pm e:last thurs 4am");
		pompom.execute("event clubbing f:now e:tomorrow 4am");
		pompom.execute("event clubbing f:next wed 10pm e:next thurs 4am");

		Item firstEvent = taskList.get(0);
		Item secondEvent = taskList.get(1);
		Item thirdEvent = taskList.get(2);

		POMPOM.refreshStatus();

		assertEquals(POMPOM.STATUS_COMPLETED, firstEvent.getStatus());
		assertEquals(POMPOM.STATUS_ONGOING, secondEvent.getStatus());
		assertEquals(POMPOM.STATUS_PENDING, thirdEvent.getStatus());

	}

}
