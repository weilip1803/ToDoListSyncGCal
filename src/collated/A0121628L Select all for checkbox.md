# A0121628L Select all for checkbox
###### gui\MainController.java
``` java
	 */
	public void selectAllCheckBox(CheckBox cb) {
		cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov,
					Boolean old_val, Boolean new_val) {

				int rows = table.getItems().size();
				for (int i = 0; i < rows; i++) {
					boolean preVal = displayList.get(i).getChecked();

					displayList.get(i).setChecked(!preVal);

				}
			}
		});
	}

	/**
```
