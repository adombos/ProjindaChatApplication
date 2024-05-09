import java.net.*; 
import java.io.*; 

public class Client implements Runnable {
    private Socket socket = null; 
    private Thread thread = null; 
    private BufferedReader console = null; 
    private BufferedWriter streamOut = null; 
    private ClientThread client = null; 
    private volatile boolean isRunning = true; 

    public Client (String serverName, int serverPort) {
        System.out.println("Connecting..."); 
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

    public void run() {
        while (isRunning) {
            try {
                String input = console.readLine();
                streamOut.write(input);
                streamOut.flush();
                System.out.println("Message sent..."); //printed in terminal of sender client 
            } catch (IOException ioe) {
                System.out.println("Error when sending: " + ioe.getMessage());
                stop();
            }
        }
    }

    public void handle(String msg) {
        if (msg.equals(".exit")) {
            System.out.println("print enter to exit"); 
            stop(); 
        } else {
            System.out.println(msg); 
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
            System.out.println("Error occurred while starting the client: " + ioe.getMessage()); 
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
            System.out.println("Error: " + ioe.getMessage());
        }
    }

    public static void main(String args[]) {
        Client client = null; 
        if (args.length != 2) {
            System.out.println("Please provide only server name and port number.");
        } else {
            client = new Client(args[0], Integer.parseInt(args[1])); 
        }   
    }
}
