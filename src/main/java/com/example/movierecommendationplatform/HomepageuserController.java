package com.example.movierecommendationplatform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class HomepageuserController implements Initializable {

    @FXML
    private TableView<Movie> tableMovie;
    @FXML
    private TableColumn id;
    @FXML
    private TableColumn name;
    @FXML
    private TableColumn category;
    @FXML
    private TableColumn releaseDate;
    @FXML
    private TableColumn rating;
    @FXML
    private Button logOutButton;
    @FXML
    private Button likeButton;
    @FXML
    private Button orderByDateButton;
    @FXML
    private Button orderByRatingButton;

    private static Connection connection;

    private PreparedStatement pst;

    private static ObservableList<Movie> movies;
    private final Member member = new Member("", "", "", "", "");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DBHandler handler = new DBHandler();
        connection = handler.getConnection();
        id.setCellValueFactory(new PropertyValueFactory("id"));
        name.setCellValueFactory(new PropertyValueFactory("name"));
        category.setCellValueFactory(new PropertyValueFactory("category"));
        releaseDate.setCellValueFactory(new PropertyValueFactory("releaseDate"));
        rating.setCellValueFactory(new PropertyValueFactory("rating"));
        try {
            readMember();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            fetchData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fetchData() throws SQLException {
        movies = FXCollections.observableArrayList();
        String mostLikedGenre = null;

        if (Objects.equals(member.getLikedMovies(), "0")) {
            try {
                ResultSet rs = connection.createStatement().executeQuery("SELECT * from Movie ORDER BY RAND()");
                while (rs.next()) {
                    String id = Integer.toString(rs.getInt(1));
                    String name = rs.getString(2);
                    String category = rs.getString(3);
                    String date = rs.getString(4);
                    String rating = Integer.toString(rs.getInt(5));
                    Movie movie = new Movie(id, name, category, date, rating);
                    System.out.println("Row added " + movie.getId());
                    movies.add(movie);
                }
                tableMovie.setItems(movies);
                tableMovie.setVisible(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else {
            try {
                ResultSet rr = connection.createStatement().executeQuery("SELECT mm.category, COUNT(*) FROM membermovie mm WHERE mm.member_id='" + member.getId() + "' GROUP BY mm.category ORDER BY COUNT(*) DESC");
                while(rr.next()) {
                    mostLikedGenre = rr.getString("category");
                    break;
                }
                ResultSet rs = connection.createStatement().executeQuery("SELECT * from Movie WHERE category = '" + mostLikedGenre + "' ORDER BY RAND()");
                while (rs.next()) {
                    String id = Integer.toString(rs.getInt(1));
                    String name = rs.getString(2);
                    String category = rs.getString(3);
                    String date = rs.getString(4);
                    String rating = Integer.toString(rs.getInt(5));
                    Movie movie = new Movie(id, name, category, date, rating);
                    System.out.println("Row added " + movie.getId());
                    movies.add(movie);
                }
                tableMovie.setItems(movies);
                rs = connection.createStatement().executeQuery("SELECT * FROM Movie WHERE category != '" + mostLikedGenre + "' ORDER BY RAND()");
                while(rs.next()){
                    String id = Integer.toString(rs.getInt(1));
                    String name = rs.getString(2);
                    String category = rs.getString(3);
                    String date = rs.getString(4);
                    String rating = Integer.toString(rs.getInt(5));
                    Movie movie = new Movie(id, name, category, date, rating);
                    System.out.println("Row added " + movie.getId());
                    movies.add(movie);
                }
                tableMovie.setItems(movies);
                tableMovie.setVisible(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        useStream();
    }
    public void readMember() throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader("member.txt"));
        member.setId(bf.readLine());
        member.setUsername(bf.readLine());
        member.setPassword(bf.readLine());
        member.setType(bf.readLine());
        member.setLikedMovies(bf.readLine());
    }
    public void likeMovie() throws SQLException {
        int index = tableMovie.getSelectionModel().getSelectedIndex();
        Movie movie = tableMovie.getItems().get(index);
        pst = connection.prepareStatement("UPDATE movie SET rating=? WHERE movie_id=?");
        int likes = Integer.parseInt(movie.getRating()) + 1;
        pst.setString(1, String.valueOf(likes));
        pst.setString(2, movie.getId());
        pst.executeUpdate();
        String insert = "INSERT INTO MemberMovie(member_id, movie_id, category) VALUES(?, ?, ?)";
        pst = connection.prepareStatement(insert);
        pst.setString(1, member.getId());
        pst.setString(2, movie.getId());
        pst.setString(3, movie.getCategory());
        pst.executeUpdate();
        pst = connection.prepareStatement("UPDATE member SET liked_movies=? WHERE member_id=?");
        pst.setString(1, "1");
        pst.setString(2, member.getId());
        pst.executeUpdate();
        member.setLikedMovies("1");
        fetchData();
    }
    public void orderByDate() throws SQLException {
        tableMovie.getItems().clear();
        String mostLikedGenre = null;
        ResultSet rr = connection.createStatement().executeQuery("SELECT mm.category, COUNT(*) FROM membermovie mm WHERE mm.member_id='" + member.getId() + "' GROUP BY mm.category ORDER BY COUNT(*) DESC");
        while(rr.next()) {
            mostLikedGenre = rr.getString("category");
            break;
        }
        ResultSet rs = connection.createStatement().executeQuery("SELECT * from Movie WHERE category = '" + mostLikedGenre + "' ORDER BY release_date DESC");
        while (rs.next()) {
            String id = Integer.toString(rs.getInt(1));
            String name = rs.getString(2);
            String category = rs.getString(3);
            String date = rs.getString(4);
            String rating = Integer.toString(rs.getInt(5));
            Movie movie = new Movie(id, name, category, date, rating);
            System.out.println("Row added " + movie.getId());
            movies.add(movie);
        }
        tableMovie.setItems(movies);
        rs = connection.createStatement().executeQuery("SELECT * FROM Movie WHERE category != '" + mostLikedGenre + "' ORDER BY RAND()");
        while(rs.next()){
            String id = Integer.toString(rs.getInt(1));
            String name = rs.getString(2);
            String category = rs.getString(3);
            String date = rs.getString(4);
            String rating = Integer.toString(rs.getInt(5));
            Movie movie = new Movie(id, name, category, date, rating);
            System.out.println("Row added " + movie.getId());
            movies.add(movie);
        }
        tableMovie.setItems(movies);
        tableMovie.setVisible(true);
    }

    public void orderByRating() throws SQLException {
        tableMovie.getItems().clear();
        String mostLikedGenre = null;
        ResultSet rr = connection.createStatement().executeQuery("SELECT mm.category, COUNT(*) FROM membermovie mm WHERE mm.member_id='" + member.getId() + "' GROUP BY mm.category ORDER BY COUNT(*) DESC");
        while(rr.next()) {
            mostLikedGenre = rr.getString("category");
            break;
        }
        ResultSet rs = connection.createStatement().executeQuery("SELECT * from Movie WHERE category = '" + mostLikedGenre + "' ORDER BY rating DESC");
        while (rs.next()) {
            String id = Integer.toString(rs.getInt(1));
            String name = rs.getString(2);
            String category = rs.getString(3);
            String date = rs.getString(4);
            String rating = Integer.toString(rs.getInt(5));
            Movie movie = new Movie(id, name, category, date, rating);
            System.out.println("Row added " + movie.getId());
            movies.add(movie);
        }
        tableMovie.setItems(movies);
        rs = connection.createStatement().executeQuery("SELECT * FROM Movie WHERE category != '" + mostLikedGenre + "' ORDER BY RAND()");
        while(rs.next()){
            String id = Integer.toString(rs.getInt(1));
            String name = rs.getString(2);
            String category = rs.getString(3);
            String date = rs.getString(4);
            String rating = Integer.toString(rs.getInt(5));
            Movie movie = new Movie(id, name, category, date, rating);
            System.out.println("Row added " + movie.getId());
            movies.add(movie);
        }
        tableMovie.setItems(movies);
        tableMovie.setVisible(true);
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

    public static void useStream() throws SQLException {
        List<Movie> newMovies = movies.stream().collect(Collectors.toList());
        for(Movie movie : newMovies){
            System.out.println("Id: " + movie.getId());
            System.out.println("Name: " + movie.getName());
            System.out.println("Category: " + movie.getCategory());
            System.out.println("Release date: " + movie.getReleaseDate());
            System.out.println("Rating: " + movie.getRating());
        }
    }
}
