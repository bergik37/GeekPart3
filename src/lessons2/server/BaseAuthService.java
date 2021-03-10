package lessons2.server;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaseAuthService implements AuthService {
    private  List<User> userlist;
    public BaseAuthService() {
        userlist = new ArrayList<>();
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }

    @Override
    public String getNickByLoginPass(String login, String password) {

        return userlist.stream().map(a -> {
                    if (a.login.equals(login) && a.password.equals(password)) {
                        return a.nick;
                    }
                    return "";
                }).collect(Collectors.joining());
    }

    public static class User {
        private String login;
        private String password;
        private String nick;

        public User() { }

        public String getLogin() {
            return login; }
        public void setLogin(String login) {
            this.login = login; }

        public String getPassword() {
            return password; }
        public void setPassword(String password) {
            this.password = password; }

        public String getNick() {
            return nick; }

        public void setNick(String nick) {
            this.nick = nick; }

        @Override
        public String toString() {
            return "User{login='" + login + '\'' +
                    ", password='" + password + '\'' +
                    ", nick='" + nick + '\'' +
                    '}';
        }
    }
}
