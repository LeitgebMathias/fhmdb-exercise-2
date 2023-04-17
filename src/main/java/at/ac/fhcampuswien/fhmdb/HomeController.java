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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
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

    public void searchByID() {

    }


    // TODO : Diese Methode wird so angepasst, dass die Parameter für die Methode aus MovieAPI vorbereitet werden.
    // Der Aufruf der MovieAPI - Methode passiert dann in dieser Methode
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

        // TODO : Aufruf von "getFilteredMovieListAsJSON" um "releaseYear" und "rating" erweitern.
        String listOfMoviesAsJSON = MovieAPI.getFilteredMovieListAsJSON(searchQuery,genreString,releaseYearString,ratingString);

        List<Movie> filteredMovies = Movie.createMovieListFromJson(listOfMoviesAsJSON);

        observableMovies.clear();
        observableMovies.addAll(filteredMovies);
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
    }

    // Liste für alle möglichen release years; distinct filtert die mehrmals vorkommenden Jahre heraus
    List<Integer> possibleReleaseYears() {
        return observableMovies.stream()
                .map((movie) -> movie.getReleaseYear())
                .distinct()
                .sorted() // sorted in ascending order
                .collect(Collectors.toList());
    }

    List <Double> possibleRatings() {
        List <Double> ratings = new ArrayList<>();
        for (double i = 1.0f; i <= 10.0f; i += 1.0f) {
            ratings.add(i);
        }
        return ratings;
    }

}