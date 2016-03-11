package prog.kiev.ua;

import prog.kiev.ua.util.Requests;
import java.io.IOException;
import java.util.Scanner;


public class Main {
	public static final String URL_PATH = "http://localhost:8080";

	public static void main(String[] args) throws IOException {

		try (Scanner scanner = new Scanner(System.in)) {
			User user = authorization(scanner);
			String cookie = user.getCookie();
			createTreadMessages("public", cookie);
			createTreadMessages("personal", cookie);

			while (true) {
				String text = scanner.nextLine();
				if (text.isEmpty())
					break;

				if (text.contains("-c")) {
					boolean fine = executeCommand(text, cookie, scanner, user);
					if (!fine)
						break;
					continue;
				}

				Message msg = new Message();
				boolean isGroup = false;

				if (text.contains("-t")) {
					makeMsg(text, msg);
				} else if (text.contains("-g")) {
					isGroup = true;
					makeMsg(text, msg);
				} else {
					msg.setText(text);
				}

				try {
					String url = "/send";
					if (isGroup) {
						url = "/sendtogroup";
					}
					int statusCode = msg.send(URL_PATH + url, user);

					if (statusCode != 200) {
						System.out.println("HTTP error: " + statusCode);
						return;
					}
				} catch (IOException ex) {
					System.out.println("Error: " + ex.getMessage());
					return;
				}
			}
		}
	}

	private static boolean executeCommand(String text, String cookie,
										  Scanner scanner, User user) throws IOException {
		if (text.contains("print users")) {
			Requests.printUsers("/getusers", cookie);
		} else if (text.contains("print online users")) {
			Requests.printUsers("/getonlineusers", cookie);
		} else if (text.contains("create group")) {
			String[] group = enterGroup(scanner, user);
			if (group == null)
				return false;
			int statusCode = Requests.createGroup(group, cookie);
			if (statusCode != 200) {
				System.out.println(">>Error : group was not created");
			} else {
				System.out.println(">> group create");
			}
		}
		return true;
	}

	private static void makeMsg(String text, Message m) {
		String[] strings = text.split("//");
		String nameTo = strings[0].trim().substring(3);
		String msg = strings[1].trim();
		m.setTo(nameTo);
		m.setText(msg);
	}

	private static String[] enterGroup(Scanner scanner, User user) {
		System.out.println("Enter the group name :");
		String nameGroup = scanner.nextLine();
		System.out.println("Enter names of participants :");
		String group = scanner.nextLine();
		if (nameGroup.isEmpty() || group.isEmpty())
			return null;
		group = nameGroup + " " + group + " " + user.getLogin();
		return group.split(" ");
	}

	private static User authorization(Scanner scanner) throws IOException {
		User user = null;
		int statusCode = 0;
		do {
			System.out.println("Enter login: ");
			String login = scanner.nextLine();
			System.out.println("Enter password: ");
			String password = scanner.nextLine();
			if (login.isEmpty() || password.isEmpty())
				continue;
			user = new User(login, password);
			statusCode = Requests.authorize(user);

			if (statusCode != 200) {
				System.out.println("Incorrect login or password ! \n try again..");
			}
		} while (statusCode != 200);
		return user;
	}

	private static void createTreadMessages(String typeMsg, String cookie) {
		GetMsgThread thread = new GetMsgThread(typeMsg, cookie);
		thread.setDaemon(true);
		thread.start();
	}



}
