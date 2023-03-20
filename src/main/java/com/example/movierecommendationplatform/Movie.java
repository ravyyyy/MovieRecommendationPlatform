package com.example.movierecommendationplatform;

import javafx.beans.property.SimpleStringProperty;

import java.util.Comparator;

public class Movie{
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty category;
    private final SimpleStringProperty releaseDate;
    private final SimpleStringProperty rating;

    public void setId(String id){
        this.id.set(id);
    }
    public void setName(String name){
        this.name.set(name);
    }
    public void setCategory(String category){
        this.category.set(category);
    }
    public void setReleaseDate(String releaseDate){
        this.releaseDate.set(releaseDate);
    }
    public void setRating(String rating){
        this.rating.set(rating);
    }
    public String getId(){
        return this.id.get();
    }

    public String getName(){
        return this.name.get();
    }
    public String getCategory(){
        return this.category.get();
    }
    public String getReleaseDate(){
        return this.releaseDate.get();
    }
    public String getRating(){
        return this.rating.get();
    }
    public Movie(String id, String name, String category, String releaseDate, String rating){
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.releaseDate = new SimpleStringProperty(releaseDate);
        this.rating = new SimpleStringProperty(rating);
    }
    static class RatingComparator implements Comparator<Movie>{
        RatingComparator(Movie this$0){

        }
        public int compare(Movie movie1, Movie movie2){return Integer.compare(Integer.parseInt(movie1.getRating()),
                Integer.parseInt(movie2.getRating()));}
    }
}
