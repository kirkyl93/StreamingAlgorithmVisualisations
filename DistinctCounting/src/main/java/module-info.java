module counting.distinctcounting {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.datasketches.memory;
    requires datasketches.java;


    opens counting.distinctcounting to javafx.fxml;
    exports counting.distinctcounting;
}