package prog.kiev.ua;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class GetMsgThread extends Thread {
    private int n;
    private final String type;
    private final String userCookie;

    public GetMsgThread(String type, String userCookie) {
        this.type = type;
        this.userCookie = userCookie;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                URL url = new URL("http://localhost:8080/get?from=" + n + "&type=" + type);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("Cookie", userCookie);

                InputStream is = http.getInputStream();
                try {
                    int sz = is.available();
                    if (sz > 0) {
                        byte[] buf = new byte[is.available()];
                        is.read(buf);

                        Gson gson = new GsonBuilder().create();
                        Message[] list = gson.fromJson(new String(buf), Message[].class);

                        for (Message m : list) {
                            if (type.equals("personal"))
                                System.out.print(">>");
                            System.out.println(m);
                            n++;
                        }
                    }
                } finally {
                    is.close();
                }
                Thread.sleep(2000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
}
