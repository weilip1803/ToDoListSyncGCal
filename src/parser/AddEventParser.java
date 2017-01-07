package parser;

import java.util.ArrayList;
import java.util.Date;
import command.InvalidCommand;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import command.Command;
import main.POMPOM;
import command.AddCommand;
import command.AddRecurringCommand;

/**
 * @@author Josh
 *
 */
public class AddEventParser extends ArgsParser {

	private String itemTitle = null;
	private String itemDescription = null;
	private String itemPriority = null;
	private String itemStatus = null;
	private String itemLabel = null;
	private Date itemStartDate = null;
	private Date itemEndDate = null;
	private DateGroup itemRecurringDateGroup = null;
	private Long itemRecurringPeriod = null;
	private boolean itemIsRecurring = false;
	private String itemEndDateTitle = ""; // for use if title is empty.
	private Date exceptStartDate;
	private Date exceptEndDate;

	private static final int INDEX_INVALID = -1;
	private static final int INDEX_COMMAND_BEGIN = 0;

	private final String COMMAND_PREFIX_LABEL = "l:";
	private final String COMMAND_PREFIX_STATUS = "s:";
	private final String COMMAND_PREFIX_PRIORITY = "p:";
	private final String COMMAND_PREFIX_DESCRIPTION = ":";
	private final String STRING_SPACE = " ";
	private final String STRING_EMPTY = "";
	private final String DATETIMEPARSER_INDICATOR_START = "start";
	private final String DATETIMEPARSER_INDICATOR_END = "end";
	private final String DATETIMEPARSER_INDICATOR_RECURRING = "recurring";
	private final String DATETIMEPARSER_INDICATOR_EXCEPT = "except";

	public AddEventParser(String userCommand) {
		super(userCommand);
		if (this.hasNoArguments) {
			invalidArgs();
			return;
		} else {
			extractDataFromArguments();
		}
	}

	/*
	 * This method extracts all data from commandArgumentsString.
	 * commandArgumentsString is always modified every time some data is
	 * extracted from it.
	 * 
	 * The order which each argument is extracted is important and must not be
	 * reordered. extractTitle must always be last. extractRecurring must always
	 * be before extractEndDate. extractPriority, extractStatus and extractLabel
	 * must always be extracted first.
	 */
	private void extractDataFromArguments() {
		extractPriority();
		System.out.println(commandArgumentsString);
		extractStatus();
		System.out.println(commandArgumentsString);
		extractLabel();
		System.out.println(commandArgumentsString);
		extractRecurring();
		System.out.println(commandArgumentsString);
		extractStartDate();
		System.out.println(commandArgumentsString);
		extractEndDate();
		System.out.println(commandArgumentsString);
		extractDescription();
		System.out.println(commandArgumentsString);
		extractTitle();
	}

	/*
	 * This method extracts the title from commandArgumentsString. It simply
	 * sets whatever that is left in commandArgumentsString as the title.
	 * 
	 * If commandArgumentsString is empty by this time, then it must have been
	 * extracted by extractEndDate. itemEndDateTitle is created just for the
	 * purpose of getting the title in case the parser extracted the date
	 * wrongly.
	 */
	private void extractTitle() {
		if (hasNoTitleToExtract()) {
			itemTitle = itemEndDateTitle;
			itemEndDate = new Date();
		} else {
			itemTitle = commandArgumentsString.trim();
		}
	}

	/*
	 * This method creates the AddCommand() object with the relevant fields
	 * passed into the AddCommand() constructor and then returns the newly
	 * created AddCommand() object.
	 */
	protected Command getCommand() {
		if (itemIsRecurring) {
			ArrayList<AddCommand> addCommandList = executeRecurring();
			return checkAndReturnAddRecurringCommand(addCommandList);
		} else {
			return getNonRecurringCommand();
		}
	}

	private ArrayList<AddCommand> executeRecurring() {

		String recurDateString = itemRecurringDateGroup.getText();
		long newRecurInterval = DateTimeParser
				.calculateInterval(recurDateString);
		long currentTime = (new Date().getTime());
		long recurInterval = itemRecurringDateGroup.getRecurInterval();

		return getAddCommandArrayList(newRecurInterval, currentTime,
				recurInterval);
	}

	/*
	 * This method returns non-recurring addCommands. It determines which fields
	 * are empty and then determines whether it should be a task or event and
	 * whether it is a floating task or not.
	 */
	protected Command getNonRecurringCommand() {
		Date currentDate = new Date();

		if (hasCommandTitleOnly()) {
			return new InvalidCommand("Event Must have Start Date");
		} else if (hasCommandTitleAndEndDateOnly()) {
			return new InvalidCommand("Event Must have Start Date");
		} else if (hasCommandTitleAndStartDateOnly()) {
			return new AddCommand(POMPOM.LABEL_EVENT, itemTitle, null, null,
					POMPOM.STATUS_ONGOING, null, itemStartDate, null);
		} else {
			return new AddCommand(POMPOM.LABEL_EVENT, itemTitle,
					itemDescription, itemPriority, POMPOM.STATUS_ONGOING,
					itemLabel, itemStartDate, itemEndDate);
		}
	}

	/**
	 * This method builds and returns an ArrayList of AddCommand objects.
	 * 
	 * @param recurInterval
	 *            is the time in milliseconds between two subsequent periods of
	 *            the task.
	 *
	 * @param currentTime
	 *            is the time in milliseconds to the current time.
	 * 
	 * @param timeToNearestDate
	 *            is the total time from current time to the next date of the
	 *            task.
	 **/
	private ArrayList<AddCommand> getAddCommandArrayList(long recurInterval,
			long currentTime, long timeToNextDate) {
		ArrayList<AddCommand> addCommandArrayList = new ArrayList<AddCommand>();
		Date mostRecentEnd = new Date(timeToNextDate + currentTime);
		while (mostRecentEnd.before(itemEndDate)) {
			Date mostRecent = mostRecentEnd;
			timeToNextDate += recurInterval;
			mostRecentEnd = new Date(timeToNextDate + currentTime);
			if (!isSkippableDate(mostRecent)) {
				addCommandArrayList.add(getRecurringAddCommand(mostRecent,
						mostRecentEnd));
			}
		}
		return addCommandArrayList;
	}

	/**
	 * This method returns the type of AddCommand getAddCommandArrayList()
	 * needs. The types of fields filled will determine which AddCommand type
	 * object to create.
	 * 
	 * @param mostRecent
	 *            is the start date of the event to be added.
	 * @param mostRecentEnd
	 *            is the end date of the event to be added.
	 */
	private AddCommand getRecurringAddCommand(Date mostRecent,
			Date mostRecentEnd) {
		AddCommand defaultAddCommandFormat = null;

		// Take note of the "only". hasCommandTitleAndEndDateONLY() means that
		// all other fields are empty.
		if (hasCommandTitleAndEndDateOnly()) {
			defaultAddCommandFormat = new AddCommand(POMPOM.LABEL_EVENT,
					itemTitle, null, null, POMPOM.STATUS_ONGOING, null,
					mostRecent, mostRecentEnd);
		} else if (hasCommandTitleAndEndDate()) {
			defaultAddCommandFormat = new AddCommand(POMPOM.LABEL_EVENT,
					itemTitle, itemDescription, itemPriority,
					POMPOM.STATUS_ONGOING, itemLabel, mostRecent, mostRecentEnd);
		} else {
			return null;
		}
		return defaultAddCommandFormat;
	}

	/**
	 * This method checks the ArrayList of AddCommand is empty or not. If it is
	 * empty, it will return an InvalidCommand object.
	 * 
	 * @param addCommandList
	 *            is the ArrayList of AddCommands that is generated by
	 *            getAddCommandArrayList().
	 */
	private Command checkAndReturnAddRecurringCommand(
			ArrayList<AddCommand> addCommandList) {
		if (isEmptyAddCommandList(addCommandList)) {
			return new InvalidCommand(commandArgumentsString);
		} else {
			return new AddRecurringCommand(addCommandList);
		}
	}

	/**
	 * This method removes the description field from commandArgumentsString and
	 * updates the description variable (itemDescription).
	 */
	private void extractDescription() {
		int indexOfPrefix = commandArgumentsString
				.indexOf(COMMAND_PREFIX_DESCRIPTION);
		if (isValidIndex(indexOfPrefix)) {
			itemDescription = commandArgumentsString
					.substring(indexOfPrefix + 1);
			commandArgumentsString = commandArgumentsString.substring(0,
					indexOfPrefix);
		}
	}

	/**
	 * This method removes the priority field from commandArgumentsString and
	 * updates the priority variable (itemPriority).
	 */
	public void extractPriority() {
		int indexOfPrefix = commandArgumentsString
				.indexOf(COMMAND_PREFIX_PRIORITY);
		int indexSpace = commandArgumentsString.indexOf(STRING_SPACE,
				indexOfPrefix);
		if (isValidIndex(indexOfPrefix)) {
			itemPriority = extractFieldData(indexOfPrefix, indexSpace,
					COMMAND_PREFIX_PRIORITY);
			commandArgumentsString = removeFieldFromArgument(indexOfPrefix,
					indexSpace);
		}
	}

	/**
	 * This method removes the status field from commandArgumentsString and
	 * updates the status variable (itemStatus).
	 */
	public void extractStatus() {
		int indexOfPrefix = commandArgumentsString
				.indexOf(COMMAND_PREFIX_STATUS);
		int indexSpace = commandArgumentsString.indexOf(STRING_SPACE,
				indexOfPrefix);
		if (isValidIndex(indexOfPrefix)) {
			itemStatus = extractFieldData(indexOfPrefix, indexSpace,
					COMMAND_PREFIX_STATUS);
			commandArgumentsString = removeFieldFromArgument(indexOfPrefix,
					indexSpace);
		}
	}

	/**
	 * This method removes the label field from commandArgumentsString and
	 * updates the label variable (itemLabel).
	 */
	public void extractLabel() {
		int indexOfPrefix = commandArgumentsString
				.indexOf(COMMAND_PREFIX_LABEL);
		int indexSpace = commandArgumentsString.indexOf(STRING_SPACE,
				indexOfPrefix);
		if (isValidIndex(indexOfPrefix)) {
			itemLabel = extractFieldData(indexOfPrefix, indexSpace,
					COMMAND_PREFIX_LABEL);
			commandArgumentsString = removeFieldFromArgument(indexOfPrefix,
					indexSpace);
		}
	}

	/**
	 * This method removes the start date field from commandArgumentsString and
	 * updates the start date variable (itemStartDate).
	 */
	public void extractStartDate() {
		DateTimeParser startDateTimeParser = new DateTimeParser(
				DATETIMEPARSER_INDICATOR_START, commandArgumentsString);
		commandArgumentsString = commandArgumentsString.replace(
				startDateTimeParser.getString(), STRING_EMPTY);
		itemStartDate = startDateTimeParser.getDate();

	}

	/**
	 * This method removes the end date field from commandArgumentsString and
	 * updates the end date variable (itemEndDate).
	 */
	public void extractEndDate() {
		DateTimeParser endDateTimeParser = new DateTimeParser(
				DATETIMEPARSER_INDICATOR_END, commandArgumentsString);
		System.out.println("enddate:" + endDateTimeParser.getString());
		commandArgumentsString = commandArgumentsString.replace(
				endDateTimeParser.getString(), STRING_EMPTY);
		itemEndDate = endDateTimeParser.getDate();
		itemEndDateTitle = endDateTimeParser.getString();
	}

	/**
	 * This method removes the recurring field from commandArgumentsString and
	 * updates the recurring variable (itemRecurringDateGroup).
	 */
	public void extractRecurring() {
		DateTimeParser recurringTimeParser = new DateTimeParser(
				DATETIMEPARSER_INDICATOR_RECURRING, commandArgumentsString);
		commandArgumentsString = commandArgumentsString.replace(
				recurringTimeParser.getString(), STRING_EMPTY);
		itemRecurringDateGroup = recurringTimeParser.getRecurringDateGroup();
		if (itemRecurringDateGroup == null) {
			return;
		}
		itemIsRecurring = recurringTimeParser.getRecurring();
		extractExcept(recurringTimeParser);
	}

	/**
	 * This method removes the except fields from commandArgumentsString and
	 * updates the excepts variable (exceptStartDate and exceptEndDate).
	 */
	private void extractExcept(DateTimeParser recurringTimeParser) {
		commandArgumentsString = commandArgumentsString.replace(
				recurringTimeParser.getString(), STRING_EMPTY);
		DateTimeParser exceptTimeParser = new DateTimeParser(
				DATETIMEPARSER_INDICATOR_EXCEPT, commandArgumentsString);
		if (canGetExceptDateGroups(exceptTimeParser)) {
			exceptStartDate = exceptTimeParser.getExceptStartDateGroup()
					.getDates().get(0);
			exceptEndDate = exceptTimeParser.getExceptEndDateGroup().getDates()
					.get(0);
		}
	}

	/**
	 * This method removes a field (prefix+data) from commandArgumentsString.
	 * 
	 * (i.e. <add do stuff p:high s:open> -> <add do stuff s:open>)
	 * 
	 * @param indexOfPrefix
	 *            is the index of the field prefix in commandArgumentsString
	 * @param indexSpace
	 *            is the index of the first space after the field prefix in
	 *            commandArgumentsString
	 */
	private String removeFieldFromArgument(int indexPrefixBegin, int indexSpace) {
		if (isValidIndex(indexSpace)) {
			return commandArgumentsString.substring(INDEX_COMMAND_BEGIN,
					indexPrefixBegin);
		}
		return commandArgumentsString.substring(INDEX_COMMAND_BEGIN,
				indexPrefixBegin)
				+ commandArgumentsString.substring(indexSpace);
	}

	/**
	 * This method returns the data of the specified field. (minus the prefix) *
	 * 
	 * @param indexOfPrefix
	 *            is the index of the field prefix in commandArgumentsString
	 * @param indexSpace
	 *            is the index of the first space after the field prefix in
	 *            commandArgumentsString
	 * @param prefix
	 *            is the prefix of the field that is to be removed.
	 */
	private String extractFieldData(int indexOfPrefix, int indexSpace,
			String prefix) {
		if (isValidIndex(indexSpace)) {
			return commandArgumentsString.substring(getPrefixEndIndex(
					indexOfPrefix, prefix));
		}
		return commandArgumentsString.substring(
				getPrefixEndIndex(indexOfPrefix, prefix), indexSpace);
	}

	private int getPrefixEndIndex(int index, String prefix) {
		return index + prefix.length();
	}

	public String getTitle() {
		return itemTitle;
	}

	public String getDescription() {
		return itemDescription;
	}

	public String getPriority() {
		return itemPriority;
	}

	public String getStatus() {
		return itemStatus;
	}

	public Date getStartDate() {
		return itemStartDate;
	}

	public Date getEndDate() {
		return itemEndDate;
	}

	public boolean getIsRecurring() {
		return itemIsRecurring;
	}

	public long getRecurringPeriod() {
		return itemRecurringPeriod;
	}

	public boolean isNullTitle() {
		return itemTitle == null;
	}

	public boolean isNullDescription() {
		return itemDescription == null;
	}

	public boolean isNullPriority() {
		return itemPriority == null;
	}

	public boolean isNullStatus() {
		return itemStatus == null;
	}

	public boolean isNullLabel() {
		return itemLabel == null;
	}

	public boolean isNullStartDate() {
		return itemStartDate == null;
	}

	public boolean isNullEndDate() {
		return itemEndDate == null;
	}

	public boolean hasCommandTitleOnly() {
		return (isNullDescription() && isNullPriority() && isNullStatus()
				&& isNullLabel() && isNullStartDate() && isNullEndDate());
	}

	public boolean hasCommandTitleAndEndDateOnly() {
		return (isNullDescription() && isNullPriority() && isNullStatus()
				&& isNullLabel() && isNullStartDate());
	}

	public boolean hasCommandTitleAndStartDateOnly() {
		return (isNullDescription() && isNullPriority() && isNullStatus()
				&& isNullLabel() && isNullEndDate());
	}

	private boolean isValidIndex(int index) {
		return index != INDEX_INVALID;
	}

	private boolean hasCommandTitleAndEndDate() {
		return !isNullTitle() && !isNullEndDate();
	}

	private boolean hasNoTitleToExtract() {
		return commandArgumentsString.trim().equals(STRING_EMPTY);
	}

	private boolean isEmptyAddCommandList(ArrayList<AddCommand> addCommandList) {
		return addCommandList == null;
	}

	private boolean isSkippableDate(Date mostRecent) {
		return hasExceptDates() && isBetweenExceptDates(mostRecent);
	}

	private boolean isBetweenExceptDates(Date checkDate) {
		return checkDate.before(exceptEndDate)
				&& checkDate.after(exceptStartDate);
	}

	private boolean hasExceptDates() {
		return exceptEndDate != null && exceptStartDate != null;
	}

	private boolean canGetExceptDateGroups(DateTimeParser exceptDateTimeParser) {
		return exceptDateTimeParser.getExceptEndDateGroup() != null
				&& exceptDateTimeParser.getExceptStartDateGroup() != null;
	}
}
