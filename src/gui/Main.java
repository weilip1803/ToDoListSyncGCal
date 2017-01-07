package gui;

import java.io.IOException;
import java.net.URL;

import utils.Item;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * @@author Jorel
 *
 */
public class Main extends Application {

	@FXML
	Pane content; 
	
	MainController mainController;
	public static void main(String[] args) {
		Application.launch(Main.class, args); 
	}

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

	public Stage newTaskDialog(MainController mainControl) {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("NewTask.fxml"));
			Pane page = (Pane) loader.load();			
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("New Task");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			NewTaskController newTaskController = loader.getController();
			newTaskController.setDialogStage(dialogStage);
			newTaskController.setMainControl(mainControl);		
			
			dialogStage.showAndWait(); 
			return dialogStage;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Stage editTaskDialog(Item item, MainController mainControl) {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("EditTask.fxml"));
			Pane page = (Pane) loader.load();

			Stage dialogStage = new Stage(); 
			dialogStage.setTitle("Edit Task");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			EditTaskController controller = loader.getController();
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
