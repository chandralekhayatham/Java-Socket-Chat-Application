package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private static final String SERVER = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        try {

            Socket socket = new Socket(SERVER, PORT);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter writer = new PrintWriter(
                    socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            System.out.println("================================");
            System.out.println("     JAVA CHAT CLIENT");
            System.out.println("================================");

            System.out.print("Enter Username : ");
            String username = scanner.nextLine();

            writer.println(username);

            Thread receiveThread = new Thread(() -> {

                try {

                    String message;

                    while ((message = reader.readLine()) != null) {

                        System.out.println(message);

                    }

                } catch (IOException e) {

                    System.out.println("Disconnected from server.");

                }

            });

            receiveThread.start();

            while (true) {

                String msg = scanner.nextLine();

                if (msg.equalsIgnoreCase("exit")) {

                    writer.println("left the chat.");

                    socket.close();

                    break;

                }

                writer.println(msg);

            }

            scanner.close();

        } catch (IOException e) {

            System.out.println("Unable to connect to server.");

            e.printStackTrace();

        }

    }

}