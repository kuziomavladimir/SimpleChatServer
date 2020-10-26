package sample;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label label1;

    @FXML
    private ListView<?> usersListView;

    @FXML
    private Button runButton;

    @FXML
    private Button stopButton;

    private ConnectionHandler ch;
    private Thread t;
    private Thread workThread;
    ServerSocket serverSock;
    Socket clientSocket;
    PrintWriter writer;
    Runnable rr;

    @FXML
    void initialize() {

        runButton.setOnAction(event -> {
            doConnect();
        });

        stopButton.setOnAction(event -> {//Не работает. Потом разберусь
            try
            {
                ch.stopConnection();
//                ch = null;
//                serverSock.close();
//                clientSocket.close();
//                writer.close();
//                t.interrupt();
//                workThread.interrupt();
//                t = null;
//                workThread = null;
//                rr = null;

            } catch (Exception e)
            {
                System.out.println("exption на кнопке");
                e.printStackTrace();
            }
        });

    }

    public void doConnect()
    {
        ch = new ConnectionHandler();
        ch.clientOutputStreams = new ArrayList();
        rr = () -> {

            try
            {
                serverSock = new ServerSocket(5000);
                //Thread t;

                while (true)
                {
                    clientSocket = serverSock.accept();
                    writer = new PrintWriter(clientSocket.getOutputStream());
                    ch.clientOutputStreams.add(writer);

                    t = new Thread(ch.new ClientHandler(clientSocket));
                    t.setDaemon(true);
                    t.start();
                    System.out.println("Присоединен клиент");
                }

            } catch (Exception ex) {ex.printStackTrace();}
        };

        workThread = new Thread(rr);
        workThread.setDaemon(true);
        workThread.start();
        System.out.println("Server is started");
    }


}