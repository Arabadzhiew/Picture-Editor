![](https://i.imgur.com/OoPdEZC.png)

# Overview 
Picture Editor is a JavaFX program that lets you edit or create images of your own. It was created with the goal of making something that is easy to use and yet, effective. It has everything that you will ever need - nothing more, nothing less.

## To run the program
Follow these steps to instal the application on your pc:

1. Download the .exe installer inside of this repository;
2. Run it and configure to your liking;
3. When you are done, shortcuts will be created on your desktop and start menu, use them to access the application.
## Capabilities of the program
- The program is capable of handling images of up to `4k` resolution;
-  As far as image creation and editing goes, you can:  
    - Create your own images;
    - Edit already existing images;
    - Create your own images from existing images;

## Tool set

As said above, this program has everything that you will ever need from a picture edditor. It has:

- Crop tool - to crop and move around parts of your image;
- Scission tool - to scission a part of your image, creating a new, smaller image from it;
- Coordinate drawer tool - for percision drawing of shapes, using set coordinates;
- Pencil tool -  to draw on the image as if it was a white board. The thickness option applies to it;
- Filler tool - it fills the area where you have clicked on with the color you have picked inside the color picker. It fills pixel by pixel using a flood fill algorithm;
- Text too -  lets you add custom text to your image in a dynamic way;
- Pipette tool - sets the color of your color picker with the color of the pixel you have it clicked on;
- Line, Rectangle, Oval and Triangle drawer tools - can be either filled or unfilled.  The thickness option applies to all of them;
- Color picker - let's you pick the desired color that you want to use for most of the above mentioned tools;
- Coordinate display - displays where your cursor is located at any moment, when inside the canvas;
- Scale tool - lets you change the scale of the image for easier editing;
- Resize tool - lets you size up, or down your canvas.
## About the code

Since this is a JavaFX application it is not entirely written in `Java` , so this section is going to be sepparated in two sections `Java`  and `non-Java` code:
### Java code:
The java code consists of 7 different classes:

- MainWindow.java - The class with the main method. It is also responsible for loading the main window from the fxml file;
- Controller.java  - The controller class of the fxml file. It handles all of the logic of the fxml file;
- CoordinateDrawer.java  -  This is the coordinate drawer window class. It handles all of the interface and logic work inside of it;
- CoonfirmationBox.java - It is responsible for the confirmation box that pops up before closing the program;
- About.java - It is responsible for the about window, wich is accesable trough the `Help` tab;
- CanvasTextBrains.java - Responsible for a good part of the `Text` tool's logic.
- SeedPixlel.java - This class is used to create `SeedPixel` objects that are required for the flood fill algorithm of the `Fill` tool.
### non-Java code:

#### Css:
There are 2 css files for this project, since if it was only 1, it would have been too overcrowded

- style.css - This is the main and the bigger of the 2 css files. It is responsible for the `MainWindow.java` and `CoordinateDrawer.java` design;
-  secondaryStyle.css - This one is responsible for the design of `ConfirmationBox.java` and `About.java.
#### FXML:
- interface.fxml - This file is responsible for laying out most of the `MainWindow.java`'s nodes. It used `Controller.java` as it's controller class.
#### Other:
Since this program uses `Apache Maven` as it's build tool, it has a:
-  pom.xml - Responsible for the all the dendencies that are loaded into the project, also for the plugins (one of wich is the `JavaFX Maven Plugin`) and their configurations.


