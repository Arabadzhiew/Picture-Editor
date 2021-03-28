package com.arabadzhiev;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CoordinateDrawer {
	
	public int[] display(){
		int[] coordinates = new int[4];
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setResizable(false);
		window.setTitle("Coordinate drawer");
		
		GridPane mainPane = new GridPane();
		mainPane.setId("main-pane");
		setRowConstraints(mainPane, 10.0, VPos.CENTER);
		setRowConstraints(mainPane, 80.0, VPos.CENTER);
		setRowConstraints(mainPane, 10.0, VPos.TOP);
		setColumnConstraints(mainPane, 100.0, HPos.CENTER);
		Label messageLabel = new Label("Enter your coordinates bellow");
		messageLabel.getStyleClass().add("highlight-label");
		mainPane.add(messageLabel, 0, 0);
		GridPane innerMainPane = new GridPane();
		mainPane.add(innerMainPane, 0, 1);
		Button drawButton = new Button("Draw");
		mainPane.add(drawButton, 0, 2);
		
		setRowConstraints(innerMainPane, 100.0, VPos.CENTER);
		setColumnConstraints(innerMainPane, 50.0, HPos.CENTER);
		setColumnConstraints(innerMainPane, 50.0, HPos.CENTER);
		GridPane startPane = new GridPane();
		innerMainPane.add(startPane, 0, 0);
		GridPane endPane = new GridPane();
		innerMainPane.add(endPane, 1, 0);
		
		setRowConstraints(startPane, 33.3, VPos.CENTER);
		setRowConstraints(startPane, 33.3, VPos.CENTER);
		setRowConstraints(startPane, 33.3, VPos.CENTER);
		setColumnConstraints(startPane, 100.0, HPos.CENTER);
		Label startLabel = new Label("Start");
		startLabel.getStyleClass().add("highlight-label");
		startPane.add(startLabel, 0, 0);
		HBox startXBox = new HBox();
		startXBox.setAlignment(Pos.TOP_CENTER);
		startXBox.setSpacing(10);
		Label startXLabel = new Label("x:");
		TextField startXField = new TextField();
		startXField.setMaxWidth(50);
		startXBox.getChildren().addAll(startXLabel,startXField);
		startPane.add(startXBox, 0, 1);
		HBox startYBox = new HBox();
		startYBox.setAlignment(Pos.TOP_CENTER);
		startYBox.setSpacing(10);
		Label startYLabel = new Label("y:");
		TextField startYField = new TextField();
		startYField.setMaxWidth(50);
		startYBox.getChildren().addAll(startYLabel,startYField);
		startPane.add(startYBox, 0, 2);
		
		setRowConstraints(endPane, 33.3, VPos.CENTER);
		setRowConstraints(endPane, 33.3, VPos.CENTER);
		setRowConstraints(endPane, 33.3, VPos.CENTER);
		setColumnConstraints(endPane, 100.0, HPos.CENTER);
		Label endLabel = new Label("End");
		endLabel.getStyleClass().add("highlight-label");
		endPane.add(endLabel, 0, 0);
		HBox endXBox = new HBox();
		endXBox.setAlignment(Pos.TOP_CENTER);
		endXBox.setSpacing(10);
		Label endXLabel = new Label("x:");
		TextField endXField = new TextField();
		endXField.setMaxWidth(50);
		endXBox.getChildren().addAll(endXLabel,endXField);
		endPane.add(endXBox, 0, 1);
		HBox endYBox = new HBox();
		endYBox.setAlignment(Pos.TOP_CENTER);
		endYBox.setSpacing(10);
		Label endYLabel = new Label("y:");
		TextField endYField = new TextField();
		endYField.setMaxWidth(50);
		endYBox.getChildren().addAll(endYLabel,endYField);
		endPane.add(endYBox, 0, 2);
		
		Scene scene = new Scene(mainPane,350,300);
		scene.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
		window.setScene(scene);
		window.showAndWait();
		
		return coordinates;
	}
	
	private void setRowConstraints(GridPane gp, double percentHeight, VPos vAlignment) {
		RowConstraints rc = new RowConstraints();
		rc.setPercentHeight(percentHeight);
		rc.setValignment(vAlignment);
		gp.getRowConstraints().add(rc);
	}
	
	private void setColumnConstraints(GridPane gp, double percentWidth, HPos hAlignment) {
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPercentWidth(percentWidth);
		cc.setHalignment(hAlignment);
		gp.getColumnConstraints().add(cc);
	}

}
