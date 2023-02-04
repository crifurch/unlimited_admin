package dev.crifurch.unlimitedadmin.api.utils;

import com.google.gson.Gson;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    public static Map<String, Object> readJsonFromUrl(String link) throws IOException {
        InputStream input = new URL(link).openStream();
        // Input Stream Object To Start Streaming.
        try {                                 // try catch for checked exception
            BufferedReader re = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            // Buffer Reading In UTF-8
            String content = Read(re);
            input.close();
            Gson gson = new Gson();
            final Map<String, Object> hashMap = gson.fromJson(content, HashMap.class);
            return hashMap;
        } catch (Exception e) {
            return null;
        } finally {
            input.close();
        }
    }

    private static String Read(Reader re) throws IOException {
        StringBuilder str = new StringBuilder();
        int temp;
        do {
            temp = re.read();
            str.append((char) temp);

        } while (temp != -1);
        return str.toString();

    }

}
