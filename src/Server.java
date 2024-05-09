import java.net.*; 
import java.io.*; 

public class Server implements Runnable {
    private ServerSocket server = null; 
    private Thread thread = null; 
    private ServerThread clients[] = new ServerThread[10]; 
    private int clientCount = 0;  
    private volatile boolean isRunning = true; 

    public Server(int port) {
        try {
            System.out.println("Connecting to port " + port); 
            server = new ServerSocket(port); 
            System.out.println(String.format("Server %s started: ", server)); 
            start(); 
        } catch (IOException ioe) {
            System.out.println("Error when starting server: " + ioe.getMessage()); 
        }
    }

    public void run() {
        while (isRunning) {
            try {
                System.out.println("Waiting for a client..."); 
                addThread(server.accept()); 
            } catch (IOException ioe) {
                System.out.println("Error when connecting client: " + ioe.getMessage());
                stopServer();   
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this); 
            thread.start(); 
        }
    }

    public void stopServer() {
        isRunning = false; 
        try {
            server.close(); 
        } catch (IOException ioe) {
            System.out.println("Error closing server socket"); 
        }
    }

    private int searchClient(int ID) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].getID() == ID) {
                return i; 
            }
        }
        return -1; 
    }

    public synchronized void handle(int ID, String input) {
        if (input.equals(".exit")) {
            clients[searchClient(ID)].send(".exit"); 
            remove(ID); 
        } else {
            // could add if the client ID is not that of the sender then we send 
            for (int i = 0; i < clientCount;  i++) {
                clients[i].send(input); 
            }
        }
    }

    public synchronized void remove(int ID) {
        int pos = searchClient(ID); 
        if (pos >= 0) {
            ServerThread threadToTerminate = clients[pos]; 
            System.out.println("Removing client thread " + clients[pos]); 

            if (pos < clientCount-1) {
                for (int i = pos+1; i > clientCount; i++) {
                    clients[i-1] = clients[i];
                }
            }
            clientCount --; 

            try {
                threadToTerminate.close(); 
            } catch (IOException io) {
                System.out.println("Error when trying to close thread"); 
            }
        }
    }

    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            System.out.println("Client accepted " + socket); 
            clients[clientCount] = new ServerThread(this, socket); 
            try {
                clients[clientCount].open(); 
                clients[clientCount].start(); 
                clientCount++; 
            } catch (IOException ioe) {
                System.out.println("Error starting new thread"); 
            }
        } else {
            System.out.println("Maximum number of clients reach"); 
        }
    }

    public static void main(String args[]) {
        Server server = null; 
        if (args.length != 1) {
            System.out.println("Provide port number when evoking class"); 
        } else {
            server = new Server(Integer.parseInt(args[0])); 
        }
    }
}



