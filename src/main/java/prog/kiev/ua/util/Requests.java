package prog.kiev.ua.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import prog.kiev.ua.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Requests {
    public static final String URL_PATH = "http://localhost:8080";
    private Requests() {}

    public static int authorize(User user) throws IOException {
        URL obj = new URL(URL_PATH + "/login");
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(user);
            os.write(json.getBytes());

            String cookie = conn.getHeaderField("Set-Cookie");
            user.setCookie(cookie);
            return conn.getResponseCode();
        }
    }

    public static void printUsers(String requestUrl, String cookie) throws IOException {
        URL url = new URL(URL_PATH + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Cookie", cookie);

        try (InputStream in = connection.getInputStream()) {
            int sz = in.available();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sz > 0) {
                byte[] buf = new byte[in.available()];
                in.read(buf);

                Gson gson = new GsonBuilder().create();
                String[] list = gson.fromJson(new String(buf), String[].class);
                System.out.println(list.length);
                for (String login : list)
                    System.out.print(login + " | ");
                System.out.println();
            }
        }
    }

    public static int createGroup(String[] group, String cookie) throws IOException {
        URL url = new URL(URL_PATH + "/creategroup");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Cookie", cookie);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Cookie", cookie);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            String json = toJSON(group);
            os.write(json.getBytes());

            return conn.getResponseCode();
        }
    }

    private static String toJSON(String[] list) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(list);
    }

}
