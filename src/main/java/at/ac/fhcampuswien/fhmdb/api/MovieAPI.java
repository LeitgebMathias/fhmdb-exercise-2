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

    public static String getFilteredMovieListAsJSON(String query, String genre, String releaseYear, String rating) {


        StringBuilder URL = new StringBuilder("http://localhost:8080/movies?");
        // Falls Sonderzeichen in der Suchabfrage vorkommen, müssen diese für die URL Encoded werden.
        if (query != null) {
            // Es kann damit auch nach Sonderzeichen gefiltert werden
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

            return response.body().string();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Diese Methode verwendet den zweiten Endpoint der API.
    public static String getMovieFromIdAsJSON(String id) throws RuntimeException {

        String URL = "http://localhost:8080/movies" + "/" + id;

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("User-Agent","*/*")
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            // Wenn die API keinen Film mit der übergebenen ID findet, wird ein HTTP-ERROR 400 zurückgeliefert.
            // In diesem Fall soll in der aufrufenden Funktion eine Infomeldung angezeigt werden.
            if(response.code() >= 400 &&  response.code() <= 499) {
                return "ERROR_400";
            }
            // Falls kein Fehler aufgetreten ist, wird der JSON String des Films zurückgegeben.
            return response.body().string();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
