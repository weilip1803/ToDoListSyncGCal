# A0121628L Displays Notification Message
###### gui\MainController.java
``` java
	 */
	public void displayReturnMessage(String message) {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(0), new KeyValue(returnMsg
						.textProperty(), message)));
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(4), new KeyValue(returnMsg
						.textProperty(), " ")));
		timeline.play();
	}

	/**
	 * HighLight the clicked label
	 * 
```
