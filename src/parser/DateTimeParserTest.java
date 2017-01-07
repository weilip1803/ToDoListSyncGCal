package parser;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
/**
 *  @@author Josh
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
