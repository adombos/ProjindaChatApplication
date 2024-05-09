import java.net.*; 
import java.io.*; 

public class ServerThread extends Thread {
    private Socket socket = null; 
    private Server server = null; 
    private int ID = -1; 
    private BufferedReader streamIn = null; 
    private BufferedWriter streamOut = null; 
    private boolean isRunning = true; 

    public ServerThread(Server _server, Socket _socket) {
        super(); 
        server = _server; 
        socket = _socket; 
        ID = socket.getPort(); 
    }

    public void send(String msg) {
        try {
            streamOut.write(msg); 
            streamOut.flush(); 
            System.out.println("Successfully sent message: " + msg); 
        } catch (IOException ioe) {
            System.out.println("Error sending msg"); 
            server.remove(ID); 
            stopThread(); 
        }
    }

    public int getID() {
        return ID; 
    }

    public void run() {
        System.out.println(String.format("Server thread %d is currently running", ID)); 
        while (isRunning) {
            try {
                String input; 
                while ((input = streamIn.readLine()) != null) {
                    System.out.println("Received message: " + input); 
                    server.handle(ID, input); 
                }
            } catch (IOException ioe) {
                System.out.println("Error reading message"); 
                server.remove(ID);
                stopThread(); 
            }
        }
    }

    public void open() throws IOException {
        streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        streamOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
    }

    public void close() throws IOException {
        if (socket != null) {
            socket.close(); 
        } if (streamIn != null) {
            streamIn.close(); 
        } if (streamOut != null) {
            streamOut.close(); 
        }
    }

    public void stopThread() {
        isRunning = false;

        try { 
            if (socket != null) {
                socket.close(); 
            }
        } catch (IOException ioe) {
            System.out.println("Error when closing socket");
        }
    }
}
