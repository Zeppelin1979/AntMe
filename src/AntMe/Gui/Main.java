package AntMe.Gui;
	
import java.util.Locale;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			boolean restart = true;
			Preferences prefs = Preferences.userNodeForPackage(getClass());

			while (restart) {
				restart = false;
				switch (prefs.get("culture", "de"))
				{
				case "de":
					Locale.setDefault(Locale.GERMANY);
					break;
				case "en":
					Locale.setDefault(Locale.ENGLISH);
					break;
				}
				
				BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("AntMe.fxml"));
				Scene scene = new Scene(root,400,400);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.show();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
