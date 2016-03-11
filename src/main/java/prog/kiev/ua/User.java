package prog.kiev.ua;

public class User {
    private final String login;
    private final String password;
    private String cookie;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getLogin() {
        return login;
    }
}
