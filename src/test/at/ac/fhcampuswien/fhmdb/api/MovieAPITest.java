package at.ac.fhcampuswien.fhmdb.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieAPITest {

    @Test
    void getMovieFromIdAsJSON() {
        String theGodfatherMovieId = "cf98675d-010e-493b-8bcd-690ffacd4bae";

        String apiResponsesIdEndpoint = MovieAPI.getMovieFromIdAsJSON(theGodfatherMovieId);

        String expectedResult =
                "{\"id\":\"cf98675d-010e-493b-8bcd-690ffacd4bae\",\"title\":\"The Godfather\"," +
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