# William
###### parser\parseTest.java
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
