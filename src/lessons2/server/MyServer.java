package lessons2.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MyServer {

    static final String DRIVER = "com.mysql.jdbc.Driver";
    static final String DB = "jdbc:mysql://localhost:3306/lessons";
    static final String USER = "root";
    static final String PASSWORD = "123qRT!";
    private final int PORT = 8102;
    private List<ClientHandler> clients;
    private AuthService authService;

    public MyServer() {

        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DB, USER, PASSWORD);
            statement = conn.createStatement();
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            ResultSet set = statement.executeQuery("SELECT * FROM new_table");
            while (set.next()) {
                BaseAuthService.User user = new BaseAuthService.User();
                //user.setId(set.getInt(1));
                user.setLogin(set.getString(2));
                user.setPassword(set.getString("password"));
                user.setNick(set.getString("nick"));
                System.out.println(user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress().getCanonicalHostName());
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Ошибка в работе сервера");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }

    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler c : clients) {
            if (c.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendMessageToCertainClient(ClientHandler from, String toName, String message) {
        for (ClientHandler c : clients) {
            if (c.getName().equals(toName)) {
                c.sendMsg(message);
                from.sendMsg(message);
            }
        }
    }

    public synchronized void getOnlineUsersList(ClientHandler clientHandler) {
        StringBuilder sb = new StringBuilder();
        for (ClientHandler c : clients) {
            if (!c.equals(clientHandler)) {
                sb.append(c.getName()).append(", ");
            }
        }
        int size = sb.length();
        sb.deleteCharAt(size - 1);
        sb.deleteCharAt(size - 2);
        clientHandler.sendMsg(sb.toString());
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
    }
}


