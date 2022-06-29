module counting.approximatecounting {
    requires javafx.controls;
    requires javafx.fxml;


    opens counting.approximatecounting to javafx.fxml;
    exports counting.approximatecounting;
}