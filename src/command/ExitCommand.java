package command;

import java.io.IOException;

import main.POMPOM;
/**
 * @@author wen hao
 *
 */
public class ExitCommand {
	
	public ExitCommand() {
		try {
			POMPOM.getStorage().saveStorage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
