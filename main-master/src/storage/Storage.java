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
 * This Class contains all the methods for users to read and write their
 * storage.txt file and their settings.txt file.
 * 
 * @@author A0121628L
 * 
 */
public class Storage {

	private final static String DEFAULT_FILE_DIRECTORY = "PomPom Storage & Settings";
	private final static String DEFAULT_FILE_NAME = "Storage.txt";
	public final static String DEFAULT_STORAGE_FILE_PATH = DEFAULT_FILE_DIRECTORY
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

	private final String ERROR_DIALOG_TITLE = "Error Dialog";
	private final String ERROR_DIALOG_HEADER = "Storage settings or file error";
	private final String FILE_READ_ERROR_MESSAGE = "ERROR READING %s FILE! SETTINGS FILE WAS MODIFIED ILLEGAL!"
			+ "PLEASE REMOVE ILLEGAL FILE OR MODIFY FILE. REMOVE/EDIT SETTINGS FILE TO RESET";
	private final String CREATE_FILE_DIRECTORY_ERROR_MESSAGE = "ERROR IN CREATING DIRERCTORY. "
			+ "FILE DIRECTORY ALREADY EXSITS. REMOVE/EDIT SETTINGS FILE TO RESET";
	private final String LIGHT_STORAGE_ERROR = "File is a directory. Storage path set to default";
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
	 * This method initializes the main objects of the storage which are the
	 * settings/storage.txt and Gson objects(JSON library).
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

	/** Getters and Setters */
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

	// Id resets on 10k
	public long getIdCounter() {
		idCounter = idCounter + 1;
		if (idCounter == 10000) {
			idCounter = 1L;
		}
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
		settingsString = FileHandler.getStringFromFile(SETTINGS_FILE_PATH);
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
	 * Checks storagePath exists in settings or not
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
			// Create file if does not exist
			boolean madeFile = true;
			try{
			madeFile = storageFile.createNewFile();
			}
			// If directory cannot be created due to a same named file in
						// between,
						// feed back user with an error POPUP message
			catch(IOException e){
				storageFilePathError(CREATE_FILE_DIRECTORY_ERROR_MESSAGE);
			}						
			if (!madeFile) {
				storageFilePathError(CREATE_FILE_DIRECTORY_ERROR_MESSAGE);
				return false;
			}
//
//			storageFile.createNewFile();
			return false;
		}
		// If the file is a directory set to default storage path and return a error dialog
		if(storageFile.isDirectory()){
			storageFilePathError(LIGHT_STORAGE_ERROR);
			storageFilePath = DEFAULT_STORAGE_FILE_PATH;
			storageFile = new File(DEFAULT_STORAGE_FILE_PATH);
			return false;
		}
		return true;
	}
	

	// Cannot create file if parentFile is not directory or file with same name
	// already exsit
	public void storageFilePathError(String input) {
		logger.log(Level.WARNING, CREATE_FILE_DIRECTORY_ERROR_MESSAGE);
		storageFilePath = DEFAULT_STORAGE_FILE_PATH;
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(ERROR_DIALOG_TITLE);
		alert.setHeaderText(ERROR_DIALOG_HEADER);
		alert.setContentText(CREATE_FILE_DIRECTORY_ERROR_MESSAGE);

		alert.showAndWait();
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
	private UserItemList deserializeStorageString(String jsonString)
			throws IOException {
		UserItemList userTaskList = null;
		if (checkEmptyString(jsonString)) {
			UserItemList utl = new UserItemList("Not Set",
					new ArrayList<Item>());
			utl.setIdCounter(0);
			taskList = new ArrayList<Item>();
			return utl;
		}
		try {
			userTaskList = gsonItem.fromJson(jsonString, UserItemList.class);
		} // Prints out error dialog when error occurs
		catch (Exception e) {
			System.err.println("ERROR IN STORAGE (deserializeStorageString): "
					+ e);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(ERROR_DIALOG_TITLE);
			alert.setHeaderText(ERROR_DIALOG_HEADER);
			alert.setContentText(String.format(FILE_READ_ERROR_MESSAGE,
					"STORAGE"));

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
					"eff3f6", "616060", "000000");
			defaultSettings.setStoragePath(DEFAULT_STORAGE_FILE_PATH);
			final String json = gsonSettings.toJson(defaultSettings);
			FileHandler.writeStringToFile(settingsFile, json);
			return defaultSettings;
		}
		// Prints out error dialog when error occurs
		try {
			settings = gsonSettings.fromJson(jsonString, Settings.class);
		}
		// If error in reading storage file pop up an error dialog to guide user
		catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(ERROR_DIALOG_TITLE);
			alert.setHeaderText(ERROR_DIALOG_HEADER);
			alert.setContentText(String.format(FILE_READ_ERROR_MESSAGE,
					"SETTINGS"));
			alert.showAndWait();
			System.err
					.println("ERROR IN SETTINGS (deserializeSettingsString): "
							+ e);

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
