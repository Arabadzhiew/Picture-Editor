package com.arabadzhiev;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class Controller implements Initializable, EventHandler<ActionEvent>{
	private final int CROP = 1;
	private final int MOVE = 2;
	private final int SCISSION = 3;
	private final int PENCIL = 4;
	private final int FILL = 5;
	private final int TEXT = 6;
	private final int TEXT_EDIT = 7;
	private final int PIPETTE = 8;
	private final int LINE_DRAWER = 9;
	private final int RECTANGLE_DRAWER = 10;
	private final int OVAL_DRAWER = 11;
	private final int TRIANGLE_DRAWER = 12;
	
	private int currentTool = PENCIL;
	private int undoCounter = 0;
	private int savedFileSnapshotIndex;
	private double thickness = 2;
	private double startX=0;
	private double startY=0;
	private double lastX;
	private double lastY;
	private double endX=0;
	private double endY=0;
	private boolean brandNew=true;
	private boolean filePresent=false;
	private boolean filledShapes=false;
	
	private File file;
	private CoordinateDrawer cd = new CoordinateDrawer();
	private PixelReader pr;
	private PixelWriter pw;
	private FileChooser imageDirectory = new FileChooser();
	private List<String> allExtentions = new ArrayList<String>();
	private FileChooser.ExtensionFilter allExt;
	private WritableImage cropImage;
	private ImageCursor fillCursor;
	private ImageCursor pipetteCursor;
	private ImageView savedImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/saved.png").toString()));
	private ImageView notSavedImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/notSaved.png").toString()));
	private Tooltip savedTT = new Tooltip("File is saved");
	private Tooltip notSavedTT = new Tooltip("File is not saved");
	private CanvasTextBrains ctBrains;
	private About about = new About();
	
	public GraphicsContext gc;
	public WritableImage rubberBandSnapshot;
	public SnapshotParameters sParameters = new SnapshotParameters();
	public ObservableList<WritableImage> canvasSnapshots = FXCollections.observableArrayList(new ArrayList<WritableImage>());
	public Rectangle2D rubberBandBounds;
	public TextArea canvasText = new TextArea();
	public boolean firstSnapshot;
	public static boolean saved = true;
	
	@FXML private BorderPane rootPane;
	@FXML private MenuItem newItem;
	@FXML private MenuItem openItem;
	@FXML private MenuItem editItem;
	@FXML private MenuItem saveItem;
	@FXML private MenuItem saveAsItem;
	@FXML private RadioMenuItem twoPxItem;
	@FXML private RadioMenuItem fourPxItem;
	@FXML private RadioMenuItem eightPxItem;
	@FXML private RadioMenuItem sixteenPxItem;
	@FXML private MenuItem filledShapesItem;
	@FXML private MenuItem aboutItem;
	@FXML private Button cropButton;
	@FXML private Button scissionButton;
	@FXML private Button cdButton;
	@FXML private Button pencilButton;
	@FXML private Button fillButton;
	@FXML private Button textButton;
	@FXML private Button pipetteButton;
	@FXML private Button lineButton;
	@FXML private Button rectangleButton;
	@FXML private Button ovalButton;
	@FXML private Button triangleButton;
	@FXML private Button undoButton;
	@FXML private Button redoButton;
	@FXML private AnchorPane canvasAnchor;
	@FXML private AnchorPane resizePane;
	@FXML private ScrollPane canvasSnapshotAnchor;
	@FXML private Canvas canvas;
	@FXML private Label locator;
	@FXML private TextField widthField;
	@FXML private TextField heightField;
	@FXML private Label savedIcon;
	@FXML private Label savedLabel;
	@FXML private Slider scaleSlider;
	@FXML private Label scaleLabel;
	
	@FXML public ScrollPane canvasPane;
	@FXML public  ColorPicker colorPicker;
	
	private ArrayList<Button> linkedButtons = new ArrayList<Button>();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		allExtentions.add("*.png");
		allExtentions.add("*.jpg");
		allExtentions.add("*.gif");
		allExt = new FileChooser.ExtensionFilter("All image extentions", allExtentions);
		imageDirectory.setInitialDirectory(new File(System.getProperty("user.home"), "Desktop"));
		imageDirectory.setInitialFileName("unnamed");
		canvasSnapshots.add(canvas.snapshot(null, null));
		gc = canvas.getGraphicsContext2D();
		//gc.setFill(Color.WHITE);
		//gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		pw = gc.getPixelWriter();
		sParameters.setFill(Color.WHITE);
		pr = canvas.snapshot(sParameters, null).getPixelReader();
		ctBrains = new CanvasTextBrains(this);
		canvasText.setOnMouseMoved(ctBrains.mouseMoved);
		canvasText.addEventFilter(MouseEvent.MOUSE_PRESSED, ctBrains.mousePressed);
		canvasText.setOnMouseReleased(ctBrains.mouseReleased);
		canvasText.setOnMouseDragged(ctBrains.mouseDragged);
		canvasText.setStyle("-fx-text-fill: "+getColorHex(colorPicker.getValue()));
		
		colorPicker.valueProperty().addListener((property, olvValue, newValue)->{
			if(canvasAnchor.getChildren().contains(canvasText)) {
				String hex = getColorHex(newValue);
				canvasText.setStyle("-fx-text-fill: "+hex);
			}
			
		});
		
		rootPane.widthProperty().addListener((property,oldVal,newVal) ->{
			canvasPane.setMaxWidth((rootPane.getWidth()*0.66)+50);
		});
		rootPane.heightProperty().addListener((property,oldVal,newVal) ->{
			canvasPane.setMaxHeight((rootPane.getHeight()*0.66)+50);
		});
		
		widthField.setText(""+(int)canvas.getWidth());
		heightField.setText(""+(int)canvas.getHeight());
		widthField.textProperty().addListener((property, oldValue, newValue) -> { 
			int parsedNewVal = parseFromTextField(widthField,newValue,600);
			if(parsedNewVal>3840) {
				widthField.setText(""+3840);
				pr = canvas.snapshot(sParameters, null).getPixelReader();
			}else if(parsedNewVal!=0&&canvas.getWidth()!=parsedNewVal) {
				canvasSnapshotAnchor.setPrefWidth(parsedNewVal);
				canvas.setWidth(parsedNewVal);
				WritableImage resizeSnapshot = canvas.snapshot(sParameters, null);
				canvasSnapshots.add(resizeSnapshot);
				pr = resizeSnapshot.getPixelReader();
			}
		});
		heightField.textProperty().addListener((property, oldValue, newValue) -> {
			int parsedNewVal = parseFromTextField(heightField,newValue,400);
			if(parsedNewVal>2160) {
				heightField.setText(""+2160);
			}else if(parsedNewVal!=0&&canvas.getHeight()!=parsedNewVal) {
				canvasSnapshotAnchor.setPrefHeight(parsedNewVal);
				canvas.setHeight(parsedNewVal);
				WritableImage resizeSnapshot = canvas.snapshot(sParameters, null);
				canvasSnapshots.add(resizeSnapshot);
				pr = canvas.snapshot(sParameters, null).getPixelReader();
			}
		});
		
		canvasSnapshots.addListener((ListChangeListener<WritableImage>) change -> {
			if(filePresent) {
				saveItem.setDisable(false);
				savedIcon.setGraphic(notSavedImageView);
				savedIcon.setTooltip(notSavedTT);
			}
			if(saved) {
				saved = false;
			}
		});
		
		scaleSlider.valueProperty().addListener((property, oldValue, newValue) -> {
			double value = (double)newValue;
			switch((int)value) {
				case 1:
					resizePane.setScaleX(0.125);
					resizePane.setScaleY(0.125);
					scaleLabel.setText("Scale: x0.125");
					break;
				case 2:
					resizePane.setScaleX(0.25);
					resizePane.setScaleY(0.25);
					scaleLabel.setText("Scale: x0.25");
					break;
				case 3:
					resizePane.setScaleX(0.5);
					resizePane.setScaleY(0.5);
					scaleLabel.setText("Scale: x0.5");
					break;
				case 4:
					resizePane.setScaleX(1);
					resizePane.setScaleY(1);
					scaleLabel.setText("Scale: x1");
					break;
				case 5:
					resizePane.setScaleX(2);
					resizePane.setScaleY(2);
					scaleLabel.setText("Scale: x2");
					break;
				case 6:
					resizePane.setScaleX(4);
					resizePane.setScaleY(4);
					scaleLabel.setText("Scale: x4");
					break;
				case 7:
					resizePane.setScaleX(8);
					resizePane.setScaleY(8);
					scaleLabel.setText("Scale: x8");
					break;	
			}
		});
		
		
		canvas.setCursor(Cursor.CROSSHAIR);
		fillCursor = new ImageCursor(new Image(getClass().getResource("/resources/fillCursor.png").toString()),64,37);
		pipetteCursor = new ImageCursor(new Image(getClass().getResource("/resources/pipetteCursor.png").toString()),4,62);
		
		savedImageView.setFitWidth(16);
		savedImageView.setFitHeight(16);
		notSavedImageView.setFitWidth(16);
		notSavedImageView.setFitHeight(16);
		
		ImageView pencilImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/pencil.png").toString()));
		pencilImageView.setFitWidth(20);
		pencilImageView.setFitHeight(20);
		pencilButton.setGraphic(pencilImageView);
		ImageView fillImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/fill.png").toString()));
		fillImageView.setFitWidth(20);
		fillImageView.setFitHeight(20);
		fillButton.setGraphic(fillImageView);
		ImageView textImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/text.png").toString()));
		textImageView.setFitWidth(20);
		textImageView.setFitHeight(20);
		textButton.setGraphic(textImageView);
		ImageView pipetteImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/pipette.png").toString()));
		pipetteImageView.setFitWidth(20);
		pipetteImageView.setFitHeight(20);
		pipetteButton.setGraphic(pipetteImageView);
		
		ImageView lineImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/line.png").toString()));
		lineImageView.setFitWidth(20);
		lineImageView.setFitHeight(20);
		lineButton.setGraphic(lineImageView);
		ImageView rectangleImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/rectangle.png").toString()));
		rectangleImageView.setFitWidth(20);
		rectangleImageView.setFitHeight(20);
		rectangleButton.setGraphic(rectangleImageView);
		ImageView ovalImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/oval.png").toString()));
		ovalImageView.setFitWidth(20);
		ovalImageView.setFitHeight(20);
		ovalButton.setGraphic(ovalImageView);
		ImageView triangleImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/triangle.png").toString()));
		triangleImageView.setFitWidth(20);
		triangleImageView.setFitHeight(20);
		triangleButton.setGraphic(triangleImageView);
		
		ImageView undoImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/undo.png").toString()));
		undoImageView.setFitWidth(20);
		undoImageView.setFitHeight(20);
		undoButton.setGraphic(undoImageView);
		ImageView redoImageView = new ImageView(new Image(getClass().getResource("/resources/buttonImages/redo.png").toString()));
		redoImageView.setFitWidth(20);
		redoImageView.setFitHeight(20);
		redoButton.setGraphic(redoImageView);
		
		linkedButtons.add(cropButton);
		linkedButtons.add(scissionButton);
		linkedButtons.add(cdButton);
		linkedButtons.add(pencilButton);
		linkedButtons.add(fillButton);
		linkedButtons.add(textButton);
		linkedButtons.add(pipetteButton);
		linkedButtons.add(lineButton);
		linkedButtons.add(rectangleButton);
		linkedButtons.add(ovalButton);
		linkedButtons.add(triangleButton);
	}
	
	public void listenForMovement(MouseEvent e) {
		locator.setText("x:" + e.getX() + " y:" + e.getY());
		if(currentTool==MOVE) {
			if(e.getX()>=rubberBandBounds.getMinX()&&e.getX()<=rubberBandBounds.getMaxX()&&
				e.getY()>=rubberBandBounds.getMinY()&&e.getY()<=rubberBandBounds.getMaxY()) {
				if(!canvas.getCursor().equals(Cursor.MOVE)) {
					canvas.setCursor(Cursor.MOVE);
				}
			}else {
				if(!canvas.getCursor().equals(Cursor.CROSSHAIR)) {
					canvas.setCursor(Cursor.CROSSHAIR);
				}
			}
		}else if(currentTool==TEXT_EDIT) {
			double minX = rubberBandBounds.getMinX();
			double minY = rubberBandBounds.getMinY();
			double maxX = rubberBandBounds.getMaxX();
			double maxY = rubberBandBounds.getMaxY();
			if((e.getX()>=minX-5&&e.getX()<=minX&&e.getY()>=minY-5&&e.getY()<=maxY+5)||
					(e.getY()>=minY-5&&e.getY()<=minY&&e.getX()>=minX-5&&e.getX()<=maxX+5)||
					(e.getX()>=maxX&&e.getX()<=maxX+5&&e.getY()>=minY-5&&e.getY()<=maxY+5)||
					(e.getY()>=maxY&&e.getY()<=maxY+5&&e.getX()>=minX-5&&e.getX()<=maxX+5)){
				if(!canvas.getCursor().equals(Cursor.MOVE)) {
					canvas.setCursor(Cursor.MOVE);
				}
			}else {
				if(!canvas.getCursor().equals(Cursor.TEXT)) {
					canvas.setCursor(Cursor.TEXT);
				}
			}
		}else if(!(currentTool==FILL)&&!(currentTool==PIPETTE)&&!(currentTool==TEXT)){
			if(!canvas.getCursor().equals(Cursor.CROSSHAIR)) {
				canvas.setCursor(Cursor.CROSSHAIR);
			}
		}else if(currentTool==TEXT) {
			if(!canvas.getCursor().equals(Cursor.TEXT)) {
				canvas.setCursor(Cursor.TEXT);
			}
		}
	}
	public void listenForDrag(MouseEvent e) {
		locator.setText("x:" + e.getX() + " y:" + e.getY());
		if(currentTool==CROP) {
			gc.setStroke(Color.RED);
			gc.setLineDashes(4);
			rubberBand(startX, startY, e.getX(), e.getY());
		}else if(currentTool==MOVE) {
			gc.drawImage(rubberBandSnapshot, 0, 0);
			double minX = e.getX()-lastX+rubberBandBounds.getMinX();
			double minY = e.getY()-lastY+rubberBandBounds.getMinY();
			lastX=e.getX();
			lastY=e.getY();
			rubberBandBounds = new Rectangle2D(minX,minY,cropImage.getWidth(),cropImage.getHeight());
			gc.drawImage(cropImage, minX,minY);
		}else if(currentTool==SCISSION) {
			gc.setStroke(Color.RED);
			gc.setLineDashes(4);
			rubberBand(startX, startY, e.getX(), e.getY());
		}else if(currentTool==PENCIL) {
			gc.fillOval(e.getX()-thickness/2, e.getY()-thickness/2, thickness, thickness);
		}else if(currentTool==TEXT) {
			gc.setStroke(Color.RED);
			gc.setLineDashes(4);
			rubberBand(startX,startY,e.getX(),e.getY());
		}else if(currentTool==TEXT_EDIT) {
			if(!canvasText.getSelectedText().equals("")) {
				canvasText.deselect();
			}
			if(!firstSnapshot) {
				firstSnapshot=true;
			}
			gc.drawImage(rubberBandSnapshot, 0, 0);
			double minX = e.getX()-lastX+rubberBandBounds.getMinX();
			double minY = e.getY()-lastY+rubberBandBounds.getMinY();
			lastX=e.getX();
			lastY=e.getY();
			rubberBandBounds = new Rectangle2D(minX,minY,canvasText.getMinWidth(),canvasText.getMinHeight());
			canvasText.setLayoutX(minX);
			canvasText.setLayoutY(minY);
		}else if(currentTool==LINE_DRAWER) {
			gc.drawImage(rubberBandSnapshot, 0, 0);
			gc.setLineWidth(thickness);
			gc.strokeLine(startX, startY, e.getX(), e.getY());
		}else if(currentTool==RECTANGLE_DRAWER) {
			gc.drawImage(rubberBandSnapshot, 0, 0);
			drawRectangle(startX, startY, e.getX(), e.getY(), filledShapes);
		}else if(currentTool==OVAL_DRAWER) {
			gc.drawImage(rubberBandSnapshot, 0, 0);
			drawOval(startX, startY, e.getX(), e.getY(), filledShapes);
		}else if(currentTool==TRIANGLE_DRAWER) {
			gc.drawImage(rubberBandSnapshot, 0, 0);
			drawTriangle(startX, startY, e.getX(), e.getY(), filledShapes);
		}
	}
	
	public void listenForPress(MouseEvent e) {
		startX = (int)e.getX();
		startY = (int)e.getY();
		lastX = e.getX();
		lastY = e.getY();
		brandNew = false;
		if(currentTool==CROP) {
			rubberBandSnapshot = canvas.snapshot(sParameters, null);
		}else if(currentTool==MOVE) {
			if(e.getX()>=rubberBandBounds.getMinX()&&e.getX()<=rubberBandBounds.getMaxX()&&
					e.getY()>=rubberBandBounds.getMinY()&&e.getY()<=rubberBandBounds.getMaxY()) {
				canvas.setCursor(Cursor.CLOSED_HAND);
			}else {
				gc.drawImage(rubberBandSnapshot, 0, 0);
				gc.drawImage(cropImage, rubberBandBounds.getMinX(), rubberBandBounds.getMinY());
				rubberBandSnapshot = canvas.snapshot(sParameters, null);
				currentTool=CROP;
			}
		}else if(currentTool==SCISSION) {
			rubberBandSnapshot = canvas.snapshot(sParameters, null);
		}else if(currentTool==PENCIL) {
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
			gc.fillOval(startX-thickness/2, startY-thickness/2, thickness, thickness);
		}else if(currentTool==FILL) {
			fill((int)e.getX(),(int)e.getY(),pr.getColor((int)e.getX(),(int)e.getY()),colorPicker.getValue());	
		}else if(currentTool==TEXT) {
			rubberBandSnapshot = canvas.snapshot(sParameters, null);
		}else if(currentTool==TEXT_EDIT) {
			double minX = rubberBandBounds.getMinX();
			double minY = rubberBandBounds.getMinY();
			double maxX = rubberBandBounds.getMaxX();
			double maxY = rubberBandBounds.getMaxY();
			if((e.getX()>=minX-5&&e.getX()<=minX&&e.getY()>=minY-5&&e.getY()<=maxY+5)||
					(e.getY()>=minY-5&&e.getY()<=minY&&e.getX()>=minX-5&&e.getX()<=maxX+5)||
					(e.getX()>=maxX&&e.getX()<=maxX+5&&e.getX()>=minX-5&&e.getX()<=maxX+5)||
					(e.getY()>=maxY&&e.getY()<=maxY+5&&e.getY()>=minY-5&&e.getY()<=maxY+5)){
				if(!firstSnapshot) {
					firstSnapshot=true;
					gc.drawImage(rubberBandSnapshot, 0, 0);
					if(undoCounter>0) {
						clearSnapshots();
					}
					canvasText.setStyle("-fx-display-caret: false; " + "-fx-text-fill: " + getColorHex(colorPicker.getValue()) + "; " +
					"-fx-highlight-fill: transparent; -fx-highlight-text-fill: " + getColorHex(colorPicker.getValue()));
					WritableImage snapshot = canvasSnapshotAnchor.snapshot(sParameters, null);
					canvasText.setStyle("-fx-text-fill: " + getColorHex(colorPicker.getValue()));
					canvasSnapshots.add(snapshot);
					pr = snapshot.getPixelReader();
					undoButton.setDisable(false);
				}
				canvas.setCursor(Cursor.CLOSED_HAND);
			}else{
				boolean hasText=false;
				textCheck: for(int i = 0; i<canvasText.getText().length(); i++) {
					if(canvasText.getText().charAt(i)!=' ') {
						hasText=true;
						break textCheck;
					}
				}
				if(hasText) {
					gc.drawImage(rubberBandSnapshot, 0, 0);
					if(undoCounter>0) {
						clearSnapshots();
					}
					canvasText.setStyle("-fx-display-caret: false; " + "-fx-text-fill: " + getColorHex(colorPicker.getValue()) + "; " +
					"-fx-highlight-fill: transparent; -fx-highlight-text-fill: " + getColorHex(colorPicker.getValue()));
					WritableImage snapshot = canvasSnapshotAnchor.snapshot(sParameters, null);
					canvasText.setStyle("-fx-text-fill: " + getColorHex(colorPicker.getValue()));
					canvasSnapshots.add(snapshot);
					pr = snapshot.getPixelReader();
					undoButton.setDisable(false);
					gc.drawImage(snapshot, 0, 0);
					canvasAnchor.getChildren().remove(canvasText);
					rubberBandSnapshot = canvas.snapshot(sParameters, null);
				}
				currentTool=TEXT;
			}
			
		}else if(currentTool==PIPETTE) {
			colorPicker.setValue(pr.getColor((int)e.getX(), (int)e.getY()));
		}else if(currentTool==LINE_DRAWER){
			rubberBandSnapshot = (canvas.snapshot(sParameters, null));
			gc.setStroke(colorPicker.getValue());
		}else if(currentTool==RECTANGLE_DRAWER) {
			rubberBandSnapshot = (canvas.snapshot(sParameters, null));
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
			
		}else if(currentTool==OVAL_DRAWER) {
			rubberBandSnapshot = (canvas.snapshot(sParameters, null));
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
			
		}else if(currentTool==TRIANGLE_DRAWER) {
			rubberBandSnapshot = (canvas.snapshot(sParameters, null));
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
		}
	}	
	
	public void listenForRelease(MouseEvent e) {
		endX = e.getX();
		endY = e.getY();
		boolean createdCrop = false;
		boolean createdText = false;
		if(currentTool==CROP) {
			gc.drawImage(rubberBandSnapshot, 0, 0);
			if(endX-startX!=0&&endY-startY!=0) {
				crop(startX, startY, endX, endY, true);
				createdCrop = true;
			}
		}else if(currentTool==MOVE) {
			canvas.setCursor(Cursor.MOVE);
		}else if(currentTool==SCISSION) {
			if(endX-startX!=0&&endY-startY!=0) {
				crop(startX, startY, endX, endY, false);
			}
		}else if(currentTool==TEXT) {
			gc.drawImage(rubberBandSnapshot, 0, 0);
			if(endX-startX!=0&&endY-startY!=0) {
				if(endX-startX<0&&endY-startY<0) {
					canvasText.setLayoutX(endX);
					canvasText.setLayoutY(endY);
					canvasText.setMinSize(startX-endX, startY-endY);
					canvasText.setMaxSize(startX-endX, startY-endY);
				}else if(endX-startX<0&&endY-startY>=0) {
					canvasText.setLayoutX(endX);
					canvasText.setLayoutY(startY);
					canvasText.setMinSize(startX-endX, endY-startY);
					canvasText.setMaxSize(startX-endX, endY-startY);
				}else if(endX-startX>=0&&endY-startY<0) {
					canvasText.setLayoutX(startX);
					canvasText.setLayoutY(endY);
					canvasText.setMinSize(endX-startX, startY-endY);
					canvasText.setMaxSize(endX-startX, startY-endY);
				}else {
					canvasText.setLayoutX(startX);
					canvasText.setLayoutY(startY);
					canvasText.setMinSize(endX-startX, endY-startY);
					canvasText.setMaxSize(endX-startX, endY-startY);
				}
				rubberBandBounds = new Rectangle2D(canvasText.getLayoutX(),canvasText.getLayoutY(),canvasText.getMinWidth(),canvasText.getMinHeight());
				canvasText.setText("");
				if(!canvasAnchor.getChildren().contains(canvasText)) {
					canvasAnchor.getChildren().add(canvasText);
				}
				canvasText.requestFocus();
				gc.setStroke(Color.GREEN);
				drawRectangle(startX, startY, endX, endY, false);
				createdText=true;
			}
		}else if(currentTool==TEXT_EDIT) {
			if(!firstSnapshot) {
				canvasSnapshots.remove(canvasSnapshots.size()-1);
			}
			gc.setLineDashes(4);
			gc.setStroke(Color.GREEN);
			rubberBand(rubberBandBounds.getMinX(), rubberBandBounds.getMinY(), rubberBandBounds.getMaxX(), rubberBandBounds.getMaxY());
			canvas.setCursor(Cursor.MOVE);
		}
		
		if((!(startX==endX&&startY==endY)&&!(currentTool==CROP)&&!(currentTool==TEXT)&&!(currentTool==TEXT_EDIT)&&!(currentTool==PIPETTE))||currentTool==FILL) {
			if(currentTool==MOVE) {
				gc.drawImage(rubberBandSnapshot, 0, 0);
				gc.drawImage(cropImage, rubberBandBounds.getMinX(), rubberBandBounds.getMinY());
			}
			if(undoCounter>0) {
				clearSnapshots();
			}
			WritableImage snapshot = canvas.snapshot(sParameters, null);
			canvasSnapshots.add(snapshot);
			pr = snapshot.getPixelReader();
			undoButton.setDisable(false);
			if(currentTool==MOVE) {
				gc.setLineDashes(4);
				drawRectangle(rubberBandBounds.getMinX(), rubberBandBounds.getMinY(), rubberBandBounds.getMaxX(), rubberBandBounds.getMaxY(), false);
			}
		}
		
		if(currentTool==TEXT_EDIT&&!canvasText.getText().equals("")&&startX!=endX&&startY!=endY) {
			gc.drawImage(rubberBandSnapshot, 0, 0);
			if(undoCounter>0) {
				clearSnapshots();
			}
			WritableImage snapshot = canvasSnapshotAnchor.snapshot(sParameters, null);
			drawRectangle(rubberBandBounds.getMinX(), rubberBandBounds.getMinY(), rubberBandBounds.getMaxX(), rubberBandBounds.getMaxY(), false);
			canvasSnapshots.add(snapshot);
			pr = snapshot.getPixelReader();
			undoButton.setDisable(false);
		}
		
		if(currentTool==CROP&&createdCrop) {
			currentTool=MOVE;
		}else if(currentTool==TEXT&&createdText) {
			currentTool=TEXT_EDIT;
			firstSnapshot=false;
		}
		
		if(gc.getLineDashes()!=null) {
			gc.setLineDashes(null);
		}
	}
	
	public void addNewSnapshot() {
		if(undoCounter>0) {
			clearSnapshots();
		}
		WritableImage snapshot = canvasSnapshotAnchor.snapshot(sParameters, null);
		canvasSnapshots.add(snapshot);
		pr = snapshot.getPixelReader();
		undoButton.setDisable(false);
	}
	
	public void addNewSnapshot(WritableImage snapshot) {
		if(undoCounter>0) {
			clearSnapshots();
		}
		canvasSnapshots.add(snapshot);
		pr = snapshot.getPixelReader();
		undoButton.setDisable(false);
	}
	
	public String getColorHex(Color color) {
		String hex = "#";
		for(int i=2; i<8; i++) {
			hex = hex + color.toString().charAt(i);
		}
		return hex;
	}
	
	public void rubberBand(double startX, double startY, double endX, double endY) {
		if(endX<0) {
			endX=0;
		}else if(endX>canvas.getWidth()) {
			endX=canvas.getWidth();
		}
		
		if(endY<0) {
			endY=0;
		}else if(endY>canvas.getHeight()) {
			endY=canvas.getHeight();
		}
		gc.drawImage(rubberBandSnapshot, 0, 0);
		drawRectangle(startX, startY, endX, endY, false);
	}
	
	private int parseFromTextField(TextField tf, String value, int backupValue) {
		int returnInt = 0;
		try {
			char firstChar = value.charAt(0);
			NumberFormatException nb = new NumberFormatException();
			if(!(firstChar=='1'||firstChar=='2'||firstChar=='3'||firstChar=='4'||firstChar=='5'||firstChar=='6'||
					firstChar=='7'||firstChar=='8'||firstChar=='9')) {
				throw nb;
			}
			for(int i=1; i< value.length();i++) {
				char currentChar = value.charAt(i);
				if(!(currentChar=='0'||currentChar=='1'||currentChar=='2'||currentChar=='3'||currentChar=='4'||
						currentChar=='5'||currentChar=='6'||currentChar=='7'||currentChar=='8'||currentChar=='9')) {
					throw nb;
				}
			}
			returnInt = Integer.parseInt(value);
		}catch(NumberFormatException e1) {
			tf.setText(""+backupValue);
		}catch(StringIndexOutOfBoundsException e2) {
			//
		}
		return returnInt;
	}
	
	private void crop(double startX, double startY, double endX, double endY, boolean fullCrop) {
		if(endX<0) {
			endX=0;
		}else if(endX>canvas.getWidth()) {
			endX=canvas.getWidth();
		}
		
		if(endY<0) {
			endY=0;
		}else if(endY>canvas.getHeight()) {
			endY=canvas.getHeight();
		}
		if(endX-startX<0 && endY-startY<0) {
			cropImage = new WritableImage(pr, (int)endX, (int)endY, (int)(startX-endX), (int)(startY-endY));
			if(fullCrop) {
				rubberBandBounds = new Rectangle2D(endX,endY,cropImage.getWidth(),cropImage.getHeight());
				gc.setFill(Color.WHITE);
				drawRectangle(endX,endY,startX,startY,true);
				rubberBandSnapshot = canvas.snapshot(sParameters, null);
				gc.drawImage(cropImage, endX, endY);
			}
		}else if(endX-startX<0 && endY-startY>=0){
			cropImage = new WritableImage(pr, (int)endX, (int)startY, (int)(startX-endX), (int)(endY-startY));
			if(fullCrop) {
				rubberBandBounds = new Rectangle2D(endX,startY,cropImage.getWidth(),cropImage.getHeight());
				gc.setFill(Color.WHITE);
				drawRectangle(endX,startY,startX,endY,true);
				rubberBandSnapshot = canvas.snapshot(sParameters, null);
				gc.drawImage(cropImage, endX, startY);
			}
		}else if(endX-startX>=0 && endY-startY<0) {
			cropImage = new WritableImage(pr, (int)startX, (int)endY, (int)(endX-startX), (int)(startY-endY));
			if(fullCrop) {
				rubberBandBounds = new Rectangle2D(startX,endY,cropImage.getWidth(),cropImage.getHeight());
				gc.setFill(Color.WHITE);
				drawRectangle(startX,endY,endX,startY,true);
				rubberBandSnapshot = canvas.snapshot(sParameters, null);
				gc.drawImage(cropImage, startX, endY);
			}
		}else if(endX-startX>=0&&endY-startY>=0){
			cropImage = new WritableImage(pr, (int)startX, (int)startY, (int)(endX-startX), (int)(endY-startY));
			if(fullCrop) {
				rubberBandBounds = new Rectangle2D(startX,startY,cropImage.getWidth(),cropImage.getHeight());
				gc.setFill(Color.WHITE);
				drawRectangle(startX,startY,endX,endY,true);
				rubberBandSnapshot = canvas.snapshot(sParameters, null);
				gc.drawImage(cropImage, startX, startY);
			}
		}
		if(!fullCrop) {
			gc.drawImage(cropImage, 0, 0);
			canvasSnapshotAnchor.setPrefWidth(cropImage.getWidth());
			canvas.setWidth(cropImage.getWidth());
			canvasSnapshotAnchor.setPrefHeight(cropImage.getHeight());
			canvas.setHeight(cropImage.getHeight());
			widthField.setText(""+(int)cropImage.getWidth());
			heightField.setText(""+(int)cropImage.getHeight());
		}else if(fullCrop) {
			gc.setStroke(Color.GREEN);
			drawRectangle(startX, startY, endX, endY, false);
		}
	}
	
	private void drawRectangle(double startX, double startY, double endX, double endY, boolean filled) {
		if(filled) {
			if(endX-startX<0 && endY-startY<0) {
				gc.fillRect(endX, endY, startX-endX, startY-endY);
			}else if(endX-startX<0 && endY-startY>=0){
				gc.fillRect(endX, startY, startX-endX, endY-startY);
			}else if(endX-startX>=0 && endY-startY<0) {
				gc.fillRect(startX, endY, endX-startX, startY-endY);
			}else {
				gc.fillRect(startX, startY, endX-startX, endY-startY);
			}
		}else if(!filled) {
			gc.setLineWidth(thickness);
			if(endX-startX<0 && endY-startY<0) {
				gc.strokeRect(endX, endY, startX-endX, startY-endY);
			}else if(endX-startX<0 && endY-startY>=0){
				gc.strokeRect(endX, startY, startX-endX, endY-startY);
			}else if(endX-startX>=0 && endY-startY<0) {
				gc.strokeRect(startX, endY, endX-startX, startY-endY);
			}else {
				gc.strokeRect(startX, startY, endX-startX, endY-startY);
			}
		}
	}
	private void drawOval(double startX, double startY, double endX, double endY, boolean filled) {
		if(filled) {
			if(endX-startX<0 && endY-startY<0) {
				gc.fillOval(endX, endY, startX-endX, startY-endY);
			}else if(endX-startX<0 && endY-startY>=0){
				gc.fillOval(endX, startY, startX-endX, endY-startY);
			}else if(endX-startX>=0 && endY-startY<0) {
				gc.fillOval(startX, endY, endX-startX, startY-endY);
			}else {
				gc.fillOval(startX, startY, endX-startX, endY-startY);
			}
		}else if(!filled) {
			gc.setLineWidth(thickness);
			if(endX-startX<0 && endY-startY<0) {
				gc.strokeOval(endX, endY, startX-endX, startY-endY);
			}else if(endX-startX<0 && endY-startY>=0){
				gc.strokeOval(endX, startY, startX-endX, endY-startY);
			}else if(endX-startX>=0 && endY-startY<0) {
				gc.strokeOval(startX, endY, endX-startX, startY-endY);
			}else {
				gc.strokeOval(startX, startY, endX-startX, endY-startY);
			}
		}
	}
	
	private void drawTriangle(double startX, double startY, double endX, double endY, boolean filled) {
		if(filled) {
			gc.fillPolygon(new double[] {startX+(endX-startX)/2,endX,startX}, new double[] {startY,endY,endY}, 3);
		}else if(!filled) {
			gc.setLineWidth(thickness);
			gc.strokePolygon(new double[] {startX+(endX-startX)/2,endX,startX}, new double[] {startY,endY,endY}, 3);
		}
	}
	
	private void shapeDrawer(double startX, double startY, double endX, double endY, boolean applyColor) {
		if(applyColor) {
			gc.setFill(colorPicker.getValue());
			gc.setStroke(colorPicker.getValue());
		}
		switch(currentTool) {
			case LINE_DRAWER:
				gc.setLineWidth(thickness);
				gc.strokeLine(startX, startY, endX, endY);
				break;
			case RECTANGLE_DRAWER:
				drawRectangle(startX,startY,endX,endY,filledShapes);
				break;
			case OVAL_DRAWER:
				drawOval(startX, startY, endX, endY, filledShapes);
				break;
			
			case TRIANGLE_DRAWER:
				drawTriangle(startX, startY, endX, endY, filledShapes);
				break;
		}
		if(undoCounter>0) {
			clearSnapshots();
		}
		WritableImage snapshot = canvas.snapshot(sParameters, null);
		canvasSnapshots.add(snapshot);
		pr = snapshot.getPixelReader();
		undoButton.setDisable(false);
	}
	
	private void open() {
		imageDirectory.setTitle("Choose a input file");
		imageDirectory.getExtensionFilters().clear();
		imageDirectory.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("PNG", "*.png"), 
				new FileChooser.ExtensionFilter("JPEG", "*.jpg"), new FileChooser.ExtensionFilter("GIF", "*.gif"), allExt);
		imageDirectory.setSelectedExtensionFilter(allExt);
		File file = imageDirectory.showOpenDialog(rootPane.getScene().getWindow());
		try {
			Image image = new Image(file.toURI().toString());
			canvasSnapshotAnchor.setPrefWidth(image.getWidth());
			canvas.setWidth(image.getWidth());
			widthField.setText(""+(int)image.getWidth());
			canvasSnapshotAnchor.setPrefHeight(image.getHeight());
			canvas.setHeight(image.getHeight());
			heightField.setText(""+(int)image.getHeight());
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			gc.drawImage(image, 0, 0);
			addNewSnapshot();
		}catch(NullPointerException e) {
			//
		}
	}
	
	private void edit() {
		imageDirectory.setTitle("Choose a input file");
		imageDirectory.getExtensionFilters().clear();
		imageDirectory.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("PNG", "*.png"), 
				new FileChooser.ExtensionFilter("JPEG", "*.jpg"), new FileChooser.ExtensionFilter("GIF", "*.gif"), allExt);
		imageDirectory.setSelectedExtensionFilter(allExt);
		File file = imageDirectory.showOpenDialog(rootPane.getScene().getWindow());
		if(file!=null) {
			this.file = file;
		}
		try {
			Image image = new Image(file.toURI().toString());
			canvasSnapshotAnchor.setPrefWidth(image.getWidth());
			canvas.setWidth(image.getWidth());
			widthField.setText(""+(int)image.getWidth());
			canvasSnapshotAnchor.setPrefHeight(image.getHeight());
			canvas.setHeight(image.getHeight());
			heightField.setText(""+(int)image.getHeight());
			filePresent = false;
			canvasSnapshots.clear();
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			gc.drawImage(image, 0, 0);
			undoCounter = 0;
			addNewSnapshot();
			filePresent = true;
			savedIcon.setGraphic(savedImageView);
			savedIcon.setTooltip(savedTT);
			savedLabel.setText(file.getName());
			undoButton.setDisable(true);
			redoButton.setDisable(true);
			saveItem.setDisable(true);
			savedFileSnapshotIndex = canvasSnapshots.size()-1-undoCounter;
		}catch(NullPointerException e) {
			//
		}
	}
	
	private void save() {
		WritableImage snapshot = canvas.snapshot(sParameters, null);
		String extention="";
		if(file!=null) {
			extention = FilenameUtils.getExtension(file.toString());
		}
		if(extention.equals("jpg")) {
			extention = "png";
		}
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), extention, file);
			saved = true;
			saveItem.setDisable(true);
			savedFileSnapshotIndex = canvasSnapshots.size()-1-undoCounter;
			savedIcon.setGraphic(savedImageView);
			savedIcon.setTooltip(savedTT);
		}catch(IOException e) {
			e.printStackTrace();
		}catch(IllegalArgumentException e2) {
			//
		}
	}
	
	private void saveAs() {
		if(currentTool==TEXT_EDIT) {
			currentTool=TEXT;
		}else if(currentTool==MOVE) {
			currentTool=CROP;
		}
		WritableImage snapshot = canvas.snapshot(sParameters, null);
		imageDirectory.setTitle("Choose the output directory of your file");
		imageDirectory.getExtensionFilters().clear();
		imageDirectory.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("PNG", "*.png"), 
				new FileChooser.ExtensionFilter("JPEG", "*.jpg"), new FileChooser.ExtensionFilter("GIF", "*.gif"));
		File file = imageDirectory.showSaveDialog(rootPane.getScene().getWindow());
		if(file!=null) {
			this.file = file;
		}
		String extention="";
		if(file!=null) {
			extention = FilenameUtils.getExtension(file.toString());
		}
		if(extention.equals("jpg")) {
			extention = "png";
		}
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), extention, file);
			saved = true;
			filePresent = true;
			savedFileSnapshotIndex = canvasSnapshots.size()-1-undoCounter;
			savedIcon.setGraphic(savedImageView);
			savedIcon.setTooltip(savedTT);
			savedLabel.setText(file.getName());
		}catch(IOException e) {
			e.printStackTrace();
		}catch(IllegalArgumentException e2) {
			//
		}
	}
	
	private void fill(int x, int y, Color checkColor, Color changeColor) {
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
		WritableImage undoImage = canvasSnapshots.get(canvasSnapshots.size()-2-undoCounter);
		if(canvas.getWidth()!=undoImage.getWidth()) {
			canvasSnapshotAnchor.setPrefWidth(undoImage.getWidth());
			canvas.setWidth(undoImage.getWidth());
			widthField.setText(""+(int)undoImage.getWidth());
		}
		if(canvas.getHeight()!=undoImage.getHeight()) {
			canvasSnapshotAnchor.setPrefHeight(undoImage.getHeight());
			canvas.setHeight(undoImage.getHeight());
			heightField.setText(""+(int)undoImage.getHeight());
		}
		if(savedFileSnapshotIndex==canvasSnapshots.size()-2-undoCounter&&filePresent) {
			saved = true;
			savedIcon.setGraphic(savedImageView);
			savedIcon.setTooltip(savedTT);
			saveItem.setDisable(true);
		}else if(filePresent){
			saved = false;
			saveItem.setDisable(false);
			savedIcon.setGraphic(notSavedImageView);
			savedIcon.setTooltip(notSavedTT);
		}
		gc.drawImage(undoImage, 0, 0);
		pr = canvasSnapshots.get(canvasSnapshots.size()-2-undoCounter).getPixelReader();
		redoButton.setDisable(false);
		undoCounter++;
		if(undoCounter+1>=canvasSnapshots.size()) {
			undoButton.setDisable(true);
		}
	}
	
	private void redo() {
		WritableImage redoImage = canvasSnapshots.get(canvasSnapshots.size()-undoCounter);
		if(canvas.getWidth()!=redoImage.getWidth()) {
			canvasSnapshotAnchor.setPrefWidth(redoImage.getWidth());
			canvas.setWidth(redoImage.getWidth());
			widthField.setText(""+(int)redoImage.getWidth());
		}
		if(canvas.getHeight()!=redoImage.getHeight()) {
			canvasSnapshotAnchor.setPrefHeight(redoImage.getHeight());
			canvas.setHeight(redoImage.getHeight());
			heightField.setText(""+(int)redoImage.getHeight());
		}
		if(savedFileSnapshotIndex==canvasSnapshots.size()-undoCounter&&filePresent) {
			saveItem.setDisable(true);
			savedIcon.setGraphic(savedImageView);
			savedIcon.setTooltip(savedTT);
		}else if(filePresent){
			saveItem.setDisable(false);
			savedIcon.setGraphic(notSavedImageView);
			savedIcon.setTooltip(notSavedTT);
		}
		gc.drawImage(redoImage, 0, 0);
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
	
	private void buttonManagement(Queue<Button> toDisable) {
		ArrayList<Button> disabled = new ArrayList<Button>();
		while(!toDisable.isEmpty()) {
			Button disableButton = toDisable.remove();
			disableButton.setDisable(true);
			disabled.add(disableButton);
		}
		
		for(int i=0; i < linkedButtons.size(); i++) {
			Button loopButton = linkedButtons.get(i);
			if(loopButton.isDisable()&&!disabled.contains(loopButton)) {
				loopButton.setDisable(false);
			}
		}
	}

	@Override
	public void handle(ActionEvent e) {
		Object source = e.getSource();
		Queue<Button> toDisable = new LinkedList<Button>();
		if(gc.getLineDashes()!=null) {
			gc.setLineDashes(null);
		}
		if(currentTool==MOVE) {
			gc.drawImage(canvasSnapshots.get(canvasSnapshots.size()-1), 0, 0);
		}else if(currentTool==TEXT_EDIT) {
			gc.drawImage(rubberBandSnapshot, 0, 0);
			if(!canvasText.getText().equals("")&&!firstSnapshot) {
				canvasText.setStyle("-fx-display-caret: false; -fx-text-fill: " + getColorHex(colorPicker.getValue()));
				WritableImage snapshot = canvasSnapshotAnchor.snapshot(sParameters, null);
				canvasText.setStyle("-fx-text-fill: " + getColorHex(colorPicker.getValue()));
				canvasSnapshots.add(snapshot);
				pr = snapshot.getPixelReader();
				undoButton.setDisable(false);
				gc.drawImage(snapshot, 0, 0);
			}
			canvasAnchor.getChildren().remove(canvasText);
		}
		if(source.equals(newItem)) {
			canvasSnapshotAnchor.setPrefWidth(600);
			canvas.setWidth(600);
			widthField.setText("600");
			canvasSnapshotAnchor.setPrefHeight(400);
			canvas.setHeight(400);
			heightField.setText("400");
			gc.setStroke(Color.WHITE);
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			if(!brandNew) {
				addNewSnapshot();
				brandNew=true;
			}
		}else if(source.equals(openItem)) {
			open();
		}else if(source.equals(editItem)) {
			edit();
		}else if(source.equals(saveItem)) {
			save();
		}else if(source.equals(saveAsItem)) {
			saveAs();
		}else if(source.equals(filledShapesItem)) {
			if(filledShapes) {
				filledShapes=false;
			}else {
				filledShapes=true;
			}
		}else if(source.equals(twoPxItem)) {
			thickness=2;
		}else if(source.equals(fourPxItem)) {
			thickness=4;
		}else if(source.equals(eightPxItem)) {
			thickness=8;
		}else if(source.equals(sixteenPxItem)) {
			thickness=16;
		}else if(source.equals(aboutItem)) {
			about.display();
		}else if(source.equals(cropButton)) {
			toDisable.add(cropButton);
			toDisable.add(cdButton);
			buttonManagement(toDisable);
			currentTool = CROP;
		}else if(source.equals(scissionButton)) {
			toDisable.add(scissionButton);
			toDisable.add(cdButton);
			buttonManagement(toDisable);
			currentTool= SCISSION;
		}else if(source.equals(cdButton)) {
			int[] coordinates = cd.display();
			if(cd.finishedSuccessfully) {
				shapeDrawer(coordinates[0], coordinates[1], coordinates[2], coordinates[3],true);
			}
		}else if(source.equals(pencilButton)) {
			toDisable.add(cdButton);
			toDisable.add(pencilButton);
			buttonManagement(toDisable);
			currentTool = PENCIL;
			canvas.setCursor(Cursor.CROSSHAIR);
		}else if(source.equals(fillButton)) {
			toDisable.add(cdButton);
			toDisable.add(fillButton);
			buttonManagement(toDisable);
			currentTool = FILL;
			canvas.setCursor(fillCursor);
		}else if(source.equals(textButton)) {
			toDisable.add(cdButton);
			toDisable.add(textButton);
			buttonManagement(toDisable);
			currentTool = TEXT;
			canvas.setCursor(Cursor.TEXT);
		}else if(source.equals(pipetteButton)) {
			toDisable.add(cdButton);
			toDisable.add(pipetteButton);
			currentTool = PIPETTE;
			buttonManagement(toDisable);
			canvas.setCursor(pipetteCursor);
		}else if(source.equals(lineButton)) {
			toDisable.add(lineButton);
			buttonManagement(toDisable);
			currentTool = LINE_DRAWER;
			canvas.setCursor(Cursor.CROSSHAIR);
		}else if(source.equals(rectangleButton)) {
			toDisable.add(rectangleButton);
			buttonManagement(toDisable);
			currentTool = RECTANGLE_DRAWER;
			canvas.setCursor(Cursor.CROSSHAIR);
		}else if(source.equals(ovalButton)) {
			toDisable.add(ovalButton);
			buttonManagement(toDisable);
			currentTool = OVAL_DRAWER;
			canvas.setCursor(Cursor.CROSSHAIR);
		}else if(source.equals(triangleButton)) {
			toDisable.add(triangleButton);
			buttonManagement(toDisable);
			currentTool = TRIANGLE_DRAWER;
			canvas.setCursor(Cursor.CROSSHAIR);
		}else if(source.equals(undoButton)) {
			undo();
			if(currentTool==MOVE) {
				currentTool=CROP;
			}else if(currentTool==TEXT_EDIT) {
				currentTool=TEXT;
			}
		}else if(source.equals(redoButton)) {
			redo();
			if(currentTool==MOVE) {
				currentTool=CROP;
			}else if(currentTool==TEXT_EDIT) {
				currentTool=TEXT;
			}
		}
	}
}
