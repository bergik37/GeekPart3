package lessons3.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class ClientHandler {

    private final MyServer myServer;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final InputStreamReader isr;
    private OutputStreamWriter osw;
    private String name;
    private boolean isAuthorized;

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            isr = new InputStreamReader(new
                    FileInputStream("message.txt"), "UTF-32");
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try {
                    Thread.sleep(120000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isAuthorized) {
                    System.out.println("время на авторизацию вышло");
                    closeConnection();
                }
            }
            ).start();

        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public String getName() {
        return name;
    }

    private void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authentication() throws IOException, SQLException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                String nick =
                        myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        isAuthorized = true;
                        name = nick;
                        myServer.broadcastMsg(name + " зашел в чат");
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            } else {
                sendMsg("Your command will be need start with /auth");
            }
        }
    }

    public void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            System.out.println("от " + name + ": " + strFromClient);
            if (strFromClient.trim().startsWith("/")) {
                if (strFromClient.startsWith("/w")) {
                    String[] arr = strFromClient.split(" ", 3);
                    myServer.sendMessageToCertainClient(this, arr[1], name + ": " + arr[2]);
                }
                if (strFromClient.trim().startsWith("/list")) {
                    myServer.getOnlineUsersList(this);
                }

                if (strFromClient.trim().startsWith("/end")) {
                    return;
                }
            } else {
                myServer.broadcastMessage(name + ": " + strFromClient);
            }
        }
    }

    public void sendMsg(String msg) {
        /*try (DataOutputStream outMessage = new DataOutputStream(new
                FileOutputStream("message.txt"))) {
            out.writeUTF("4");

        } catch (IOException e) {
            e.printStackTrace();
        }
        try (DataInputStream inMessage = new DataInputStream(new
                FileInputStream("message.txt"))) {
            System.out.println(in.readUTF());

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            this.osw = new OutputStreamWriter(new
                    FileOutputStream("message.txt", true), StandardCharsets.UTF_8);
            osw.write(msg + "\n");
            osw.close();
            out.writeUTF(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
