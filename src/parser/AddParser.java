package parser;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import static org.junit.Assert.assertNotNull;

import command.AddCommand;
import command.AddRecurringCommand;
import command.Command;
import command.InvalidCommand;
import main.POMPOM;

/**
 * @@author A0121760R
 *
 */
public class AddParser extends ArgsParser {


	private static final int INDEX_FIRST_DELIMITER = 0;
	private static final int INDEX_BEGIN = 0;
	public static final String INTERVAL_PERIOD_STRING_ANNUALLY = "annually";
	public static final String INTERVAL_PERIOD_STRING_YEARLY = "yearly";
	public static final String INTERVAL_PERIOD_STRING_FORTNIGHTLY = "fortnightly";
	public static final String INTERVAL_PERIOD_STRING_BIMONTHLY = "bimonthly";
	public static final String INTERVAL_PERIOD_STRING_MONTHLY = "monthly";
	public static final String INTERVAL_PERIOD_STRING_BIWEEKLY = "biweekly";
	public static final String INTERVAL_PERIOD_STRING_WEEKLY = "weekly";
	public static final String INTERVAL_PERIOD_STRING_DAILY = "daily";
	public static final String MESSAGE_EMPTY_ERROR = "Title cannot be empty!";
	public static final String MESSAGE_DATE_ERROR = "\"%s\" is not a valid date!";
	public static final String MESSAGE_BOUND_ERROR = "\"%s\" contains an invalid date!";
	public static final String MESSAGE_RECURRING_ERROR = "\"%s\" is not valid! Usage: \"r:<interval> until <bound date>";
	public static final String MESSAGE_EXCLUSION_ERROR = "\"%s\" is not valid! Usage: \"x:<start date> to <end date>\"";
	public static final String MESSAGE_PRIORITY_ERROR = "\"%s\" is not valid. Only high, medium or low is accepted!";
	public static final String MESSAGE_END_BEFORE_FROM = "End date cannot be before Start date!";
	public static final String MESSAGE_START_DATE_SPECIFIED = "To add a recurring task, the start date must be specified!";
	public static final String MESSAGE_DATES_SPECIFIED = "To add an event, both start and end dates must be specified!";
	public static final String MESSAGE_UNKNOWN = "Unknown error occured!";
private static final String LOG_CREATE_ADD_PARSER = "AddParser Created for \"%s\"";

	private static final String STRING_EMPTY = "";
	private static final String DATE_FROM = "from";
	private static final String DATE_END = "to";
	private static final String DATE_BOUND = "bound";
	private static final String DATE_EXCLUDE = "exclude"; 	
	private static final String DELIMITER_UNTIL = "\\s*until\\s*";
	private static final String DELIMITER_TO = "\\s* to\\s*";
	static final String PRIORITY_HIGH_CMD1 = "high";
	static final String PRIORITY_HIGH_CMD2 = "hi";
	static final String PRIORITY_HIGH_CMD3 = "h";
	static final String PRIORITY_MEDIUM_CMD1 = "medium";
	static final String PRIORITY_MEDIUM_CMD2 = "med";
	static final String PRIORITY_MEDIUM_CMD3 = "m";
	static final String PRIORITY_LOW_CMD1 = "low";
	static final String PRIORITY_LOW_CMD2 = "lo";
	static final String PRIORITY_LOW_CMD3 = "l";
	
	private static final char TAG_FROM = 'f';
	private static final char TAG_END = 'e';
	private static final char TAG_RECURRING = 'r';
	private static final char TAG_LABEL = 'l';
	private static final char TAG_PRIORITY = 'p';
	private static final char TAG_EXCEPTION = 'x';
	private static final char DELIMITER = ':';
	
	private static final int NUMBER_OF_TOKENIZED_PARTS = 2;	
	private static final int RECURRING_INTERVAL_INDEX = 0;
	private static final int RECURRING_BOUND_DATE_INDEX = 1;
	private static final int DAYS_IN_ONE_YEAR = 365;
	private static final int EXCLUSION_TOKEN_END_DATE_INDEX = 1;
	private static final int EXCLUSION_TOKEN_START_DATE_INDEX = 0;
	private static final int INTERVAL_YEARLY = 1;
	private static final int INTERVAL_BIMONTHLY = 2;
	private static final int INTERVAL_MONTHLY = 1;
	private static final int INTERVAL_BIWEEKLY = 14;
	private static final int INTERVAL_WEEKLY = 7;
	private static final int INTERVAL_DAILY = 1;

	private PrettyTimeParser timeParser = new PrettyTimeParser();
	private int[] delimiterIndexes;
	private String title;
	private String from;
	private String end;
	private String recurring;
	private String label;
	private String priority;
	private String exclusion;
	private boolean isEmptyTitle; // Condition to become true: title is empty
	private boolean isEvent;
	private boolean isFromError;
	private boolean isEndError;
	private boolean isPriorityError;
	private boolean isRecurring;
	private boolean isRecurringError;
	private boolean isBoundError;
	private boolean isTokenizeError;
	private boolean isExclusionError;
	private boolean isExclusionDateInvalid;
	private boolean isExclusionDateSeqError;
	private boolean hasExclusion;

	public AddParser(String commandArguments, boolean isEvent) {
		super(commandArguments);
		getDelimiterIndexes(this.commandArgumentsString);
		initAttributes(isEvent);
		extractDataFields();
		
		logger.log(Level.INFO, String.format(LOG_CREATE_ADD_PARSER ,
												commandArgumentsString));
	}

	/**
	 * This method gets the indexes of all delimiter characters in the command
	 * string.
	 * 
	 * @param commandArgumentsString
	 *            is the command string the user has entered.
	 */
	private void getDelimiterIndexes(String commandArgumentsString) {
		this.delimiterIndexes = new int[1000];
		int currentIndex = 0;
		for (int i = 1; i < commandArgumentsString.length(); i++) {
			char prefix = commandArgumentsString.charAt(i-1);
			if (isDelimiter(commandArgumentsString.charAt(i), prefix)) {
				delimiterIndexes[currentIndex] = i;
				currentIndex++;
			}
		}
	}
	
	/**
	 * This method initializes all attributes in this parser object to its
	 * default values. Also sets if this parser is supposed to parse for an
	 * event or a task.
	 * 
	 * @param isEvent
	 *            indicates whether the parser is supposed to parse for an
	 *            event. If true, it will parse for an event. Else, it will
	 *            parse for a task.
	 */
	private void initAttributes(boolean isEvent) {
		setIsEmptyTitle(false);
		setEvent(isEvent);
		setFromError(false);
		setEndError(false);
		setPriorityError(false);
		setRecurring(false);
		setRecurringError(false);
		setBoundError(false);
		setTokenizeError(false);
		setExclusionError(false);
		
		setExclusionDateInvalid(false);
		setExclusionDateSeqError(false);
	}

	/**
	 * This method calls other helper methods to extract all necessary data and
	 * complete the fields.
	 * 
	 * @param commandArgumentsString
	 *            is the command the user has passed into the parser.
	 */
	private void extractDataFields() {
		assertNotNull(commandArgumentsString);
		extractTitle(commandArgumentsString);
		extractOtherFields(commandArgumentsString);
	}

	/**
	 * This method gets the data from every field other than title in the
	 * command the user has passed in.
	 * 
	 * @param commandArgumentsString
	 *            is the command the user has passed in.
	 */
	private void extractOtherFields(String commandArgumentsString) {

		// going through every ":" in commandArgumentsString
		for (int i = 0; i < delimiterIndexes.length; i++) {

			// if this delimiter does not appear at the beginning of the
			// command,
			// extract the field with that delimiter.
			if (isNonZeroDelimiterIndex(i)) {
				switch (commandArgumentsString.charAt(delimiterIndexes[i] - 1)) {
				case (TAG_FROM):
					this.from = extractField(commandArgumentsString, i);
					break;
				case (TAG_END):
					this.end = extractField(commandArgumentsString, i);
					break;
				case (TAG_RECURRING):
					this.recurring = extractField(commandArgumentsString, i);
					break;
				case (TAG_LABEL):
					this.label = extractField(commandArgumentsString, i);
					break;
				case (TAG_PRIORITY):
					this.priority = extractField(commandArgumentsString, i);
					break;
				case (TAG_EXCEPTION):
					setHasExclusion(true); // Flag to run exclusion checking when set to true.
					this.exclusion = extractField(commandArgumentsString, i);
				}
			}
		}
	}

	/**
	 * This method extracts the data and stores it in the specified field from
	 * the command the user has passed in.
	 * 
	 * @param commandArgumentsString
	 *            is the command the user has passed in.
	 * @param field
	 *            is the field which the data is to be stored into.
	 * @param currentDelimiter
	 *            is the indicator of which delimiter is to be used amongst all
	 *            other delimiters in delimiterIndexes.
	 */
	private String extractField(String commandArgumentsString, int currentDelimiter) {
		String fieldData=null;
		int nextDelimiter = getNextDelimiter(currentDelimiter);
		if (isValidDelimiter(nextDelimiter)) {
		
		fieldData = commandArgumentsString.substring(getIndexAfterDelimiter(currentDelimiter), 
													getIndexBeforeDelimiter(delimiterIndexes[nextDelimiter])).trim();
			
			
		} else {
			fieldData = commandArgumentsString.substring(getIndexAfterDelimiter(currentDelimiter)).trim();
		}
		return fieldData;
	}
	
	/**
	 * This method extracts the title in the command the user has passed in and
	 * stores it in the title attribute of this parser.
	 * 
	 * @param commandArgumentsString
	 *            is the command the user has passed in.
	 */
	private void extractTitle(String commandArgumentsString) {
		if (isNonZeroDelimiterIndex(0)) {
			this.title = commandArgumentsString.substring(INDEX_BEGIN, 
										getIndexBeforeDelimiter(delimiterIndexes[INDEX_FIRST_DELIMITER])).trim();
		} else {
			this.title = commandArgumentsString.trim();
		}
		if (hasNoArguments()) {
			setIsEmptyTitle(true);
		}
	}
	
	/**
	 * This method extracts the date in the command the user has passed in and
	 * stores it in the stated date attributes of this parser.
	 * 
	 * @param dateString
	 *            is the date in string extracted by extractDataFields().
	 * 
	 * @param dateField
	 *            is the date field, used for identifying which date field to
	 *            set the error for.
	 * 
	 * @return the Date object representing the parsed start date. If no date is
	 *         found, null is returned.
	 */
	private Date parseDate(String dateString, String dateField) {
		List<Date> dateList;
		if (isNotEmptyDate(dateString)) {
			dateList = timeParser.parse(dateString);

			if (dateList.isEmpty()) {
				setDateFieldError(dateField);
				return null;
			} else {
				return dateList.get(0);
			}

		} else {
			return null;
		}
	}

	/**
	 * This method sets the specified date's error field to be true.
	 * 
	 * @param field
	 *            is the date field whose error field is to be set to be true.
	 */
	private void setDateFieldError(String field) {
		field = field.toLowerCase();
		switch (field) {
		case (DATE_FROM):
			setFromError(true);
			break;
		case (DATE_END):
			setEndError(true);
			break;
		case (DATE_BOUND):
			setBoundError(true);
			break;
		case (DATE_EXCLUDE):
			setExclusionDateInvalid(true);
			break;
		}
	}
	
	/**
	 * This method checks, matches and returns the appropriate priority the user has
	 * specified in the command.
	 * 
	 * @return
	 * 		the appropriate priority string which will be used by Command objects to 
	 * 		identify what priority the task/event belongs to.
	 */
	private String parsePriority() {

		if (isNotEmptyPriority()) {

			if (isValidHighPriorityCommand()) {
				return POMPOM.PRIORITY_HIGH;
			} else if (isValidMediumPriorityCommand()) {
				return POMPOM.PRIORITY_MED;
			} else if (isValidLowPriorityCommand()) {
				return POMPOM.PRIORITY_LOW;
			} else {
				setPriorityError(true);
				return null;
			}
			
		} else {
			return null;
		}
	}
	
	/**
	 * This method splits the input up into two parts according to the delimiter, then
	 * checks if there are valid number of tokens and returns them.
	 * 
	 * @param input
	 * 			is the input string for that is to be tokenized. 
	 * @param delimiter
	 * 			is the regex used to split the input string up
	 * @return
	 * 			an array of tokens split upn by the delimiter
	 */
	private String[] tokenize(String input, String delimiter) {
		assertNotNull(input);
		
		Scanner s = new Scanner(input);
		s.useDelimiter(delimiter);
		String[] tokens = new String[NUMBER_OF_TOKENIZED_PARTS];
		int numberOfTokens = 0;
		
		while (s.hasNext()) {
			tokens[numberOfTokens] = s.next();
			numberOfTokens++;
		}
		s.close();
		return checkAndReturnTokens(delimiter, tokens, numberOfTokens);
	}
	
	/**
	 * This method checks if the number of token is correct. If not, it identifies
	 * the appropriate errors and sets their flags to true.
	 * 
	 * @param delimiter
	 * 			is the delimiter used to tokenize the string.
	 * @param tokens
	 * 			is an array of strings containing the tokens.
	 * @param numberOfTokens
	 *			is the number of tokens created.
	 * @return
	 * 			the tokens if the appropriate number of tokens is received.
	 * 			Else, null is returned and the appropriate error flags are set.
	 */
	private String[] checkAndReturnTokens(String delimiter, String[] tokens, int numberOfTokens) {
		if (isCorrectNumberOfTokenizedParts(numberOfTokens)) {
			return tokens;
		} else {
			if (delimiter.equalsIgnoreCase(DELIMITER_UNTIL)) {
				setTokenizeError(true);
			} else {
				setExclusionError(true);
			}
			return null;
		}
	}

	/**
	 * This method parses the recurring dates from the command argument. It obtains
	 * the dates between the start and end date.
	 * 
	 * @param startDate
	 * 			represents the start of a recurring period
	 * @param endDate
	 * 			represents the end of a recurring period 
	 * 			(different from boundDate, which represents the end of everything)
	 * 
	 * @return
	 * 		an ArrayList of an ArrayList of Dates. Index 0 if the returned ArrayList contains
	 * 		the start dates. Index 1 of the returned ArrayList contains the end dates.
	 */
	private ArrayList<ArrayList<Date>> parseRecurringDates(Date startDate, Date endDate) {
		if (isNotRecurringCommand()) {
			setRecurring(false);
			return null;
		} else {

			setRecurring(true);
			String[] recurringTokens = tokenize(this.recurring, DELIMITER_UNTIL);

			if (isValidRecurringCommand()) {
				String interval = recurringTokens[RECURRING_INTERVAL_INDEX];
				Date boundDate = parseDate(recurringTokens[RECURRING_BOUND_DATE_INDEX], DATE_BOUND);

				ArrayList<Date> recurringStartDates = new ArrayList<Date>();
				ArrayList<Date> recurringEndDates = new ArrayList<Date>();

				if (isNotEmptyDate(startDate)) {
					recurringStartDates = processRecurringStartDate(startDate, interval, boundDate);
				}

				if (isNotEmptyDate(endDate)) {
					processRecurringEndDate(endDate, interval, recurringStartDates, recurringEndDates);
				}
				
				//Check if the date is within the excluded period.
				if (hasExclusionAndNonEmptyDates(startDate, endDate)) {
					processExclusion(recurringStartDates, recurringEndDates);
				}

				ArrayList<ArrayList<Date>> recurringDates = new ArrayList<ArrayList<Date>>();
			
				recurringDates.add(recurringStartDates);
				recurringDates.add(recurringEndDates);

				return recurringDates;
			} else {
				return null;
			}

		}

	}
	
	/**
	 * This method checks all start and end dates and sees if they are within the excluded
	 * period or not. If they are, the dates is/are removed from the ArrayList.
	 * 
	 * @param recurringStartDates
	 * 			is the arrayList containing all the start dates.
	 * @param recurringEndDates
	 * 			is the arrayList containing all the end dates.
	 */
	private void processExclusion(ArrayList<Date> recurringStartDates, 
									ArrayList<Date> recurringEndDates) {
		Date startDate;
		Date endDate;
	
		String[] exclusionTokens = tokenize(this.exclusion, DELIMITER_TO);

		if (hasNoExclusionErrors()) {

			Date exclusionStartDate = parseDate(exclusionTokens[EXCLUSION_TOKEN_START_DATE_INDEX], 
												DATE_EXCLUDE);
			Date exclusionEndDate = parseDate(exclusionTokens[EXCLUSION_TOKEN_END_DATE_INDEX], 
												DATE_EXCLUDE);

			if (isInvalidExclusionDates(exclusionStartDate, exclusionEndDate)) {
				setExclusionDateInvalid(true);

			} else if (exclusionStartDate.after(exclusionEndDate)) {
				//Start date should never be after end dates!
				setExclusionDateSeqError(true);

			} else {
				
				//No exclusion errors, proceed to remove the excluded dates.
				for (int i = 0; i < recurringStartDates.size(); i++) {

					startDate = recurringStartDates.get(i);
					endDate = recurringEndDates.get(i);

					if (isWithinExclusionDates(startDate, endDate, 
												exclusionStartDate, 
												exclusionEndDate)) {
						recurringStartDates.remove(i);
						recurringEndDates.remove(i);
						i--;
					}

				}
			}
		}
	}
	
	/**
	 * This method checks for the time period keywords, determines the
	 * interval and adds end dates until all start dates have end dates.
	 * 
	 * @param endDate
	 * 			is the first end date specified.
	 * @param interval
	 * 			is the interval period stated in string by the user.
	 * @param recurringStartDates
	 * 			is the arrayList containing all recurring the start dates.
	 * @param recurringEndDates
	 * 			is the arrayList containing all the recurring end dates.
	 */
	private void processRecurringEndDate(Date endDate, String interval,
									ArrayList<Date> recurringStartDates,
									ArrayList<Date> recurringEndDates) {
		
		recurringEndDates.add(endDate);
		Date nextEndDate;
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);

		for (int i = 0; i < recurringStartDates.size(); i++) {

			if (isDailyPeriod(interval)) {
				calEnd.add(Calendar.DAY_OF_MONTH, INTERVAL_DAILY);
				
			} else if (isWeeklyPeriod(interval)) {
				calEnd.add(Calendar.DAY_OF_MONTH, INTERVAL_WEEKLY);
				
			} else if (isBiweeklyPeriod(interval)) {
				calEnd.add(Calendar.DAY_OF_MONTH, INTERVAL_BIWEEKLY);
				
			} else if (isMonthlyPeriod(interval)) {
				calEnd.add(Calendar.MONTH, INTERVAL_MONTHLY);
				
			} else if (isBiMonthlyPeriod(interval)) {
				calEnd.add(Calendar.MONTH, INTERVAL_BIMONTHLY);
				
			} else if (isYearlyPeriod(interval)) {
				calEnd.add(Calendar.YEAR, INTERVAL_YEARLY);
				
			} else {
				setRecurringError(true);
			}

			nextEndDate = calEnd.getTime();

			calEnd.setTime(nextEndDate);
			recurringEndDates.add(nextEndDate);

		}
	}

	/**
	 * 	This method checks for the time period keywords, determines the
	 * interval and adds start dates until all the bound has been reached.
	 * 
	 * @param startDate 
	 * 			is the beginning of the period specified by the user.
	 * @param interval
	 * 			is the interval period in string specified by the user.
	 * @param boundDate
	 * 			is the date when no more events should be added after that
	 * 			as specified by the the user.
	 * @return
	 * 			the arrayList of start dates within the bound period separated
	 * 			by the interval period.
	 */
	private ArrayList<Date> processRecurringStartDate(Date startDate, String interval, Date boundDate) {
		ArrayList<Date> recurringStartDates = new ArrayList<Date>();
		recurringStartDates.add(startDate);
		Date nextStartDate;
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);

		for (int i = 0; i < DAYS_IN_ONE_YEAR; i++) {
			
			if (isDailyPeriod(interval)) {
				calStart.add(Calendar.DAY_OF_MONTH, INTERVAL_DAILY);
				
			} else if (isWeeklyPeriod(interval)) {
				calStart.add(Calendar.DAY_OF_MONTH, INTERVAL_WEEKLY);
				
			} else if (isBiweeklyPeriod(interval)) {
				calStart.add(Calendar.DAY_OF_MONTH, INTERVAL_BIWEEKLY);
				
			} else if (isMonthlyPeriod(interval)) {
				calStart.add(Calendar.MONTH, INTERVAL_MONTHLY);
				
			} else if (isBiMonthlyPeriod(interval)) {
				calStart.add(Calendar.MONTH, INTERVAL_BIMONTHLY);
				
			} else if (isYearlyPeriod(interval)) {
				calStart.add(Calendar.YEAR, INTERVAL_YEARLY);
				
			} else {
				setRecurringError(true);
			}

			nextStartDate = calStart.getTime();

			calStart.setTime(nextStartDate);

			if (nextStartDate.before(boundDate)) {
				recurringStartDates.add(nextStartDate);
			} else {
				break;
			}

		}
		return recurringStartDates;
	}
	
	/**
	 * This method checks for the all error flags and throws any error messages if necessary.
	 * After which, it determines whether the new item to be added is a task or event, and
	 * perform validation checks on it. If all checks are passed, a AddCommand or
	 * AddRecurringCommand will be returned.
	 * 
	 * @returns 
	 * 		an InvalidCommand if it fails any error checks. Else if its a normal add command,
	 * 		returns an AddCommand. Else if its a recurring add command, returns a RecurringAddCommand.
	 */
	public Command parse() {
		String errorMsg;
		Date startDate = parseDate(this.from, DATE_FROM);
		Date endDate = parseDate(this.end, DATE_END);
		
		String parsedPriority = parsePriority();
		ArrayList<ArrayList<Date>> recurringDate = parseRecurringDates(startDate, endDate);

		Command invalidCommand = checkForGeneralErrors(startDate, endDate);
		if (isNotNullInvalidCommand(invalidCommand)){
			return invalidCommand;
		}
		
		else if (isEvent()) {

			return processEvent(startDate, endDate, parsedPriority, recurringDate);

		} else if (!isEvent()) {
			return processTask(startDate, endDate, parsedPriority, recurringDate);

		} else {
			errorMsg = MESSAGE_UNKNOWN;
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;
		}
	}

	/**
	 * This method checks if the item to be added is a valid task. If it is,
	 * it checks if the task is recurring or not, then returns the appropriate
	 * Command object.
	 * 
	 * @param startDate
	 * 			is the start date of the task specified by the user.
	 * @param endDate
	 * 			is the end date of the task specified by the user.
	 * @param parsedPriority
	 * 			is the priority of the task that has been checked and returned
	 * 			by parsePriority().
	 * @param recurringDate
	 * 			is the ArrayList of dates when the parser is parsing a recurring command.
	 * @return
	 */
	private Command processTask(Date startDate, Date endDate, String parsedPriority,
								ArrayList<ArrayList<Date>> recurringDate) {
		String errorMsg;
		Command invalidCommand;
		if (isRecurring()) {
			if (isEmptyDate(startDate)) {
				errorMsg = MESSAGE_START_DATE_SPECIFIED;
				invalidCommand = new InvalidCommand(errorMsg);
				return invalidCommand;

			} else {
				return processRecurringTask(endDate, parsedPriority, recurringDate);
			}

		} else {
			AddCommand add = new AddCommand(POMPOM.LABEL_TASK, title, null, parsedPriority, 
											POMPOM.STATUS_PENDING, label, startDate, endDate);
			return add;
		}
	}

	/**
	 * This method checks if the item to be added is a valid event. If it is,
	 * it checks if the event is recurring or not, then returns the appropriate
	 * Command object.
	 * 
	 * @param startDate
	 * 			is the start date of the task specified by the user.
	 * @param endDate
	 * 			is the end date of the task specified by the user.
	 * @param parsedPriority
	 * 			is the priority of the task that has been checked and returned
	 * 			by parsePriority().
	 * @param recurringDate
	 * 			is the ArrayList of dates when the parser is parsing a recurring command.
	 * @return
	 */
	private Command processEvent(Date startDate, Date endDate, String parsedPriority,
									ArrayList<ArrayList<Date>> recurringDate) {
		String errorMsg;
		Command invalidCommand;
		if (isInvalidExclusionDates(startDate, endDate)) {
			errorMsg = MESSAGE_DATES_SPECIFIED;
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (isRecurring()) {
			return processRecurringEvent(parsedPriority, recurringDate);

		} else if (!isRecurring()) {
			AddCommand add = new AddCommand(POMPOM.LABEL_EVENT, title, null, parsedPriority, 
											POMPOM.STATUS_PENDING, label, startDate, endDate);
			return add;

		} else {
			errorMsg = MESSAGE_UNKNOWN;
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;
		}
	}
	
	/**
	 * This method helps to convert all dates into AddCommand tasks.
	 *  
	 * @param endDate
	 * 			is the end date of the first period of the recurring task.
	 * @param parsedPriority
	 * 			is the priority of the recurring task
	 * @param recurringDate
	 * 			is the arrayList containing all recurring start and end dates.
	 * @return
	 * 		an ArrayList of AddCommands with different start and end times (if specified).
	 */
	private Command processRecurringTask(Date endDate, String parsedPriority,
											ArrayList<ArrayList<Date>> recurringDate) {
		ArrayList<Date> recurringStartDates = recurringDate.get(0);
		ArrayList<Date> recurringEndDates = recurringDate.get(1);
		ArrayList<AddCommand> addList = new ArrayList<AddCommand>();
		for (int i = 0; i < recurringStartDates.size(); i++) {
			if (isEmptyDate(endDate)) {
				AddCommand toAdd = new AddCommand(POMPOM.LABEL_TASK, title, null, parsedPriority, 
													POMPOM.STATUS_PENDING, label, recurringStartDates.get(i), 
													null, true);
				addList.add(toAdd);
			} else {
				AddCommand toAdd = new AddCommand(POMPOM.LABEL_TASK, title, null, parsedPriority,
													POMPOM.STATUS_PENDING, label, recurringStartDates.get(i), 
													recurringEndDates.get(i), true);
				addList.add(toAdd);
			}
		}

		AddRecurringCommand addRecurring = new AddRecurringCommand(addList);
		return addRecurring;
	}

	/**
	 * This method helps to convert all dates into AddCommand events.
	 *  
	 * @param endDate
	 * 			is the end date of the first period of the recurring event.
	 * @param parsedPriority
	 * 			is the priority of the recurring event.
	 * @param recurringDate
	 * 			is the arrayList containing all recurring start and end dates.
	 * @return
	 * 		an ArrayList of AddCommands with different start and end times (if specified).
	 */
	private Command processRecurringEvent(String parsedPriority, ArrayList<ArrayList<Date>> recurringDate) {
		ArrayList<Date> recurringStartDates = recurringDate.get(0);
		ArrayList<Date> recurringEndDates = recurringDate.get(1);
		ArrayList<AddCommand> addList = new ArrayList<AddCommand>();
		for (int i = 0; i < recurringStartDates.size(); i++) {

			AddCommand toAdd = new AddCommand(POMPOM.LABEL_EVENT, title, null, parsedPriority,
												POMPOM.STATUS_PENDING, label, recurringStartDates.get(i), 
												recurringEndDates.get(i), true);
			addList.add(toAdd);
		}

		AddRecurringCommand addRecurring = new AddRecurringCommand(addList);
		return addRecurring;
	}

	/**
	 * This method will check if any flags were raised throughout the parsing process.
	 * If there is any raised flag, an InvalidCommand object will be returned.
	 * 
	 * @param startDate
	 * 			is the start date specified by the user.
	 * @param endDate
	 * 			is the end date specified by the user.
	 * @return
	 * 		null if there is no raised flags. InvalidCommand if there is any raised
	 * `	error flags.
	 */
	private Command checkForGeneralErrors(Date startDate, Date endDate) {
		String errorMsg;
		InvalidCommand invalidCommand;
		if (isEmptyArguments() || isEmptyTitle) {
			errorMsg = MESSAGE_EMPTY_ERROR;
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (startDate != null && endDate != null && endDate.before(startDate)) {
			errorMsg = MESSAGE_END_BEFORE_FROM;
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (isFromError()) {
			errorMsg = String.format(MESSAGE_DATE_ERROR, this.from);
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (isEndError()) {
			errorMsg = String.format(MESSAGE_DATE_ERROR, this.end);
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (isPriorityError()) {
			errorMsg = String.format(MESSAGE_PRIORITY_ERROR, this.priority);
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (isRecurringError() || isTokenizeError) {
			errorMsg = String.format(MESSAGE_RECURRING_ERROR, this.recurring);
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (isBoundError()) {
			errorMsg = String.format(MESSAGE_BOUND_ERROR, this.recurring);
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (isExclusionError()) {
			errorMsg = String.format(MESSAGE_EXCLUSION_ERROR, this.exclusion);
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (isExclusionDateInvalid()) {
			errorMsg = String.format(MESSAGE_DATE_ERROR, this.exclusion);
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;

		} else if (isExclusionDateSeqError()) {
			errorMsg = MESSAGE_END_BEFORE_FROM;
			invalidCommand = new InvalidCommand(errorMsg);
			return invalidCommand;
		} else {
			return null;
		}
	}
	
	//GETTERS AND SETTERS

	/**
	 * Gets the index of the character that is before the next delimiter.
	 * 
	 * @param nextDelimiter
	 *            is the index of the chosen delimiter.
	 * @return the index before the chosen delimiter
	 */
	private int getIndexBeforeDelimiter(int delimiterIndex) {
		return delimiterIndex - 1;
	}

	/**
	 * Gets the index of the character after the currently selected delimiter.
	 * 
	 * @param currentDelimiter
	 *            is the index which the desired delimiter appears in the
	 *            delimiter indexes array.
	 * @return the index after the index of the desired delimiter in the
	 *         delimiter index array.
	 */
	private int getIndexAfterDelimiter(int chosenDelimiter) {
		return delimiterIndexes[chosenDelimiter] + 1;
	}
	private boolean isValidRecurringCommand() {
		return !isRecurringError() && !isBoundError() && !isTokenizeError();
	}
	
	private int getNextDelimiter(int currentDelimiter) {
		return currentDelimiter + 1;
	}

	private boolean isNonZeroDelimiterIndex(int selectDelimiter) {
		return delimiterIndexes[selectDelimiter] != 0;
	}

	private boolean isDelimiter(char character, char selectedPrefix) {
		boolean isValidPrefix=false;
		char[] prefixes = {TAG_FROM, TAG_END, TAG_RECURRING,
							TAG_LABEL, TAG_PRIORITY, TAG_EXCEPTION};
		
		for (char currentPrefix: prefixes){
			if (selectedPrefix == currentPrefix){
				isValidPrefix=true;
			}
		}
		return (character == DELIMITER && isValidPrefix);
	}

	private boolean isEmptyArguments() {
		return commandArgumentsString.equals(STRING_EMPTY);
	}

	private boolean isNotEmptyDate(String dateField) {
		return dateField != null;
	}
	
	private boolean isNotEmptyDate(Date dateField) {
		return dateField != null;
	}
	
	private boolean isEmptyDate(Date dateField) {
		return dateField == null;
	}

	private void setIsEmptyTitle(boolean isEmptyTitle) {
		this.isEmptyTitle = isEmptyTitle;
	}

	private boolean isEvent() {
		return isEvent;
	}

	private void setEvent(boolean isEvent) {
		this.isEvent = isEvent;
	}

	private boolean isFromError() {
		return isFromError;
	}

	private void setFromError(boolean isFromError) {
		this.isFromError = isFromError;
	}

	private boolean isEndError() {
		return isEndError;
	}

	private void setEndError(boolean isEndError) {
		this.isEndError = isEndError;
	}

	private boolean isPriorityError() {
		return isPriorityError;
	}

	private void setPriorityError(boolean isPriorityError) {
		this.isPriorityError = isPriorityError;
	}

	private boolean isRecurring() {
		return isRecurring;
	}

	private void setRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	private boolean isRecurringError() {
		return isRecurringError;
	}

	private void setRecurringError(boolean isRecurringError) {
		this.isRecurringError = isRecurringError;
	}

	private boolean isBoundError() {
		return isBoundError;
	}

	private void setBoundError(boolean isBoundError) {
		this.isBoundError = isBoundError;
	}

	private boolean isTokenizeError() {
		return isTokenizeError;
	}

	private void setTokenizeError(boolean isTokenizeError) {
		this.isTokenizeError = isTokenizeError;
	}

	private boolean isExclusionError() {
		return isExclusionError;
	}

	private void setExclusionError(boolean isExceptionError) {
		this.isExclusionError = isExceptionError;
	}

	private boolean isExclusionDateInvalid() {
		return isExclusionDateInvalid;
	}

	private void setExclusionDateInvalid(boolean isExclusionDateInvalid) {
		this.isExclusionDateInvalid = isExclusionDateInvalid;
	}

	private boolean hasExclusion() {
		return hasExclusion;
	}

	private void setHasExclusion(boolean hasException) {
		this.hasExclusion = hasException;
	}

	private boolean isExclusionDateSeqError() {
		return isExclusionDateSeqError;
	}

	private void setExclusionDateSeqError(boolean isExclusionDateSeqError) {
		this.isExclusionDateSeqError = isExclusionDateSeqError;
	}
	
	private boolean isCorrectNumberOfTokenizedParts(int numberOfTokens) {
		return numberOfTokens == NUMBER_OF_TOKENIZED_PARTS;
	}
	
	private boolean isWithinExclusionDates(Date startDate, Date endDate, Date exclusionStartDate,
			Date exclusionEndDate) {
		return exclusionStartDate.before(endDate) && exclusionEndDate.after(startDate);
	}

	private boolean isInvalidExclusionDates(Date exclusionStartDate, Date exclusionEndDate) {
		return exclusionStartDate == null || exclusionEndDate == null;
	}

	private boolean hasNoExclusionErrors() {
		return !isExclusionError() && !isExclusionDateInvalid();
	}
	
	private boolean isYearlyPeriod(String interval) {
		return interval.equalsIgnoreCase(INTERVAL_PERIOD_STRING_ANNUALLY) ||
				interval.equalsIgnoreCase(INTERVAL_PERIOD_STRING_YEARLY);
	}

	private boolean isBiMonthlyPeriod(String interval) {
		return interval.equalsIgnoreCase(INTERVAL_PERIOD_STRING_BIMONTHLY);
	}

	private boolean isMonthlyPeriod(String interval) {
		return interval.equalsIgnoreCase(INTERVAL_PERIOD_STRING_MONTHLY);
	}

	private boolean isDailyPeriod(String interval) {
		return interval.equalsIgnoreCase(INTERVAL_PERIOD_STRING_DAILY);
	}

	private boolean isWeeklyPeriod(String interval) {
		return interval.equalsIgnoreCase(INTERVAL_PERIOD_STRING_WEEKLY);
	}

	private boolean isBiweeklyPeriod(String interval) {
		return interval.equalsIgnoreCase(INTERVAL_PERIOD_STRING_BIWEEKLY) || 
				interval.equalsIgnoreCase(INTERVAL_PERIOD_STRING_FORTNIGHTLY);
	}

	private boolean isNotRecurringCommand() {
		return this.recurring == null;
	}
	
	private boolean isNotNullInvalidCommand(Command invalidCommand) {
		return invalidCommand!=null;
	}
	
	private boolean hasExclusionAndNonEmptyDates(Date startDate, Date endDate) {
		return hasExclusion() && isNotEmptyDate(startDate) && isNotEmptyDate(endDate);
	}

	private boolean isValidDelimiter(int nextDelimiter) {
		return nextDelimiter < 6 && isNonZeroDelimiterIndex(nextDelimiter);
	}

	private boolean hasNoArguments() {
		return isEmptyArguments() || this.title.trim().equals(STRING_EMPTY);
	}
	
	private boolean isNotEmptyPriority() {
		return this.priority != null;
	}

	private boolean isValidHighPriorityCommand() {
		//Appropriate commands are: "high", "hi", "h"
		return this.priority.equalsIgnoreCase(PRIORITY_HIGH_CMD1) 
				|| this.priority.equalsIgnoreCase(PRIORITY_HIGH_CMD2) 
				|| this.priority.equalsIgnoreCase(PRIORITY_HIGH_CMD3);
	}
	
	private boolean isValidMediumPriorityCommand() {
		//Appropriate commands are: "medium", "med", "m"
		return this.priority.equalsIgnoreCase(PRIORITY_MEDIUM_CMD1) 
				|| this.priority.equalsIgnoreCase(PRIORITY_MEDIUM_CMD2) 
				|| this.priority.equalsIgnoreCase(PRIORITY_MEDIUM_CMD3);
	}
	
	private boolean isValidLowPriorityCommand() {
		//Appropriate commands are: "low", "lo", "l"
		return this.priority.equalsIgnoreCase(PRIORITY_LOW_CMD1) 
				|| this.priority.equalsIgnoreCase(PRIORITY_LOW_CMD2) 
				|| this.priority.equalsIgnoreCase(PRIORITY_LOW_CMD3);
	}
}