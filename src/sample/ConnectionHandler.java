package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class ConnectionHandler
{
    ArrayList clientOutputStreams;
    PrintWriter writer;

    BufferedReader reader;
    Socket sock;
    InputStreamReader isReader;


    public class ClientHandler implements Runnable
    {
        //BufferedReader reader;
        //Socket sock;

        public ClientHandler(Socket clientSocket)
        {
            try
            {
                sock = clientSocket;
                isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {ex.printStackTrace();}
        }

        public void run()
        {
            String message;
            try
            {
                while ((message = reader.readLine()) != null)
                {
                    System.out.println("read " + message);
                    if (message.startsWith("~~rezervStringForRegistration"))
                    {
                        System.out.println("Регистрация");
                        dbRegistrator(message);
                    }
                    else if (message.startsWith("~~rezervStringForSignIn"))
                    {
                        System.out.println("Вход нового пользователя");
                        dbLoginator(message);

                    }
                    else tellEveryone(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void tellEveryone(String message)
    {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext())
        {
            try
            {
                writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            }catch (Exception ex) {ex.printStackTrace();}
        }
    }

    public void stopConnection()
    {
        try
        {
            clientOutputStreams = null;
            writer.close();
            //reader.close();
            System.out.println(sock.isClosed());
//            isReader.close();
        }catch (Exception e)
        {
            System.out.println("Эксепшн");
            e.printStackTrace();
        }
    }

    public void dbRegistrator(String message)
    {
        String[] words = message.split(",");
        User user = new User(words[1], words[2], words[3]);
        System.out.println(user.geteMail() + user.getPassword() + user.getNickName());
        DataBaseHandler dbHandler = new DataBaseHandler();
        dbHandler.signUpUser(user);

    }
    public void dbLoginator(String message)
    {
        String[] words = message.split(",");
        User user = new User(words[1], words[2], "");
        DataBaseHandler dbHandler = new DataBaseHandler();

        ResultSet result = dbHandler.getUser(user);

        int counter = 0;
        try
        {
            while(result.next())
            {
                counter++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (counter > 0)
        {
            try {
                writer = new PrintWriter(sock.getOutputStream());
                writer.println("~~rezervStrindOpenProfile");
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Пользователь не найден");
            try {
                writer = new PrintWriter(sock.getOutputStream());
                writer.println("User Not Found");
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

//    public void go()
//    {
//        clientOutputStreams = new ArrayList();
//        try
//        {
//            ServerSocket serverSock = new ServerSocket(5000);
//
//            while (true)
//            {
//                Socket clientSocket = serverSock.accept();
//                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
//                clientOutputStreams.add(writer);
//
//                Thread t = new Thread(new ClientHandler(clientSocket));
//                t.start();
//                System.out.println("Присоединен клиент");
//            }
//
//        } catch (Exception ex) {ex.printStackTrace();}
//    }
}
