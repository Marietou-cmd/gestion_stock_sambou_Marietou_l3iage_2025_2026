module com.gestionstock {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.gestionstock to javafx.fxml, javafx.graphics;
    opens com.gestionstock.controller to javafx.fxml;
}