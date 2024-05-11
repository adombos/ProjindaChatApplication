import java.net.*; 
import java.io.*; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame implements Runnable, ActionListener {
    private Socket socket = null; 
    private Thread thread = null; 
    private BufferedReader console = null; 
    private BufferedWriter streamOut = null; 
    private ClientThread client = null; 
    private volatile boolean isRunning = true; 
    private JTextField inputField;
    private JTextArea displayArea;

    public Client (String serverName, int serverPort) {
        System.out.println("Connecting..."); 
        setTitle("Chat Client"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        setSize(400, 400); 
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(); 
        panel.setLayout(new BorderLayout());

        // Add input field 
        inputField = new JTextField(); 
        inputField.addActionListener(this);
        panel.add(inputField, BorderLayout.CENTER); 

        // Add send button 
        JButton sendButton = new JButton("Send"); 
        sendButton.addActionListener(this);
        panel.add(sendButton, BorderLayout.EAST); 

        add(panel, BorderLayout.SOUTH); 

        displayArea = new JTextArea(); 
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea); 
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);

        try { 
            socket = new Socket(serverName, serverPort); 
            System.out.println("Connected: " + socket);
            start();  

        } catch (UnknownHostException uhe) {
            System.out.println("Unknown host: " + uhe.getMessage()); 
        } catch (IOException ioe) {
            System.out.println("Error connecting to server: " + ioe.getMessage()); 
        }
    }

    public void actionPerformed(ActionEvent e) {
        sendMessage(); 
    }

    public void sendMessage() {
        String message = inputField.getText().trim(); 
        if (!message.isEmpty()) {
            try {
                streamOut.write(message);
                streamOut.newLine();
                streamOut.flush();
                displayArea.append("You: " + message + "\n");
                inputField.setText("");
            } catch (IOException ioe) {
                System.out.println("Error sending message: " + ioe.getMessage()); 
            }
        }
    }

    public void run() {
        while (isRunning) {
            try {
                String input = console.readLine();
                if (input != null) {
                    streamOut.write(input);
                    streamOut.newLine(); 
                    streamOut.flush();
                    System.out.println("Message sent...");
                } else {
                    stop(); 
                }
            } catch (IOException ioe) {
                System.out.println("Error when sending message: " + ioe.getMessage());
                stop();
            }
        }
    }

    public void handle(String msg) {
        if (msg == null) {
            System.out.print("Message is null"); 
            stop(); 
        } else {
            displayArea.append(msg + "\n");
            System.out.println(msg); //Print msg to the terminal 

        }
    }

    public void start() {
        try {
            console = new BufferedReader(new InputStreamReader(System.in)); 
            streamOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 

            if (thread == null) {
                client = new ClientThread(this, socket); 
                thread = new Thread(this); 
                thread.start(); 
            }
        } catch (IOException ioe) {
            System.out.println("Error when starting client: " + ioe.getMessage()); 
            stop(); 
        }
    }

    public void stop() {
        isRunning = false; 
        try {
            if (console != null) {
                console.close(); 
            }
            if (streamOut != null) {
                streamOut.close(); 
            }
            if (socket != null) {
                socket.close(); 
            }
        } catch (IOException ioe) {
            System.out.println("Error when closing: " + ioe.getMessage());
        }
    }

    public static void main(String args[]) {
        Client client = null; 
        if (args.length != 2) {
            System.out.println("Please provide server name and port number.");
        } else {
            client = new Client(args[0], Integer.parseInt(args[1])); 
        }   
    }
}
