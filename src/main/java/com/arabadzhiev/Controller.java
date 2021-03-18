package com.arabadzhiev;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class Controller implements Initializable, EventHandler<ActionEvent>{
	private final int PENCIL = 1;
	private final int RECTANGLE_DRAWER = 2;
	private final int OVAL_DRAWER = 3;
	private final int TRIANGLE_DRAWER = 4;
	
	private int currentTool = PENCIL;
	private double startX=0;
	private double startY=0;
	private double endX=0;
	private double endY=0;
	
	private GraphicsContext gc;
	private FileChooser imageDirectory = new FileChooser();
	
	public BorderPane rootPane;
	public MenuItem saveItem;
	public Button pencilButton;
	public Button rectangleButton;
	public Button ovalButton;
	public Button triangleButton;
	public AnchorPane canvasPane;
	public Canvas canvas;
	public Label locator;
	public ColorPicker colorPicker;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		imageDirectory.setTitle("Choose your output directory");
	}
	
	public void listenForMovement(MouseEvent e) {
		if(gc==null) {
			gc = canvas.getGraphicsContext2D();
		}
		locator.setText("x:" + e.getX() + " y:" + e.getY());
	}
	
	public void listenForDrag(MouseEvent e) {
		locator.setText("x:" + e.getX() + " y:" + e.getY());
		if(gc==null) {
			gc = canvas.getGraphicsContext2D();
		}
		if(currentTool==PENCIL) {
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
			gc.fillOval(e.getX(), e.getY(), 2, 2);
		}
	}
	
	public void listenForPress(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
	}
	
	public void listenForRelease(MouseEvent e) {
		endX = e.getX();
		endY = e.getY();
		switch(currentTool) {
			case RECTANGLE_DRAWER:
				gc.setFill(colorPicker.getValue());
				gc.setStroke(colorPicker.getValue());
				if(endX-startX<0 && endY-startY<0) {
					gc.fillRect(endX, endY, startX-endX, startY-endY);
				}else if(endX-startX<0 && endY-startY>=0){
					gc.fillRect(endX, startY, startX-endX, endY-startY);
				}else if(endX-startX>=0 && endY-startY<0) {
					gc.fillRect(startX, endY, endX-startX, startY-endY);
				}else {
					gc.fillRect(startX, startY, endX-startX, endY-startY);
				}
				break;
				
			case OVAL_DRAWER:
				gc.setFill(colorPicker.getValue());
				gc.setStroke(colorPicker.getValue());
				if(endX-startX<0 && endY-startY<0) {
					gc.fillOval(endX, endY, startX-endX, startY-endY);
				}else if(endX-startX<0 && endY-startY>=0){
					gc.fillOval(endX, startY, startX-endX, endY-startY);
				}else if(endX-startX>=0 && endY-startY<0) {
					gc.fillOval(startX, endY, endX-startX, startY-endY);
				}else {
					gc.fillOval(startX, startY, endX-startX, endY-startY);
				}
				break;
				
			case TRIANGLE_DRAWER:
				gc.setFill(colorPicker.getValue());
				gc.setStroke(colorPicker.getValue());
				gc.strokePolygon(new double[] {startX+(endX-startX)/2,endX,startX}, new double[] {startY,endY,endY}, 3);
		}
	}

	@Override
	public void handle(ActionEvent e) {
		Object source = e.getSource();
		if(source.equals(pencilButton)) {
			pencilButton.setDisable(true);
			rectangleButton.setDisable(false);
			ovalButton.setDisable(false);
			triangleButton.setDisable(false);
			currentTool = PENCIL;
		}else if(source.equals(rectangleButton)) {
			pencilButton.setDisable(false);
			rectangleButton.setDisable(true);
			ovalButton.setDisable(false);
			triangleButton.setDisable(false);
			currentTool = RECTANGLE_DRAWER;
		}else if(source.equals(ovalButton)) {
			pencilButton.setDisable(false);
			rectangleButton.setDisable(false);
			ovalButton.setDisable(true);
			triangleButton.setDisable(false);
			currentTool = OVAL_DRAWER;
		}else if(source.equals(triangleButton)) {
			pencilButton.setDisable(false);
			rectangleButton.setDisable(false);
			ovalButton.setDisable(false);
			triangleButton.setDisable(true);
			currentTool = TRIANGLE_DRAWER;
		}else if(source.equals(saveItem)) {
			System.out.println("Saving file...");
			saveAs("png");
			System.out.println("File was saved succesfuly!");
		}
	}
	
	private void saveAs(String format) {
		SnapshotParameters sParameters = new SnapshotParameters();
		sParameters.setFill(Color.TRANSPARENT);
		WritableImage image = canvasPane.snapshot(sParameters, null);
		File file = imageDirectory.showSaveDialog(rootPane.getScene().getWindow());
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
