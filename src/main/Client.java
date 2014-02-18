package main;
import java.net.*;
import java.io.*;

import pubsub.Publisher;


/**
 * DEFINITION:
 * 
 * This class is a multi-threaded client for the chat room. It creates two
 * threads. One to read data from the standard input (i.e. what the client
 * types) and to send to the user. One to read data from the server and print
 * to standard output. 
 * 
 */
public class Client {

    // The client socket.
    private Socket clientSocket; 
    // To read from the socket.
    public  BufferedReader input;
    // To write to the socket.
    public  PrintWriter output; 
    // To be able to write to the server, closed has to be false.
    public static boolean closed = false;
    // The user name assigned to each client. 
    public String username;
    // The port which the client Socket should attach to.
    private int port;
    // The host name. 
    private String host;
    private Publisher publisher;

    /**
     * Construct the Client. 
     */
    public Client(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.publisher = new Publisher();
    }

    /**
     * Start a dialog.
     * @return true if dialog is started by connecting to the server, else false.
     * @throws IOException 
     */
    public String startDialog() throws IOException {
        try {
            // Open a socket on a given host and port. 
            clientSocket = new Socket(host, port); 
        } catch (UnknownHostException e) {
            return ("Error, Unknown host: "+ e);
        } catch (IOException e) {
            return ("Error connecting the server: "+ e);
        }
        try {
            // Open input and output streams to be able to write to the server.
            input  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(),true); 

        } catch (IOException e) {
            closeConnection();
            return ("Error exception opening input/output streams: "+ e);

        }
        output.println(username);
        if (input.readLine().equals("Username already taken.")) {
            closeConnection();
            return "Username already taken.";
        }
        else {
            // Start the thread for particular client to listen to the server.
            publisher.addInputStream(input);
            publisher.start();
            return "Connected";
        }
    }
    /**
     * Close the output stream, close the input stream, close the socket
     * when the user logs out and the "Bye" message is received from the
     * server so that the variable closed is set to true.
     */
    public void closeConnection() {
        try {
            output.close();
            input.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();			
        }

    }
    
    /**
     * Returns publisher associated with this instance of Client
     */
    public Publisher getPublisher() {
        return publisher;
    }
    
    /**
     * Returns client socket associated with this instance of Client
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

}
