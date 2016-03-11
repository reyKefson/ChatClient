package prog.kiev.ua;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date date = new Date();
	private String from;
	private String to;
	private String text;
	
	public String toJSON() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}
	
	public static Message fromJSON(String s) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(s, Message.class);
	}
	
	@Override
	public String toString() {
		String time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date);
		return new StringBuilder().append("[").append(time)
				.append(", From: ").append(from).append(", To: ").append(to)
				.append("] ").append(text).toString();
	}

	public int send(String url, User user) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Cookie", user.getCookie());

		conn.setDoOutput(true);
	
		OutputStream os = conn.getOutputStream();
		try {
			String json = toJSON();
			os.write(json.getBytes());
			
			return conn.getResponseCode();
		} finally {
			os.close();
		}
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
