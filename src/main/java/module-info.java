module com.arabadzhiev {
	requires org.apache.commons.io;
	requires java.desktop;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.base;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    
    opens com.arabadzhiev to javafx.fxml;
    exports com.arabadzhiev;
}