package com.example.movierecommendationplatform;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label connectionResult;

    private DBHandler handler;

    private boolean type;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handler = new DBHandler();
    }

    @FXML
    public void loginAction(ActionEvent e){
        String ttype, id = null, likedMovies = null;
        Connection connection = handler.getConnection();
        String q1 = "SELECT * from member where username=? and password=?";

        try{
            PreparedStatement pst = connection.prepareStatement(q1);
            pst.setString(1, usernameField.getText());
            pst.setString(2, passwordField.getText());
            ResultSet rs = pst.executeQuery();

            int count = 0;
            while(rs.next())
            {
                count++;
                type = rs.getBoolean("type");
                id = Integer.toString(rs.getInt("member_id"));
                likedMovies = rs.getString(5);
            }
            if(type){
                ttype = "1";
            }else{
                ttype = "0";
            }

            Member member = new Member(id, usernameField.getText(), passwordField.getText(),
                    ttype, likedMovies);
            PauseTransition pt = new PauseTransition();
            pt.setDuration(Duration.seconds(2));
            if(count == 1)
            {
                pt.setOnFinished(ev -> {
                    System.out.print("Login Successfully");
                });
                connectionResult.setText("Login Successfully");
                connectionResult.setTextFill(Color.GREEN);

                loginButton.getScene().getWindow().hide();
                createMember(member);
                if(type) {
                    Stage homepage = new Stage();
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("homepage.fxml")));
                    Scene scene = new Scene(root);
                    homepage.setScene(scene);
                    homepage.setTitle("Admin Homepage");
                    homepage.show();
                    homepage.setResizable(false);
                }else{
                    Stage homepage = new Stage();
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("homepageuser.fxml")));
                    Scene scene = new Scene(root);
                    homepage.setScene(scene);
                    homepage.setTitle("User Homepage");
                    homepage.show();
                    homepage.setResizable(false);
                }
            }
            else {
                pt.setOnFinished(ev -> {
                    System.out.println("Login Unsuccessfully");
                });
                connectionResult.setText("Login Unsuccessfully");
                connectionResult.setTextFill(Color.RED);
            }
            pt.play();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                connection.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    @FXML
    public void signUp(ActionEvent e) throws IOException {
        loginButton.getScene().getWindow().hide();

        Stage signup = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signup.fxml")));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.setTitle("Sign Up");
        signup.show();
        signup.setResizable(false);
    }

    public static void createMember(Member member) throws IOException {
        PrintWriter pw = new PrintWriter("member.txt", StandardCharsets.UTF_8);
        pw.println(member.getId());
        pw.println(member.getUsername());
        pw.println(member.getPassword());
        pw.println(member.getType());
        pw.println(member.getLikedMovies());
        pw.close();
    }
}