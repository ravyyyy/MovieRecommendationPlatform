module com.example.movierecommendationplatform {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens com.example.movierecommendationplatform to javafx.fxml;
    exports com.example.movierecommendationplatform;
}