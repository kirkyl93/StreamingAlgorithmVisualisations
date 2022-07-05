module counting.distinctcounting {
    requires javafx.controls;
    requires javafx.fxml;


    opens counting.distinctcounting to javafx.fxml;
    exports counting.distinctcounting;
}