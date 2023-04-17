package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.api.MovieAPI;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.models.SortedState;
import at.ac.fhcampuswien.fhmdb.ui.MovieCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HomeController implements Initializable {
    @FXML
    public JFXButton searchBtn;

    @FXML
    public TextField searchField;

    @FXML
    public JFXListView movieListView;

    @FXML
    public JFXComboBox genreComboBox;

    @FXML
    public JFXButton sortBtn;

    public List<Movie> allMovies;

    protected ObservableList<Movie> observableMovies = FXCollections.observableArrayList();

    protected SortedState sortedState;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeState();
        initializeLayout();
    }

    public void initializeState() {
        allMovies = Movie.initializeMovies();
        observableMovies.clear();
        observableMovies.addAll(allMovies); // add all movies to the observable list
        sortedState = SortedState.NONE;
    }

    public void initializeLayout() {
        movieListView.setItems(observableMovies);   // set the items of the listview to the observable list
        movieListView.setCellFactory(movieListView -> new MovieCell()); // apply custom cells to the listview

        Object[] genres = Genre.values();   // get all genres
        genreComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        genreComboBox.getItems().addAll(genres);    // add all genres to the combobox
        genreComboBox.setPromptText("Filter by Genre");
    }

    // sort movies based on sortedState
    // by default sorted state is NONE
    // afterwards it switches between ascending and descending
    public void sortMovies() {
        if (sortedState == SortedState.NONE || sortedState == SortedState.DESCENDING) {
            observableMovies.sort(Comparator.comparing(Movie::getTitle));
            sortedState = SortedState.ASCENDING;
        } else if (sortedState == SortedState.ASCENDING) {
            observableMovies.sort(Comparator.comparing(Movie::getTitle).reversed());
            sortedState = SortedState.DESCENDING;
        }
    }


    // TODO : Diese Methode wird so angepasst, dass die Parameter für die Methode aus MovieAPI vorbereitet werden.
    // Der Aufruf der MovieAPI - Methode passiert dann in dieser Methode
    public void applyAllFilters(String searchQuery, Object genre) {

        if (searchQuery.isEmpty()) searchQuery = null;

        // Wenn kein Genre ausgwählt wurde, oder "No filter" ausgewählt wurde, wird kein Wert an "getFilteredMovieListAsJSON" übergeben
        String genreString = null;
        if (genre != null && !genre.toString().equals("No filter")) genreString = genre.toString();

        // TODO : Aufruf von "getFilteredMovieListAsJSON" um "releaseYear" und "rating" erweitern.
        String listOfMoviesAsJSON = MovieAPI.getFilteredMovieListAsJSON(searchQuery,genreString,null,null);

        List<Movie> filteredMovies = Movie.createMovieListFromJson(listOfMoviesAsJSON);

        observableMovies.clear();
        observableMovies.addAll(filteredMovies);

        // Console Output for Testing
        System.out.println("---------------");
        System.out.println("Most popular Actor : " + getMostPopularActor(filteredMovies));
        System.out.println("Longest Movie Title : " + getLongestMovieTitle(filteredMovies));
        System.out.println("Number of Movie from Director " + filteredMovies.get(0).getDirectors().get(0) + " : " +
                countMoviesFrom(filteredMovies,filteredMovies.get(0).getDirectors().get(0)));
        System.out.println("---------------");
        System.out.println("Filme zwischen 2000 und 2020");
        for ( Movie movie : getMoviesBetweenYears(filteredMovies,2000,2020)) {
            System.out.println(movie.getTitle());
        }
    }

    public void searchBtnClicked(ActionEvent actionEvent) {
        String searchQuery = searchField.getText().trim().toLowerCase();
        Object genre = genreComboBox.getSelectionModel().getSelectedItem();

        applyAllFilters(searchQuery, genre);

        if(sortedState != SortedState.NONE) {
            sortMovies();
        }
    }

    public void sortBtnClicked(ActionEvent actionEvent) {
        sortMovies();
    }



    //Hilfestellungen für alle Streams:
    //https://www.geeksforgeeks.org/stream-flatmap-java-examples/
    //abgerufen am 15.04.2023 um 21:13 Uhr.
    //https://howtodoinjava.com/java8/stream-flatmap-example/
    //abgerufen am 15.04.2023 um 21:14 Uhr
    //https://www.baeldung.com/java-difference-map-and-flatmap
    //abgerufen am 15.04.2023 um 21:14 Uhr

    //https://stackify.com/streams-guide-java-8/
    //abgerufen am 15.04.2023 um 21:39 Uhr


    //Stream Nr 1: "Gibt jene Person zurück, die am öftesten im mainCast der übergebenen Filme vorkommt."
    //Stream wurde getestet, funktioniert;
    public String getMostPopularActor(List<Movie> movies) {

        return movies.stream()
                .flatMap(movie -> movie.getMainCast().stream()) //get MainCast als flatMap (alle Listen der Objekte werden zu einer großen Liste vereint)
                .collect(Collectors.groupingBy(actor -> actor, Collectors.counting())) //Zählen der Namen der MainCasts
                //Map (Key-Value-Pair) wird erstellt, Key = Name, Value = Häufigkeit
                .entrySet().stream()
                //gibt höchsten Value (Häufigkeit) aus:
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey) //gibt den Namen (in einem neuen Stream) zurück

                //Zur Absicherung, falls kein Name gefunden wurde (& da ansonsten kein String, sondern Optional verwendet werden müsste):
                .orElse("");
    }

    //Stream Nr 2: "Filtert auf den längsten Filmtitel der übergebenen Filme und gibt die Anzahl der Buchstaben des Titels zurück."
    public int getLongestMovieTitle(List<Movie> movies) {

        return movies.stream().map(Movie::getTitle)
                .mapToInt(String::length)

                .max()
                //.toString();

                .orElse(0); //notwendig, da ohne diesem orElse(0) kein "public int" möglich wäre, sondern nur OptionalInt
                //siehe: https://stackoverflow.com/questions/47976489/difference-between-optionalint-and-int
                //abgerufen am 15.04.2023 um 21:58 Uhr
    }

    //Stream Nr 3: "Gibt die Anzahl der Filme eines bestimmten Regisseurs zurück."
    public long countMoviesFrom(List<Movie> movies, String director){

        long numOfMovies;
        numOfMovies = movies.stream()
                .filter(movie -> movie.getDirectors().contains(director))
                .count();

        return numOfMovies;
    }

    //Stream Nr 4: "Gibt jene Filme zurück, die zwischen zwei gegebenen Jahren veröffentlicht wurden."
    public List<Movie> getMoviesBetweenYears(List<Movie> movies, int startYear, int endYear){

        return movies.stream()
                .filter(movie -> movie.getReleaseYear()
                        >= startYear && movie.getReleaseYear() <= endYear) //Bedingung: Zwischen 2 Jahreszahlen
                .collect(Collectors.toList()); //Rückgabe als Liste
    }
}