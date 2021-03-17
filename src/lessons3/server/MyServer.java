package lessons3.server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MyServer {
    private final int PORT = 8105;
    private List<ClientHandler> clients;
    private AuthService authService;

    public MyServer() {


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
    /*public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
    }*/

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
    }


}


