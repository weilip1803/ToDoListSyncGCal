package gui;

import java.io.IOException;

import utils.Item;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * @@author A0126375A
 */

public class Main extends Application {

	MainController mainController;

	/**
	 * This operation is to launch Main.class
	 */
	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}

	/**
	 * This operation is to load the main POMPOM FXML file
	 * @param Stage stage
	 */
	@Override
	public void start(Stage stage) throws Exception {
		getClass();
		Parent root = FXMLLoader.load(getClass().getResource("POMPOM.fxml"));
		stage.setTitle("POMPOM");
		Scene scene = new Scene(root);

		scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ESCAPE) {
					stage.close();
				}
			}
		});
		stage.setScene(scene);

		stage.show();

	}

	/**
	 * This operation is to load the New FXML file POPUP
	 * @param input is the main controller
	 */
	public Stage newDialog(MainController mainControl) {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("New.fxml"));
			Pane page = (Pane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("New");
			dialogStage.initModality(Modality.APPLICATION_MODAL);

			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			NewController newTaskController = loader.getController();
			newTaskController.setDialogStage(dialogStage);
			newTaskController.setMainControl(mainControl);

			dialogStage.showAndWait();
			return dialogStage;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This operation is to load the Edit FXML file POPUP
	 * @param input is the main controller
	 */
	public Stage editDialog(Item item, MainController mainControl) {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("Edit.fxml"));
			Pane page = (Pane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			EditController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setItem(item);

			controller.setMainControl(mainControl);

			dialogStage.showAndWait();
			return dialogStage;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
 
	/**
	 * This operation is to load the HELP FXML File POPUP
	 */
	public void helpDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("Help.fxml"));
			Pane page = (Pane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Help");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			Scene scene = new Scene(page);

			scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ESCAPE) {
						dialogStage.close();
					}
				}
			});

			dialogStage.setScene(scene);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
