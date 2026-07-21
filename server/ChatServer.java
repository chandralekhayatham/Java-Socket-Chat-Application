package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {

    private static final int PORT = 5000;

    private static Set<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("     JAVA SOCKET CHAT SERVER");
        System.out.println("========================================");
        System.out.println("Server Started Successfully...");
        System.out.println("Listening on Port : " + PORT);
        System.out.println();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {

                Socket socket = serverSocket.accept();

                System.out.println("Client Connected : "
                        + socket.getInetAddress());

                ClientHandler client =
                        new ClientHandler(socket);

                clients.add(client);

                new Thread(client).start();

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public static void broadcast(String message) {

        for (ClientHandler client : clients) {

            client.sendMessage(message);

        }

    }

    public static void removeClient(ClientHandler client) {

        clients.remove(client);

    }

}