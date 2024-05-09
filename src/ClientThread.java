import java.net.*; 
import java.io.*; 


public class ClientThread extends Thread {
    private Socket socket = null; 
    private Client client = null; 
    private BufferedReader streamIn = null; 

    public ClientThread (Client _client, Socket _socket) {
        client = _client; 
        socket = _socket; 
        open(); 
        start(); 
    }

    public void open() {
        try {
            streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
        } catch (IOException ioe) {
            System.out.println("Error when opening input stream"); 
            client.stop(); 
        }
    }

    public void close() {
        try {
            if (streamIn != null) {
                streamIn.close(); 
            } 
        } catch (IOException ioe) {
            System.out.println("Error when closing stream");
        }
    }


    public void run() {
        while(true) {
            try { 
                String input = streamIn.readLine(); 
                System.out.println("The send method seems to work"); 
                client.handle(input); // does not seem to work 
            } catch (IOException ioe) {
                System.out.println("Reading error");
                client.stop(); 
            }
        }
    }
}
