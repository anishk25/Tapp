package app.anish.com.tapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by anish_khattar25 on 7/28/17.
 */

public final class HttpUtils {

    public static String getResponseString(HttpURLConnection urlConnection) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder responseOutput = new StringBuilder();
        String line;

        while((line = bufferedReader.readLine()) != null) {
            responseOutput.append(line);
        }

        bufferedReader.close();

        return responseOutput.toString();
    }
}
