# A0121628L When you hit the enter key on the keyboard
###### gui\MainController.java
``` java
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void enterCommandKey(KeyEvent event) throws IOException {
		if (event.getCode().equals(KeyCode.ENTER)) {
			// clear input string
			String input = inputCommand.getText();
			// Execute command
			String msg = pompom.execute(input);
			switchToTab(POMPOM.getCurrentTab().toLowerCase());
			POMPOM.getStorage().saveStorage();

			// Update GUI
			displayReturnMessage(msg);
			configureTable();
			setNotificationLabels();
			selectRow(input);
			lanuchHelp(POMPOM.showHelp);
			inputCommand.clear();
			inputCommand.setPromptText("Command:");

		}

	}

	/*********************************** Helper methods **********************************/

	/**
```
