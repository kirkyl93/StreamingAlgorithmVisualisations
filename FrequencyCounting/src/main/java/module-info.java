module counting.frequencycounting {
    requires javafx.controls;
    requires javafx.fxml;


    opens counting.frequencycounting to javafx.fxml;
    exports counting.frequencycounting;
}