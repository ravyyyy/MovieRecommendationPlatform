package com.example.movierecommendationplatform;

import javafx.beans.property.SimpleStringProperty;

public class Member {
    private final SimpleStringProperty id;
    private final SimpleStringProperty username;
    private final SimpleStringProperty password;
    private final SimpleStringProperty type;
    private final SimpleStringProperty likedMovies;
    public void setId(String id){
        this.id.set(id);
    }
    public void setUsername(String username){
        this.username.set(username);
    }
    public void setPassword(String password){
        this.password.set(password);
    }
    public void setType(String type){
        this.type.set(type);
    }
    public void setLikedMovies(String likedMovies){
        this.likedMovies.set(likedMovies);
    }
    public String getId(){
        return this.id.get();
    }
    public String getUsername(){
        return this.username.get();
    }
    public String getPassword(){
        return this.password.get();
    }
    public String getType(){
        return this.type.get();
    }
    public String getLikedMovies(){
        return this.likedMovies.get();
    }
    public Member(String id, String username, String password, String type, String likedMovies){
        this.id = new SimpleStringProperty(id);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.type = new SimpleStringProperty(type);
        this.likedMovies = new SimpleStringProperty(likedMovies);
    }
}
