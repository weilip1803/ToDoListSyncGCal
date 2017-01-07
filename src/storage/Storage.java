package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LoggingPermission;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javax.naming.InitialContext;
import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.javafx.util.Logging;

import utils.FileHandler;
import utils.Item;
import utils.ItemAdapter;
import utils.Settings;
import utils.SettingsAdapter;
import utils.UserItemList;

/**
 * @@author A0121628L 
 * This Class contains all the methods for users to read and
 *          write their storage.txt file and their settings.txt file.
 */
public class Storage {

	private final String DEFAULT_FILE_DIRECTORY = "PomPom Storage & Settings";
	private final String DEFAULT_FILE_NAME = "Storage.txt";
	private final String DEFAULT_STORAGE_FILE_PATH = DEFAULT_FILE_DIRECTORY
			+ "/" + DEFAULT_FILE_NAME;

	/** storageFile is not final as user can reset storage file path */
	private final File DEFAULT_DIRECTORY_FILE = new File(DEFAULT_FILE_DIRECTORY);
	private File storageFile = new File(DEFAULT_STORAGE_FILE_PATH);
	private String storageFilePath = DEFAULT_STORAGE_FILE_PATH;

	private final String SETTINGS_FILE_NAME = "settings.txt";
	private final String SETTINGS_FILE_PATH = DEFAULT_FILE_DIRECTORY + "/"
			+ SETTINGS_FILE_NAME;

	private final File settingsFile = new File(SETTINGS_FILE_PATH);
	private Settings settings;

	/** The main data that is being extracted to this objects */
	private UserItemList userItemList;
	private ArrayList<Item> taskList;
	private long idCounter;

	/** This library need to be initialized for the parser */
	public PrettyTimeParser timeParser;

	/** Gson Library objects to read settings and storage file in json format */
	final GsonBuilder GSON_ITEM_BUILDER = new GsonBuilder();
	final GsonBuilder GSON_SETTINGS_BUILDER = new GsonBuilder();
	private Gson gsonItem;
	private Gson gsonSettings;

	private Logger logger = Logger.getLogger("Storage");

	public Storage() throws IOException {

	}

	/**
	 * This method intializes the main objects of the storage which are the
	 * settings/storage.txt and gson objects.
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {
		initializeGsonObjects();
		initializeSettings();
		initializeStorage();
		logger.log(Level.INFO, "Initialized Storage");
		// Loading NLP library for parser for quick access
		timeParser = new PrettyTimeParser();
		timeParser.parseSyntax("next year");
	}

	// Getters and Setters
	public UserItemList getUserTaskList() {
		return userItemList;
	}

	public void setUserTaskList(UserItemList userTaskList) {
		this.userItemList = userTaskList;
	}

	public ArrayList<Item> getTaskList() {
		return taskList;
	}

	public void setTaskList(ArrayList<Item> taskList) {
		this.taskList = taskList;
	}

	public long getIdCounter() {
		idCounter = idCounter + 1;
		return idCounter;
	}

	public void setIdCounter(long idCounter) {
		this.idCounter = idCounter;
	}

	public void setStorageFile(File storageFile) {
		this.storageFile = storageFile;
	}

	public void setStorageFilePath(String storageFilePath) {
		if (storageFilePath.trim().equals("") || storageFilePath == null) {
			storageFilePath = DEFAULT_STORAGE_FILE_PATH;
			return;
		}
		this.storageFilePath = storageFilePath;
	}

	public String getStorageFilePath() {
		return storageFilePath;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * This method initialize the GSON objects to read the storage text data
	 * file in JSON
	 */
	private void initializeGsonObjects() {
		GSON_ITEM_BUILDER.registerTypeAdapter(UserItemList.class,
				new ItemAdapter());
		GSON_ITEM_BUILDER.setPrettyPrinting();
		gsonItem = GSON_ITEM_BUILDER.create();

		GSON_SETTINGS_BUILDER.registerTypeAdapter(Settings.class,
				new SettingsAdapter());
		GSON_SETTINGS_BUILDER.setPrettyPrinting();
		gsonSettings = GSON_SETTINGS_BUILDER.create();
		logger.log(Level.INFO, "Gson objects Initialized with no errors");
	}

	/**
	 * This method initialize the settings if the settings.txt exist if not it
	 * creates a new file
	 * 
	 * @throws IOException
	 */
	private void initializeSettings() throws IOException {
		/** Check set directory folder exist a not if not create folder */
		checkDirectoryFolder();
		try {
			/** Check Settings file exist a not if not create file */
			checkSettingsFile();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failure Settings Path: "
					+ SETTINGS_FILE_PATH);
			e.printStackTrace();
		}
		String settingsString = null;
		try {
			settingsString = FileHandler.getStringFromFile(SETTINGS_FILE_PATH);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failure Reading Settings ");
			e.printStackTrace();
		}
		settings = deserializeSettingsString(settingsString);
	}

	/**
	 * This method initializes the storage.txt file(in specified location if
	 * stated in settings) or not the default location if path_directory.txt
	 * file does not exist create new storage.txt in specified directory
	 * 
	 * @throws IOException
	 */
	private void initializeStorage() throws IOException {
		// Check for user set directory in settings. If exist use directory.
		if (existStoragePath()) {
			setStorageFile(new File(settings.getStoragePath()));
			setStorageFilePath(settings.getStoragePath());
		}
		// Check for storage file if do not exist create file

		checkStorageFile();
		String storageString = FileHandler.getStringFromFile(storageFilePath);
		userItemList = deserializeStorageString(storageString);
		taskList = userItemList.getTaskArray();
		idCounter = userItemList.getIdCounter();
	}

	/**
	 * Checks storagePath exisits in settings or not
	 * 
	 * @return boolean whether storage path exist or not
	 */
	private boolean existStoragePath() {
		return settings.getStoragePath() != null;
	}

	/**
	 * This method check the directory folder of the default directory file
	 * exist or not. if the directory file does not exist it creates the
	 * directory file
	 * 
	 * @return
	 */
	private boolean checkDirectoryFolder() {
		if (!DEFAULT_DIRECTORY_FILE.exists()) {
			DEFAULT_DIRECTORY_FILE.mkdir();
			return false;
		}
		return true;
	}

	//
	/**
	 * This method check storage file exist or no if doesn't it creates a new
	 * one at the user specified directory
	 * 
	 * @return storage file existence
	 * @throws IOException
	 */
	private boolean checkStorageFile() throws IOException {
		if (!storageFile.exists()) {
			if (storageFile.getParentFile() != null) {
				storageFile.getParentFile().mkdirs();
			}

			storageFile.createNewFile();
			return false;
		}
		return true;
	}

	/**
	 * This method create settings file if it doesn't exist
	 * 
	 * @throws IOException
	 */
	private void checkSettingsFile() throws IOException {
		if (!settingsFile.exists()) {
			settingsFile.createNewFile();

		}
		return;
	}

	/**
	 * This method check whether the input string is empty or null or not
	 * 
	 * @param string
	 * @return boolean whether string is empty or not
	 */
	private boolean checkEmptyString(String string) {
		if (string.equals("") || string.equals(null)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * This method Use the Gson library to get a Storage object from String
	 * 
	 * @param jsonString
	 * @return
	 * @throws IOException 
	 */
	private UserItemList deserializeStorageString(String jsonString) throws IOException {
		UserItemList userTaskList = null;
		if (checkEmptyString(jsonString)) {
			UserItemList utl = new UserItemList("Not Set",
					new ArrayList<Item>());
			utl.setIdCounter(0);
			taskList = new ArrayList<Item>();
			return utl;
		}
		try {
			userTaskList = gsonItem.fromJson(jsonString,
					UserItemList.class);
		} catch (Exception e) {
			System.err.println("ERROR IN STORAGE (deserializeStorageString): " + e);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Look, an Error Dialog");
			alert.setContentText("ERROR READING STORAGE FILE! STORAGE FILE WAS MODIFIED WRONGlY!"
					+ "PLEASE REMOVE ILLEGAL FILE OR MODIFY FILE");
			
			alert.showAndWait();			
		}
		return userTaskList;
	}

	/**
	 * This method Use the Gson library to get a Settings object from String
	 * 
	 * @param jsonString
	 * @return
	 * @throws IOException
	 */
	private Settings deserializeSettingsString(String jsonString)
			throws IOException {
		if (checkEmptyString(jsonString)) {
			Settings defaultSettings = new Settings(DEFAULT_STORAGE_FILE_PATH,
					"eff3f6", "616060","000000");  
			defaultSettings.setStoragePath(DEFAULT_STORAGE_FILE_PATH);
			final String json = gsonSettings.toJson(defaultSettings);
			FileHandler.writeStringToFile(settingsFile, json);
			return defaultSettings;
		}
		Settings settings = null;
		
		try {
			settings = gsonSettings.fromJson(jsonString, Settings.class);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Look, an Error Dialog");
			alert.setContentText("ERROR READING SETTINGS FILE! SETTINGS FILE WAS MODIFIED WRONGlY!"
					+ "PLEASE REMOVE ILLEGAL FILE OR MODIFY FILE");			
			alert.showAndWait();
			System.err.println("ERROR IN SETTINGS (deserializeSettingsString): " + e);
			
		}
		return gsonSettings.fromJson(jsonString, Settings.class);
	}

	/**
	 * This method save the current userTaskList to the storage text file
	 * 
	 * @throws IOException
	 */
	public void saveStorage() throws IOException {
		userItemList.setTaskArray(taskList);
		userItemList.setIdCounter(idCounter);
		final String json = gsonItem.toJson(userItemList);
		FileHandler.writeStringToFile(storageFile, json);
	}

	/**
	 * This method save the current Settings to the settings text file
	 * 
	 * @throws IOException
	 */
	public void saveSettings() throws IOException {
		assert settings != null : "Settings not set";
		settings.setStoragePath(storageFilePath);
		final String json = gsonSettings.toJson(settings);
		FileHandler.writeStringToFile(settingsFile, json);
	}
}
