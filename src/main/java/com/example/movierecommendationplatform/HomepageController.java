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
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.ResourceBundle;

public class HomepageController implements Initializable{
    @FXML
    private Button addMovieButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField releaseDateField;

    @FXML
    private TextField ratingField;
    @FXML
    private Button logOutButton;

    @FXML
    private Label errorLabel;

    private DBHandler handler;

    private final Member member = new Member("", "", "", "", "");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handler = new DBHandler();
        try {
            createMember();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMovie(ActionEvent e) throws SQLException {
        boolean correctForm = true;
        Connection connection = handler.getConnection();
        String movieName = nameField.getText();
        String movieCategory = categoryField.getText();
        String movieReleaseDate = releaseDateField.getText();
        String movieRating = ratingField.getText();

        //Check empty strings
        if(Objects.equals(movieName, "") || Objects.equals(movieCategory, "") ||
                Objects.equals(movieReleaseDate, "") || Objects.equals(movieRating, "")){
            errorLabel.setText("No TextField can be empty!");
            errorLabel.setTextFill(Color.RED);
            correctForm = false;
        }

        //Check movie name length
        if(movieName.length() > 50){
            errorLabel.setText("Movie name is too long!");
            errorLabel.setTextFill(Color.RED);
            correctForm = false;
        }

        //Check movie name
        String nameCheck = "SELECT * from Movie WHERE name = '" + movieName + "'";
        PreparedStatement pst = connection.prepareStatement(nameCheck);
        ResultSet rs = pst.executeQuery();
        int count = 0;
        while(rs.next()){
            count++;
        }
        if(count != 0){
            errorLabel.setText("Movie with that name already exists!");
            errorLabel.setTextFill(Color.RED);
            correctForm = false;
        }

        //Check movie category
        char[] chars = movieCategory.toCharArray();
        for(char c : chars){
            if(!Character.isLetter(c)){
                errorLabel.setText("Movie category MUST contain only letters!");
                errorLabel.setTextFill(Color.RED);
                correctForm = false;
            }
        }

        //Check movie release date
        if(!movieReleaseDate.matches("\\d{4}-[01]\\d-[0-3]\\d")){
            errorLabel.setText("Movie release date is not in format YYYY-MM-DD!");
            errorLabel.setTextFill(Color.RED);
            correctForm = false;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        try{
            df.parse(movieReleaseDate);
        }
        catch (ParseException pe){
            errorLabel.setText("Movie release date does not have a valid form!");
            errorLabel.setTextFill(Color.RED);
            correctForm = false;
        }

        //Check movie rating
        char[] chars2 = movieRating.toCharArray();
        for(char c : chars2){
            if(!Character.isDigit(c)){
                errorLabel.setText("Movie rating must contain only digits!");
                errorLabel.setTextFill(Color.RED);
                correctForm = false;
            }
        }

        //Add movie
        if(correctForm){
            String insert = "INSERT INTO Movie(name, category, release_date, rating)VALUES(?, ?, ?, ?)";
            PreparedStatement pst2 = connection.prepareStatement(insert);

            pst2.setString(1, movieName);
            pst2.setString(2, movieCategory);
            pst2.setString(3, movieReleaseDate);
            pst2.setString(4, movieRating);

            pst2.executeUpdate();

            //Confirmation
            PauseTransition pt = new PauseTransition();
            pt.setDuration(Duration.seconds(2));
            pt.setOnFinished(ev -> {
                System.out.print("Movie added successfully!");
            });
            pt.play();
            errorLabel.setText("Movie added successfully!");
            errorLabel.setTextFill(Color.GREEN);
        }
    }
    public void createMember() throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader("member.txt"));
        member.setId(bf.readLine());
        member.setUsername(bf.readLine());
        member.setPassword(bf.readLine());
        member.setType(bf.readLine());
        member.setLikedMovies(bf.readLine());
    }
    public void logOut() throws IOException {
        logOutButton.getScene().getWindow().hide();

        Stage signup = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.setTitle("Log In");
        signup.show();
        signup.setResizable(false);
    }
}
