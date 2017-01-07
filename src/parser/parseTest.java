package parser;

import command.AddCommand;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.Test;


/**
 *  @@author Josh
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
	
	