# Josh
###### AddEventParser.java
``` java
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
```
###### AddParser.java
``` java
 *
 */
public class AddParser extends ArgsParser{
	
	private String itemTitle= null;
	private String itemDescription = null;
	private String itemPriority = null;
	private String itemStatus = null;
	private String itemLabel = null;
	private Date itemStartDate = null;
	private Date itemEndDate = null;
	private DateGroup itemRecurringDateGroup = null;
	private Long itemRecurringPeriod = null;
	private boolean itemIsRecurring = false;
	private boolean itemIsEvent = false;
	private String itemEndDateTitle=""; //for use if title is empty.
	private Date exceptStartDate;
	private Date exceptEndDate;
	protected boolean isValidArguments = true;
	
	private static final int INDEX_INVALID = -1;
	private static final int INDEX_COMMAND_BEGIN = 0;
	
	private final String COMMAND_COMMON_DELIMITER = ":";
	private final String COMMAND_PREFIX_LABEL = "l:";
	private final String COMMAND_PREFIX_PRIORITY = "p:";
	private final String COMMAND_PREFIX_STARTDATE = "f:";
	private final String COMMAND_PREFIX_ENDDATE = "f:";
	private final String STRING_SPACE = " ";
	private final String STRING_EMPTY="";
	private final String DATETIMEPARSER_INDICATOR_START = "start";
	private final String DATETIMEPARSER_INDICATOR_END = "end";
	private final String DATETIMEPARSER_INDICATOR_RECURRING = "recurring";
	private final String DATETIMEPARSER_INDICATOR_EXCEPT = "except";
	private final String PRIORITY_CUSTOM1_HIGH = "h";
	private final String PRIORITY_CUSTOM1_MED = "m";
	private final String PRIORITY_CUSTOM1_LOW = "l";
	
	//Task Constructor
	public AddParser(String userCommand, String eventMarker){
		super(userCommand);	
		if (this.hasNoArguments){
			invalidArgs();
			return;
		} else {
			switch (eventMarker){
				case POMPOM.LABEL_EVENT:
					itemIsEvent=true;
				case POMPOM.LABEL_TASK:
					extractDataFromArguments();
			}
		}
	}
	
	
	/*
	 * This method extracts all data from commandArgumentsString.
	 * commandArgumentsString is always modified every time some
	 * data is extracted from it. 	
	 * 
	 * The order which each argument is extracted is important and
	 * must not be reordered. extractTitle must always be last.
	 * extractRecurring must always be before extractEndDate.
	 * extractPriority, extractStatus and extractLabel must always
	 * be extracted first.
	 * 
	 */
	private void extractDataFromArguments() {
		extractPriority();
		extractLabel();
		extractRecurring();
		extractStartDate();
		extractEndDate();		
		extractTitle();
		
	}
	
	/*
	 * This method extracts the title from commandArgumentsString.
	 * It simply sets whatever that is left in commandArgumentsString
	 * as the title.
	 * 
	 * If commandArgumentsString is empty by this time, then it must
	 * have been extracted by extractEndDate. itemEndDateTitle is created
	 * just for the purpose of getting the title in case the parser extracted
	 * the date wrongly.
	 * 
	 */
	private void extractTitle() {
		if (hasNoTitleToExtract()){
			itemTitle=itemEndDateTitle;
			itemEndDate = null;
		} else{
			itemTitle = commandArgumentsString.trim();
		}
	}

	
	/*
	 * This method creates the AddCommand() object with the relevant
	 * fields passed into the AddCommand() constructor and then returns
	 * the newly created AddCommand() object.
	 */
	protected Command getCommand(){
		
		if (isValidArguments){	
			if (itemIsRecurring){
				ArrayList<AddCommand> addCommandList= executeRecurring();
				return checkAndReturnAddRecurringCommand(addCommandList);
			} else{
				return getNonRecurringCommand();
			}
		} else{
			return new InvalidCommand(commandArgumentsString);
		}
	}
	
	/*
	 * This method uses the fields needed for adding all the recurring tasks, then 
	 * gets an arraylist of AddCommands needed for the AddRecurringCommand
	 */
	private ArrayList<AddCommand> executeRecurring(){
		
		String recurDateString = itemRecurringDateGroup.getText();
		System.out.println(recurDateString);
		long newRecurInterval=DateTimeParser.calculateInterval(recurDateString);
		long currentTime = (new Date().getTime());
		long recurInterval=itemRecurringDateGroup.getRecurInterval();

		return getAddCommandArrayList(newRecurInterval, currentTime, recurInterval);
	}
	
	/*
	 * This method returns non-recurring addCommands. It determines which
	 * fields are empty and then determines whether it should be a task
	 * or event and whether it is a floating task or not.
	 */
	protected Command getNonRecurringCommand(){
		Date currentDate = new Date();
		
		if (itemIsEvent){
			return createEventAddCommand(currentDate);
		} else{
			return createTaskAddCommand(currentDate);
		}
	}


	private Command createTaskAddCommand(Date currentDate) {
		if(hasCommandTitleOnly()){
			return new AddCommand(POMPOM.LABEL_TASK, itemTitle, null, null, 
									POMPOM.STATUS_FLOATING, null, null, null);
		} else if (hasCommandTitleAndEndDateOnly()){
			return new AddCommand(POMPOM.LABEL_TASK, itemTitle, null, null, 
									POMPOM.STATUS_ONGOING, null, currentDate, itemEndDate);
		} else if (hasCommandTitleAndStartDateOnly()){
			return new AddCommand(POMPOM.LABEL_TASK, itemTitle, null, null, 
									POMPOM.STATUS_ONGOING, null, itemStartDate, null);	
		} else {
			return new AddCommand(POMPOM.LABEL_TASK, itemTitle, itemDescription, itemPriority, 
									POMPOM.STATUS_ONGOING, itemLabel, itemStartDate, itemEndDate);
		}
	}
	
	private Command createEventAddCommand(Date currentDate) {
		if (!hasStartAndEndDate()){
			return new InvalidCommand(commandArgumentsString);
		} else {
			return new AddCommand(POMPOM.LABEL_EVENT, itemTitle, itemDescription, itemPriority, 
					POMPOM.STATUS_ONGOING, itemLabel, itemStartDate, itemEndDate);
		}
	}
	
	/**
	 * This method builds and returns an ArrayList of AddCommand objects.
	 * 
	 * @param recurInterval
	 * 		is the time in milliseconds between two subsequent periods of the task.
	 *
	 * @param currentTime
	 * 		is the time in milliseconds to the current time.
	 * 
	 * @param timeToNearestDate
	 * 		is the total time from current time to the next date of the task.
	 **/
	private ArrayList<AddCommand> getAddCommandArrayList(long recurInterval, long currentTime,
															long timeToNextDate) {
		ArrayList<AddCommand> addCommandArrayList = new ArrayList<AddCommand>();
		System.out.println(recurInterval+" "+currentTime+ " "+timeToNextDate + " "+itemEndDate);
		
		Date mostRecentEnd=new Date(timeToNextDate+currentTime);
		while (mostRecentEnd.before(itemEndDate)){
			System.out.println(recurInterval+" "+currentTime+ " "+timeToNextDate);
			Date mostRecent= mostRecentEnd;
			timeToNextDate += recurInterval;
			mostRecentEnd=new Date(timeToNextDate+currentTime);			
			if(!isSkippableDate(mostRecent)){
				addCommandArrayList.add(getRecurringAddCommand(mostRecent, mostRecentEnd));
			} 
		}
		return addCommandArrayList;
	}
	/**
	 * This method returns the type of AddCommand getAddCommandArrayList() needs. The types
	 * of fields filled will determine which AddCommand type object to create.
	 * 
	 * @param mostRecent 
	 * 			is the start date of the event to be added.
	 * @param mostRecentEnd
	 * 			is the end date of the event to be added.
	 */
	private AddCommand getRecurringAddCommand(Date mostRecent, Date mostRecentEnd) {
		AddCommand defaultAddCommandFormat=null;
		
		//Take note of the "only". hasCommandTitleAndEndDateONLY() means that all other fields are empty.
		if (hasCommandTitleAndEndDateOnly()){
			 defaultAddCommandFormat = new AddCommand(POMPOM.LABEL_TASK, itemTitle, 
					 									null, null, POMPOM.STATUS_ONGOING, 
					 									null, mostRecent, mostRecentEnd);
		} else if (hasCommandTitleAndEndDate()) {
			defaultAddCommandFormat = new AddCommand(POMPOM.LABEL_EVENT, itemTitle, itemDescription, 
														itemPriority, POMPOM.STATUS_ONGOING,
														itemLabel, mostRecent, mostRecentEnd);
		} else{
			return null;
		}
		return defaultAddCommandFormat;
	}

	/**
	 * This method checks the ArrayList of AddCommand is empty or not. If it is empty,
	 * it will return an InvalidCommand object.
	 * 
	 * @param addCommandList
	 * 			is the ArrayList of AddCommands that is generated by getAddCommandArrayList().
	 */
	private Command checkAndReturnAddRecurringCommand(ArrayList<AddCommand> addCommandList) {
		if (isEmptyAddCommandList(addCommandList)){
			return new InvalidCommand(commandArgumentsString);
		} else{ 
			return new AddRecurringCommand(addCommandList);
		}
	}
	

	/**
	 *  This method removes the priority field from commandArgumentsString and updates 
	 *  the priority variable (itemPriority).
	 */
	public void extractPriority(){
		int indexOfPrefix = commandArgumentsString.indexOf(COMMAND_PREFIX_PRIORITY);
		int indexFieldEnd = getIndexOfNextField(indexOfPrefix, COMMAND_PREFIX_PRIORITY);
		if (isValidIndex(indexOfPrefix)){
			String rawItemPriority = extractFieldData(indexOfPrefix, indexFieldEnd, COMMAND_PREFIX_PRIORITY).trim();
			itemPriority = parseAndCheckItemPriority(rawItemPriority);
			commandArgumentsString = removeFieldFromArgument(indexOfPrefix, indexFieldEnd);
		}
	}

	/**
	 *  This method removes the label field from commandArgumentsString and updates 
	 *  the label variable (itemLabel).
	 */
	public void extractLabel(){
		int indexOfPrefix = commandArgumentsString.indexOf(COMMAND_PREFIX_LABEL);	
		int indexFieldEnd = getIndexOfNextField(indexOfPrefix, COMMAND_PREFIX_LABEL);
		if (isValidIndex(indexOfPrefix)){
			itemLabel = extractFieldData(indexOfPrefix, indexFieldEnd, COMMAND_PREFIX_LABEL).trim();
			commandArgumentsString = removeFieldFromArgument(indexOfPrefix, indexFieldEnd);
		}
	}
	
	/**
	 *  This method removes the start date field from commandArgumentsString and updates 
	 *  the start date variable (itemStartDate).
	 */
	public void extractStartDate(){
		int indexOfPrefix = commandArgumentsString.indexOf(COMMAND_PREFIX_STARTDATE);
		if (isValidIndex(indexOfPrefix) && isValidArguments){
			DateTimeParser startDateTimeParser = new DateTimeParser(DATETIMEPARSER_INDICATOR_START,commandArgumentsString);			
			System.out.println(startDateTimeParser.getString()+" LOLOLOL");
			System.out.println(commandArgumentsString);
			commandArgumentsString = commandArgumentsString.replace(startDateTimeParser.getString(), STRING_EMPTY);
			System.out.println(commandArgumentsString);
			itemStartDate=startDateTimeParser.getDate();
			isValidArguments = isValidDate(itemStartDate);
		}
		
	}
	
	/**
	 *  This method removes the end date field from commandArgumentsString and updates 
	 *  the end date variable (itemEndDate).
	 */
	public void extractEndDate(){
		//failing condition: got e:, but dont have parsable date.
		int indexOfPrefix = commandArgumentsString.indexOf(COMMAND_PREFIX_ENDDATE);
		if (isValidArguments){
			DateTimeParser endDateTimeParser = new DateTimeParser(DATETIMEPARSER_INDICATOR_END,commandArgumentsString);
			commandArgumentsString = commandArgumentsString.replace(endDateTimeParser.getString(), STRING_EMPTY);
			itemEndDate = endDateTimeParser.getDate();
			itemEndDateTitle= endDateTimeParser.getString();
			if (!isValidDate(itemEndDate) && isValidIndex(indexOfPrefix)){
				isValidArguments = isValidDate(itemEndDate);	
			}
		}

	}
	
	/**
	 *  This method removes the recurring field from commandArgumentsString and updates 
	 *  the recurring variable (itemRecurringDateGroup).
	 */
	public void extractRecurring(){
		DateTimeParser recurringTimeParser = new DateTimeParser(DATETIMEPARSER_INDICATOR_RECURRING,commandArgumentsString);
		commandArgumentsString = commandArgumentsString.replace(recurringTimeParser.getString(), STRING_EMPTY);
		itemRecurringDateGroup = recurringTimeParser.getRecurringDateGroup();
		if (itemRecurringDateGroup==null){
			return;
		}
		itemIsRecurring = recurringTimeParser.getRecurring();
		extractExcept(recurringTimeParser);
	}

	/**
	 *  This method removes the except fields from commandArgumentsString and updates 
	 *  the excepts variable (exceptStartDate and exceptEndDate).
	 */
	private void extractExcept(DateTimeParser recurringTimeParser) {
		System.out.println(recurringTimeParser.getString()+" looooooolololol");
		commandArgumentsString=commandArgumentsString.replace(recurringTimeParser.getString(), STRING_EMPTY);
		DateTimeParser exceptTimeParser = new DateTimeParser(DATETIMEPARSER_INDICATOR_EXCEPT,commandArgumentsString);
		System.out.println(canGetExceptDateGroups(exceptTimeParser));
		if (canGetExceptDateGroups(exceptTimeParser)){
			exceptStartDate=exceptTimeParser.getExceptStartDateGroup().getDates().get(0);
			exceptEndDate=exceptTimeParser.getExceptEndDateGroup().getDates().get(0);
			commandArgumentsString = commandArgumentsString.replace(exceptTimeParser.getString(), STRING_EMPTY);
			System.out.println("before: "+ commandArgumentsString);
				
		}
	}

	/**
	 * This method removes a field (prefix+data) from commandArgumentsString. 
	 * 
	 * (i.e. <add do stuff p:high s:open> -> <add do stuff s:open>)
	 * 
	 * @param indexOfPrefix 
	 * 			is the index of the field prefix in commandArgumentsString
	 * @param indexSpace
	 * 			is the index of the first space after the field prefix in commandArgumentsString
	 */
	private String removeFieldFromArgument(int indexPrefixBegin, int indexSpace) {
		if (!isValidIndex(indexSpace)){
			return commandArgumentsString.substring(INDEX_COMMAND_BEGIN, indexPrefixBegin);
		}
			return commandArgumentsString.substring(INDEX_COMMAND_BEGIN, indexPrefixBegin)
									+ commandArgumentsString.substring(indexSpace);
	}
	
	private String parseAndCheckItemPriority(String rawItemPriority) {
		rawItemPriority = rawItemPriority.toLowerCase();
		if (isValidPriority(rawItemPriority)){
			return rawItemPriority;
		} else {
			switch (rawItemPriority){
				case PRIORITY_CUSTOM1_HIGH:	
					return POMPOM.PRIORITY_HIGH;
				case PRIORITY_CUSTOM1_MED:	
					return POMPOM.PRIORITY_MED;
				case PRIORITY_CUSTOM1_LOW:	
					return POMPOM.PRIORITY_LOW;
			}
		}
		isValidArguments=false;
		return null;
	}

	
	/**
	 * This method returns the data of the specified field. (minus the prefix)
	 * 	 * 
	 * @param indexOfPrefix 
	 * 			is the index of the field prefix in commandArgumentsString
	 * @param indexSpace
	 * 			is the index of the first space after the field prefix in commandArgumentsString
	 * @param prefix
	 * 			is the prefix of the field that is to be removed.
	 */
	private String extractFieldData(int indexOfPrefix, int indexSpace, String prefix) {
		if (!isValidIndex(indexSpace)){
			return  commandArgumentsString.substring(getPrefixEndIndex(indexOfPrefix, prefix));
		}
		return commandArgumentsString.substring(getPrefixEndIndex(indexOfPrefix, prefix), indexSpace);
	}
	
	/**
	 * This method gets the index of the next field/end of the current field.
	 * @param indexOfPrefix
	 * 			is the index of the current field
	 * @param String
	 * 			is the prefix of the current field
	 * 
	 */
	private int getIndexOfNextField(int indexOfPrefix, String prefix) {
		int indexOfPrefixEnd = indexOfPrefix+prefix.length(); 
		int indexOfNextDelimiter = commandArgumentsString.indexOf(COMMAND_COMMON_DELIMITER,indexOfPrefixEnd);
		int indexOfNextField = commandArgumentsString.lastIndexOf(STRING_SPACE,indexOfNextDelimiter);
		return indexOfNextField;
	}
	
	private int getPrefixEndIndex(int index, String prefix) {
		return index+prefix.length();
	}
	
	public String getTitle(){
		return itemTitle;
	}
	public String getDescription(){
		return itemDescription;
	}
	public String getPriority(){
		return itemPriority;
	}
	
	public String getLabel(){
		return itemLabel;
	}
	
	public String getStatus(){
		return itemStatus;
	}
	public Date getStartDate(){
		return itemStartDate;
	}
	public Date getEndDate(){
		return itemEndDate;
	}
	
	public boolean getIsRecurring(){
		return itemIsRecurring;
	}
	
	public long getRecurringPeriod(){
		return itemRecurringPeriod;
	}
	
	public DateGroup getItemRecurringDateGroup(){
		return itemRecurringDateGroup;
	}
	
	private boolean isValidPriority(String priorityString){
		priorityString = priorityString.toLowerCase();
		return (priorityString.equals(POMPOM.PRIORITY_HIGH)
				|| priorityString.equals(POMPOM.PRIORITY_MED)
				|| priorityString.equals(POMPOM.PRIORITY_LOW));
	}
	
	private boolean isValidDate(Date parsedDate){
		return parsedDate!=null;
	}
	
	public boolean isNullTitle(){
		return itemTitle==null;
	}
	
	public boolean isNullDescription(){
		return itemDescription==null;
	}
	
	public boolean isNullPriority(){
		return itemPriority==null;
	}
	
	public boolean isNullStatus(){
		return itemStatus==null;
	}
	
	public boolean isNullLabel(){
		return itemLabel==null;
	}
	
	public boolean isNullStartDate(){
		return itemStartDate==null;
	}
	
	public boolean isNullEndDate(){
		return itemEndDate==null;
	}

	public boolean hasCommandTitleOnly(){
		return (isNullDescription() 
				&& isNullPriority()
				&& isNullStatus()
				&& isNullLabel()
				&& isNullStartDate()
				&& isNullEndDate());
	}
	
	public boolean hasCommandTitleAndEndDateOnly(){
		return (isNullDescription() 
				&& isNullPriority()
				&& isNullStatus()
				&& isNullLabel()
				&& isNullStartDate());
	}
	
	public boolean hasCommandTitleAndStartDateOnly(){
		return (isNullDescription() 
				&& isNullPriority()
				&& isNullStatus()
				&& isNullLabel()
				&& isNullEndDate());
	}
	
	public boolean hasStartAndEndDate(){
		return !(isNullTitle()
				|| isNullEndDate()
				|| isNullStartDate());
	}

	private boolean isValidIndex(int index) {
		return index>INDEX_INVALID;
	}
	
	private boolean hasCommandTitleAndEndDate() {
		return !isNullTitle() && !isNullEndDate();
	}

	private boolean hasNoTitleToExtract() {
		return commandArgumentsString.trim().equals(STRING_EMPTY);
	}
	
	private boolean isEmptyAddCommandList(ArrayList<AddCommand> addCommandList) {
		return addCommandList==null;
	}
	
	private boolean isSkippableDate(Date mostRecent) {
		return hasExceptDates() && isBetweenExceptDates(mostRecent);
	}
	
	private boolean isBetweenExceptDates(Date checkDate){
		return checkDate.before(exceptEndDate)
				&& checkDate.after(exceptStartDate);
	}

	private boolean hasExceptDates() {
		return exceptEndDate!=null && exceptStartDate!=null;
	}
	
	private boolean canGetExceptDateGroups(DateTimeParser exceptDateTimeParser) {
		return exceptDateTimeParser.getExceptEndDateGroup()!=null &&
				exceptDateTimeParser.getExceptStartDateGroup()!=null;
	}
}
```
###### AddParserTest.java
``` java
 *
 */
public class AddParserTest{

	Parser parser = Parser.getInstance();
	PrettyTimeParser timeParser = new PrettyTimeParser();
	
	/*
	 * Tests if can add floating tasks
	 */
	@Test
	public void testAddCommandTitleOnly(){
		AddParser add = new AddParser("do project",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertNull(add.getPriority());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can set priority normally
	 */
	@Test
	public void testAddCommandPriority(){
		AddParser add = new AddParser("do project p:high",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("high",add.getPriority());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can set priority normally (with a space in between)
	 */
	@Test
	public void testAddCommandPriority2(){
		AddParser add = new AddParser("do project p: high",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("high",add.getPriority());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can set priority with shortcut
	 */
	@Test
	public void testAddCommandPriorityShortcut(){
		AddParser add = new AddParser("do project p:h",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("high",add.getPriority());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can set label
	 */
	@Test
	public void testAddCommandLabel(){
		AddParser add = new AddParser("do project l:school work",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("school work",add.getLabel());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getPriority());	
		assertTrue(add.isValidArguments);
		
	}
	
	/*
	 * Tests if can set label and priority
	 */
	@Test
	public void testAddCommandLabelAndPriority(){
		AddParser add = new AddParser("do project l: school work p: high",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		assertEquals("school work",add.getLabel());
		assertEquals("high",add.getPriority());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can add tasks with end date
	 */
	@Test
	public void testAddCommandEndDate(){
		AddParser add = new AddParser("do project 28 march",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifferenceInSeconds = getEndDateDifference(add,"28 march");
		assertEquals(endDateDifferenceInSeconds,0);	
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can add tasks with end date prefix
	 */
	@Test
	public void testAddCommandEndDatePrefix(){
		AddParser add = new AddParser("do project e: 28 march",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifferenceInSeconds = getEndDateDifference(add, "28 march");
		assertEquals(endDateDifferenceInSeconds,0);	
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}

	
	/*
	 * Tests if can add tasks with start and end date.
	 */
	@Test
	public void testAddCommandStartEndDate(){
		AddParser add = new AddParser("do project e:28 march f:16 march",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"28 march");
		long startDateDifference= getStartDateDifference(add,"16 march");
		assertEquals(endDateDifference, 0);	
		assertEquals(startDateDifference, 0);	
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can add tasks with all fields filled
	 */
	@Test
	public void testAddCommandFullTask(){
		AddParser add = new AddParser("do project e:28 march f:16 march l:soc homework p:h",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"28 march");
		long startDateDifference= getStartDateDifference(add,"16 march");
		assertEquals("soc homework",add.getLabel());
		assertEquals(POMPOM.PRIORITY_HIGH, add.getPriority());
		assertEquals(endDateDifference, 0);	
		assertEquals(startDateDifference, 0);	
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	@Test
	public void testAddCommandFullEvent(){
		AddParser add = new AddParser("do project e:28 march f:16 march l:soc homework p:h",POMPOM.LABEL_EVENT);
		assertEquals("do project",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"28 march");
		long startDateDifference= getStartDateDifference(add,"16 march");
		assertEquals("soc homework",add.getLabel());
		assertEquals(POMPOM.PRIORITY_HIGH, add.getPriority());
		assertEquals(endDateDifference, 0);	
		assertEquals(startDateDifference, 0);	
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	
	/*
	 * Tests if can switch the order of the title and end date
	 */
	@Test
	public void testAddCommandReorder(){
		AddParser add = new AddParser("28 march do project",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		long endDateDifferenceInSeconds = getEndDateDifference(add,"28 march");
		assertEquals(endDateDifferenceInSeconds,0);		
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can switch the order of the start date, end date and title. 
	 */
	@Test
	public void testAddCommandReorderWithEndDate(){
		AddParser add = new AddParser("e:28 march f:16 march do project",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		Date endDate= timeParser.parseSyntax("28 march").get(0).getDates().get(0);
		Date startDate= timeParser.parseSyntax("16 march").get(0).getDates().get(0);
		assertEquals(endDate.compareTo(add.getEndDate()),1);	
		assertEquals(startDate.compareTo(add.getStartDate()),1);
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can switch the order of the start date, end date and title.
	 */
	@Test
	public void testAddCommandReorderWithEndDateFromFirst(){
		AddParser add = new AddParser("f:16 march e:28 march do project",POMPOM.LABEL_TASK);
		assertEquals("do project",add.getTitle());
		Date endDate= timeParser.parseSyntax("28 march").get(0).getDates().get(0);
		Date startDate= timeParser.parseSyntax("16 march").get(0).getDates().get(0);
		assertEquals(endDate.compareTo(add.getEndDate()),1);	
		assertEquals(startDate.compareTo(add.getStartDate()),1);
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());	
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * Tests if can add in weekly recurring tasks with end date
	 */
	@Test
	public void testAddCommandRecurringBasic(){
		AddParser add = new AddParser("do cs2103 every friday e:6 june",POMPOM.LABEL_TASK);
		assertEquals("do cs2103",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"6 june");
		assertEquals(endDateDifference,0);		
		DateGroup itemRecurringDateGroup = add.getItemRecurringDateGroup();
		Date addStartDate = itemRecurringDateGroup.getDates().get(0);
		long startDateDifference= getStartDateDifference(addStartDate,"this friday");
		assertEquals(startDateDifference,0);	
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());	
		assertTrue(add.getIsRecurring());
		assertTrue(add.isValidArguments);
	}
	
	/*
	 * tests if can switch the order of the fields for adding recurring tasks with
	 * end date.
	 */
	@Test
	public void testAddCommand8(){
		AddParser add = new AddParser("do cs2103 e:6 june every friday",POMPOM.LABEL_TASK);
		assertEquals("do cs2103",add.getTitle());
		long endDateDifference= getEndDateDifference(add,"6 june");
		assertEquals(endDateDifference,0);		
		DateGroup itemRecurringDateGroup = add.getItemRecurringDateGroup();
		Date addStartDate = itemRecurringDateGroup.getDates().get(0);
		long startDateDifference= getStartDateDifference(addStartDate,"this friday");
		assertEquals(startDateDifference,0);
		assertNull(add.getDescription());
		assertNull(add.getLabel());
		assertNull(add.getPriority());
		assertNull(add.getStatus());	
		assertTrue(add.getIsRecurring());
		assertTrue(add.isValidArguments);
	}	
	
	/*
	 * Tests if can add in a task with only a parsable title 
	 */
	@Test
	public void testAddCommand9(){
		AddParser add = new AddParser("2103",POMPOM.LABEL_TASK);
		assertEquals("2103",add.getTitle());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertNull(add.getPriority());
		assertTrue(add.isValidArguments);
	}	
	
	/*
	 * Tests if can add in a task with only a parsable title 
	 */
	@Test
	public void testAddCommandInvalidPriority(){
		AddParser add = new AddParser("2103 p:lol",POMPOM.LABEL_TASK);
		assertEquals("2103",add.getTitle());
		assertNull(add.getEndDate());
		assertNull(add.getStartDate());
		assertNull(add.getDescription());
		assertNull(add.getStatus());
		assertNull(add.getLabel());	
		assertNull(add.getPriority());
		assertFalse(add.isValidArguments);
	}	
	
	//helper methods
	private long getEndDateDifference(AddParser add, String dateString) {
		long expectedEndDateInMillis= timeParser.parseSyntax(dateString).get(0).getDates().get(0).getTime();
		long parsedEndDateInMillis = add.getEndDate().getTime(); 
		long endDateDifferenceInSeconds = (expectedEndDateInMillis-parsedEndDateInMillis)/1000;
		return endDateDifferenceInSeconds;
	}
	
	private long getStartDateDifference(AddParser add, String dateString) {
		long expectedStartDateInMillis= timeParser.parseSyntax(dateString).get(0).getDates().get(0).getTime();
		long parsedStartDateInMillis = add.getStartDate().getTime(); 
		long startDateDifferenceInSeconds = (expectedStartDateInMillis-parsedStartDateInMillis)/1000;
		return startDateDifferenceInSeconds;
	}
	
	private long getEndDateDifference(Date add, String dateString) {
		long expectedEndDateInMillis= timeParser.parseSyntax(dateString).get(0).getDates().get(0).getTime();
		long parsedEndDateInMillis = add.getTime(); 
		long endDateDifferenceInSeconds = (expectedEndDateInMillis-parsedEndDateInMillis)/1000;
		return endDateDifferenceInSeconds;
	}
	
	private long getStartDateDifference(Date add, String dateString) {
		long expectedStartDateInMillis= timeParser.parseSyntax(dateString).get(0).getDates().get(0).getTime();
		long parsedStartDateInMillis = add.getTime(); 
		long startDateDifferenceInSeconds = (expectedStartDateInMillis-parsedStartDateInMillis)/1000;
		return startDateDifferenceInSeconds;
	}
}
	
	
```
###### ArgsParser.java
``` java
 *
 */
public class ArgsParser {
	
	protected static Logger logger = Logger.getLogger("Parser");
	
	protected boolean hasNoArguments=false;
	protected String commandArgumentsString;
	
	public ArgsParser(String commandArguments){
		commandArgumentsString = commandArguments;
		checkForAnyArguments();
	}

	private void checkForAnyArguments() {
		if (commandArgumentsString.equals("")){	
			hasNoArguments=true;
		}
	}
	
	public Command invalidArgs(){
		return new InvalidCommand(commandArgumentsString);
	}
}
```
###### DateTimeParser.java
``` java
 *
 */
public class DateTimeParser{
	
	private String originalString="";
	private String dateTimeString="";
	private Date dateTime=null;
	private static PrettyTimeParser timeParser=new PrettyTimeParser();
	private boolean isRecurring = false;
	private DateGroup exceptStartDateGroup=null;
	private DateGroup exceptEndDateGroup=null;
	
	private boolean isFlippedDate=false;
	

	private static final String COMMAND_PREFIX_RECURRING = "every";
	private static final String COMMAND_PREFIX_EXCEPT = "except";
	private static final String COMMAND_PREFIX_STARTDATE = "f:";
	private static final String COMMAND_PREFIX_ENDDATE = "e:";
	private static final String INDICATOR_START = "start";
	private static final String INDICATOR_END = "end";
	private static final String INDICATOR_RECURRING = "recurring";	
	private static final String INDICATOR_EXCEPT = "except";
	
	private static final String STRING_EMPTY = "";
	
	private static final int INVALID_INDEX = -1;
	
	private static DateGroup recurringDateGroup=null;
	
	private static Logger logger = Logger.getLogger("Parser");
	
	public DateTimeParser(String parseType, String commandArgumentsString){
		
		//Checks if the parse type is invalid.
		//Only "recurring","except","start","end" is accepted.
		if (isInvalidParseType(parseType)){
			logger.log(Level.SEVERE,"Bug! invalid parsetype specified! " + parseType);
			return;
		}
		
		//extracts the field related to the parse type.
		commandArgumentsString = extractFieldByParseType(parseType, commandArgumentsString);
		
		//corrects the string if in a mm/dd/yyyy format
		originalString=correctDateFormat(commandArgumentsString);
		System.out.println(originalString);
		System.out.println(parseType);
		
		processFieldByParseType(parseType); 
		
		
		//flipped dates are dates specified as MM/DD/YYYY
		if (isFlippedDate){
			dateTimeString=reverseCorrectDateFormat(dateTimeString);
		}
	}
	
	private void processFieldByParseType(String parseType) {
		switch (parseType){
			case INDICATOR_START:
				processStartDate();
				return;
			case INDICATOR_END:
				processEndDate();
				return;
			case INDICATOR_RECURRING:
				processRecurring();	
				return;
			case INDICATOR_EXCEPT:
				processExcept();
				return;
		}
	}
	

	private String extractFieldByParseType(String parseType, String commandArgumentsString) {
		if(isRecurringParseType(parseType)){
			commandArgumentsString=extractRecurringString(commandArgumentsString);
			commandArgumentsString=extractExceptDates(commandArgumentsString);
		} else if (isExceptParseType(parseType)){
			commandArgumentsString=extractExceptDates(commandArgumentsString);
		} else{
			commandArgumentsString=breakUpStartAndEndDates(parseType, commandArgumentsString);			
		}
		return commandArgumentsString;
	}
	
	static String breakUpStartAndEndDates(String parseType, String rawString){

		if (isNotStartOrEndParseType(parseType)){
			logger.log(Level.SEVERE,"invalid startOrEnd given for breakupStart()");
			return rawString;
		}		
		
		int startDateIndex = rawString.indexOf(COMMAND_PREFIX_STARTDATE);
		int endDateIndex = rawString.indexOf(COMMAND_PREFIX_ENDDATE);
		
		if (isInvalidIndexes(startDateIndex, endDateIndex)){
			return rawString;
		} else{
			return getSpecifiedDate(parseType, rawString, startDateIndex, endDateIndex);
		}
		
	}

	private static String getSpecifiedDate(String parseType, String rawString, int startDateIndex, int endDateIndex) {
		String extractedField = extractFieldBetweenTwoIndexes(startDateIndex, endDateIndex, rawString); 
		String restOfTheString = rawString.replace(extractedField,"");
		return getSpecifiedDatePortion(parseType, extractedField, restOfTheString);
	}
	
	private static String extractFieldBetweenTwoIndexes(int firstIndex, int secondIndex, String rawString){
		int minIndex=Math.min(firstIndex, secondIndex);
		int maxIndex=Math.max(firstIndex, secondIndex);
		return rawString.substring(minIndex,maxIndex);
	}

	private static String getSpecifiedDatePortion(String parseType, String firstPortion, String restOfTheString) {
		switch (parseType){
			case INDICATOR_START:
				return returnStartDatePortion(firstPortion, restOfTheString);
			case INDICATOR_END:
				return returnEndDatePortion(firstPortion, restOfTheString);		
		}
		logger.log(Level.SEVERE, "Invalid ParseType for DateTimeParser.getSpecifiedDatePortion() used! ");
		return STRING_EMPTY;
	}

	private static String returnEndDatePortion(String firstPortion, String restOfTheString) {
		if (hasEndPrefix(firstPortion)){
			return firstPortion.trim();
		} else{
			return restOfTheString;
		}
	}

	private static String returnStartDatePortion(String firstPortion, String restOfTheString) {
		if (hasStartPrefix(firstPortion)){
			return firstPortion.trim();
		} else{
			return restOfTheString;
		}
	}
	
	private String extractRecurringString(String rawString){
		int endIndex = rawString.indexOf(COMMAND_PREFIX_ENDDATE);
		int recurringIndex = rawString.indexOf(COMMAND_PREFIX_RECURRING);
		
		if (isInvalidIndexes(endIndex, recurringIndex)){
			return rawString;
		} else{		
			return getRecurringDate(rawString, endIndex, recurringIndex);
		}
		
	}

	private String getRecurringDate(String rawString, int endIndex, int recurringIndex) {
		String extractedField= extractFieldBetweenTwoIndexes(endIndex, recurringIndex, rawString);
		String restOfTheString=rawString.replace(extractedField,"");
		return getRecurringDatePortion(extractedField, restOfTheString);
	}

	private String getRecurringDatePortion(String firstPortion, String restOfTheString) {
		if (firstPortion.contains(COMMAND_PREFIX_RECURRING)){
			return firstPortion;
		} else{
			return restOfTheString;
		}
	}
	
	private String extractExceptDates(String rawString){
		int endIndex = rawString.indexOf(COMMAND_PREFIX_ENDDATE);
		int exceptIndex = rawString.indexOf(COMMAND_PREFIX_EXCEPT);
		int minIndex=Math.min(endIndex, exceptIndex);
		int maxIndex=Math.max(endIndex, exceptIndex);
		if (isInvalidIndexes(endIndex, exceptIndex)){
			return rawString;
		} else{
			return getExtractDatePortion(rawString, minIndex, maxIndex);
		}
	}

	private String getExtractDatePortion(String rawString, int minIndex, int maxIndex) {
		String firstPortion= rawString.substring(minIndex,maxIndex);
		String restOfTheString=rawString.replace(firstPortion,STRING_EMPTY);
		return getExtractDatePortion(firstPortion, restOfTheString);
	}

	private String getExtractDatePortion(String firstPortion, String restOfTheString) {
		if (firstPortion.contains(COMMAND_PREFIX_EXCEPT)){
			return firstPortion;
		} else{
			return restOfTheString;
		}
	}
	
	private void processStartDate(){
		//unique feature of start date: f:
		if (!hasStartPrefix(originalString)){
			return;
		}
		List<DateGroup> dateGroup = parseAndCheckDate(originalString.replace(COMMAND_PREFIX_STARTDATE,STRING_EMPTY));
		String parsedDateString = dateGroup.get(0).getText();
		int fromFieldStartIndex = originalString.indexOf(COMMAND_PREFIX_STARTDATE);
		int fromFieldEndIndex = originalString.indexOf(parsedDateString)+parsedDateString.length();
		originalString=originalString.substring(fromFieldStartIndex,fromFieldEndIndex);
		dateTimeString = originalString;
		dateTime = dateGroup.get(0).getDates().get(0);
	}
	
	private void processEndDate(){
		List<DateGroup> dateGroup;
		//unique feature of end date: after title:description is the enddate.
		if (hasEndPrefix(originalString)){
			dateGroup = parseAndCheckDate(originalString.replace(COMMAND_PREFIX_ENDDATE,STRING_EMPTY));
			String parsedDateString = dateGroup.get(0).getText();
			int fromFieldStartIndex = originalString.indexOf(COMMAND_PREFIX_ENDDATE);
			int fromFieldEndIndex = originalString.indexOf(parsedDateString)+parsedDateString.length();
			originalString=originalString.substring(fromFieldStartIndex,fromFieldEndIndex);
			dateTimeString = originalString;
		} else{
			dateGroup = parseAndCheckDate(originalString);
		}
		if (!isEmptyDateGroup(dateGroup)){
			dateTime = dateGroup.get(0).getDates().get(0);
		}
	}
	
	private void processRecurring(){
		
		if (!originalString.contains(COMMAND_PREFIX_RECURRING)){
			return;
		} 
		
		//every day change to every 1 day
		//every one day change to every 1 day
		
		
		
		List<DateGroup> dateGroup=parseAndCheckDate(originalString);
		recurringDateGroup=dateGroup.get(0);
	}
	
	private void processExcept(){
		
		if (!originalString.contains(COMMAND_PREFIX_EXCEPT)){
			return;
		} 
		
		String[] datesSplit = originalString.split("to");
		
		if (datesSplit.length!=2){
			return;
		}
		exceptStartDateGroup=parseAndCheckDate(datesSplit[0]).get(0);
		exceptEndDateGroup=parseAndCheckDate(datesSplit[1]).get(0);
		
		int indexStart = originalString.indexOf(exceptStartDateGroup.getText())-8;
		
		int indexEnd = originalString.indexOf(exceptEndDateGroup.getText())+
				exceptEndDateGroup.getText().length();
		
		String getContains = originalString.substring(indexStart,indexEnd);
		dateTimeString = getContains;
	}
	
	public List<DateGroup> parseAndCheckDate(String stringWithDate){
		String output="";
		String initialParsedString = parseDateToString(stringWithDate);
		if (initialParsedString.equals("")){
			System.out.println(initialParsedString);
			return timeParser.parseSyntax("");
		}
		String[] rawStringArray = stringWithDate.split(" ");
		String[] parsedStringArray = initialParsedString.split(" ");
		System.out.println(Arrays.toString(rawStringArray) + " " + Arrays.toString(parsedStringArray));
		if (Arrays.equals(rawStringArray, parsedStringArray)){
			dateTimeString=stringWithDate;
			return timeParser.parseSyntax(stringWithDate);
		} else{
			for (int i=0; i<rawStringArray.length; i++){
				for (int j=0; j<parsedStringArray.length; j++){
					
					String currentRawStringPortion = rawStringArray[i];
					String currentParsedStringPortion = parsedStringArray[j];
				
					if (currentRawStringPortion.equals(currentParsedStringPortion) && !output.contains(currentRawStringPortion)){
						output+=(rawStringArray[i] + " ");
					} else if (!currentRawStringPortion.equals(currentParsedStringPortion) 
							&& currentRawStringPortion.contains(currentParsedStringPortion)){
						output = foo(stringWithDate, currentRawStringPortion);
						return parseAndCheckDate(output.trim());
					 	
						} else{ 
							//do nothing
					}
				} 
			}
		}
		return parseAndCheckDate(output.trim());
	}

	//detect messed up titles
	private String foo(String stringWithDate, String currentRawStringPortion) {
		int index = stringWithDate.indexOf(currentRawStringPortion);
		int indexSpace = stringWithDate.indexOf(" ",index);
		if (indexSpace==-1){
			stringWithDate="";
		} else{
			stringWithDate=stringWithDate.substring(0,index)+
					stringWithDate.substring(indexSpace+1);	
		}
		return stringWithDate;
	}
	
	private String parseDateToString(String dateString){
		String output="";
		try{
			if (timeParser.parseSyntax(dateString).get(0).isRecurring()){
				output+="every ";
				isRecurring=true;
			}	
			output+=timeParser.parseSyntax(dateString).get(0).getText();
		} catch (Exception e){
			output= "";
		}
			return output;
	}
	
	private String correctDateFormat(String commandArguments){
		String[] commandArgumentsArray = commandArguments.split(" ");
		DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
		formatter1.setLenient(false);
		String prefix = null;
		for (String stringPortion:commandArgumentsArray){
			try {
				if (hasPrefix(stringPortion)){
					prefix = stringPortion.substring(0, 2);
					stringPortion=stringPortion.substring(2);				
				}
				formatter1.parse(stringPortion);	
				isFlippedDate=true;
				String[] dateArray = stringPortion.split("/");
				String newDateString = dateArray[1]+"/"+dateArray[0]+"/"+dateArray[2];
				if (prefix!=null){
					stringPortion=prefix+stringPortion;
					newDateString=prefix+newDateString;
				}
				commandArguments = commandArguments.replace(stringPortion,newDateString);
				return commandArguments;
			} catch (ParseException e) {
				//If input date is in different format or invalid.
			}
		}
		return commandArguments;
	}
	
	private String reverseCorrectDateFormat(String commandArguments){
		String[] commandArgumentsArray = commandArguments.split(" ");
		DateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy");
		formatter1.setLenient(false);
		isFlippedDate=false;
		String prefix = null;
		for (String stringPortion:commandArgumentsArray){
			try{
				if (hasPrefix(stringPortion)){
					prefix = stringPortion.substring(0, 2);
					stringPortion=stringPortion.substring(2);				
				}	
				formatter1.parse(stringPortion);	
				String[] dateArray = stringPortion.split("/");
				String newDateString = dateArray[1]+"/"+dateArray[0]+"/"+dateArray[2];
				if (prefix!=null){
					stringPortion=prefix+stringPortion;
					newDateString=prefix+newDateString;
				}
				commandArguments = commandArguments.replace(stringPortion,newDateString);
				return commandArguments;
			}catch (ParseException e){
				//do nothing
			}
		}
		return commandArguments;
	}
	
	public static long calculateInterval(String day){
		System.out.println(day);
		Date dateone=timeParser.parseSyntax("last " + day).get(0).getDates().get(0);
		Date datetwo=timeParser.parseSyntax("next " +day).get(0).getDates().get(0);
		long interval = datetwo.getTime()-dateone.getTime();
		System.out.println("int: "+interval);
		if (interval/10*10 == 0){
			interval = timeParser.parseSyntax("every " +day).get(0).getRecurInterval();
			return interval/10*10;
		}
		return (interval)/10*10/2;
	}
	
	public Date getDate(){
		return dateTime;
	}
	
	public DateGroup getRecurringDateGroup(){
		return recurringDateGroup;
	}
	
	public String getString(){
		return dateTimeString;
	}
	
	public boolean getRecurring(){
		return isRecurring;
	}
	
	public DateGroup getExceptStartDateGroup(){
		return exceptStartDateGroup;
	}
	
	public DateGroup getExceptEndDateGroup(){
		return exceptEndDateGroup;
	}
	
	
	public static boolean hasPrefix(String toCheck){
		return (isValidSubstring(toCheck,COMMAND_PREFIX_STARTDATE)
				|| isValidSubstring(toCheck,COMMAND_PREFIX_ENDDATE));
	}
	
	public static boolean hasStartPrefix(String toCheck){
		return isValidSubstring(toCheck,COMMAND_PREFIX_STARTDATE);
	}
	
	public static boolean hasEndPrefix(String toCheck){
		return (isValidSubstring(toCheck,COMMAND_PREFIX_ENDDATE));
	}

	private static boolean isValidSubstring(String toCheck, String substring) {
		return toCheck.contains(substring);
	}
	
	
	private boolean isExceptParseType(String parseType) {
		return parseType.equals(INDICATOR_EXCEPT);
	}

	private boolean isRecurringParseType(String parseType) {
		return parseType.equals(INDICATOR_RECURRING);
	}
	
	private static boolean isStartParseType(String parseType) {
		return parseType.equals(INDICATOR_START);
	}
	
	private static boolean isEndParseType(String parseType) {
		return parseType.equals(INDICATOR_END);
	}
	
	private static boolean isInvalidIndexes(int firstIndex, int secondIndex) {
		return firstIndex<0 || secondIndex<0;
	}
	
	private boolean isInvalidParseType(String parseType){
		return !(isStartParseType(parseType) || isEndParseType(parseType)
				|| isRecurringParseType(parseType) || isExceptParseType(parseType));
	}
	
	private static boolean isNotStartOrEndParseType(String parseType){
		return !(isEndParseType(parseType) || isStartParseType(parseType));
	}
	
	public boolean isEmptyDateGroup(List<DateGroup> dateGroup){
		return dateGroup.size()<=0;
	}
	
	public boolean hasParsableDate(String unparsedString){
		return !isEmptyDateGroup(timeParser.parseSyntax(unparsedString));
	}
	

}
	
```
###### DateTimeParserTest.java
``` java
 *
 */
public class DateTimeParserTest {

	PrettyTimeParser timeParser = new PrettyTimeParser();
	
	
	
	@Test
	public void testEveryWeek() {
		DateTimeParser dp = new DateTimeParser("end","shopping every monday");
		String output = dp.getString();
		assertEquals(output,"every monday");
	}
	
	@Test
	public void testFromEveryWeek() {
		//make this invalid!
		DateTimeParser dp = new DateTimeParser("start","f:every monday shopping ");
		String output = dp.getString();
		assertEquals(output,"f:every monday");
	}
	
	@Test
	public void testFormat1() {
		DateTimeParser dp = new DateTimeParser("end","shopping e:28 march f:16 march");
		String output = dp.getString();
		assertEquals(output,"e:28 march");
	}
	
	@Test
	public void testFormat2() {
		DateTimeParser dp = new DateTimeParser("start","shopping f:16 march e:28 march");
		String output = dp.getString();
		assertEquals(output,"f:16 march");
	}
	
	@Test
	public void testFormat3() {
		DateTimeParser dp = new DateTimeParser("end","shopping 16/03/2016");
		String output = dp.getString();
		assertEquals(output,"16/03/2016");
	}
	
	@Test
	public void testFormat4() {
		DateTimeParser dp = new DateTimeParser("start","shopping f:16/03/2016 e:28/03/2016");
		String output = dp.getString();
		assertEquals(output,"f:16/03/2016");
	}
	
	@Test
	public void testFormat5() {
		DateTimeParser dp = new DateTimeParser("start","shopping f:16 march e:28/03/2016");
		String output = dp.getString();
		assertEquals(output,"f:16 march");
	}
	
	@Test
	public void testFormat6() {
		DateTimeParser dp = new DateTimeParser("end","shopping e:28/03/2016 f:16 march");
		String output = dp.getString();
		assertEquals(output,"e:28/03/2016");
	}
	
	@Test
	public void testFormat7() {
		DateTimeParser dp = new DateTimeParser("end","shopping e:28/03/2016 f:16 mar");
		String output = dp.getString();
		assertEquals(output,"e:28/03/2016");
	}
	
	@Test
	public void testFormat8() {
		DateTimeParser dp = new DateTimeParser("end","shopping 16 march");
		String output = dp.getString();
		assertEquals(output,"16 march");
	}
	
	@Test
	public void testHasPrefix() {
		boolean output = DateTimeParser.hasPrefix("shopping 16 march");
		assertFalse(output);
	}
	
	@Test
	public void testAddStart() {
		DateTimeParser dp = new DateTimeParser("end","shopping tomorrow");
		Date endDate = timeParser.parseSyntax("tomorrow").get(0).getDates().get(0);
		Date output = dp.getDate();
		assertEquals(endDate.toString().trim(),output.toString().trim());
	}
	
	@Test
	public void testbreakUpStartAndEndDatesStart(){
		String output=DateTimeParser.breakUpStartAndEndDates("start","do project e: 28 march f:16 march");
		assertEquals(output,"do project f:16 march");
	}
	
	@Test
	public void testbreakUpStartAndEndDatesEnd(){
		String output=DateTimeParser.breakUpStartAndEndDates("end","do project e: 28 march f:16 march");
		assertEquals(output,"e: 28 march");
	}
	
	@Test
	public void testParseMessedUpTitle() {
		DateTimeParser dp = new DateTimeParser("end","do cs1231231231231232132341 tomorrow");
		Date endDate = timeParser.parseSyntax("tomorrow").get(0).getDates().get(0);
		Date output = dp.getDate();
		assertEquals(endDate.toString().trim(),output.toString().trim());
	}
	
	@Test
	public void testCalculateInterval(){
		long testOutput = DateTimeParser.calculateInterval("sunday");
		assertEquals(testOutput,1000*60*60*24*7);	
	}
	
	//confirms that the recurring option for DateTimeParser gives back the upcoming day specified
	@Test
	public void testEveryRecurring(){
		DateTimeParser dp = new DateTimeParser("recurring","do cs2103 e:28 march 0000 every wednesday");
		Date testDate = dp.getRecurringDateGroup().getDates().get(0);
		Date expectedDate = new PrettyTimeParser().parseSyntax("wednesday").get(0).getDates().get(0);
		assertEquals(testDate.toString(),expectedDate.toString());
	}
	
//	@Test
	public void testEveryEnd(){
		DateTimeParser dp = new DateTimeParser("end","do cs2103 e:28 march every friday");
	}
	
	@Test
	public void testParser(){
		PrettyTimeParser parser = new PrettyTimeParser();
		List<DateGroup> dg=parser.parseSyntax("every monday until 28 march except 1 march to 16 march");	
	}
	
	
	@Test
	public void testExtractException(){
		DateTimeParser dp = new DateTimeParser("except","e: 28 march except 16 march to 31 march");
		System.out.println(dp.getString());
		
	}
	
	@Test
	public void testExtractException2(){
		DateTimeParser dp = new DateTimeParser("except","e: 28 march except 16 march to 31 march");
		System.out.println(dp.getExceptStartDateGroup().getDates().get(0).toString());
		System.out.println(dp.getExceptEndDateGroup().getDates().get(0).toString());
		
	}
	
	@Test
	public void testExtractException3(){
		DateTimeParser dp = new DateTimeParser("except","e: 28 march except 16 march to 31 march");
		System.out.println(dp.getExceptStartDateGroup().getDates().get(0).toString());
		System.out.println(dp.getExceptEndDateGroup().getDates().get(0).toString());
		dp.parseAndCheckDate("every 1 day");
	}

	@Test
	public void testPrettyTimeParser(){
		PrettyTimeParser parser = new PrettyTimeParser();
		DateGroup dg = parser.parseSyntax("every 1 month except 10 april to 30 april").get(1);
		System.out.println(dg.getDates().toString()
				);
	}

}
```
###### DeleteParser.java
``` java
 *
 */
public class DeleteParser extends ArgsParser{
	
	private int itemID;
	private Command outputCommand = null;
	
	public DeleteParser(String userCommand){
		super(userCommand);
		getItemId();
	}
	
	public Command executeCommand(){
		if (outputCommand == null){
			return new DelCommand(itemID);
		} else{
			return outputCommand;
		}
		
	}
	
	public void getItemId(){
		try{
			itemID = Integer.parseInt(commandArgumentsString);
		} catch (Exception e){
			InvalidParser InvalidArgumentParser = new InvalidParser(commandArgumentsString);
			outputCommand = InvalidArgumentParser.executeCommand();
			logger.log(Level.INFO,"tried to parse '"+commandArgumentsString+"' into integer but failed.");
		}
	}	
}
```
###### DoneParser.java
``` java
 *
 */

public class DoneParser extends ArgsParser{
	
	private int itemID;
	private Command outputCommand = null;
	
	public DoneParser(String userCommand){
		super(userCommand);
		getItemId();
	}
	
	public Command executeCommand(){
		if (outputCommand == null){
			return new EditCommand(itemID, "status", POMPOM.STATUS_COMPLETED);
		} else{
			return outputCommand;
		}
		
	}
	
	public void getItemId(){
		try{
			itemID = Integer.parseInt(commandArgumentsString);
		} catch (Exception e){
			InvalidParser InvalidArgumentParser = new InvalidParser(commandArgumentsString);
			outputCommand = InvalidArgumentParser.executeCommand();
			logger.log(Level.INFO,"tried to parse '"+commandArgumentsString+"' into integer but failed.");
		}
	}	
}
```
###### EditParser.java
``` java
 *
 */
public class EditParser extends ArgsParser{
	
	private final int INDEX_TASK_ID = 0;
	private final int INDEX_FIELDS = 1;
	private final int INDEX_NEW_DATAS = 2; 
	
	private static final String FIELD_PRIORITY = "priority";
	private static final String FIELD_TITLE = "title";	
	private static final String FIELD_STATUS = "status";
	private static final String FIELD_LABEL = "label";
	private static final String FIELD_START_DATE = "start date";
	private static final String FIELD_END_DATE = "end date";

	private static final String[] FIELD_ARRAY = {FIELD_PRIORITY, FIELD_TITLE, 
												 FIELD_STATUS, FIELD_LABEL, 
												 FIELD_START_DATE, FIELD_END_DATE};
	
	private int taskID;
	private String field;
	private String newData;
	private Date newDate = null;
	private String taskTitle=null;
	
	private ArrayList<String> argsArray;
	
	
	public EditParser(String userCommand){
		super(userCommand);
		
		extractTaskID();
		extractFields();
		extractNewData();
	}
	
	public Command executeCommand(){
		System.out.println(newData);
		System.out.println(newDate);
		if (isEditingDateField()){
			return new EditCommand(taskID, field, newDate); 
		} else{
			return new EditCommand(taskID, field, newData);
		}
	}

	private boolean isEditingDateField() {
		return (newDate!=null);
	}
	
	public void extractTaskID(){
		String taskIDString = commandArgumentsString.split(" ",2)[0];
		commandArgumentsString = commandArgumentsString.split(" ",2)[1];
		try{
			taskID = Integer.parseInt(taskIDString);
		} catch (Exception e){
			taskTitle = taskIDString;
		}
	}
	
	public void extractFields(){
		
		for (String currField: FIELD_ARRAY){
			if (commandArgumentsString.indexOf(currField)==0){
				field=currField;
				commandArgumentsString = commandArgumentsString.replace(field, "").trim();
			}
		}
	}
	
	public void extractNewData(){
		newData = commandArgumentsString;
		if (field.equals("start date") || field.equals("end date") ){
			newDate = getDate(newData);
		}
	}

	private Date getDate(String data) {
		return POMPOM.timeParser.parseSyntax(data).get(0).getDates().get(0);
	}
	
	public String getField(){
		return field;
	}
	
	public String getNewData(){
		return newData;
	}
	
}
```
###### EditParserTest.java
``` java
 *
 */
public class EditParserTest {

	@Test
	public void testGetFields() {
		EditParser editParser = new EditParser("1 title new title");
		assertEquals(editParser.getField(),"title");
	}
	
	/*
	 * New data field has the name of the title inside.
	 */
	@Test
	public void testGetNewData() {
		EditParser editParser = new EditParser("1 title new title");
		
		assertEquals(editParser.getNewData(),"new title");
	}

}
```
###### EditRecurringParser.java
``` java
 *
 */
public class EditRecurringParser extends ArgsParser{
	
	private final int INDEX_TASK_ID = 0;
	private final int INDEX_FIELDS = 1;
	private final int INDEX_NEW_DATAS = 2; 
	
	private Long taskID;
	private String field;
	private String newData;
	
	private ArrayList<String> argsArray;
	
	
	public EditRecurringParser(String userCommand){
		super(userCommand);
		
		argsArray = new ArrayList<String>(Arrays.asList(commandArgumentsString.split(" ")));
		extractTaskID();
		System.out.println(argsArray.toString());
		extractFields();
		System.out.println(argsArray.toString());
		extractNewData();
	}
	
	public Command executeCommand(){
		return new EditRecurringCommand(taskID, field, newData); 
	}
	
	public void extractTaskID(){
		try{
			taskID = Long.parseLong(argsArray.remove(0));
		} catch (Exception e){
			taskID = (long) -1;
		}
	}
	
	public void extractFields(){
		field = argsArray.remove(0);
	}
	
	public void extractNewData(){
		newData = String.join(" ",argsArray);
	}
	
	public String getField(){
		return field;
	}
	
	public String getNewData(){
		return newData;
	}
	
}
```
###### ExitParser.java
``` java
 *
 */
public class ExitParser {
	public void executeCommand(){
		new ExitCommand();
		
	}
}
```
###### HelpParser.java
``` java
 *
 */
public class HelpParser{
	public HelpParser(){
		
	}
	public Command executeCommand(){
		return null;
	}
}
```
###### InvalidParser.java
``` java
 *
 */
public class InvalidParser {
	
	private String invalidCommand;
	public InvalidParser(String userCommand){
		invalidCommand = userCommand;
	}
	
	public Command executeCommand(){
		return new InvalidCommand(invalidCommand);
	}
}
```
###### Parser.java
``` java
 *
 */
public class Parser{
	
	/** List of Command types */
	private static final String CMD_ADD = "add";
	private static final String CMD_DELETE = "delete";
	private static final String CMD_DONE = "done";
	private static final String CMD_EDIT = "edit";
	private static final String CMD_EXIT = "exit";
	private static final String CMD_SEARCH = "search";
	private static final String CMD_SHOW = "show";
	private static final String CMD_UNDO ="undo";
	private static final String CMD_PATH = "setpath";
	private static final String CMD_EVENT = "event";
	private static final String CMD_HELP_1 = "help";
	private static final String CMD_HELP_2 = "?";
	private static final String CMD_DELETE_RECUR_1 = "delete recur";
	private static final String CMD_EDIT_RECUR_1= "edit recur";
	private static final String CMD_DELETE_RECUR_2 = "delete r";
	private static final String CMD_EDIT_RECUR_2 = "edit r";
	
	private static final String[] CMD_ARRAY = {CMD_ADD, CMD_DELETE, CMD_DONE,
												CMD_EDIT, CMD_EXIT, CMD_SEARCH,
												CMD_SHOW, CMD_UNDO, CMD_PATH,
												CMD_EVENT, CMD_HELP_1, CMD_HELP_2, CMD_DELETE_RECUR_1,
												CMD_EDIT_RECUR_1, CMD_DELETE_RECUR_2,
												CMD_EDIT_RECUR_2};
	

	private static final String CMD_VIEW = "view";	
	
	private static final int COMMAND_ARRAY_SIZE = 2; 
	private static final int COMMAND_TYPE_INDEX = 0;
	private static final int COMMAND_ARGUMENT_INDEX = 1;
	
	private static Parser parserInstance;
		
	private static Logger logger = Logger.getLogger("Parser");
	
	public static Parser getInstance()
	{
		if (parserInstance == null)
			parserInstance = new Parser();

		return parserInstance;
	}
	
	private Parser(){

	}
	
	/**
	 * This operation takes in the command specified by the
	 * user, executes it and returns a message about
	 * the execution information to the user.
	 * 
	 * @param userCommand
	 * 			is the command the user has given to the
	 * 			program
	 * @return
	 * 		the message containing information about the
	 * 		execution of the command. 
	 */
	public Command executeCommand(String userCommand){	
		
		
		String[] parsedCommandArray = splitCommand(userCommand);
		String commandType = parsedCommandArray[COMMAND_TYPE_INDEX];
		String commandArgument = parsedCommandArray[COMMAND_ARGUMENT_INDEX];

		switch (commandType){
			case CMD_ADD:
				AddParser addTaskArgumentParser = new AddParser(commandArgument, POMPOM.LABEL_TASK);
				return addTaskArgumentParser.getCommand();
			case CMD_EVENT:
				AddParser addEventArgumentParser = new AddParser(commandArgument, POMPOM.LABEL_EVENT);
				return addEventArgumentParser.getCommand();
			case CMD_DELETE:
				DeleteParser deleteArgumentParser = new DeleteParser(commandArgument);
				return deleteArgumentParser.executeCommand();
			case CMD_EDIT:
				EditParser EditArgumentParser = new EditParser(commandArgument);
				return EditArgumentParser.executeCommand();
			case CMD_SEARCH:
				SearchParser searchParser = new SearchParser(commandArgument);
				return searchParser.executeCommand();
			case CMD_SHOW:
				//return new ShowParser(commandArgument);
				//Hard coded
			case CMD_EXIT:
				ExitParser exitParser = new ExitParser();
				//return exitParser.executeCommand();
				System.exit(0);
			case CMD_UNDO:
				UndoParser undoParser = new UndoParser();
				return undoParser.executeCommand();
			case CMD_HELP_1:
			case CMD_HELP_2:
				HelpParser helpParser = new HelpParser();
				return helpParser.executeCommand();
			case CMD_PATH:
				return new PathCommand(commandArgument);
			case CMD_DELETE_RECUR_1:
			case CMD_DELETE_RECUR_2:
				return new command.DelRecurringCommand(Long.parseLong(commandArgument));
			case CMD_EDIT_RECUR_1:
			case CMD_EDIT_RECUR_2:
				EditRecurringParser EditRecurringArgumentParser = new EditRecurringParser(commandArgument);
				return EditRecurringArgumentParser.executeCommand();
			case CMD_VIEW:
				ViewParser viewParser = new ViewParser(commandArgument);
				return viewParser.executeCommand();
			case CMD_DONE:
				DoneParser DoneArgumentParser = new DoneParser(commandArgument);
				return DoneArgumentParser.executeCommand();
	}	
		InvalidParser InvalidArgumentParser = new InvalidParser(userCommand);
		return InvalidArgumentParser.executeCommand();

	}
	
	private String[] splitCommand(String userCommand){
		String[] outputArray = new String[COMMAND_ARRAY_SIZE];
		for (String command: CMD_ARRAY){
			if (userCommand.indexOf(command)==0){
				System.out.println(userCommand);
				outputArray[COMMAND_TYPE_INDEX]=command.trim();
				outputArray[COMMAND_ARGUMENT_INDEX]=userCommand.substring(command.length()).trim();
			}
		}
		return outputArray;
	}

}
 	
```
###### ParserTest.java
``` java
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
		Command outputCommand = parser.executeCommand("add do cs2013");
		assertTrue(outputCommand instanceof command.AddCommand);
	}
	
	/*
	 * This is the boundary case for the invalid user commands partition
	 */
	@Test
	public void testFailCommand() {
		Command outputCommand = parser.executeCommand("ad do cs2013");
		assertTrue(outputCommand instanceof command.InvalidCommand);
	}
	
	/*
	 * This is the boundary case for the invalid user commands partition
	 */
	@Test
	public void testRecurringCommands() {
		Command outputCommand = parser.executeCommand("delere recur do cs2013");
		assertTrue(outputCommand instanceof command.InvalidCommand);
	}
	


}
```
###### parseTest.java
``` java
 *
 */

public class parseTest{

	Parser parser = Parser.getInstance();
	

	//@Test
	public static void testAddParser() {
		//edit 1 status asd
		//add go club
		//add do homework next week
		//add do cs2103:finish v0.1 p:high l:hw s:open f:next monday next tuesday
		//edit 1 title asd
		//delete 1
		//exit
	}
	
	public static void prettyTime(){
		PrettyTimeParser timeParser = new PrettyTimeParser();
		List<DateGroup> dgl = timeParser.parseSyntax("every day");
		for (DateGroup dg: dgl){
			System.out.println(dg.getText());
		}	
		System.out.println("ended");
	}
	public static void main(String[] args){
		prettyTime();
	}
}
	
	
```
###### SearchParser.java
``` java
 *
 */
public class SearchParser extends ArgsParser{
	String keyWord;
	public SearchParser(String commandArguments) {
		super(commandArguments);
		keyWord = commandArguments;
	}
	public Command executeCommand(){
		
			System.out.println(commandArgumentsString + "lol");
			return new SearchCommand(keyWord);
		
		
	}

}
```
###### UndoParser.java
``` java
 *
 */

public class UndoParser {


	
	public Command executeCommand(){
		return new UndoCommand();
		
	}
	

	
}
```
###### ViewParser.java
``` java
 *
 */
public class ViewParser extends ArgsParser{
	
	private String view;
	
	public ViewParser(String commandArgument) {
		super(commandArgument);
		view = commandArgument;
	}
	public Command executeCommand(){
		
			return new ViewCommand(view);
		
		
	}

}
```
