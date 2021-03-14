package lessons3.threeClients.app;


import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Model {

    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 8102;
    private Socket socket;
    private Button send_Button;
    boolean isAuthorized = false;



    DataInputStream dis;
    DataOutputStream dos;

    private TextField input_text;
    private TextArea output_text;

    void setTextField(TextField s) {
        input_text = s;
    }

    void setTextArea(TextArea s) {
        output_text = s;
    }

    void setButton(Button s) {
        send_Button = s;
    }

    public Model() {
        try {
            connection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connection() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                while (true) {
                    String message = dis.readUTF();
                    if (message.startsWith("/authok")) {
                        isAuthorized = true;
                        output_text.appendText(message+"\n");
                        break;
                    }
                    output_text.appendText(message+"\n");
                }
                while (isAuthorized){
                    String messageFromServer = dis.readUTF();
                    output_text.appendText(messageFromServer + "\n");
                }
            } catch (IOException ignored) {
            }

        }).start();
    }

    public void send() {
        if (input_text.getText() != null && !input_text.getText().trim().isEmpty()) {
            try {
                dos.writeUTF(input_text.getText());
                if(input_text.getText().equals("/end")) {
                    isAuthorized = false;
                    closeConnection();
                }
                input_text.setText("");
            } catch (IOException ignored) {
            }
        }
    }



    private void closeConnection() {
        try {
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException ignored) {
        }
    }
    }



