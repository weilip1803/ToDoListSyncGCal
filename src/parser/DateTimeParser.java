package parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

import command.InvalidCommand;
/**
 *  @@author Josh
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
	