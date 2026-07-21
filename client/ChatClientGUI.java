package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatClientGUI extends JFrame {

    private JTextArea chatArea;

    private JTextField messageField;
    private JTextField usernameField;

    private JButton connectButton;
    private JButton sendButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton darkModeButton;
    private JButton exitButton;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private boolean darkMode = false;

    public ChatClientGUI() {

        setTitle("💬 Java Socket Chat");

        setSize(750, 550);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(240,245,250));

        Font font = new Font("Segoe UI", Font.PLAIN, 15);

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(new Color(0,120,215));

        header.setBorder(new EmptyBorder(15,15,15,15));

        JLabel title = new JLabel("Java Socket Chat Application");

        title.setForeground(Color.WHITE);

        title.setFont(new Font("Segoe UI", Font.BOLD,24));

        header.add(title);

        add(header,BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());

        center.setBorder(new EmptyBorder(15,15,15,15));

        chatArea = new JTextArea();

        chatArea.setEditable(false);

        chatArea.setLineWrap(true);

        chatArea.setWrapStyleWord(true);

        chatArea.setFont(font);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        center.add(scrollPane);

        add(center,BorderLayout.CENTER);

        JPanel south = new JPanel();

        south.setLayout(new BoxLayout(south,BoxLayout.Y_AXIS));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel userLabel = new JLabel("Username");

        userLabel.setFont(font);

        topRow.add(userLabel);

        usernameField = new JTextField(18);

        usernameField.setFont(font);

        topRow.add(usernameField);

        connectButton = new JButton("Connect");

        connectButton.setBackground(new Color(0,120,215));

        connectButton.setForeground(Color.WHITE);

        topRow.add(connectButton);

        south.add(topRow);

        JPanel bottomRow = new JPanel(new BorderLayout());

        messageField = new JTextField();

        messageField.setFont(font);

        bottomRow.add(messageField,BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        sendButton = new JButton("Send");

        clearButton = new JButton("Clear Chat");

        saveButton = new JButton("Save Chat");

        darkModeButton = new JButton("🌙 Dark Mode");

        exitButton = new JButton("Exit");

        buttons.add(sendButton);

        buttons.add(clearButton);

        buttons.add(saveButton);

        buttons.add(darkModeButton);

        buttons.add(exitButton);

        bottomRow.add(buttons,BorderLayout.EAST);

        south.add(bottomRow);

        add(south,BorderLayout.SOUTH);

        connectButton.addActionListener(e -> connectToServer());

        sendButton.addActionListener(e -> sendMessage());

        messageField.addActionListener(e -> sendMessage());

        clearButton.addActionListener(e -> chatArea.setText(""));

        saveButton.addActionListener(e -> saveChat());

        darkModeButton.addActionListener(e -> toggleDarkMode());

        exitButton.addActionListener(e -> {

            try {

                if(writer != null){

                    writer.println(usernameField.getText()+" left the chat.");

                }

                if(socket != null){

                    socket.close();

                }

            }

            catch(Exception ex){

                ex.printStackTrace();

            }

            System.exit(0);

        });

    }
        private void connectToServer() {

        try {

            String username = usernameField.getText().trim();

            if (username.isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Please enter a username.");

                return;

            }

            socket = new Socket("localhost", 5000);

            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            writer = new PrintWriter(
                    socket.getOutputStream(), true);

            writer.println(username);

            chatArea.append("====================================\n");
            chatArea.append("Connected to Server\n");
            chatArea.append("Welcome " + username + "!\n");
            chatArea.append("====================================\n\n");

            connectButton.setEnabled(false);

            usernameField.setEditable(false);

            Thread receiveThread = new Thread(() -> {

                try {

                    String message;

                    while ((message = reader.readLine()) != null) {

                        chatArea.append(message + "\n");

                        chatArea.setCaretPosition(
                                chatArea.getDocument().getLength());

                    }

                } catch (IOException ex) {

                    chatArea.append("\nServer Disconnected.\n");

                }

            });

            receiveThread.start();

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to connect to server!");

        }

    }

    private void sendMessage() {

        String msg = messageField.getText().trim();

        if (msg.isEmpty())
            return;

        if (writer == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please connect to the server first.");

            return;

        }

        writer.println(msg);

        String time = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        chatArea.append("[Me " + time + "] " + msg + "\n");

        chatArea.setCaretPosition(
                chatArea.getDocument().getLength());

        messageField.setText("");

        messageField.requestFocus();

    }

    private void saveChat() {

        JFileChooser chooser = new JFileChooser();

        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Text Files",
                        "txt"));

        if (chooser.showSaveDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            try (BufferedWriter bw =
                         new BufferedWriter(
                                 new FileWriter(
                                         chooser.getSelectedFile() + ".txt"))) {

                bw.write(chatArea.getText());

                JOptionPane.showMessageDialog(
                        this,
                        "Chat saved successfully!");

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to save chat!");

            }

        }

    }

    private void toggleDarkMode() {

        if (!darkMode) {

            getContentPane().setBackground(
                    new Color(35,35,35));

            chatArea.setBackground(
                    new Color(45,45,45));

            chatArea.setForeground(Color.WHITE);

            messageField.setBackground(
                    new Color(60,60,60));

            messageField.setForeground(Color.WHITE);

            usernameField.setBackground(
                    new Color(60,60,60));

            usernameField.setForeground(Color.WHITE);

            darkModeButton.setText("☀ Light Mode");

            darkMode = true;

        } else {

            getContentPane().setBackground(
                    new Color(240,245,250));

            chatArea.setBackground(Color.WHITE);

            chatArea.setForeground(Color.BLACK);

            messageField.setBackground(Color.WHITE);

            messageField.setForeground(Color.BLACK);

            usernameField.setBackground(Color.WHITE);

            usernameField.setForeground(Color.BLACK);

            darkModeButton.setText("🌙 Dark Mode");

            darkMode = false;

        }

    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            ChatClientGUI gui = new ChatClientGUI();

            gui.setVisible(true);

        });

    }

}