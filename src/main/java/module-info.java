module com.tv.cnc {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.tv.cnc to javafx.fxml;
    exports com.tv.cnc;
}
