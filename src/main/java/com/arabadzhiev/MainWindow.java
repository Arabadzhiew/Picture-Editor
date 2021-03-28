package com.arabadzhiev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {
	
	public static Rectangle2D stageBounds;
	
	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("interface.fxml"));
			stage.setTitle("Picture Editor");
			stage.setScene(new Scene(root,800,600));
			stage.show();
			stageBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(),stage.getHeight());
			stage.widthProperty().addListener((property, oldVal, newVal) ->
				stageBounds = new Rectangle2D(stage.getX(),stage.getY(),(double)newVal,stage.getHeight())
			);
			stage.heightProperty().addListener((property, oldVal, newVal) ->
				stageBounds = new Rectangle2D(stage.getX(),stage.getY(),stage.getWidth(),(double)newVal)
			);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
