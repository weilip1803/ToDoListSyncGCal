# A0121628L Set the notification numbers in the labels Eg Number
###### gui\MainController.java
``` java
	 *          of task today
	 */
	public void setNotificationLabels() {
		GUIModel.update();
		taskNo.setText(ListClassifier.getTodayTask(POMPOM.getStorage()
				.getTaskList()));
		overdueNo.setText(ListClassifier.getOverdueTask(POMPOM.getStorage()
				.getTaskList()));
		eventsNo.setText(ListClassifier.getTodayEvent(POMPOM.getStorage()
				.getTaskList()));
	}

	/**
```
