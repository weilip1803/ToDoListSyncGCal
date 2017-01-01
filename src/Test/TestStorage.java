package Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.naming.InitialContext;
import javax.xml.stream.events.StartDocument;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.prettytime.shade.com.joestelmach.natty.generated.DateParser_NumericRules.int_00_to_23_optional_prefix_return;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import storage.Storage;
import utils.FileHandler;
import utils.Item;
import utils.ItemAdapter;
import utils.ListClassifier;
import utils.Settings;
import utils.SettingsAdapter;
import utils.UserItemList;
import static org.junit.Assert.*;

/**
 * @@author A0121628L
 */
public class TestStorage {
	/** Storage directory for testing */
	private final String DEFAULT_FILE_DIRECTORY = "test";
	private final String DEFAULT_FILE_NAME = "Storage.txt";
	private final String DEFAULT_STORAGE_FILE_PATH_TESTING = DEFAULT_FILE_DIRECTORY
			+ "/" + DEFAULT_FILE_NAME;

	/** Gson testing for reading and saving */
	GsonBuilder gsonItemBuilder;
	Gson gsonItem;
	UserItemList taskListInput;

	/** Storage Items for testing */
	Item item1;
	Item item2;
	Item item3;
	Item item4;
	Storage storageStub;
	String originalStorageFilePath;

	/**
	 * Initialization of stub and item variables for adding later. Create a gson
	 * Item builder which will be used to create a JSON String from UserItemList
	 * vice-versa. So we register UserItemList to write this Object into a
	 * string. So a Gsonbuilder will create a gson instance to do this.
	 */
	@Before
	public void initGson() {

		gsonItemBuilder = new GsonBuilder();
		gsonItemBuilder.registerTypeAdapter(UserItemList.class,
				new ItemAdapter());
		gsonItemBuilder.setPrettyPrinting();
		gsonItem = gsonItemBuilder.create();
	}

	/**
	 * Initialize variables from empty storage and create 4 items for testing
	 * later. Change the storage file path to new empty folder for testing to so
	 * that it would not affect the actual folder data
	 */
	@Before
	public void initStorage() throws ParseException, IOException {

		storageStub = new Storage();
		storageStub.init();

		/** Save the Orignal file path and change back to it later */
		originalStorageFilePath = storageStub.getStorageFilePath();

		/** Create new storage for test */
		File testFile = new File(DEFAULT_STORAGE_FILE_PATH_TESTING);
		testFile.delete();

		/** Set to testing location and initialize storage */
		storageStub.setStorageFilePath(DEFAULT_STORAGE_FILE_PATH_TESTING);
		storageStub.saveSettings();

		/** Reinitialize storage with new storagefile */
		storageStub.init();
		addItems(storageStub.getTaskList());
	}

	/********************* Helper Methods * @throws IOException **************************************/

	/**
	 * This method compares the 2 list in the parameters whether they have the
	 * same items or not
	 * 
	 * @param listA
	 * @param listB
	 * @return boolean value of comparing the 2 list
	 */
	public boolean isSameItemList(ArrayList<Item> listA, ArrayList<Item> listB) {
		int lengthA = listA.size();
		int lengthB = listB.size();
		boolean sameList = true;
		if (lengthA != lengthB) { 
			return false;
		} else {
			/** Test every aspect of the item value are equals to each other */
			for (int i = 0; i < lengthA; i++) {
				// If the previous item was not the same return false.
				if (!sameList) {
					return false;
				}
				Item currentItemA = listA.get(0);
				Item currentItemB = listB.get(0);

				sameList = currentItemA.getId().equals(currentItemB.getId());
				sameList = currentItemA.getTitle().equals(
						currentItemB.getTitle());
				sameList = currentItemA.getDescription().equals(
						currentItemB.getDescription());
				sameList = currentItemA.getLabel().equals(
						currentItemB.getLabel());
				sameList = currentItemA.getPriority().equals(
						currentItemB.getPriority());
				sameList = currentItemA.getTitle().equals(
						currentItemB.getTitle());
				sameList = currentItemA.getTitle().equals(
						currentItemB.getTitle());
			}
			return sameList;
		}

	}

	/**
	 * This method add items for testing into the list from the parameter
	 * 
	 * @param list
	 * @throws ParseException
	 */
	public void addItems(ArrayList<Item> list) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
		sdf.parse("Wed, 4 Jul 2016 12:08");

		item1 = new Item(1L, "event", "Swim", "High", "Nice day", "Done",
				"red label", sdf.parse("Wed, 04 Jan 2016 12:08"),
				sdf.parse("Wed, 30 Jan 2016 12:08"));

		item2 = new Item(2L, "task", "Sleep", "Medium", "yawn", "Undone",
				"blue label", sdf.parse("Wed, 04 Jul 2016 12:08"),
				sdf.parse("Wed, 11 Jul 2016 12:08"));

		item3 = new Item(3L, "task", "Fight", "Medium", "Muay thai", "Undone",
				"blue label", sdf.parse("Wed, 01 Jul 2016 12:08"),
				sdf.parse("Wed, 10 Jul 2016 12:08"));

		item4 = new Item(4L, "task", "Mug", "Medium", "I want to study",
				"Undone", "blue label", new Date(), new Date());
		list.add(item1);
		list.add(item2);
		list.add(item3);
		list.add(item4);
	}

	/**
	 * This method add completed items into the list for testing
	 * 
	 * @param lst
	 * @throws ParseException
	 */
	public void addCompletedItems(ArrayList<Item> lst) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
		sdf.parse("Wed, 4 Jul 2016 12:08");

		Item doneItem1 = new Item(1L, "event", "Swim", "High", "Nice day",
				"completed", "red label", sdf.parse("Wed, 04 Jan 2016 12:08"),
				sdf.parse("Wed, 30 Jan 2016 12:08"));

		Item doneItem2 = new Item(2L, "task", "Sleep", "Medium", "yawn",
				"completed", "blue label", sdf.parse("Wed, 04 Jul 2016 12:08"),
				sdf.parse("Wed, 11 Jul 2016 12:08"));

		Item doneItem3 = new Item(3L, "task", "Fight", "Medium", "Muay thai",
				"completed", "blue label", sdf.parse("Wed, 01 Jul 2016 12:08"),
				sdf.parse("Wed, 10 Jul 2016 12:08"));

		Item doneItem4 = new Item(4L, "task", "Mug", "Medium",
				"I want to study", "completed", "blue label", new Date(),
				new Date());
		lst.add(doneItem1);
		lst.add(doneItem2);
		lst.add(doneItem3);
		lst.add(doneItem4);
	}

	public void addTodayEventandTask(ArrayList<Item> lst) throws ParseException {
		Item doneItem1 = new Item(1L, "event", "Swim", "High", "Nice day",
				"completed", "red label", new Date(), new Date());

		Item doneItem2 = new Item(2L, "task", "Sleep", "Medium", "yawn",
				"completed", "blue label", new Date(), new Date());
		lst.add(doneItem1);
		lst.add(doneItem2);
	}

	/********************* UNIT TEST CASES * @throws IOException **************************************/

	/**
	 * Tests whether the file specified will be created when the constructor is
	 * called.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testInitialization() throws IOException {
		Storage testStorage = new Storage();
		testStorage.init();
		assertNotNull(testStorage.getIdCounter());
		assertNotNull(testStorage.getStorageFilePath());
		assertNotNull(testStorage.getTaskList());
		assertNotNull(testStorage.getUserTaskList());
	}

	/**
	 * This method test whether getIdCounter increments itself a not
	 * 
	 * @throws IOException
	 */
	@Test
	public void correctIdRead() throws IOException {
		assertEquals(storageStub.getUserTaskList().getIdCounter() + 1,
				storageStub.getIdCounter());
	}

	/**
	 * The code to initialize the storageFilePath is done in the method
	 * initstorage. So now we will just check whether the path is set correctly
	 * a not
	 * 
	 * @throws SecurityException
	 * @throws IOException
	 */
	@Test
	public void testWriteToSettings() throws SecurityException, IOException {
		assertEquals(storageStub.getStorageFilePath(),
				DEFAULT_STORAGE_FILE_PATH_TESTING);
	}

	/**
	 * This method will add the items to storage and save it to the storage text
	 * file And test whether we can get back the same items using our gson file
	 * reader.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAddAndSave() throws IOException {
		// addItems(storageStub.getTaskList());
		storageStub.saveStorage();
		// Read Storage file at location storageFilePath(Tested above) and
		// check for equivalence by reading storage file with Gson
		String jsonUserItemList = FileHandler
				.getStringFromFile(DEFAULT_STORAGE_FILE_PATH_TESTING);
		UserItemList taskListOutput = gsonItem.fromJson(jsonUserItemList,
				UserItemList.class);
		assertTrue(isSameItemList(storageStub.getTaskList(),
				taskListOutput.getTaskArray()));
	}

	/**
	 * This method will delete the items to storage and save it to the storage
	 * text file And test whether we can get back the same items using our gson
	 * file reader.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDeleteAndSave() throws IOException {
		// addItems(storageStub.getTaskList());
		storageStub.getTaskList().remove(2);
		storageStub.getTaskList().remove(1);
		storageStub.saveStorage();

		// Same code as above to check for equivalence
		String jsonUserItemList = FileHandler
				.getStringFromFile(DEFAULT_STORAGE_FILE_PATH_TESTING);
		UserItemList taskListOutput = gsonItem.fromJson(jsonUserItemList,
				UserItemList.class);
		assertTrue(isSameItemList(storageStub.getTaskList(),
				taskListOutput.getTaskArray()));
	}

	/**
	 * This method will edit the items to storage and save it to the storage
	 * text file And test whether we can get back the same items using our gson
	 * file reader.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testEditAndSave() throws IOException {
		// addItems(storageStub.getTaskList());
		storageStub.getTaskList().get(0).setTitle("Testing title");
		storageStub.getTaskList().get(0).setTitle("Testing title 2");
		storageStub.saveStorage();

		// Same code as above to check for equivalence
		String jsonUserItemList = FileHandler
				.getStringFromFile(DEFAULT_STORAGE_FILE_PATH_TESTING);
		UserItemList taskListOutput = gsonItem.fromJson(jsonUserItemList,
				UserItemList.class);
		assertTrue(isSameItemList(storageStub.getTaskList(),
				taskListOutput.getTaskArray()));
	}

	/**
	 * This method test whether the list classifier filters the item list
	 * correctly a not.
	 */
	@Test
	public void testListClassifierTask() {
		ArrayList<Item> temp = ListClassifier.getTaskList(storageStub
				.getTaskList());
		for (int i = 0; i < temp.size(); i++) {
			assertTrue(temp.get(i).getType().equals("task"));
		}
	}

	/**
	 * This method test whether the list classifier filters the item list
	 * correctly a not.
	 */
	@Test
	public void testListClassifierDoneTask() throws ParseException {
		addCompletedItems(storageStub.getTaskList());
		ArrayList<Item> temp = ListClassifier.getDoneTaskList(storageStub
				.getTaskList());
		for (Item item : temp) {
			assertTrue(item.getType().equals("task"));
			assertTrue(item.getStatus().equals("completed"));
		}
	}

	/**
	 * From the item list get a sublist containing all the Events from the main
	 * list
	 */
	@Test
	public void testListClassifierEvent() {
		ArrayList<Item> temp = ListClassifier.getEventList(storageStub
				.getTaskList());
		for (Item item : temp) {
			assertTrue(item.getType().equals("event"));
		}
	}

	// Check does it returns the correct number of task today
	@Test
	public void testListClassifierTodayNumOfTask() throws ParseException {
		ArrayList<Item> temp = storageStub.getTaskList();
		addTodayEventandTask(temp);
		// @Before adds one today task and the other one from the above method
		assertEquals(ListClassifier.getTodayTask(temp), "2");

	}

	/**
	 * Check whether it returns the correct number of event today
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testListClassifierTodayNumOfEvent() throws ParseException {
		ArrayList<Item> temp = storageStub.getTaskList();
		addTodayEventandTask(temp);
		assertEquals(ListClassifier.getTodayEvent(temp), "1");

	}

	/**
	 * This method test whether the list classifier filters the item list
	 * correctly a not.
	 */
	@Test
	public void testListClassifierDoneEvent() throws ParseException {
		addCompletedItems(storageStub.getTaskList());
		ArrayList<Item> temp = ListClassifier.getDoneEventList(storageStub
				.getTaskList());
		for (Item item : temp) {
			assertTrue(item.getType().equals("event"));
			assertTrue(item.getStatus().equals("completed"));
		}
	}

	/**
	 * This method test whether the list classifier filters the item list by
	 * priority correctly a not.
	 */
	@Test
	public void testClassifyPriority() {
		ObservableList<Item> temp = ListClassifier.getSpecifiedPrirorirty(
				FXCollections.observableArrayList(storageStub.getTaskList()),
				"High");
		for (Item item : temp) {
			assertTrue(item.getPriority().equals("High"));
		}
	}

	/** Change back the settings of the original User or tester */
	@After
	public void returnToOriginalSettings() throws IOException {
		storageStub.setStorageFilePath(originalStorageFilePath);
		storageStub.saveSettings();
	}

}
