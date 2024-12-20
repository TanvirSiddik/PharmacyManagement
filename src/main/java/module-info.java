module com.pharmacy.org.pharmacy {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;


    opens com.pharmacy.org.pharmacy to javafx.fxml;
    exports com.pharmacy.org.pharmacy;
    exports com.pharmacy.org.pharmacy.Controllers;
    exports com.pharmacy.org.pharmacy.Models;
    opens com.pharmacy.org.pharmacy.Models to javafx.base;
    opens com.pharmacy.org.pharmacy.Controllers to javafx.fxml;
}