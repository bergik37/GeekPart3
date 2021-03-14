package lessons3.oneClients.app;


import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Model {


    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 8105;
    boolean isAuthorized = false;
    DataInputStream dis;
    DataOutputStream dos;
    private Socket socket;
    private Button send_Button;
    private TextField input_text;
    private TextArea output_text;

    private InputStreamReader isr;
    private OutputStreamWriter osw;

    public Model() {
        try {
            connection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setTextField(TextField s) {
        input_text = s;
    }

    void setTextArea(TextArea s) {
        output_text = s;
    }


    void setButton(Button s) {
        send_Button = s;
    }

    public void connection() throws IOException {

        ArrayList<String> list = new ArrayList();
        isr = new InputStreamReader(new FileInputStream("message.txt"));
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                while (true) {
                    String message = dis.readUTF();
                    if (message.startsWith("/authok")) {
                        isAuthorized = true;
                        if (isAuthorized) {
                            int i = 0;
                            isr = new InputStreamReader(new FileInputStream("message.txt"));
                            while ((i = isr.read()) != -1) {
                                list.add(String.valueOf((char) i));
                                list.replaceAll(e -> e.replace(',', ' '));
                                //output_text.appendText((char) i+"\n");
                                System.out.print((char) i);
                            }
                            output_text.appendText(list + "\n");
                        }
                        output_text.appendText(message + "\n");
                        break;
                    }
                    output_text.appendText(message + "\n");
                }
                while (isAuthorized) {
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
                this.osw = new OutputStreamWriter(new
                        FileOutputStream("message.txt", true), StandardCharsets.UTF_8);
                dos.writeUTF(input_text.getText());
                if (!input_text.getText().equals("/auth") || !input_text.getText().equals("/list") || !input_text.getText().equals("/end"))
                    ;
                osw.write(input_text.getText() + "\n");
                osw.close();
                if (input_text.getText().equals("/end")) {
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



