module com.arabadzhiev {	
	requires java.desktop;
    requires javafx.fxml;
    requires javafx.swing;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    
    exports com.arabadzhiev;
}