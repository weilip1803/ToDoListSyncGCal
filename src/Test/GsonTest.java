package Test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import utils.Item;
import utils.ItemAdapter;
import utils.Settings;
import utils.SettingsAdapter;
import utils.UserItemList;
import static org.junit.Assert.*;

/**
 * @@author A0121628L
 *
 */

public class GsonTest {
	// Basic example to show my teammates how the gson library works.
	// UserItemList objects for test
	GsonBuilder GSON_ITEM_BUILDER;
	Gson gsonItem;

	UserItemList taskListInput;
	Item item1;
	Item item2;
	long initialCounter;

	// Settings Objects for test
	GsonBuilder GSON_SETTINGS_BUILDER = new GsonBuilder();
	Gson gsonSettings;
	Settings settings;
	private final String TEST_STORAGE_STRING = "TEST";

	// Create a gson String builder to create a text . we register
	// UserItemList to write this
	// Object into a string. So a Gsonbuilder will create a instance of gson
	// to do this.
	@Before
	public void initObjects() {
		// Gson objects for UserItemList
		GSON_ITEM_BUILDER = new GsonBuilder();
		GSON_ITEM_BUILDER.registerTypeAdapter(UserItemList.class,
				new ItemAdapter());
		GSON_ITEM_BUILDER.setPrettyPrinting();
		gsonItem = GSON_ITEM_BUILDER.create();
		// Gson Objects for settings
		final GsonBuilder GSON_SETTINGS_BUILDER = new GsonBuilder();
		GSON_SETTINGS_BUILDER.registerTypeAdapter(Settings.class,
				new SettingsAdapter());
		GSON_SETTINGS_BUILDER.setPrettyPrinting();
		gsonSettings = GSON_SETTINGS_BUILDER.create();

		// Add userItemList data
		taskListInput = new UserItemList();
		item1 = new Item(1L, "Event", "It is a sunny day", "High",
				"I want to swim", "Done", "red label", new Date(), new Date());

		item2 = new Item(2L, "Task", "It is a rainny day", "Medium",
				"I want to study", "Undone", "blue label", new Date(),
				new Date());
		initialCounter = 0L;
		taskListInput.setUserName("Wei Lip");
		taskListInput.setIdCounter(initialCounter);
		ArrayList<Item> tArray = new ArrayList<>();
		tArray.add(item1);
		tArray.add(item2);
		taskListInput.setTaskArray(tArray);

		// Add Settings data
		settings = new Settings();
		settings.setStoragePath(TEST_STORAGE_STRING);
	}

	/********************* Helper Methods **************************************/

	public boolean isSameItemList(ArrayList<Item> listA, ArrayList<Item> listB) {
		int lengthA = listA.size();
		int lengthB = listB.size();
		boolean sameList = true;
		if (lengthA != lengthB) {
			return false;
		} else {
			// Test every aspect of the item value are equals to each other
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

	/********************* Unit Tests **************************************/
	// Test the correct reading and writing of UserItemList object into the text
	// file
	@Test
	public void testGsonUserItemList() {
		final String itemJson = gsonItem.toJson(taskListInput);

		// get Object from above json string and test whether it writes to the
		// object with same values or not
		final UserItemList taskListOutput = gsonItem.fromJson(itemJson,
				UserItemList.class);

		assertEquals(taskListOutput.getUserName(), taskListInput.getUserName());
		assertEquals(taskListOutput.getIdCounter(),
				taskListInput.getIdCounter());
		ArrayList<Item> taskList1 = taskListOutput.getTaskArray();
		ArrayList<Item> taskList2 = taskListInput.getTaskArray();
		assertTrue(isSameItemList(taskList1, taskList2));

	}

	// Test the correct reading and writing of Settings object into the text
	// file
	@Test
	public void testGsonSettings() {
		final String settingsJson = gsonSettings.toJson(settings);

		final Settings settingsFromString = gsonSettings.fromJson(settingsJson,
				Settings.class);
		assertEquals(settingsFromString.getStoragePath(), TEST_STORAGE_STRING);

	}
}
