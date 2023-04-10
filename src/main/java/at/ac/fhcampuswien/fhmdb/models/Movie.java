package at.ac.fhcampuswien.fhmdb.models;

import at.ac.fhcampuswien.fhmdb.api.MovieAPI;
import com.google.gson.Gson;

import java.util.List;

public class Movie {

    private final String id;
    private final String title;
    private final List<Genre> genres;

    private final int releaseYear;
    private final String description;

    private final String imgUrl;

    private final int lengthInMinutes;

    private final List<String> directors;

    private final List<String> writers;

    private final List<String> mainCast;

    private final float rating;


    public Movie(String id, String title, List<Genre> genres, int releaseYear, String description, String imgUrl,
                 int lengthInMinutes, List<String> directors, List<String> writers, List<String> mainCast,
                 float rating) {
        this.id = id;
        this.title = title;
        this.genres = genres;
        this.releaseYear = releaseYear;
        this.description = description;
        this.imgUrl = imgUrl;
        this.lengthInMinutes = lengthInMinutes;
        this.directors = directors;
        this.writers = writers;
        this.mainCast = mainCast;
        this.rating = rating;
    }



    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof Movie other)) {
            return false;
        }
        return this.title.equals(other.title) && this.description.equals(other.description) && this.genres.equals(other.genres);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getId() {
        return id;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getLengthInMinutes() {
        return lengthInMinutes;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public List<String> getWriters() {
        return writers;
    }

    public List<String> getMainCast() {
        return mainCast;
    }

    public float getRating() {
        return rating;
    }

    public static List<Movie> createMovieListFromJson (String movieDataAsJSON){
        Gson gson = new Gson();
        return List.of(gson.fromJson(movieDataAsJSON, Movie[].class));
    }

    public static List<Movie> initializeMovies(){
        // Die Filme werden hier initial über die API geholt.
        String movieDataAsJSON = MovieAPI.getFilteredMovieListAsJSON(null,null,null,null);
        // Der JSON-String der API wird hier in eine Liste von Filmen umgewandelt.
        return Movie.createMovieListFromJson(movieDataAsJSON);
    }
}
