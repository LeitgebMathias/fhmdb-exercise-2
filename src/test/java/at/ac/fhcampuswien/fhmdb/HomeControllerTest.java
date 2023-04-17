package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

//Test für den Stream der Methode "getMostPopularActor":

    @Test
    public void testGetMostPopularActor() {
        Movie movie1 = new Movie("1", "Inception", Arrays.asList(Genre.ACTION, Genre.ADVENTURE, Genre.SCIENCE_FICTION),
                2010, "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
                "https://www.imdb.com/title/tt1375666/", 148, Arrays.asList("Christopher Nolan"),
                Arrays.asList("Christopher Nolan"), Arrays.asList("Leonardo DiCaprio", "Morgan Freeman", "Ellen Page", "Tom Hardy"), 8.8f);

        Movie movie2 = new Movie("2", "The Shawshank Redemption", Arrays.asList(Genre.DRAMA),
                1994, "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                "https://www.imdb.com/title/tt0111161/", 142, Arrays.asList("Frank Darabont"),
                Arrays.asList("Stephen King", "Frank Darabont"), Arrays.asList("Leonardo DiCaprio", "Morgan Freeman"), 9.3f);

        Movie movie3 = new Movie("3", "The Dark Knight", Arrays.asList(Genre.ACTION, Genre.CRIME, Genre.DRAMA),
                2008, "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                "https://www.imdb.com/title/tt0468569/", 152, Arrays.asList("Christopher Nolan"),
                Arrays.asList("Jonathan Nolan", "Christopher Nolan"), Arrays.asList("Morgan Freeman", "Heath Ledger", "Aaron Eckhart"), 9.0f);

        List<Movie> movies = Arrays.asList(movie1, movie2, movie3);

        HomeController crtl = new HomeController();

        String mostPopularActor = crtl.getMostPopularActor(movies);

        assertEquals("Morgan Freeman", mostPopularActor);
    }

    @Test
    public void getLongestMovieTitle() {

        Movie movie1 = new Movie("1", "Inception", Arrays.asList(Genre.ACTION, Genre.ADVENTURE, Genre.SCIENCE_FICTION),
                2010, "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
                "https://www.imdb.com/title/tt1375666/", 148, Arrays.asList("Christopher Nolan"),
                Arrays.asList("Christopher Nolan"), Arrays.asList("Leonardo DiCaprio", "Morgan Freeman", "Ellen Page", "Tom Hardy"), 8.8f);

        Movie movie2 = new Movie("2", "The Shawshank Redemption", Arrays.asList(Genre.DRAMA),
                1994, "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                "https://www.imdb.com/title/tt0111161/", 142, Arrays.asList("Frank Darabont"),
                Arrays.asList("Stephen King", "Frank Darabont"), Arrays.asList("Leonardo DiCaprio", "Morgan Freeman"), 9.3f);

        Movie movie3 = new Movie("3", "The Dark Knight", Arrays.asList(Genre.ACTION, Genre.CRIME, Genre.DRAMA),
                2008, "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                "https://www.imdb.com/title/tt0468569/", 152, Arrays.asList("Christopher Nolan"),
                Arrays.asList("Jonathan Nolan", "Christopher Nolan"), Arrays.asList("Morgan Freeman", "Heath Ledger", "Aaron Eckhart"), 9.0f);

        List<Movie> movies = Arrays.asList(movie1, movie2, movie3);

        HomeController crtl = new HomeController();

        int longestOne = crtl.getLongestMovieTitle(movies);

        assertEquals(24, longestOne); //Der längste Film ("The Shawshank Redemption") hat 24 Zeichen.
    }
}