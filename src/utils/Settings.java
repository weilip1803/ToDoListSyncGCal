package utils;

/**
 * Holder object which contains all the settings variables
 * 
 * @@author A0121628L
 *
 */
public class Settings {
	private String storagePath;
	private String backgroundColour;
	private String returnMsgColour;
	private String inputTxtColour;

	public Settings(String storagePath, String backgroundColour,
			String returnMsgColour, String inputTxtColour) {
		super();
		this.storagePath = storagePath;
		this.backgroundColour = backgroundColour;
		this.returnMsgColour = returnMsgColour;
		this.inputTxtColour = inputTxtColour;
	}

	public Settings() {

	}

	public String getReturnMsgColour() {
		return returnMsgColour;
	}

	public void setReturnMsgColour(String returnMsgColour) {
		this.returnMsgColour = returnMsgColour;
	}

	public String getInputTxtColour() {
		return inputTxtColour;
	}

	public void setInputTxtColour(String inputTxtColour) {
		this.inputTxtColour = inputTxtColour;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public String getBackgroundColour() {
		return backgroundColour;
	}

	public void setBackgroundColour(String backgroundColour) {
		this.backgroundColour = backgroundColour;
	}

}
