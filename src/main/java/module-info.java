module EvilClicker {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens com.github.suhndern.evilclicker to javafx.fxml;
    exports com.github.suhndern.evilclicker;
}