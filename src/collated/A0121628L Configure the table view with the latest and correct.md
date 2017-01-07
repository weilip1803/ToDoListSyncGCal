# A0121628L Configure the table view with the latest and correct
###### gui\MainController.java
``` java
	 *          information
	 */
	void configureTable() {
		table.setEditable(true);

		taskID.setCellValueFactory(new PropertyValueFactory<Item, Number>("id"));
		taskName.setCellValueFactory(new PropertyValueFactory<Item, String>(
				"title"));

		// Checkbox init
		checkBox.setGraphic(selectAll);
		checkBox.setCellFactory(CheckBoxTableCell.forTableColumn(checkBox));
		checkBox.setCellValueFactory(c -> c.getValue().checkedProperty());
		taskStartDateTime
				.setCellValueFactory(new PropertyValueFactory<Item, String>(
						"sd"));
		taskEndDateTime
				.setCellValueFactory(new PropertyValueFactory<Item, String>(
						"ed"));
		taskLabel.setCellValueFactory(new PropertyValueFactory<Item, String>(
				"label"));
		taskPriority
				.setCellValueFactory(new PropertyValueFactory<Item, String>(
						"priority"));
		taskStatus.setCellValueFactory(new PropertyValueFactory<Item, String>(
				"status"));

		GUIModel.update();

		selectAllCheckBox(selectAll);
		// Populate data

		table.setItems(displayList);
		table.refresh();

	}

	/**
	 * Highlight the item which has just been added or modified
	 * 
```
