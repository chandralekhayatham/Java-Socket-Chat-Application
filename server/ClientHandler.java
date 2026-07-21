package server;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;

    public ClientHandler(Socket socket) {

        this.socket = socket;

        try {

            reader = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            writer = new PrintWriter(
                    socket.getOutputStream(), true);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    @Override
    public void run() {

        try {

            username = reader.readLine();

            String joinTime =
                    LocalTime.now().format(
                            DateTimeFormatter.ofPattern("HH:mm"));

            ChatServer.broadcast(
                    "🟢 [" + joinTime + "] "
                            + username + " joined the chat.");

            String message;

            while ((message = reader.readLine()) != null) {

                String time =
                        LocalTime.now().format(
                                DateTimeFormatter.ofPattern("HH:mm"));

                ChatServer.broadcast(
                        "[" + time + "] "
                                + username + " : "
                                + message);

            }

        } catch (IOException e) {

            System.out.println(username + " disconnected.");

        } finally {

            try {

                socket.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

            ChatServer.removeClient(this);

            if (username != null) {

                String leaveTime =
                        LocalTime.now().format(
                                DateTimeFormatter.ofPattern("HH:mm"));

                ChatServer.broadcast(
                        "🔴 [" + leaveTime + "] "
                                + username + " left the chat.");

            }

        }

    }

    public void sendMessage(String message) {

        writer.println(message);

    }

}