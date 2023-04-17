package at.ac.fhcampuswien.fhmdb.api;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieAPITest {

    @Test
    void getMovieFromIdAsJSON() {
        String theGodfatherMovieJSON = MovieAPI.getFilteredMovieListAsJSON("The Godfather","DRAMA","1972","9");
        String theGodfatherMovieId = Movie.createMovieListFromJson(theGodfatherMovieJSON).get(0).getId();

        String apiResponsesIdEndpoint = MovieAPI.getMovieFromIdAsJSON(theGodfatherMovieId);

        String expectedResult =
                "{\"id\":\"" + theGodfatherMovieId  + "\",\"title\":\"The Godfather\"," +
                        "\"genres\":[\"DRAMA\"],\"releaseYear\":1972,\"description\":" +
                        "\"The aging patriarch of an organized crime dynasty transfers control of his " +
                        "clandestine empire to his reluctant son.\"," +
                        "\"imgUrl\":\"http://www.imdb.com/title/tt0068646/mediaviewer/rm1067457536\"," +
                        "\"lengthInMinutes\":175," +
                        "\"directors\":[\"Francis Ford Coppola\"]," +
                        "\"writers\":[\"Mario Puzo\",\"Francis Ford Coppola\"]," +
                        "\"mainCast\":[\"Marlon Brando\",\"Al Pacino\",\"James Caan\"],\"rating\":9.2}";


        assertEquals(apiResponsesIdEndpoint,expectedResult);
    }
}