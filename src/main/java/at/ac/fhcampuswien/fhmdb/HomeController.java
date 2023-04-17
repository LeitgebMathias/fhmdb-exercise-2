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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
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
    public JFXComboBox releaseYearComboBox;

    @FXML
    public JFXComboBox ratingComboBox;

    @FXML
    public JFXButton sortBtn;

    @FXML

    public JFXButton searchByIDBtn;

    public Label infoLabel;

    public List<Movie> allMovies;

    protected ObservableList<Movie> observableMovies = FXCollections.observableArrayList();

    protected SortedState sortedState;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeState();
        initializeLayout();
    }

    public void initializeState() {
        try{
            allMovies = Movie.initializeMovies();
        }catch(Exception e){
            showErrorInfo();
            // Falls beim Laden der Filme ein Fehler aufgetreten ist, werden keine
            // Suchergebnisse angezeigt.
            allMovies = new ArrayList<>() {};
        }
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

        releaseYearComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        releaseYearComboBox.getItems().addAll(possibleReleaseYears()); // add all release years to the combobox
        releaseYearComboBox.setPromptText("Filter by Release Year");

        ratingComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        ratingComboBox.getItems().addAll(possibleRatings()); // add all ratings to the combobox
        ratingComboBox.setPromptText("Filter by Rating");

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


    public void applyAllFilters(String searchQuery, Object genre, Object releaseYear, Object rating) {

        if (searchQuery.isEmpty()) searchQuery = null;

        // Wenn kein Genre oder "No filter" ausgewählt wurde, wird kein Wert an "getFilteredMovieListAsJSON" übergeben
        String genreString = null;
        if (genre != null && !genre.toString().equals("No filter")) genreString = genre.toString();

        // Wenn kein Release Year oder "No filter" ausgewählt wurde, wird kein Wert an "getFilteredMovieListAsJSON" übergeben
        String releaseYearString = null;
        if (releaseYear != null && !releaseYear.toString().equals("No filter")) releaseYearString = releaseYear.toString();

        // Wenn kein Rating oder "No filter" ausgewählt wurde, wird kein Wert an "getFilteredMovieListAsJSON" übergeben
        String ratingString = null;
        if (rating != null && !rating.toString().equals("No filter")) ratingString = rating.toString();

        // API Call wird ausgeführt, wobei im Falle eines Fehlers (z.B. Verbindung konnte nicht hergestellt werden)
        // eine Fehlermeldung und eine leere Liste angezeigt wird.
        String listOfMoviesAsJSON;
        try {
            listOfMoviesAsJSON = MovieAPI.getFilteredMovieListAsJSON(searchQuery,genreString,releaseYearString,ratingString);
        }catch(Exception e){
            showErrorInfo();
            observableMovies.clear();
            return;
        }

        // Falls es beim letzten Versuch Verbindungsprobleme zur API gab, und jetzt hat es wieder funktioniert,
        // die Fehlermeldung wieder entfernt.
        if (!Objects.equals(infoLabel.getText(), "Welcome to FHMDb!")) infoLabel.setText("Welcome to FHMDb!");

        List<Movie> filteredMovies = Movie.createMovieListFromJson(listOfMoviesAsJSON);

        observableMovies.clear();
        observableMovies.addAll(filteredMovies);
        updateReleaseYearValues();

        // Console Output for Testing
        System.out.println("---------------");
        System.out.println("Most popular Actor : " + getMostPopularActor(filteredMovies));
        System.out.println("Longest Movie Title : " + getLongestMovieTitle(filteredMovies));
        System.out.println("Number of Movie from Director " + filteredMovies.get(0).getDirectors().get(0) + " : " +
                countMoviesFrom(filteredMovies,filteredMovies.get(0).getDirectors().get(0)));
        System.out.println("---------------");
        System.out.println("Filme zwischen 2000 und 2020");
        for ( Movie movie : getMoviesBetweenYears(filteredMovies,2000,2020)) {
            System.out.println("Filmtitel : " + movie.getTitle());
            System.out.println("Zugehörige Film-ID : " + movie.getId());
            System.out.println("--");
        }
    }

    public void searchBtnClicked(ActionEvent actionEvent) {

        String searchQuery = searchField.getText().trim().toLowerCase();

        Object selectedGenre = null;
        if (genreComboBox.getSelectionModel().getSelectedItem() != null) {
            selectedGenre = genreComboBox.getSelectionModel().getSelectedItem();
        }

        Object selectedReleaseYear = null;
        if (releaseYearComboBox.getSelectionModel().getSelectedItem() != null) {
            selectedReleaseYear = releaseYearComboBox.getSelectionModel().getSelectedItem();
        }

        Object selectedRating = null;
        if (ratingComboBox.getSelectionModel().getSelectedItem() != null) {
        selectedRating = ratingComboBox.getSelectionModel().getSelectedItem();
        }

        applyAllFilters(searchQuery, selectedGenre, selectedReleaseYear, selectedRating);

        if(sortedState != SortedState.NONE) {
            sortMovies();
        }
    }

    public void sortBtnClicked(ActionEvent actionEvent) {
        sortMovies();
    }

    public void searchByIDBtnClicked (ActionEvent actionEvent) {
        String movieData;

        String searchQuery = searchField.getText().trim().toLowerCase();
        try{
            movieData = MovieAPI.getMovieFromIdAsJSON(searchQuery);
            if (movieData.equals("ERROR_400")){
                observableMovies.clear();
                infoLabel.setText("We could not find a movie with the entered ID.");
            }
        } catch (Exception e){
            // Falls kein Eintrag mit der übergebenen ID gefunden wurde, wird eine leere Liste und
            // eine Fehlermeldung angezeigt.
            observableMovies.clear();
            showErrorInfo();
            return;
        }

        // moviedata wird um eckige Klammern ergänzt.
        // Der Grund ist, dass die Funktion "createMovieListFromJson" nur ein JSON Array verarbeiten kann.
        // Der API "ID-Endpoint" liefert allerdings nur ein JSON-Objekt und kein JSON Array.
        // Mit den eckigen Klammern wird movieData zu einem JSON-Array mit nur einem Objekt, welches die Methode
        // "createMovieListFromJson"  verarbeiten kann.
        movieData = "[" + movieData + "]";

        List<Movie> filteredMovie = Movie.createMovieListFromJson(movieData);

        observableMovies.clear();
        observableMovies.addAll(filteredMovie);
    }

    // Liste für alle möglichen release years; distinct filtert die mehrmals vorkommenden Jahre heraus
    List<Integer> possibleReleaseYears() {
        return observableMovies.stream()
                .map((movie) -> movie.getReleaseYear())
                .distinct()
                .sorted() // sorted in ascending order
                .collect(Collectors.toList());
    }

    // Die Funktion updated, die Einträge in "ReleaseYear" DropDown Menü, falls es bei den Filmen, die von der API
    // geholt worden sind, ReleaseYears gibt, welche aktuell nicht im DropDown ersichtlich sind.
    private void updateReleaseYearValues() {
        if(possibleReleaseYears().size() > (releaseYearComboBox.getItems().size() - 1)){
            releaseYearComboBox.getItems().clear();
            releaseYearComboBox.getItems().add("No filter");  // add "no filter" to the combobox
            releaseYearComboBox.getItems().addAll(possibleReleaseYears());
        }
    }

    List <Double> possibleRatings() {
        List <Double> ratings = new ArrayList<>();
        for (double i = 1.0f; i <= 10.0f; i += 1.0f) {
            ratings.add(i);
        }
        return ratings;
    }
    
    private void showErrorInfo(){
        // Falls es beim API Request zu einem Fehler kam, wird eine leere Liste angezeigt.
        infoLabel.setText("Connection to Remote API could not be established. - Please try again!");
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