package Test;

import java.util.ArrayList;

import org.junit.Test;
import command.EditCommand;
import command.MultiEditCommand;
import main.POMPOM;
import utils.Item;
import static java.lang.Math.toIntExact;
import static org.junit.Assert.assertEquals;

/**
 * @@author wen hao
 *
 */
public class TestPOMPOM {

	@Test
	public void testStatusPending() {
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241 f:tomorrow e:next friday");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs3241 f:next monday");
		Item secondTask = taskList.get(1);

		pompom.refreshStatus();
		/**
		 * check if the statuses are pending as current date is before start
		 * date
		 */
		assertEquals(POMPOM.STATUS_PENDING, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_PENDING, secondTask.getStatus());

	}

	@Test
	public void testStatusOngoing() {
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241 f:now e:next friday");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs3241 f:now");
		Item secondTask = taskList.get(1);

		pompom.execute("add do cs3241 next tuesday");
		Item thirdTask = taskList.get(2);

		pompom.refreshStatus();
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
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241 f:yesterday e:yesterday");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs3241 last monday");
		Item secondTask = taskList.get(1);

		pompom.refreshStatus();
		/**
		 * check if the statuses are overdue as current date after end date
		 */
		assertEquals(POMPOM.STATUS_OVERDUE, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_OVERDUE, secondTask.getStatus());

	}

	@Test
	public void testStatusFloating() {
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs2103t");
		Item secondTask = taskList.get(1);

		pompom.refreshStatus();
		/**
		 * check if the statuses are floating as start and end date are not
		 * specified
		 */
		assertEquals(POMPOM.STATUS_FLOATING, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_FLOATING, secondTask.getStatus());

	}

	@Test
	public void testStatusCompleted() {
		POMPOM pompom = new POMPOM();

		ArrayList<Item> taskList = POMPOM.getStorage().getTaskList();
		taskList.clear();

		pompom.execute("add do cs3241 f:now e:next friday");
		Item firstTask = taskList.get(0);

		pompom.execute("add do cs3241 f:now");
		Item secondTask = taskList.get(1);

		pompom.execute("add do cs3241 next tuesday");
		Item thirdTask = taskList.get(2);

		ArrayList<EditCommand> editList = new ArrayList<EditCommand>();
		for (int i = 0; i < 3; i++) {
			Item currentTask = taskList.get(i);
			EditCommand command = new EditCommand(currentTask.getId(), "status", POMPOM.STATUS_COMPLETED);
			editList.add(command);
		}

		MultiEditCommand multiEdit = new MultiEditCommand(editList);
		multiEdit.execute();

		pompom.refreshStatus();
		/**
		 * check if the statuses remain completed as edit command was used
		 */
		assertEquals(POMPOM.STATUS_COMPLETED, firstTask.getStatus());
		assertEquals(POMPOM.STATUS_COMPLETED, secondTask.getStatus());
//		assertEquals(POMPOM.STATUS_COMPLETED, thirdTask.getStatus());

	}

}
