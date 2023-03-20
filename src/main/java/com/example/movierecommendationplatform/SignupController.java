package com.example.movierecommendationplatform;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class SignupController implements Initializable {
    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signupButton;

    @FXML
    private TextField usernameField;

    @FXML
    private Label confirmationLabel;

    private DBHandler handler;
    private PreparedStatement pst;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handler = new DBHandler();
    }

    @FXML
    public void signupAction(ActionEvent e){
        // Save Data

        String insert = "INSERT INTO member(username, password, type, liked_movies)VALUES(?, ?, ?, ?)";
        Connection connection = handler.getConnection();
        try{
            pst = connection.prepareStatement(insert);
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        try {
            pst.setString(1, usernameField.getText());
            pst.setString(2, passwordField.getText());
            pst.setString(3, "0");
            pst.setString(4, "0");

            pst.executeUpdate();

            //Confirmation

            PauseTransition pt = new PauseTransition();
            pt.setDuration(Duration.seconds(2));
            pt.setOnFinished(ev -> {
                System.out.print("Sign up Successfully");
            });
            pt.play();

            confirmationLabel.setText("Sign up Successfully");
            confirmationLabel.setTextFill(Color.GREEN);
        } catch (SQLException e3) {
            PauseTransition pt = new PauseTransition();
            pt.setDuration(Duration.seconds(2));
            pt.setOnFinished(ev -> {
                System.out.print("Sign up Unsuccessfully");
            });
            pt.play();

            confirmationLabel.setText("Sign up Unsuccessfully");
            confirmationLabel.setTextFill(Color.RED);

            e3.printStackTrace();
        }
    }

    @FXML
    public void logIn(ActionEvent e) throws IOException {
        //Change Scenes

        signupButton.getScene().getWindow().hide();

        Stage login = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        Scene scene = new Scene(root);
        login.setScene(scene);
        login.show();
        login.setResizable(false);
    }
}
