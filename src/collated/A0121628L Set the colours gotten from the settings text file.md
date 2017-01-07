# A0121628L Set the colours gotten from the settings text file
###### gui\MainController.java
``` java
	 */
	public void setSettingsColours() {
		Settings setting = GUIModel.getSettings();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setBackgroundColor(setting.getBackgroundColour());
				returnMsg.setStyle(CSS_STYLE_TEXT
						+ setting.getReturnMsgColour());
				inputCommand.setStyle(CSS_STYLE_TEXT
						+ setting.getInputTxtColour());

			}
		});
	}

	/**
```
