package at.ac.fhcampuswien.fhmdb.api;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MovieAPI {

    private static final OkHttpClient client = new OkHttpClient();

    public static String getFilteredMovieListAsJSON(String query, String genre, Integer releaseYear, Double rating) {

        StringBuilder URL = new StringBuilder("http://localhost:8080/movies?");
        // Falls Sonderzeichen in der Suchabfrage vorkommen, müssen diese für die URL Encoded werden.
        if (query != null) {
            // Leerzeichen werden ignoriert.
            query = URLEncoder.encode(query, StandardCharsets.UTF_8);
            // Der Befehl oben wandelt ein Leerzeichen in ein "+" um. Das wird mit dem folgenden Befehl korrigiert.
            query = query.replace("+","%20");
            URL.append("query=").append(query).append("&");
        }
        if (genre != null) URL.append("genre=").append(genre).append("&");
        if (releaseYear != null) URL.append("releaseYear=").append(releaseYear).append("&");
        if (rating != null) URL.append("ratingFrom=").append(rating);

        // Sorgt für eine saubere URL. Also kein "?" oder "&" am Ende.
        if (URL.charAt(URL.length()-1) == '?' || URL.charAt(URL.length()-1) == '&') {
            URL.deleteCharAt(URL.length() - 1);
        }

        Request request = new Request.Builder()
                .url(URL.toString())
                .addHeader("User-Agent","*/*")
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            // TODO : Error Handling einbauen, falls keine HTTP-200 Message zurückkommt.
            return response.body().string();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Diese Methode verwendet den zweiten Endpoint der API.
    public static String getMovieFromIdAsJSON(String id){
        // TODO : Muss ich noch fertig schreiben
        return null;
    }

}
