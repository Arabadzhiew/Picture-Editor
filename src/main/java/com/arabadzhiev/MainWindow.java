package com.arabadzhiev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

public class MainWindow extends Application {
	
	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("interface.fxml"));
			primaryStage.setTitle("Picture Editor");
			primaryStage.setScene(new Scene(root,800,600));
			primaryStage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
