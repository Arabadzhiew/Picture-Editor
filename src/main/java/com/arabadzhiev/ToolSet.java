package com.arabadzhiev;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class ToolSet {
	public ToolSet(PixelWriter pw) {
		this.pw = pw;
	}
	PixelWriter pw;
	
	public void pencil(int x, int y, Color color) {
		pw.setColor(x, y, color);
		
	}

}
