package com.arabadzhiev;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class Controller implements Initializable, EventHandler<ActionEvent>{
	private final int PENCIL = 1;
	private final int LINE_DRAWER = 2;
	private final int RECTANGLE_DRAWER = 3;
	private final int OVAL_DRAWER = 4;
	private final int TRIANGLE_DRAWER = 5;
	private final int FILL = 6;
	
	private int currentTool = PENCIL;
	private double thickness = 2;
	private int undoCounter = 0;
	private double startX=0;
	private double startY=0;
	private double endX=0;
	private double endY=0;
	
	private ToolSet ts;
	private GraphicsContext gc;
	private PixelReader pr;
	public PixelWriter pw;
	private FileChooser imageDirectory = new FileChooser();
	private ArrayList<WritableImage> canvasSnapshots = new ArrayList<WritableImage>();
	private WritableImage dragSnapshot;
	
	@FXML private BorderPane rootPane;
	@FXML private MenuItem saveItem;
	@FXML private Button pencilButton;
	@FXML private Button lineButton;
	@FXML private Button rectangleButton;
	@FXML private Button ovalButton;
	@FXML private Button triangleButton;
	@FXML private Button fillButton;
	@FXML private Button undoButton;
	@FXML private Button redoButton;
	@FXML private AnchorPane canvasPane;
	@FXML private Canvas canvas;
	@FXML private Label locator;
	@FXML private ColorPicker colorPicker;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		imageDirectory.setTitle("Choose your output directory");
		canvasSnapshots.add(canvas.snapshot(null, null));
		canvas.setCursor(Cursor.CROSSHAIR);
		gc = canvas.getGraphicsContext2D();
		pw = gc.getPixelWriter();
		pr = canvas.snapshot(null, null).getPixelReader();
		gc.setImageSmoothing(false);
		ts = new ToolSet(pw);
	}
	
	public void listenForMovement(MouseEvent e) {
		locator.setText("x:" + e.getX() + " y:" + e.getY());
	}
	
	public void listenForDrag(MouseEvent e) {
		locator.setText("x:" + e.getX() + " y:" + e.getY());
		if(currentTool==PENCIL) {
			gc.fillOval(e.getX()-thickness/2, e.getY()-thickness/2, thickness, thickness);
		}else if(currentTool==LINE_DRAWER) {
			gc.drawImage(dragSnapshot, 0, 0);
			gc.strokeLine(startX, startY, e.getX(), e.getY());
		}else if(currentTool==RECTANGLE_DRAWER) {
			gc.drawImage(dragSnapshot, 0, 0);
			drawRectangle(startX, startY, e.getX(), e.getY());
		}else if(currentTool==OVAL_DRAWER) {
			gc.drawImage(dragSnapshot, 0, 0);
			drawOval(startX, startY, e.getX(), e.getY());
		}else if(currentTool==TRIANGLE_DRAWER) {
			gc.drawImage(dragSnapshot, 0, 0);
			gc.strokePolygon(new double[] {startX+(e.getX()-startX)/2,e.getX(),startX}, new double[] {startY,e.getY(),e.getY()}, 3);
		}
	}
	
	public void listenForPress(MouseEvent e) {
		startX = (int)e.getX();
		startY = (int)e.getY();
		if(currentTool==PENCIL) {
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
			gc.fillOval(startX-thickness/2, startY-thickness/2, thickness, thickness);
		}else if(currentTool==LINE_DRAWER){
			dragSnapshot = (canvas.snapshot(null, null));
			gc.setStroke(colorPicker.getValue());
			gc.setLineWidth(thickness);
			
		}else if(currentTool==RECTANGLE_DRAWER) {
			dragSnapshot = (canvas.snapshot(null, null));
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
			
		}else if(currentTool==OVAL_DRAWER) {
			dragSnapshot = (canvas.snapshot(null, null));
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
			
		}else if(currentTool==TRIANGLE_DRAWER) {
			dragSnapshot = (canvas.snapshot(null, null));
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
		}else if(currentTool==FILL) {
			fill2((int)e.getX(),(int)e.getY(),pr.getColor((int)e.getX(),(int)e.getY()),colorPicker.getValue());
		}
	}	
	
	public void listenForRelease(MouseEvent e) {
		endX = e.getX();
		endY = e.getY();
		switch(currentTool) {
			case LINE_DRAWER:
				gc.strokeLine(startX, startY, endX, endY);
				break;
			case RECTANGLE_DRAWER:
				drawRectangle(startX,startY,endX,endY);
				break;
			case OVAL_DRAWER:
				drawOval(startX, startY, endX, endY);
				break;
				
			case TRIANGLE_DRAWER:
				gc.strokePolygon(new double[] {startX+(endX-startX)/2,endX,startX}, new double[] {startY,endY,endY}, 3);
				break;
		}
		if(undoCounter>0) {
			clearSnapshots();
		}
		WritableImage snapshot = canvas.snapshot(null, null);
		canvasSnapshots.add(snapshot);
		pr = snapshot.getPixelReader();
		undoButton.setDisable(false);
	}
	
	private void drawRectangle(double startX, double startY, double endX, double endY) {
		if(endX-startX<0 && endY-startY<0) {
			gc.fillRect(endX, endY, startX-endX, startY-endY);
		}else if(endX-startX<0 && endY-startY>=0){
			gc.fillRect(endX, startY, startX-endX, endY-startY);
		}else if(endX-startX>=0 && endY-startY<0) {
			gc.fillRect(startX, endY, endX-startX, startY-endY);
		}else {
			gc.fillRect(startX, startY, endX-startX, endY-startY);
		}
	}
	private void drawOval(double startX, double startY, double endX, double endY) {
		if(endX-startX<0 && endY-startY<0) {
			gc.fillOval(endX, endY, startX-endX, startY-endY);
		}else if(endX-startX<0 && endY-startY>=0){
			gc.fillOval(endX, startY, startX-endX, endY-startY);
		}else if(endX-startX>=0 && endY-startY<0) {
			gc.fillOval(startX, endY, endX-startX, startY-endY);
		}else {
			gc.fillOval(startX, startY, endX-startX, endY-startY);
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
	
	private void fill(int x, int y) {
		Queue<SeedPixel> seedPixels = new LinkedList<SeedPixel>();
		Queue<SeedPixel> fillPixels = new LinkedList<SeedPixel>();
		boolean[][] filled = new boolean[(int)canvas.getWidth()][(int)canvas.getHeight()];
		Color checkColor = pr.getColor(x, y);
		Color fillColor = colorPicker.getValue();
		pw.setColor(x, y, fillColor);
		seedPixels.add(new SeedPixel(x,y));
		fillPixels.add(new SeedPixel(x,y));
		while(!seedPixels.isEmpty()) {
			int forLenght = seedPixels.size();
			for(int i=0; i<forLenght; i++) {
				SeedPixel currentSeed = seedPixels.remove();
				int seedX = currentSeed.getX();
				int seedY = currentSeed.getY();
				if(seedX-1>=0) {
					if(pr.getColor(seedX-1, seedY).equals(checkColor)&&!filled[seedX-1][seedY]) {
						seedPixels.add(new SeedPixel(seedX-1,seedY));
						fillPixels.add(new SeedPixel(seedX-1,seedY));
						filled[seedX-1][seedY]=true;
					}
				}
				if(seedX+1<canvas.getWidth()) {
					if(pr.getColor(seedX+1, seedY).equals(checkColor)&&!filled[seedX+1][seedY]) {
						seedPixels.add(new SeedPixel(seedX+1,seedY));
						fillPixels.add(new SeedPixel(seedX+1,seedY));
						filled[seedX+1][seedY]=true;
					}
				}
				if(seedY-1>=0) {
					if(pr.getColor(seedX, seedY-1).equals(checkColor)&&!filled[seedX][seedY-1]) {
						seedPixels.add(new SeedPixel(seedX,seedY-1));
						fillPixels.add(new SeedPixel(seedX,seedY-1));
						filled[seedX][seedY-1]=true;
					}
				}
				if(seedY+1<canvas.getHeight()) {
					if(pr.getColor(seedX, seedY+1).equals(checkColor)&&!filled[seedX][seedY+1]) {
						seedPixels.add(new SeedPixel(seedX,seedY+1));
						fillPixels.add(new SeedPixel(seedX,seedY+1));
						filled[seedX][seedY+1]=true;
					}
				}
			}
		}
		while(!fillPixels.isEmpty()) {
			SeedPixel fillPixel = fillPixels.remove();
			pw.setColor(fillPixel.getX(), fillPixel.getY(), fillColor);
		}
	}
	
	private void fill2(int x, int y, Color checkColor, Color changeColor) {
		boolean[][] filled = new boolean[(int)canvas.getWidth()][(int)canvas.getHeight()];
		Queue<SeedPixel> seedPixels = new LinkedList<SeedPixel>();
		Queue<SeedPixel> fillPixels = new LinkedList<SeedPixel>();
		seedPixels.add(new SeedPixel(x,y));
		
		while(!seedPixels.isEmpty()) {
			SeedPixel seed = seedPixels.remove();
			if(fillHelper(filled,seed.getX(),seed.getY(),checkColor,fillPixels)) {
				seedPixels.add(new SeedPixel(seed.getX()-1,seed.getY()));
				seedPixels.add(new SeedPixel(seed.getX()+1,seed.getY()));
				seedPixels.add(new SeedPixel(seed.getX(),seed.getY()-1));
				seedPixels.add(new SeedPixel(seed.getX(),seed.getY()+1));
			}
		}
		while(!fillPixels.isEmpty()) {
			SeedPixel fill = fillPixels.remove();
			pw.setColor(fill.getX(), fill.getY(), changeColor);
		}
	}
	
	private boolean fillHelper(boolean[][] filled, int x, int y, Color checkColor, Queue<SeedPixel> fillPixels) {
		if(x<0) return false;
		if(y<0) return false;
		if(x>=canvas.getWidth()) return false;
		if(y>=canvas.getHeight()) return false;
		if(filled[x][y]) return false;
		if(!pr.getColor(x, y).equals(checkColor)) return false;
		fillPixels.add(new SeedPixel(x,y));
	
		filled[x][y]=true;
		return true;
	}
	
	private void undo() {
		gc.drawImage(canvasSnapshots.get(canvasSnapshots.size()-2-undoCounter), 0, 0);
		pr = canvasSnapshots.get(canvasSnapshots.size()-2-undoCounter).getPixelReader();
		redoButton.setDisable(false);
		undoCounter++;
		if(undoCounter+1>=canvasSnapshots.size()) {
			undoButton.setDisable(true);
		}
	}
	
	private void redo() {
		gc.drawImage(canvasSnapshots.get(canvasSnapshots.size()-undoCounter), 0, 0);
		pr = canvasSnapshots.get(canvasSnapshots.size()-undoCounter).getPixelReader();
		undoButton.setDisable(false);
		undoCounter--;
		if(undoCounter<=0) {
			redoButton.setDisable(true);
		}
	}
	
	private void clearSnapshots() {
		while(undoCounter>0){
			canvasSnapshots.remove(canvasSnapshots.get(canvasSnapshots.size()-1));
			undoCounter--;
		}
		redoButton.setDisable(true);
	}

	@Override
	public void handle(ActionEvent e) {
		Object source = e.getSource();
		if(source.equals(pencilButton)) {
			pencilButton.setDisable(true);
			rectangleButton.setDisable(false);
			ovalButton.setDisable(false);
			triangleButton.setDisable(false);
			fillButton.setDisable(false);
			currentTool = PENCIL;
		}else if(source.equals(lineButton)) {
			pencilButton.setDisable(false);
			lineButton.setDisable(true);
			rectangleButton.setDisable(false);
			ovalButton.setDisable(false);
			triangleButton.setDisable(false);
			fillButton.setDisable(false);
			currentTool = LINE_DRAWER;
		}else if(source.equals(rectangleButton)) {
			pencilButton.setDisable(false);
			lineButton.setDisable(false);
			rectangleButton.setDisable(true);
			ovalButton.setDisable(false);
			triangleButton.setDisable(false);
			fillButton.setDisable(false);
			currentTool = RECTANGLE_DRAWER;
		}else if(source.equals(ovalButton)) {
			pencilButton.setDisable(false);
			lineButton.setDisable(false);
			rectangleButton.setDisable(false);
			ovalButton.setDisable(true);
			triangleButton.setDisable(false);
			fillButton.setDisable(false);
			currentTool = OVAL_DRAWER;
		}else if(source.equals(triangleButton)) {
			pencilButton.setDisable(false);
			lineButton.setDisable(false);
			rectangleButton.setDisable(false);
			ovalButton.setDisable(false);
			triangleButton.setDisable(true);
			fillButton.setDisable(false);
			currentTool = TRIANGLE_DRAWER;
		}else if(source.equals(saveItem)) {
			saveAs("png");
		}else if(source.equals(undoButton)) {
			undo();
		}else if(source.equals(redoButton)) {
			redo();
		}else if(source.equals(fillButton)) {
			pencilButton.setDisable(false);
			lineButton.setDisable(false);
			rectangleButton.setDisable(false);
			ovalButton.setDisable(false);
			triangleButton.setDisable(false);
			fillButton.setDisable(true);
			currentTool = FILL;
		}
	}
}
