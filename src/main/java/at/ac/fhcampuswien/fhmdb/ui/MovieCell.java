package at.ac.fhcampuswien.fhmdb.ui;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.stream.Collectors;

public class MovieCell extends ListCell<Movie> {
    private final Label title = new Label();
    private final Label detail = new Label();
    private final Label genre = new Label();

    private final Label movieData = new Label();

    private final Label writersAndDirectors = new Label();

    private final Label mainCast = new Label();


    private final VBox layout = new VBox(title, detail, genre, movieData, writersAndDirectors, mainCast);

    @Override
    protected void updateItem(Movie movie, boolean empty) {
        super.updateItem(movie, empty);

        if (empty || movie == null) {
            setGraphic(null);
            setText(null);
        } else {
            this.getStyleClass().add("movie-cell");
            title.setText(movie.getTitle());
            detail.setText(
                    movie.getDescription() != null
                            ? movie.getDescription()
                            : "No description available"
            );

            String genres = movie.getGenres()
                    .stream()
                    .map(Enum::toString)
                    .collect(Collectors.joining(", "));
            genre.setText(genres);

            movieData.setText("Year: " + movie.getReleaseYear() + " • Runtime: " + movie.getLengthInMinutes() + " min • Rating: " + movie.getRating());

            writersAndDirectors.setText("Writers: " + movie.getWriters().toString().replaceAll("[\\[\\](){}]","") + " • Directors: " + movie.getDirectors().toString().replaceAll("[\\[\\](){}]",""));

            mainCast.setText("Main Cast: " + movie.getMainCast().toString().replaceAll("[\\[\\](){}]",""));

            // for formatting used the help of: https://stackoverflow.com/questions/25852961/how-to-remove-brackets-character-in-string-java

            // color scheme
            title.getStyleClass().add("text-yellow");
            detail.getStyleClass().add("text-white");
            genre.getStyleClass().add("text-white");
            genre.setStyle("-fx-font-style: italic");
            movieData.getStyleClass().add("text-black");
            writersAndDirectors.getStyleClass().add("text-black");
            mainCast.getStyleClass().add("text-black");


            layout.setBackground(new Background(new BackgroundFill(Color.web("#454545"), null, null)));

            // layout
            title.fontProperty().set(title.getFont().font(20));
            detail.setMaxWidth(this.getScene().getWidth() - 30);
            detail.setWrapText(true);
            layout.setPadding(new Insets(10));
            layout.spacingProperty().set(10);
            layout.alignmentProperty().set(javafx.geometry.Pos.CENTER_LEFT);
            setGraphic(layout);
        }
    }
}

